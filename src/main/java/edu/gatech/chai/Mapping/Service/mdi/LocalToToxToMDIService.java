package edu.gatech.chai.Mapping.Service.mdi;

import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Stream;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CodeType;
import org.hl7.fhir.r4.model.Bundle.BundleType;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.DiagnosticReport.DiagnosticReportStatus;
import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.HumanName.NameUse;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.MessageHeader.MessageDestinationComponent;
import org.hl7.fhir.r4.model.MessageHeader.MessageSourceComponent;
import org.hl7.fhir.r4.model.Observation.ObservationStatus;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.Specimen.SpecimenCollectionComponent;
import org.hl7.fhir.r4.model.Specimen.SpecimenContainerComponent;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.UriType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import ca.uhn.fhir.parser.IParser;
import edu.gatech.chai.MDI.Model.ToxResult;
import edu.gatech.chai.MDI.Model.ToxSpecimen;
import edu.gatech.chai.MDI.Model.ToxToMDIModelFields;
import edu.gatech.chai.MDI.context.MDIFhirContext;
import edu.gatech.chai.MDI.model.resource.BundleMessageToxToMDI;
import edu.gatech.chai.MDI.model.resource.DiagnosticReportToxicologyToMDI;
import edu.gatech.chai.MDI.model.resource.MessageHeaderToxicologyToMDI;
import edu.gatech.chai.MDI.model.resource.ObservationToxicologyLabResult;
import edu.gatech.chai.MDI.model.resource.SpecimenToxicologyLab;
import edu.gatech.chai.MDI.model.resource.util.CompositionMDIAndEDRSUtil;
import edu.gatech.chai.Mapping.Util.CommonMappingUtil;
import edu.gatech.chai.Mapping.Util.LocalModelToFhirCMSUtil;
import edu.gatech.chai.VRCL.model.PatientVitalRecords;
import edu.gatech.chai.VRDR.model.util.DecedentUtil;

@Service
public class LocalToToxToMDIService {

	@Value("${raven_generated_systemid}")
	private String raven_generated_systemid;
	@Value("${my_url}")
	private String my_url;
	@Autowired
	private MDIFhirContext mdiFhirContext;

	public LocalToToxToMDIService(){
	}
	public String convertToMDIString(ToxToMDIModelFields inputFields, int index) {
		Bundle fullBundle = convertToMDI(inputFields, index);
		return convertToMDIString(fullBundle);
	}

	public String convertToMDIString(Bundle fullBundle) {
		IParser parser = mdiFhirContext.getCtx().newJsonParser();
		String returnString = parser.encodeResourceToString(fullBundle);
		return returnString;
	}

