package edu.gatech.chai.Mapping.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Address.AddressUse;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleType;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.ContactPoint;
import org.hl7.fhir.r4.model.ContactPoint.ContactPointSystem;
import org.hl7.fhir.r4.model.ContactPoint.ContactPointUse;
import org.hl7.fhir.r4.model.DiagnosticReport.DiagnosticReportStatus;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.HumanName.NameUse;
import org.hl7.fhir.r4.model.MessageHeader.MessageSourceComponent;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Location;
import org.hl7.fhir.r4.model.MessageHeader;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Observation.ObservationStatus;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Procedure.ProcedureStatus;
import org.hl7.fhir.r4.model.Specimen.SpecimenCollectionComponent;
import org.hl7.fhir.r4.model.Specimen.SpecimenContainerComponent;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.StringType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.uhn.fhir.parser.IParser;
import edu.gatech.chai.MDI.Model.ToxToMDIModelFields;
import edu.gatech.chai.MDI.Model.ToxResult;
import edu.gatech.chai.MDI.Model.ToxSpecimen;
import edu.gatech.chai.MDI.Model.ToxToMDIModelFields;
import edu.gatech.chai.MDI.context.MDIFhirContext;
import edu.gatech.chai.MDI.model.resource.BundleMessageToxToMDI;
import edu.gatech.chai.MDI.model.resource.CompositionMDIToEDRS;
import edu.gatech.chai.MDI.model.resource.DiagnosticReportToxicologyToMDI;
import edu.gatech.chai.MDI.model.resource.MessageHeaderToxicologyToMDI;
import edu.gatech.chai.MDI.model.resource.ObservationAutopsyPerformedIndicator;
import edu.gatech.chai.MDI.model.resource.ObservationCauseOfDeathPart1;
import edu.gatech.chai.MDI.model.resource.ObservationContributingCauseOfDeathPart2;
import edu.gatech.chai.MDI.model.resource.ObservationDeathDate;
import edu.gatech.chai.MDI.model.resource.ObservationDecedentPregnancy;
import edu.gatech.chai.MDI.model.resource.ObservationHowDeathInjuryOccurred;
import edu.gatech.chai.MDI.model.resource.ObservationMannerOfDeath;
import edu.gatech.chai.MDI.model.resource.ObservationTobaccoUseContributedToDeath;
import edu.gatech.chai.MDI.model.resource.ObservationToxicologyLabResult;
import edu.gatech.chai.MDI.model.resource.ProcedureDeathCertification;
import edu.gatech.chai.MDI.model.resource.SpecimenToxicologyLab;
import edu.gatech.chai.MDI.model.resource.util.MDICommonUtil;
import edu.gatech.chai.Mapping.Util.MDIToFhirCMSUtil;
import edu.gatech.chai.VRDR.model.util.DecedentUtil;

@Service
public class MDIToToxToMDIService {
	
	@Autowired
	private MDIFhirContext mdiFhirContext;
	
	public String convertToMDIString(ToxToMDIModelFields inputFields, int index) throws ParseException {
		Bundle fullBundle = convertToMDI(inputFields, index);
		return convertToMDIString(fullBundle);
	}

	public String convertToMDIString(Bundle fullBundle) throws ParseException {
		IParser parser = mdiFhirContext.getCtx().newJsonParser();
		String returnString = parser.encodeResourceToString(fullBundle);
		return returnString;
	}
	