	public Bundle convertToMDI(ToxToMDIModelFields inputFields, int index) {
		// Create Tox-to-MDI Bundle
		BundleMessageToxToMDI returnBundle = new BundleMessageToxToMDI();
		String idTemplate = inputFields.FILEID + "-" + index;
		returnBundle.setId(idTemplate + "-Tox-Message-Bundle");
		Date now = new Date();
		// Assigning a raven generated system identifier
		returnBundle.setIdentifier(
				new Identifier().setSystem("urn:raven:temporary").setValue(Long.toString(now.getTime())));
		returnBundle.setType(BundleType.BATCH);
		// Create Message Header
		MessageHeaderToxicologyToMDI messageHeader = new MessageHeaderToxicologyToMDI();
		messageHeader.setId(idTemplate + "-MessageHeader-Tox-To-MDI");
		MessageSourceComponent msc = new MessageSourceComponent();
		msc.setName("Raven Generated Import");
		msc.setEndpoint(my_url);
		messageHeader.setSource(msc);
		LocalModelToFhirCMSUtil.addResourceToBundle(returnBundle, messageHeader);
		//Handle Local Agency
		Stream<String> agencyFields = Stream.of(inputFields.AGENCY_NAME, inputFields.AGENCY_STREET,
			inputFields.AGENCY_CITY, inputFields.AGENCY_COUNTY, inputFields.AGENCY_STATE,
			inputFields.AGENCY_ZIP, inputFields.CORONER_NAME, inputFields.INVESTIGATOR);
		//Local Agency will be an organizationrole + organization and practitioner for now.
		//TODO: Add Relevant MDI resources when they become available
		if(!agencyFields.allMatch(x -> x == null || x.isEmpty())) {
			createAgency(inputFields, idTemplate, returnBundle, messageHeader);
		}
		// Handle Laboratory Performer
		Resource performerResource = null; // Performer can be a US-Core-Practitioner or a PractitionerRole
		Reference performerReference = null;
		Stream<String> performerFields = Stream.of(inputFields.TOXORG_NAME, inputFields.TOXORG_STREET,
				inputFields.TOXORG_CITY, inputFields.TOXORG_COUNTY, inputFields.TOXORG_STATE, inputFields.TOXORG_ZIP,
				inputFields.TOXORG_COUNTRY, inputFields.TOXPERFORMER);
		if (!performerFields.allMatch(x -> x == null || x.isEmpty())) {
			performerResource = createPerformer(inputFields, idTemplate, returnBundle);
			performerReference = new Reference(
					performerResource.getResourceType().toString() + "/" + performerResource.getId());
			//Create performer manually adds practitioner, practitionerrole, whatever it needs. Don't need the hook here to complete it.
			//LocalModelToFhirCMSUtil.addResourceToBundle(returnBundle, performerResource);
		}
		// Create Patient
		Patient patientResource = null;
		Reference patientReference = null;
		Stream<String> decedentFields = Stream.of(inputFields.FIRSTNAME, inputFields.LASTNAME,
				inputFields.MIDNAME, inputFields.BIRTHDATE, inputFields.SUFFIXNAME);
		if (!decedentFields.allMatch(x -> x == null || x.isEmpty())) {
			patientResource = createPatient(inputFields, idTemplate);
			patientReference = new Reference("Patient/" + patientResource.getId());
			LocalModelToFhirCMSUtil.addResourceToBundle(returnBundle, patientResource);
		}
		// Create Diagnostic Report
		CodeableConcept toxOrderCodableConcept = new CodeableConcept();
		if (inputFields.TOXORDERCODE != null && !inputFields.TOXORDERCODE.isEmpty()) {
			toxOrderCodableConcept.setText(inputFields.TOXORDERCODE);
		}
		Date specimenReceiptDate = CommonMappingUtil.parseDateTimeFromField(inputFields,"RECEIPT_DATETIME",inputFields.RECEIPT_DATETIME);
		Date reportIssuedDate = CommonMappingUtil.parseDateTimeFromField(inputFields,"REPORTISSUANCE_DATETIME",inputFields.REPORTISSUANCE_DATETIME);
		DiagnosticReportToxicologyToMDI diagnosticReport = new DiagnosticReportToxicologyToMDI(
				DiagnosticReportStatus.FINAL, patientResource, toxOrderCodableConcept, specimenReceiptDate,
				reportIssuedDate);
		//Some post-construction manipulation of datetimes to ensure they're in the Z format
		diagnosticReport.getEffectiveDateTimeType().setTimeZoneZulu(true);
		diagnosticReport.getIssuedElement().setTimeZoneZulu(true);
		// Toxicology Identifier
		if (inputFields.TOXCASENUMBER != null && !inputFields.TOXCASENUMBER.isEmpty()) {
			Identifier identifier = new Identifier();
			identifier.setType(CompositionMDIAndEDRSUtil.trackingNumberTOXType);
			identifier.setSystem(raven_generated_systemid);
			identifier.setValue(inputFields.TOXCASENUMBER);
			diagnosticReport.addTrackingNumberExtension(identifier);
		}
		// MDI Identifier
		Stream<String> mdiIdentifierFields = Stream.of(inputFields.MDICASEID, inputFields.MDICASESYSTEM);
		if (!mdiIdentifierFields.allMatch(x -> x == null || x.isEmpty())) {
			Identifier identifier = new Identifier();
			identifier.setType(CompositionMDIAndEDRSUtil.trackingNumberMDIType);
			identifier.setSystem(raven_generated_systemid);
			identifier.setValue(inputFields.MDICASEID);
			diagnosticReport.addTrackingNumberExtension(identifier);
		}
		diagnosticReport.setId(idTemplate + "-DiagnosticReport-Tox-To-MDI");
		if (performerReference != null) {
			diagnosticReport.addPerformer(performerReference);
		}
		LocalModelToFhirCMSUtil.addResourceToBundle(returnBundle, diagnosticReport);
		messageHeader.addFocus(new Reference("DiagnosticReport/" + diagnosticReport.getId()));
		// Create Specimen
		Map<String, Reference> specimenNameToReference = new HashMap<String, Reference>();
		for (int i = 0; i < inputFields.SPECIMENS.size(); i++) {
			ToxSpecimen toxSpecimen = inputFields.SPECIMENS.get(i);
			Stream<String> specimenFields = Stream.of(toxSpecimen.NAME);
			if (!specimenFields.allMatch(x -> x == null || x.isEmpty())) {
				SpecimenToxicologyLab specimenResource = createSpecimen(toxSpecimen, i, idTemplate, patientReference);
				Reference specimenReference = new Reference("Specimen/" + specimenResource.getId());
				specimenReference.setDisplay(toxSpecimen.NAME);
				specimenNameToReference.put(toxSpecimen.NAME, specimenReference);
				diagnosticReport.addSpecimen(specimenReference);
				LocalModelToFhirCMSUtil.addResourceToBundle(returnBundle, specimenResource);
			}
		}
		// Create Result
		for (int i = 0; i < inputFields.RESULTS.size(); i++) {
			ToxResult toxResult = inputFields.RESULTS.get(i);
			Stream<String> resultFields = Stream.of(toxResult.ANALYSIS);
			if (!resultFields.allMatch(x -> x == null || x.isEmpty())) {
				ObservationToxicologyLabResult resultResource = createResult(toxResult, i, idTemplate,
						specimenNameToReference, patientReference);
				Reference resultReference = new Reference("Observation/" + resultResource.getId());
				resultReference.setDisplay(toxResult.ANALYSIS);
				diagnosticReport.addResult(resultReference);
				LocalModelToFhirCMSUtil.addResourceToBundle(returnBundle, resultResource);
			}
		}
		return returnBundle;
	}

	private Patient createPatient(ToxToMDIModelFields inputFields, String idTemplate) {
		Patient returnDecedent = new PatientVitalRecords();
		returnDecedent.setId(idTemplate + "-Decedent");
		Stream<String> caseIdFields = Stream.of(inputFields.MDICASESYSTEM);
		Stream<String> nameFields = Stream.of(inputFields.FIRSTNAME, inputFields.LASTNAME, inputFields.MIDNAME,
				inputFields.SUFFIXNAME);
		if(!caseIdFields.allMatch(x -> x == null || x.isEmpty())){
			Identifier patientIdentifier = new Identifier();
			patientIdentifier.setSystem(raven_generated_systemid);
			patientIdentifier.setValue(inputFields.MDICASEID);
		}
		if (!nameFields.allMatch(x -> x == null || x.isEmpty())) {
			HumanName name = new HumanName();
			name.addGiven(inputFields.FIRSTNAME);
			name.addGiven(inputFields.MIDNAME);
			name.setFamily(inputFields.LASTNAME);
			name.setUse(NameUse.OFFICIAL);
			if (inputFields.SUFFIXNAME != null && !inputFields.SUFFIXNAME.isEmpty()) {
				name.addSuffix(inputFields.SUFFIXNAME);
			}
			returnDecedent.addName(name);
		}
		if (inputFields.DECEDENTSEX != null && !inputFields.DECEDENTSEX.isEmpty()){
			if(LocalModelToFhirCMSUtil.containsIgnoreCase(inputFields.DECEDENTSEX, "Female")) {
				returnDecedent.setGender(AdministrativeGender.FEMALE);
			}
			else if(LocalModelToFhirCMSUtil.containsIgnoreCase(inputFields.DECEDENTSEX, "Male")) {
				returnDecedent.setGender(AdministrativeGender.MALE);
			}
			else{
				returnDecedent.setGender(AdministrativeGender.UNKNOWN);
			}
		}

		if (inputFields.BIRTHDATE != null && !inputFields.BIRTHDATE.isEmpty()) {
			Date birthDate = CommonMappingUtil.parseDateFromField(inputFields, "BIRTHDATE", inputFields.BIRTHDATE);
			returnDecedent.setBirthDate(birthDate);
		}
		return returnDecedent;
	}