	public Bundle convertToMDI(ToxToMDIModelFields inputFields, int index) throws ParseException {
		//Create Tox-to-MDI Bundle
		BundleMessageToxToMDI returnBundle = new BundleMessageToxToMDI();
		String idTemplate = inputFields.FILEID + "-" + index;
		returnBundle.setId(idTemplate + "-Tox-Message-Bundle");
		Date now = new Date();
		//Assigning a raven generated system identifier
		returnBundle.setIdentifier(new Identifier().setSystem("urn:raven:temporary").setValue(Long.toString(now.getTime())));
		returnBundle.setType(BundleType.BATCH);
		//Create Message Header
		MessageHeaderToxicologyToMDI messageHeader = new MessageHeaderToxicologyToMDI();
		messageHeader.setId(idTemplate + "-MessageHeader-Tox-To-MDI");
		MessageSourceComponent msc = new MessageSourceComponent();
		msc.setName("Raven Generated Import");
		messageHeader.setSource(msc);
		MDIToFhirCMSUtil.addResourceToBundle(returnBundle, messageHeader);
		//Handle Performer
		Resource performerResource = null; //Performer can be a US-Core-Practitioner or a PractitionerRole
		Reference performerReference = null;
		Stream<String> performerFields = Stream.of(inputFields.TOXORGNAME,inputFields.TOXORGSTREET,
			inputFields.TOXORGCITY,inputFields.TOXORGCOUNTY,inputFields.TOXORGSTATE,inputFields.TOXORGZIP,
			inputFields.TOXORGCOUNTRY,inputFields.TOXPERFORMER);
		if(!performerFields.allMatch(x -> x == null || x.isEmpty())) {
			performerResource = createPerformer(inputFields,idTemplate, returnBundle);
			performerReference = new Reference(performerResource.getResourceType().toString() +"/"+performerResource.getId());
			MDIToFhirCMSUtil.addResourceToBundle(returnBundle, performerResource);
		}
		//Create Patient
		Patient patientResource = null;
		Reference patientReference = null;
		Stream<String> decedentFields = Stream.of(inputFields.FIRSTNAME,inputFields.LASTNAME,
			inputFields.MIDNAME,inputFields.BIRTHDATE,inputFields.SUFFIXNAME);
		if(!decedentFields.allMatch(x -> x == null || x.isEmpty())) {
			patientResource = createPatient(inputFields,idTemplate);
			patientReference = new Reference("Patient/"+patientResource.getId());
			MDIToFhirCMSUtil.addResourceToBundle(returnBundle, patientResource);
		}
		//Create Diagnostic Report
		CodeableConcept toxOrderCodableConcept = new CodeableConcept();
		if(inputFields.TOXORDERCODE != null && !inputFields.TOXORDERCODE.isEmpty()){
			toxOrderCodableConcept.setText(inputFields.TOXORDERCODE);
		}
		DiagnosticReportToxicologyToMDI diagnosticReport = new DiagnosticReportToxicologyToMDI(DiagnosticReportStatus.FINAL, patientResource, toxOrderCodableConcept, new Date(), MDIToFhirCMSUtil.parseDate(inputFields.REPORTDATE));
		//Toxicology Identifier
		if(inputFields.TOXCASENUMBER != null && !inputFields.TOXCASENUMBER.isEmpty()){
			Identifier identifier = new Identifier();
			identifier.setType(MDICommonUtil.trackingNumberTOXType);
			identifier.setSystem("urn:raven-import:mdi:"+inputFields.FILEID);
			identifier.setValue(inputFields.TOXCASENUMBER);
			diagnosticReport.addTrackingNumberExtension(identifier);
		}
			//MDI Identifier
		Stream<String> mdiIdentifierFields = Stream.of(inputFields.MDICASEID,inputFields.MDICASESYSTEM);
		if(!mdiIdentifierFields.allMatch(x -> x == null || x.isEmpty())) {
			Identifier identifier = new Identifier();
			identifier.setType(MDICommonUtil.trackingNumberMDIType);
			identifier.setSystem(inputFields.MDICASESYSTEM);
			identifier.setValue(inputFields.MDICASEID);
			diagnosticReport.addTrackingNumberExtension(identifier);
		}
		diagnosticReport.setId(idTemplate + "-DiagnosticReport-Tox-To-MDI");
		if(performerReference != null){
			diagnosticReport.addPerformer(performerReference);
		}
		MDIToFhirCMSUtil.addResourceToBundle(returnBundle, diagnosticReport);
		messageHeader.addFocus(new Reference("DiagnosticReport/"+diagnosticReport.getId()));
		//Create Specimen
		Map<String, Reference> specimenNameToReference = new HashMap<String, Reference>(); //Local map to get a reference from a common string name for a specimen
		for(int i=0;i<inputFields.SPECIMENS.size();i++){
			ToxSpecimen toxSpecimen = inputFields.SPECIMENS.get(i);
			Stream<String> specimenFields = Stream.of(toxSpecimen.NAME);
			if(!specimenFields.allMatch(x -> x == null || x.isEmpty())) {
				SpecimenToxicologyLab specimenResource = createSpecimen(toxSpecimen,i,idTemplate,patientReference);
				Reference specimenReference = new Reference("Specimen/"+specimenResource.getId());
				specimenReference.setDisplay(toxSpecimen.NAME);
				specimenNameToReference.put(toxSpecimen.NAME, specimenReference);
				diagnosticReport.addSpecimen(specimenReference);
				MDIToFhirCMSUtil.addResourceToBundle(returnBundle, specimenResource);
			}
		}
		//Create Result
		for(int i=0;i<inputFields.RESULTS.size();i++){
			ToxResult toxResult = inputFields.RESULTS.get(i);
			Stream<String> resultFields = Stream.of(toxResult.ANALYSIS);
			if(!resultFields.allMatch(x -> x == null || x.isEmpty())) {
				ObservationToxicologyLabResult resultResource = createResult(toxResult,i,idTemplate,specimenNameToReference, patientReference);
				Reference resultReference = new Reference("Observation/"+resultResource.getId());
				resultReference.setDisplay(toxResult.ANALYSIS);
				diagnosticReport.addResult(resultReference);
				MDIToFhirCMSUtil.addResourceToBundle(returnBundle, resultResource);
			}
		}
		return returnBundle;
	}
	private Patient createPatient(ToxToMDIModelFields inputFields,String idTemplate) throws ParseException {
		Patient returnDecedent = new Patient();
		returnDecedent.setMeta(new Meta().addProfile("http://hl7.org/fhir/us/core/StructureDefinition/us-core-patient"));
		returnDecedent.setId(idTemplate + "-Decedent");
		Stream<String> nameFields = Stream.of(inputFields.FIRSTNAME,inputFields.LASTNAME,inputFields.MIDNAME, inputFields.SUFFIXNAME);
		if(!nameFields.allMatch(x -> x == null || x.isEmpty())) {
			HumanName name = new HumanName();
			name.addGiven(inputFields.FIRSTNAME);
			name.addGiven(inputFields.MIDNAME);
			name.setFamily(inputFields.LASTNAME);
			name.setUse(NameUse.OFFICIAL);
			if(inputFields.SUFFIXNAME != null && !inputFields.SUFFIXNAME.isEmpty()){
				name.addSuffix(inputFields.SUFFIXNAME);
			}
			returnDecedent.addName(name);
		}
		if(inputFields.BIRTHDATE != null && !inputFields.BIRTHDATE.isEmpty()) {
			Date birthDate = MDIToFhirCMSUtil.parseDate(inputFields.BIRTHDATE);
			returnDecedent.setBirthDate(birthDate);
		}
		return returnDecedent;
	}
	