	private Resource createAgency(ToxToMDIModelFields inputFields, String idTemplate, BundleMessageToxToMDI bundle, MessageHeaderToxicologyToMDI messageHeader) {
		Stream<String> organizationFields = Stream.of(inputFields.AGENCY_NAME,inputFields.AGENCY_STREET, inputFields.AGENCY_CITY, inputFields.AGENCY_COUNTY,
			inputFields.AGENCY_STATE, inputFields.AGENCY_ZIP);
		Stream<String> organizationAddressFields = Stream.of(inputFields.AGENCY_STREET, inputFields.AGENCY_CITY, inputFields.AGENCY_COUNTY,
			inputFields.AGENCY_STATE, inputFields.AGENCY_ZIP);
		Organization agencyOrganization = null;
		Practitioner coronerPractitioner = null;
		Practitioner investigatorPractitioner = null;
		PractitionerRole agencyCoronerPractitionerRole = null;
		PractitionerRole agencyInvestigatorPractitionerRole = null;

		Reference agencyOrganizationReference = null;
		Reference coronerPractitionerReference = null;
		Reference investigatorPractitionerReference = null;
		//Create Organization
		if (!organizationFields.allMatch(x -> x == null || x.isEmpty())) {
			agencyOrganization = new Organization();
			agencyOrganization.setId(idTemplate + "-Agency-Organization");
			agencyOrganization.setMeta(
					new Meta().addProfile("http://hl7.org/fhir/us/core/StructureDefinition/us-core-organization"));
			agencyOrganization.setActive(true);
			agencyOrganization.setName(inputFields.AGENCY_NAME);
			if(!organizationAddressFields.allMatch(x -> x == null || x.isEmpty())) {
				agencyOrganization.addAddress(LocalModelToFhirCMSUtil.createAddress(inputFields.AGENCY_NAME, inputFields.AGENCY_STREET,
					inputFields.AGENCY_CITY, inputFields.AGENCY_COUNTY, inputFields.AGENCY_STATE, inputFields.AGENCY_ZIP,
					""));
			}
			agencyOrganizationReference = new Reference("Organization/" + agencyOrganization.getId());
			LocalModelToFhirCMSUtil.addResourceToBundle(bundle, agencyOrganization);
			MessageDestinationComponent mdc = new MessageDestinationComponent();
			mdc.setReceiverTarget(agencyOrganization);
			mdc.setReceiver(agencyOrganizationReference);
			messageHeader.addDestination(mdc);
		}
		//Create Coroner
		if(inputFields.CORONER_NAME != null && !inputFields.CORONER_NAME.isEmpty()){
			coronerPractitioner = new Practitioner();
			coronerPractitioner.setId(idTemplate + "-Agency-Coroner");
			coronerPractitioner.setMeta(
					new Meta().addProfile("http://hl7.org/fhir/us/core/StructureDefinition/us-core-practitioner"));
			coronerPractitioner.addName(LocalModelToFhirCMSUtil.parseHumanName(inputFields.CORONER_NAME));
			LocalModelToFhirCMSUtil.setIdentiferDataAbsentReasonNotAsked(coronerPractitioner);
			coronerPractitionerReference = new Reference("Practitioner/" + coronerPractitioner.getId());
			LocalModelToFhirCMSUtil.addResourceToBundle(bundle, coronerPractitioner);
		}
		//Create Investigator
		if(inputFields.INVESTIGATOR != null && !inputFields.INVESTIGATOR.isEmpty()){
			investigatorPractitioner = new Practitioner();
			investigatorPractitioner.setId(idTemplate + "-Agency-Investigator");
			investigatorPractitioner.setMeta(
					new Meta().addProfile("http://hl7.org/fhir/us/core/StructureDefinition/us-core-practitioner"));
					investigatorPractitioner.addName(LocalModelToFhirCMSUtil.parseHumanName(inputFields.INVESTIGATOR));
			LocalModelToFhirCMSUtil.setIdentiferDataAbsentReasonNotAsked(investigatorPractitioner);
			investigatorPractitionerReference = new Reference("Practitioner/" + investigatorPractitioner.getId());
			LocalModelToFhirCMSUtil.addResourceToBundle(bundle, investigatorPractitioner);
		}
		//If Organzation and at least one practitioner exists, create a practitionerrole per org-practitioner pair
		if(agencyOrganization != null && (coronerPractitioner != null || investigatorPractitioner != null)){
			if(coronerPractitioner != null){
				agencyCoronerPractitionerRole = new PractitionerRole();
				agencyCoronerPractitionerRole.setId(idTemplate + "-Agency");
				agencyCoronerPractitionerRole.setMeta(
					new Meta().addProfile("http://hl7.org/fhir/us/core/StructureDefinition/us-core-practitioner"));
				agencyCoronerPractitionerRole.setOrganization(agencyOrganizationReference);
				agencyCoronerPractitionerRole.setPractitioner(coronerPractitionerReference);
				LocalModelToFhirCMSUtil.addResourceToBundle(bundle, agencyCoronerPractitionerRole);
			}
			if(investigatorPractitioner != null){
				agencyInvestigatorPractitionerRole = new PractitionerRole();
				agencyInvestigatorPractitionerRole.setId(idTemplate + "-Agency");
				agencyInvestigatorPractitionerRole.setMeta(
					new Meta().addProfile("http://hl7.org/fhir/us/core/StructureDefinition/us-core-practitioner"));
				agencyInvestigatorPractitionerRole.setOrganization(agencyOrganizationReference);
				agencyInvestigatorPractitionerRole.setPractitioner(investigatorPractitionerReference);
				LocalModelToFhirCMSUtil.addResourceToBundle(bundle, agencyInvestigatorPractitionerRole);
			}
		}
		return bundle;
	}

	private Resource createPerformer(ToxToMDIModelFields inputFields, String idTemplate, BundleMessageToxToMDI bundle) {
		// If there's an organization, create a PractitionerRole with Organization and
		// Practitioner, otherwise just create a practitioner
		boolean practitionerRoleSet = false;
		Resource returnPerformer = null;
		Organization organization = null;
		Reference organizationReference = null;
		PractitionerRole practitionerRole = null;
		Reference practitionerRoleReference = null;
		Practitioner practitioner = null;
		Reference practitionerReference = null;
		Stream<String> organizationFields = Stream.of(inputFields.TOXORG_NAME, inputFields.TOXORG_STREET,
				inputFields.TOXORG_CITY, inputFields.TOXORG_COUNTY, inputFields.TOXORG_STATE, inputFields.TOXORG_ZIP,
				inputFields.TOXORG_COUNTRY);
		if (!organizationFields.allMatch(x -> x == null || x.isEmpty())) {
			practitionerRoleSet = true;
			organization = new Organization();
			organization.setId(idTemplate + "-Laboratory-Organization");
			organization.setMeta(
					new Meta().addProfile("http://hl7.org/fhir/us/core/StructureDefinition/us-core-organization"));
			organization.setActive(true);
			organization.setName(inputFields.TOXORG_NAME);
			organization.addAddress(LocalModelToFhirCMSUtil.createAddress(inputFields.TOXORG_NAME, inputFields.TOXORG_STREET,
					inputFields.TOXORG_CITY, inputFields.TOXORG_COUNTY, inputFields.TOXORG_STATE, inputFields.TOXORG_ZIP,
					inputFields.TOXORG_COUNTRY));
			organizationReference = new Reference("Organization/" + organization.getId());
			LocalModelToFhirCMSUtil.addResourceToBundle(bundle, organization);
			practitionerRole = new PractitionerRole();
			practitionerRole.setId(idTemplate + "-Laboratory-PractitionerRole");
			practitionerRole.setMeta(
					new Meta().addProfile("http://hl7.org/fhir/us/core/StructureDefinition/us-core-practitionerrole"));
			practitionerRole.setOrganization(organizationReference);
			LocalModelToFhirCMSUtil.addResourceToBundle(bundle, practitionerRole);
			returnPerformer = practitionerRole;
			LocalModelToFhirCMSUtil.setIdentiferDataAbsentReasonNotAsked(practitionerRole);
			LocalModelToFhirCMSUtil.setEndpointDataAbsentReasonNotAsked(practitionerRole);
		}
		Stream<String> practitionerFields = Stream.of(inputFields.TOXPERFORMER);
		if (!practitionerFields.allMatch(x -> x == null || x.isEmpty())) {
			practitioner = new Practitioner();
			practitioner.setId(idTemplate + "-Practitioner");
			practitioner.setMeta(
					new Meta().addProfile("http://hl7.org/fhir/us/core/StructureDefinition/us-core-practitioner"));
			practitionerReference = new Reference("Practitioner/" + practitioner.getId());
			practitioner.addName(LocalModelToFhirCMSUtil.parseHumanName(inputFields.TOXPERFORMER));
			LocalModelToFhirCMSUtil.addResourceToBundle(bundle, practitioner);
			if (practitionerRoleSet) {
				practitionerRole.setPractitioner(practitionerReference);
			} else {
				returnPerformer = practitioner;
			}
		}
		return returnPerformer;
	}