	private Resource createPerformer(ToxToMDIModelFields inputFields,String idTemplate,BundleMessageToxToMDI bundle) {
		//If there's an organization, create a PractitionerRole with Organization and Practitioner, otherwise just create a practitioner
		boolean practitionerRoleSet = false;
		Resource returnPerformer = null;
		Organization organization = null;
		Reference organizationReference = null;
		PractitionerRole practitionerRole = null;
		Reference practitionerRoleReference = null;
		Practitioner practitioner = null;
		Reference practitionerReference = null;
		Stream<String> organizationFields = Stream.of(inputFields.TOXORGNAME,inputFields.TOXORGSTREET,
			inputFields.TOXORGCITY,inputFields.TOXORGCOUNTY,inputFields.TOXORGSTATE,inputFields.TOXORGZIP,
			inputFields.TOXORGCOUNTRY);
		if(!organizationFields.allMatch(x -> x == null || x.isEmpty())) {
			practitionerRoleSet = true;
			organization = new Organization();
			organization.setId(idTemplate+"-Organization");
			organization.setMeta(new Meta().addProfile("http://hl7.org/fhir/us/core/StructureDefinition/us-core-organization"));
			organization.setActive(true);
			organization.setName(inputFields.TOXORGNAME);
			organization.addAddress(MDIToFhirCMSUtil.createAddress(inputFields.TOXORGNAME, inputFields.TOXORGSTREET, inputFields.TOXORGCITY, inputFields.TOXORGCOUNTY, inputFields.TOXORGSTATE, inputFields.TOXORGZIP, inputFields.TOXORGCOUNTRY));
			organizationReference = new Reference("Organization/"+organization.getId());
			MDIToFhirCMSUtil.addResourceToBundle(bundle, organization);
			practitionerRole = new PractitionerRole();
			practitionerRole.setId(idTemplate+"-PractitionerRole");
			practitionerRole.setMeta(new Meta().addProfile("http://hl7.org/fhir/us/core/StructureDefinition/us-core-practitionerrole"));
			practitionerRole.setOrganization(organizationReference);
			MDIToFhirCMSUtil.addResourceToBundle(bundle, practitionerRole);
			returnPerformer = practitionerRole;
		}
		Stream<String> practitionerFields = Stream.of(inputFields.TOXPERFORMER);
		if(!practitionerFields.allMatch(x -> x == null || x.isEmpty())) {
			practitioner = new Practitioner();
			practitioner.setId(idTemplate+"-Practitioner");
			practitioner.setMeta(new Meta().addProfile("http://hl7.org/fhir/us/core/StructureDefinition/us-core-practitioner"));
			practitionerReference = new Reference("Practitioner/"+practitioner.getId());
			practitioner.addName(MDIToFhirCMSUtil.parseHumanName(inputFields.TOXPERFORMER));
			MDIToFhirCMSUtil.addResourceToBundle(bundle, practitioner);
			if(practitionerRoleSet){
				practitionerRole.setPractitioner(practitionerReference);
			}
			else{
				returnPerformer = practitioner;
			}
		}
		return returnPerformer;
	}