	private SpecimenToxicologyLab createSpecimen(ToxSpecimen toxSpecimen, int specimenIndex, String idTemplate,
			Reference subjectReference) {
		String idTemplateWithIndex = idTemplate + "-Specimen-" + specimenIndex;
		SpecimenToxicologyLab specimenResource = new SpecimenToxicologyLab();
		specimenResource.setId(idTemplateWithIndex);
		specimenResource.setSubject(subjectReference);
		specimenResource.setType(new CodeableConcept().setText(toxSpecimen.NAME));
		if (toxSpecimen.IDENTIFIER != null && toxSpecimen.IDENTIFIER.isEmpty()) {
			specimenResource.setAccessionIdentifier(new Identifier().setValue(toxSpecimen.IDENTIFIER));
		}
		SpecimenCollectionComponent scc = new SpecimenCollectionComponent();
		boolean sccUsed = false;
		Stream<String> collectionFields = Stream.of(toxSpecimen.COLLECTED_DATETIME, toxSpecimen.AMOUNT);
		if (!collectionFields.allMatch(x -> x == null || x.isEmpty())) {
			sccUsed = true;
			Date collectedDate = CommonMappingUtil.parseDateTimeFromField(toxSpecimen, idTemplate, idTemplateWithIndex);
			scc.setCollected(new DateTimeType(collectedDate, TemporalPrecisionEnum.SECOND, TimeZone.getTimeZone(ZoneId.of("Z"))));
			scc.getCollectedDateTimeType().setTimeZoneZulu(true);
			Quantity quantity = LocalModelToFhirCMSUtil.parseQuantity(toxSpecimen.AMOUNT);
			scc.setQuantity(quantity);
		}
		Stream<String> bodySiteFields = Stream.of(toxSpecimen.BODYSITE);
		if (!bodySiteFields.allMatch(x -> x == null || x.isEmpty())) {
			sccUsed = true;
			scc.setBodySite(new CodeableConcept().setText(toxSpecimen.BODYSITE));
		}
		if (sccUsed) {
			specimenResource.setCollection(scc);
		}
		Stream<String> containerFields = Stream.of(toxSpecimen.CONTAINER);
		if (!containerFields.allMatch(x -> x == null || x.isEmpty())) {
			SpecimenContainerComponent scc2 = new SpecimenContainerComponent();
			scc2.setDescription(toxSpecimen.CONTAINER);
		}
		Stream<String> receivedFields = Stream.of(toxSpecimen.RECEIPT_DATETIME);
		if (!receivedFields.allMatch(x -> x == null || x.isEmpty())) {
			Date receivedDateTime = CommonMappingUtil.parseDateTimeFromField(toxSpecimen, "RECEIPT_DATETIME", toxSpecimen.RECEIPT_DATETIME);
			specimenResource.setReceivedTime(receivedDateTime);
			specimenResource.getReceivedTimeElement().setTimeZoneZulu(true);
		}
		return specimenResource;
	}