	private SpecimenToxicologyLab createSpecimen(ToxSpecimen toxSpecimen,int specimenIndex, String idTemplate, Reference subjectReference) throws ParseException {
		String idTemplateWithIndex = idTemplate + "-Specimen-" + specimenIndex;
		SpecimenToxicologyLab specimenResource = new SpecimenToxicologyLab();
		specimenResource.setId(idTemplateWithIndex);
		specimenResource.setSubject(subjectReference);
		specimenResource.setType(new CodeableConcept().setText(toxSpecimen.NAME));
		if(toxSpecimen.IDENTIFIER != null && toxSpecimen.IDENTIFIER.isEmpty()){
			specimenResource.setAccessionIdentifier(new Identifier().setValue(toxSpecimen.IDENTIFIER));
		}
		SpecimenCollectionComponent scc = new SpecimenCollectionComponent();
		boolean sccUsed = false;
		Stream<String> collectionFields = Stream.of(toxSpecimen.COLLECTED_DATETIME,toxSpecimen.AMOUNT);
		if(!collectionFields.allMatch(x -> x == null || x.isEmpty())) {
			sccUsed = true;
			Date collectedDate = MDIToFhirCMSUtil.parseDateAndTime(toxSpecimen.COLLECTED_DATETIME);
			scc.setCollected(new DateTimeType(collectedDate));
			Quantity quantity = new Quantity(); //TODO: Handle quantity better than just this manner
			quantity.setCode(toxSpecimen.AMOUNT);
			scc.setQuantity(quantity);
		}
		Stream<String> bodySiteFields = Stream.of(toxSpecimen.BODYSITE);
		if(!bodySiteFields.allMatch(x -> x == null || x.isEmpty())) {
			sccUsed = true;
			scc.setBodySite(new CodeableConcept().setText(toxSpecimen.BODYSITE));
		}
		if(sccUsed){
			specimenResource.setCollection(scc);
		}
		Stream<String> containerFields = Stream.of(toxSpecimen.CONTAINER);
		if(!containerFields.allMatch(x -> x == null || x.isEmpty())) {
			SpecimenContainerComponent scc2 = new SpecimenContainerComponent();
			scc2.setDescription(toxSpecimen.CONTAINER);
		}
		Stream<String> receivedFields = Stream.of(toxSpecimen.RECEIPT_DATETIME);
		if(!receivedFields.allMatch(x -> x == null || x.isEmpty())) {
			Date receivedDateTime = MDIToFhirCMSUtil.parseDate(toxSpecimen.RECEIPT_DATETIME);
			MDIToFhirCMSUtil.addTimeToDate(receivedDateTime,toxSpecimen.RECEIPT_DATETIME);
			specimenResource.setReceivedTime(receivedDateTime);
		}
		return specimenResource;
	}