	private ObservationToxicologyLabResult createResult(ToxResult toxResult, int resultIndex, String idTemplate,
			Map<String, Reference> specimenMap, Reference subjectReference) {
		String idTemplateWithIndex = idTemplate + "-Result-" + resultIndex;
		ObservationToxicologyLabResult resultResource = new ObservationToxicologyLabResult();
		resultResource.setId(idTemplateWithIndex);
		resultResource.setSubject(subjectReference);
		resultResource.setCode(new CodeableConcept().setText(toxResult.ANALYSIS));
		resultResource.setStatus(ObservationStatus.FINAL);
		resultResource.setValue(new StringType(toxResult.VALUE)); // TODO: work with range
		if (toxResult.METHOD != null && !toxResult.METHOD.isEmpty()) {
			resultResource.setMethod(new CodeableConcept().setText(toxResult.METHOD));
		}
		if (toxResult.SPECIMEN != null && !toxResult.SPECIMEN.isEmpty()) {
			Reference specimenReference = specimenMap.get(toxResult.SPECIMEN);
			if (specimenReference != null && !specimenReference.isEmpty()) {
				resultResource.setSpecimen(specimenReference);
			}
		}
		Stream<String> receivedFields = Stream.of(toxResult.RECORD_DATE, toxResult.RECORD_TIME);
		if (!receivedFields.allMatch(x -> x == null || x.isEmpty())) {
			Date recordedDate = CommonMappingUtil.parseDateFromField(toxResult, "RECORD_DATE", toxResult.RECORD_DATE);
			if (recordedDate != null && toxResult.RECORD_TIME != null && !toxResult.RECORD_TIME.isEmpty()) {
				Date recordedTime = CommonMappingUtil.parseDateFromField(toxResult, "RECORD_TIME", toxResult.RECORD_TIME);
				LocalModelToFhirCMSUtil.addTimeToDate(recordedDate, recordedTime);
			}
			resultResource.setEffective(new DateTimeType(recordedDate, TemporalPrecisionEnum.SECOND, TimeZone.getTimeZone(ZoneId.of("Z"))));
			resultResource.getEffectiveDateTimeType().setTimeZoneZulu(true);
		}
		// TODO: Add container information
		// TODO: Work with notes
		return resultResource;
	}

	protected Patient addRace(Patient patient, String ombCategory, String detailed, String text) {
		Extension extension = new Extension(DecedentUtil.raceExtensionURL);
		if (!ombCategory.isEmpty()) {
			Extension ombCategoryExt = new Extension("ombCategory",
					new Coding().setCode(ombCategory).setSystem(DecedentUtil.raceSystem));
			extension.addExtension(ombCategoryExt);
		}
		if (!detailed.isEmpty()) {
			Extension detailedExt = new Extension("detailed",
					new Coding().setCode(detailed).setSystem(DecedentUtil.raceSystem));
			extension.addExtension(detailedExt);
		}
		if (!text.isEmpty()) {
			Extension textExt = new Extension("text", new StringType(text));
			extension.addExtension(textExt);
		}
		patient.addExtension(extension);
		return patient;
	}

	protected Patient addEthnicity(Patient patient, String ombCategory, String detailed, String text) {
		Extension extension = new Extension(DecedentUtil.ethnicityExtensionURL);
		if (!ombCategory.isEmpty()) {
			Extension ombCategoryExt = new Extension("ombCategory",
					new Coding().setCode(ombCategory).setSystem(DecedentUtil.ethnicitySystem));
			extension.addExtension(ombCategoryExt);
		}
		if (!detailed.isEmpty()) {
			Extension detailedExt = new Extension("detailed",
					new Coding().setCode(detailed).setSystem(DecedentUtil.ethnicitySystem));
			extension.addExtension(detailedExt);
		}
		if (!text.isEmpty()) {
			Extension textExt = new Extension("text", new StringType(text));
			extension.addExtension(textExt);
		}
		patient.addExtension(extension);
		return patient;
	}

}