	private ObservationToxicologyLabResult createResult(ToxResult toxResult,int resultIndex, String idTemplate, Map<String,Reference> specimenMap, Reference subjectReference) throws ParseException {
		String idTemplateWithIndex = idTemplate + "-Result-" + resultIndex;
		ObservationToxicologyLabResult resultResource = new ObservationToxicologyLabResult();
		resultResource.setId(idTemplateWithIndex);
		resultResource.setSubject(subjectReference);
		resultResource.setCode(new CodeableConcept().setText(toxResult.ANALYSIS));
		resultResource.setValue(new StringType(toxResult.VALUE)); //TODO: work with range
		if(toxResult.METHOD != null && !toxResult.METHOD.isEmpty()){
			resultResource.setMethod(new CodeableConcept().setText(toxResult.METHOD));
		}
		if(toxResult.SPECIMEN != null && !toxResult.SPECIMEN.isEmpty()){
			Reference specimenReference = specimenMap.get(toxResult.SPECIMEN);
			if(specimenReference != null && !specimenReference.isEmpty()){
				resultResource.setSpecimen(specimenReference);
			}
		}
		Stream<String> receivedFields = Stream.of(toxResult.RECORD_DATE,toxResult.RECORD_TIME);
		if(!receivedFields.allMatch(x -> x == null || x.isEmpty())) {
			Date recordedDateTime = MDIToFhirCMSUtil.parseDate(toxResult.RECORD_DATE);
			if(toxResult.RECORD_TIME != null && !toxResult.RECORD_TIME.isEmpty()){
				MDIToFhirCMSUtil.addTimeToDate(recordedDateTime,toxResult.RECORD_TIME);
			}
			resultResource.setEffective(new DateTimeType(recordedDateTime));
		}
		//TODO: Add container information
		//TODO: Work with notes
		return resultResource;
	}
	
	protected Patient addRace(Patient patient, String ombCategory, String detailed, String text) {
		Extension extension = new Extension(DecedentUtil.raceExtensionURL);
		if(!ombCategory.isEmpty()) {
			Extension ombCategoryExt = new Extension("ombCategory",
					new Coding().setCode(ombCategory).setSystem(DecedentUtil.raceSystem));
			extension.addExtension(ombCategoryExt);
		}
		if(!detailed.isEmpty()) {
			Extension detailedExt = new Extension("detailed",
					new Coding().setCode(detailed).setSystem(DecedentUtil.raceSystem));
			extension.addExtension(detailedExt);
		}
		if(!text.isEmpty()) {
			Extension textExt = new Extension("text", new StringType(text));
			extension.addExtension(textExt);
		}
		patient.addExtension(extension);
		return patient;
	}
	
	protected Patient addEthnicity(Patient patient, String ombCategory, String detailed, String text) {
		Extension extension = new Extension(DecedentUtil.ethnicityExtensionURL);
		if(!ombCategory.isEmpty()) {
			Extension ombCategoryExt = new Extension("ombCategory",
					new Coding().setCode(ombCategory).setSystem(DecedentUtil.ethnicitySystem));
			extension.addExtension(ombCategoryExt);
		}
		if(!detailed.isEmpty()) {
			Extension detailedExt = new Extension("detailed",
					new Coding().setCode(detailed).setSystem(DecedentUtil.ethnicitySystem));
			extension.addExtension(detailedExt);
		}
		if(!text.isEmpty()) {
			Extension textExt = new Extension("text", new StringType(text));
			extension.addExtension(textExt);
		}
		patient.addExtension(extension);
		return patient;
	}
}