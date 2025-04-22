package edu.gatech.chai.Mapping.Service;

import java.text.ParseException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;
import java.util.stream.Stream;

import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Address.AddressUse;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryRequestComponent;
import org.hl7.fhir.r4.model.Bundle.BundleType;
import org.hl7.fhir.r4.model.Bundle.HTTPVerb;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Composition.CompositionStatus;
import org.hl7.fhir.r4.model.ContactPoint;
import org.hl7.fhir.r4.model.ContactPoint.ContactPointSystem;
import org.hl7.fhir.r4.model.ContactPoint.ContactPointUse;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.HumanName.NameUse;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.InstantType;
import org.hl7.fhir.r4.model.IntegerType;
import org.hl7.fhir.r4.model.Location;
import org.hl7.fhir.r4.model.Location.LocationStatus;
import org.hl7.fhir.r4.model.MessageHeader.MessageSourceComponent;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Observation.ObservationComponentComponent;
import org.hl7.fhir.r4.model.Observation.ObservationStatus;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.Procedure.ProcedureStatus;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Signature;
import org.hl7.fhir.r4.model.StringType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import ca.uhn.fhir.parser.IParser;
import edu.gatech.chai.MDI.Model.DCRModelFields;
import edu.gatech.chai.MDI.Model.MDIAndEDRSModelFields;
import edu.gatech.chai.MDI.context.MDIFhirContext;
import edu.gatech.chai.MDI.model.resource.BundleDocumentMDIDCR;
import edu.gatech.chai.MDI.model.resource.BundleMessageDeathCertificateReview;
import edu.gatech.chai.MDI.model.resource.CompositionMDIDCR;
import edu.gatech.chai.MDI.model.resource.MessageHeaderDCR;
import edu.gatech.chai.MDI.model.resource.ObservationCauseOfDeathPart1;
import edu.gatech.chai.MDI.model.resource.util.ProcedureDeathCertificationUtil;
import edu.gatech.chai.Mapping.Util.CommonMappingUtil;
import edu.gatech.chai.Mapping.Util.LocalModelToFhirCMSUtil;
import edu.gatech.chai.USCore.model.util.CommonUtil;
import edu.gatech.chai.VRCL.model.AutopsyPerformedIndicator;
import edu.gatech.chai.VRCL.model.LocationVitalRecords;
import edu.gatech.chai.VRCL.model.PractitionerVitalRecords;
import edu.gatech.chai.VRDR.model.CauseOfDeathPart2;
import edu.gatech.chai.VRDR.model.DeathCertificationProcedure;
import edu.gatech.chai.VRDR.model.DeathDate;
import edu.gatech.chai.VRDR.model.DeathLocation;
import edu.gatech.chai.VRDR.model.Decedent;
import edu.gatech.chai.VRDR.model.DecedentPregnancyStatus;
import edu.gatech.chai.VRDR.model.FuneralHome;
import edu.gatech.chai.VRDR.model.InjuryIncident;
import edu.gatech.chai.VRDR.model.InjuryLocation;
import edu.gatech.chai.VRDR.model.MannerOfDeath;
import edu.gatech.chai.VRDR.model.TobaccoUseContributedToDeath;
import edu.gatech.chai.VRDR.model.util.CauseOfDeathConditionUtil;
import edu.gatech.chai.VRDR.model.util.DecedentUtil;

@Service
public class LocalToDCRService {
	
	@Value("${raven_generated_systemid}")
	private String raven_generated_systemid;
	@Value("${fhircms.url}")
	private String fhircms_url;
	@Autowired
	private MDIFhirContext mdiFhirContext;
	@Autowired
	private SignatureGenerationService signatureGenerationService;

	private static final Logger logger = LoggerFactory.getLogger(LocalToDCRService.class);

	public String convertToMDIString(DCRModelFields inputFields) throws ParseException {
		Bundle fullBundle = convertToMDI(inputFields);
		logger.info("Creating bundle:"+fullBundle.getId());
		return convertToMDIString(fullBundle);
	}

	public String convertToMDIString(Bundle fullBundle) throws ParseException {
		IParser parser = mdiFhirContext.getCtx().newJsonParser();
		String returnString = parser.encodeResourceToString(fullBundle);
		return returnString;
	}
	
	public Bundle convertToMDI(DCRModelFields inputFields) throws ParseException {
		//BundleMessageDeathCertificateReview
		BundleMessageDeathCertificateReview returnBundle = new BundleMessageDeathCertificateReview();
		returnBundle.setId(inputFields.BASEFHIRID + "DCR-Bundle-Message");
		Date now = new Date();
		DateTimeType dtElement = new DateTimeType(now);
		dtElement.setTimeZoneZulu(true);
		InstantType instantElement = new InstantType(now, TemporalPrecisionEnum.MILLI, TimeZone.getTimeZone(ZoneId.of("Z")));
		returnBundle.setTimestampElement(instantElement);
		returnBundle.setIdentifier(new Identifier().setSystem("urn:mdi:raven").setValue(Long.toString(now.getTime())));
		Identifier caseIdentifier = new Identifier().setSystem(inputFields.SYSTEMID);
		caseIdentifier.setValue("TestIdentifier");
		caseIdentifier.setType(new CodeableConcept().addCoding(new Coding().setCode("1000007").setSystem("urn:mdi:raven").setDisplay("Case Number")));
		returnBundle.setType(BundleType.MESSAGE);
		//MessageHeaderDCR
		MessageHeaderDCR messageHeaderDCR = new MessageHeaderDCR();
		messageHeaderDCR.setId(inputFields.BASEFHIRID+ "DCR-MessageHeader");
		MessageSourceComponent msc = new MessageSourceComponent();
		msc.setName("Raven Import Generated Source Message");
		msc.setSoftware("Raven");
		msc.setVersion("1.6.0");
		msc.setEndpoint(fhircms_url);
		messageHeaderDCR.setSource(msc);
		if(inputFields.MESSAGE_REASON != null && !inputFields.MESSAGE_REASON.isEmpty()){
			messageHeaderDCR.setReason(inputFields.MESSAGE_REASON);
		}
		LocalModelToFhirCMSUtil.addResourceToBundle(returnBundle, messageHeaderDCR);
		//BundleDocumentMDIDCR
		BundleDocumentMDIDCR bundleDocument = new BundleDocumentMDIDCR();
		bundleDocument.setId(inputFields.BASEFHIRID + "DCR-Bundle-Document");
		messageHeaderDCR.addFocus(new Reference("Bundle/"+bundleDocument.getId()));
		Identifier caseIdentifierBundle = new Identifier().setSystem(inputFields.SYSTEMID);
		caseIdentifierBundle.setValue("TestIdentifier-Bundle");
		caseIdentifierBundle.setType(new CodeableConcept().addCoding(new Coding().setCode("1000008").setSystem("urn:temporary:code").setDisplay("Case Number Bundle Identifier")));
		LocalModelToFhirCMSUtil.addResourceToBundle(returnBundle, bundleDocument);
		//CompositionMDIDCR
		CompositionMDIDCR mainComposition = bundleDocument.getCompositionMDIDCR();
		mainComposition.setTitle("Raven generated DCR Document");
		//Some bookkeeping to set the mainComposition id
		mainComposition.setId(inputFields.BASEFHIRID + "DCR-Composition");
		bundleDocument.getEntryFirstRep().setFullUrl(mainComposition.getId());
		mainComposition.setIdentifier(caseIdentifier);
		mainComposition.setStatus(CompositionStatus.PRELIMINARY);
		mainComposition.setDateElement(dtElement);
		//Handle extension setting
		//TODO: Add extensions back in when state management for DCR messages is more clear. 
		//mainComposition.addDCCertificationStatus("DEATH_CERT_NOT_CERT");
		//mainComposition.addDCRegistrationStatus("DEATH_CERT_NOT_REG");
		if(inputFields.MDICASEID != null && !inputFields.MDICASEID.isEmpty()){
			mainComposition.addMDICaseIdExtension(raven_generated_systemid, inputFields.MDICASEID);
		}
		if(inputFields.EDRSCASEID != null && !inputFields.EDRSCASEID.isEmpty()){
			mainComposition.addEDRSCaseIdExtension(raven_generated_systemid, inputFields.EDRSCASEID);
		}
		//Since we're creating a batch bundle we need to set the request type on all resources. However this one is handled special from "addResourceToBatchBundle"
		returnBundle.getEntryFirstRep().setRequest(new BundleEntryRequestComponent().setMethod(HTTPVerb.POST));

		Decedent decedentResource = null;
		Reference decedentReference = null;
		PractitionerVitalRecords primaryMECResource = null;
		Reference primaryMECReference = null;
		PractitionerVitalRecords certifierResource = null;
		Reference certifierReference = null;
		PractitionerVitalRecords pronouncerResource = null;
		Reference pronouncerReference = null;
		// Handle Patient/Decedent
		Stream<String> decedentFields = Stream.of(inputFields.FIRSTNAME,inputFields.MIDNAME,inputFields.LASTNAME
				,inputFields.AGE,inputFields.AGEUNIT,inputFields.RACE,inputFields.GENDER
				,inputFields.ETHNICITY,inputFields.BIRTHDATE,inputFields.MRNNUMBER
				,inputFields.MARITAL,inputFields.POSSIBLEID,inputFields.RESSTREET
				,inputFields.RESCITY,inputFields.RESCOUNTY,inputFields.RESSTATE
				,inputFields.RESZIP,inputFields.RESNAME,inputFields.LKAWHERE,inputFields.HOSPNAME
				,inputFields.MDICASEID, inputFields.EDRSCASEID);
		if(!decedentFields.allMatch(x -> x == null || x.isEmpty())) {
			decedentResource = createPatient(inputFields);
			decedentReference = new Reference("Patient/"+decedentResource.getId());
			mainComposition.setSubject(decedentReference);
			mainComposition.getDecedentDemographicsSection().addEntry(decedentReference);
			LocalModelToFhirCMSUtil.addResourceToBundle(bundleDocument, decedentResource);

			mainComposition.addFHCaseIdExtension(UUID.nameUUIDFromBytes((inputFields.FIRSTNAME + " " + inputFields.LASTNAME).getBytes()).toString()); //Derived consistent UUID 
		}
		//Special handling to identify a primaryMEC. If a certifier is found but no ME/C is found; just the MEC
		if(inputFields.MENAME == null || inputFields.MENAME.isEmpty()){
			if(inputFields.CERTIFIER_NAME != null && !inputFields.CERTIFIER_NAME.isEmpty()){
				inputFields.MENAME = inputFields.CERTIFIER_NAME;
			}
			else if(inputFields.PRONOUNCERNAME != null && !inputFields.PRONOUNCERNAME.isEmpty()){
				inputFields.MENAME = inputFields.PRONOUNCERNAME;
			}
		}
		// Handle Submittor (distinct from Primary Medical Examiner/Coroner)
		Stream<String> submittorFields = Stream.of(inputFields.SUBMITTOR_NAME,inputFields.SUBMITTOR_EMAIL, inputFields.SUBMITTOR_PHONE, inputFields.SUBMITTOR_FAX);
		if(!submittorFields.allMatch(x -> x == null || x.isEmpty())) {
			Practitioner submittor = createSubmittor(inputFields);
			Reference submittorReference = new Reference("Practitioner/"+submittor.getId());
			mainComposition.addAuthor(submittorReference);
			//Also assign a signature from the submittor's name and assign to the top bundle.
			//NOTE: This is where the SPECIAL Signature Service makes an image and encodes the base64 png for it.
			Signature submittorSignature = new Signature();
			if(inputFields.SUBMITTOR_NAME != null && !inputFields.SUBMITTOR_NAME.isEmpty()){
				submittorSignature.addType(new Coding("urn:iso-astm:E1762-95:2013","1.2.840.10065.1.12.1.1","Author's Signature"));
				submittorSignature.setWhen(now);
				submittorSignature.setWho(submittorReference);
				submittorSignature.setTargetFormat("image/png");
				submittorSignature.setSigFormat("image/png");
				submittorSignature.setData(signatureGenerationService.generateBase64ForPngOfHandwritingName(inputFields.SUBMITTOR_NAME));
				bundleDocument.setSignature(submittorSignature);
			}
			LocalModelToFhirCMSUtil.addResourceToBundle(bundleDocument, submittor);
		}
		// Handle Primary Medical Examiner/Coroner
		Stream<String> primaryMECFields = Stream.of(inputFields.MENAME,inputFields.MELICENSE,inputFields.MEPHONE,
			inputFields.ME_STREET,inputFields.ME_CITY,inputFields.ME_COUNTY,inputFields.ME_STATE,inputFields.ME_ZIP);
		if(!primaryMECFields.allMatch(x -> x == null || x.isEmpty())) {
			primaryMECResource = createPrimaryMEC(inputFields);
			primaryMECReference = new Reference("Practitioner/"+primaryMECResource.getId());
			mainComposition.addAuthor(primaryMECReference);
			LocalModelToFhirCMSUtil.addResourceToBundle(bundleDocument, primaryMECResource);
		}
		Stream<String> certifierFields = Stream.of(inputFields.CERTIFIER_NAME,inputFields.CERTIFIER_TYPE,
			inputFields.CERTIFIER_IDENTIFIER, inputFields.CERTIFIER_IDENTIFIER_SYSTEM);
		if(!certifierFields.allMatch(x -> x == null || x.isEmpty())) {
			//Special handling if the Primary Medical Examiner/Coroner is the same as the Certifier
			if(inputFields.MENAME.equalsIgnoreCase(inputFields.CERTIFIER_NAME)){
				certifierResource = primaryMECResource;
				certifierResource = updatePrimaryMECWithCertifierInformation(inputFields, certifierResource);
			}
			else{
				certifierResource = createCertifier(inputFields);
				LocalModelToFhirCMSUtil.addResourceToBundle(bundleDocument, certifierResource);
			}
			certifierReference = new Reference("Practitioner/"+certifierResource.getId());
			mainComposition.addAttester(certifierReference);
			// Handle Death Certification
			DeathCertificationProcedure deathCertification = createDeathCertificationProcedure(inputFields, decedentReference, certifierReference);
			LocalModelToFhirCMSUtil.addResourceToBundle(bundleDocument, deathCertification);
			mainComposition.getCremationClearanceInfoSection().addEntry(new Reference("Procedure/"+deathCertification.getId()));
		}
		// Handle Pronouncer
		Stream<String> pronouncerFields = Stream.of(inputFields.PRONOUNCERNAME);
		if(!pronouncerFields.allMatch(x -> x == null || x.isEmpty())) {
			//Special handling if the pronouncer is the same as the primary ME/C or Certifier
			if(inputFields.MENAME.equalsIgnoreCase(inputFields.PRONOUNCERNAME)){
				pronouncerResource = primaryMECResource;
			}
			else if(inputFields.CERTIFIER_NAME.equalsIgnoreCase(inputFields.PRONOUNCERNAME)){
				pronouncerResource = certifierResource;
			}
			else{
				pronouncerResource = createPronouncer(inputFields);
				LocalModelToFhirCMSUtil.addResourceToBundle(bundleDocument, pronouncerResource);
			}
			pronouncerReference = new Reference("Practitioner/"+pronouncerResource.getId());
		}
		// Handle Death Location
		DeathLocation deathLocation = null;
		Stream<String> deathLocFields = Stream.of(inputFields.DEATHLOCATION_STREET, inputFields.DEATHLOCATION_CITY, inputFields.DEATHLOCATION_COUNTY,
		inputFields.DEATHLOCATION_STATE, inputFields.DEATHLOCATION_ZIP, inputFields.DEATHLOCATION_COUNTRY);
		if(!deathLocFields.allMatch(x -> x == null || x.isEmpty())) {
			deathLocation = createDeathLocation(inputFields);
			mainComposition.getDeathInvestigationSection().addEntry(new Reference("Location/"+deathLocation.getId()));
			LocalModelToFhirCMSUtil.addResourceToBundle(returnBundle, deathLocation);
		}
		//Handle TobaccoUseContributedToDeath
		Stream<String> tobaccoFields = Stream.of(inputFields.TOBACCO);
		if(!tobaccoFields.allMatch(x -> x == null || x.isEmpty())) {
			TobaccoUseContributedToDeath tobacco = createObservationTobaccoUseContributedToDeath(inputFields, decedentReference);
			mainComposition.getDeathInvestigationSection().addEntry(new Reference("Observation/"+tobacco.getId()));
			LocalModelToFhirCMSUtil.addResourceToBundle(bundleDocument, tobacco);
		}
		//Handle Decedent Pregnancy
		Stream<String> pregnantFields = Stream.of(inputFields.PREGNANT);
		if(!pregnantFields.allMatch(x -> x == null || x.isEmpty())) {
			DecedentPregnancyStatus pregnant = createObservationDecedentPregnancy(inputFields, decedentReference);
			mainComposition.getDeathInvestigationSection().addEntry(new Reference("Observation/"+pregnant.getId()));
			LocalModelToFhirCMSUtil.addResourceToBundle(bundleDocument, pregnant);
		}
		//Handle Injury Location
		Stream<String> injuryLocationFields = Stream.of(inputFields.INJURYLOCATION_STREET, inputFields.INJURYLOCATION_CITY, inputFields.INJURYLOCATION_COUNTY,
		inputFields.INJURYLOCATION_STATE, inputFields.INJURYLOCATION_ZIP, inputFields.INJURYLOCATION_COUNTRY);
		if(!injuryLocationFields.allMatch(x -> x == null || x.isEmpty())) {
			InjuryLocation injuryLocation = createInjuryLocation(inputFields, decedentReference);
			mainComposition.getDeathInvestigationSection().addEntry(new Reference("Location/"+injuryLocation.getId()));
			LocalModelToFhirCMSUtil.addResourceToBundle(bundleDocument, injuryLocation);
		}
		// Handle InjuryIncident
		Stream<String> deathInjuryFields = Stream.of(inputFields.CHOWNINJURY, inputFields.CINJDATE, inputFields.CINJTIME,
				inputFields.ATWORK,inputFields.TRANSPORTATION);
		if(!deathInjuryFields.allMatch(x -> x == null || x.isEmpty())) {
			InjuryIncident deathInjuryDescription = createInjuryIncident(inputFields, decedentReference, certifierReference);
			mainComposition.getDeathInvestigationSection().addEntry(new Reference("Observation/"+deathInjuryDescription.getId()));
			LocalModelToFhirCMSUtil.addResourceToBundle(bundleDocument, deathInjuryDescription);
		}
		// Handle Death Date
		Stream<String> deathDateFields = Stream.of(inputFields.PRNDATE, inputFields.PRNTIME,
				inputFields.CDEATHDATE, inputFields.CDEATHTIME);
		if(!deathDateFields.allMatch(x -> x == null || x.isEmpty())) {
			DeathDate deathDate = createDeathDate(inputFields, decedentReference, pronouncerReference);
			mainComposition.getDeathInvestigationSection().addEntry(new Reference("Observation/"+deathDate.getId()));
			LocalModelToFhirCMSUtil.addResourceToBundle(bundleDocument, deathDate);
		}
		// Handle Cause Of Death Pathway
		Stream<String> causeOfDeathFields = Stream.of(inputFields.CAUSEA, inputFields.CAUSEB, inputFields.CAUSEC,
				inputFields.CAUSED,inputFields.OSCOND, inputFields.DURATIONA, inputFields.DURATIONB,
				inputFields.DURATIONC, inputFields.DURATIOND);
		if(!causeOfDeathFields.allMatch(x -> x == null || x.isEmpty())) {
			//THis list contains CauseOfDeathPart1 and CauseOfDeathPart2 resources
			List<Observation> causeOfDeathPathway = createCauseOfDeathPathway(inputFields, bundleDocument, mainComposition, decedentReference, certifierReference);
			for(Observation cause:causeOfDeathPathway){
				mainComposition.getDeathCertificationSection().addEntry(new Reference("Observation/"+cause.getId()));
				LocalModelToFhirCMSUtil.addResourceToBundle(bundleDocument, cause);
			}
		}

		// Handle Manner Of Death
		Stream<String> mannerFields = Stream.of(inputFields.MANNER);
		if(!mannerFields.allMatch(x -> x == null || x.isEmpty())) {
			MannerOfDeath manner = createMannerOfDeath(inputFields, decedentReference, primaryMECReference);
			mainComposition.getDeathCertificationSection().addEntry(new Reference("Observation/"+manner.getId()));
			LocalModelToFhirCMSUtil.addResourceToBundle(bundleDocument, manner);
		}
		//Handle Autopsy
		//Handle Autopsy Indicator
		Stream<String> autopsyIdenticatorFields = Stream.of(inputFields.AUTOPSYPERFORMED, inputFields.AUTOPSYRESULTSAVAILABLE);
		if(!autopsyIdenticatorFields.allMatch(x -> x == null || x.isEmpty())) {
			AutopsyPerformedIndicator autopsyPerformedIndicator = createAutopsyPerformedIndicator(inputFields, decedentReference, primaryMECReference);
			mainComposition.getDeathCertificationSection().addEntry(new Reference("Observation/"+autopsyPerformedIndicator.getId()));
			LocalModelToFhirCMSUtil.addResourceToBundle(bundleDocument, autopsyPerformedIndicator);
		}
		//Handle Autopsy Location
		Stream<String> autopsyLocationFields = Stream.of(inputFields.AUTOPSY_OFFICENAME, inputFields.AUTOPSY_STREET, inputFields.AUTOPSY_CITY, inputFields.AUTOPSY_COUNTY, inputFields.AUTOPSY_STATE, inputFields.AUTOPSY_ZIP);
		if(!autopsyLocationFields.allMatch(x -> x == null || x.isEmpty())) {
			Location autopsyPerformedLocation = createAutopsyLocation(inputFields);
			mainComposition.getDeathCertificationSection().addEntry(new Reference("Location/"+autopsyPerformedLocation.getId()));
			LocalModelToFhirCMSUtil.addResourceToBundle(bundleDocument, autopsyPerformedLocation);
		}
		//Handle Funeral Home
		Stream<String> funeralHomeFields = Stream.of(inputFields.FUNERALHOME_NAME, inputFields.FUNERALHOME_STREET, inputFields.FUNERALHOME_CITY,
		inputFields.FUNERALHOME_COUNTY, inputFields.FUNERALHOME_STATE, inputFields.FUNERALHOME_ZIP, inputFields.FUNERALHOME_PHONE, inputFields.FUNERALHOME_FAX);
		if(!funeralHomeFields.allMatch(x -> x == null || x.isEmpty())) {
			FuneralHome funeralHome = createFuneralHome(inputFields);
			mainComposition.getCremationClearanceInfoSection().addEntry(new Reference("Organization/"+funeralHome.getId()));
			LocalModelToFhirCMSUtil.addResourceToBundle(bundleDocument, funeralHome);
		}
		return returnBundle;
	}
	
	private Decedent createPatient(DCRModelFields inputFields) throws ParseException {
		Decedent returnDecedent = new Decedent();
		returnDecedent.setId(inputFields.BASEFHIRID + "Decedent");
		/*Stream<String> caseIdFields = Stream.of(inputFields.SYSTEMID,inputFields.CASEID);
		if(!caseIdFields.allMatch(x -> x == null || x.isEmpty())) {
			Identifier identifier = new Identifier().setSystem(inputFields.SYSTEMID);
			identifier.setValue(inputFields.CASEID);
			identifier.setType(new CodeableConcept().addCoding(new Coding().setCode("1000007").setSystem("urn:mdi:temporary:code").setDisplay("Case Number")));
			returnDecedent.addIdentifier(identifier);
		}*/
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
		if(inputFields.RACE != null && !inputFields.RACE.isEmpty()) {
			if(LocalModelToFhirCMSUtil.containsIgnoreCase(inputFields.RACE, "White")) {
				addRace(returnDecedent, "2106-3", "", inputFields.RACE);
			}
			else if(LocalModelToFhirCMSUtil.containsIgnoreCase(inputFields.RACE, "Hawaiian") || LocalModelToFhirCMSUtil.containsIgnoreCase(inputFields.RACE, "Pacific")) {
				addRace(returnDecedent, "2076-8", "", inputFields.RACE);
			}
			else if(LocalModelToFhirCMSUtil.containsIgnoreCase(inputFields.RACE, "Asian")) {
				addRace(returnDecedent, "2028-9", "", inputFields.RACE);
			}
			else if(LocalModelToFhirCMSUtil.containsIgnoreCase(inputFields.RACE, "Indian") || LocalModelToFhirCMSUtil.containsIgnoreCase(inputFields.RACE, "Native")) {
				addRace(returnDecedent, "1002-5", "", inputFields.RACE);
			}
			else if(LocalModelToFhirCMSUtil.containsIgnoreCase(inputFields.RACE, "Black")) {
				addRace(returnDecedent, "2054-5", "", inputFields.RACE);
			}
		}
		if(inputFields.ETHNICITY != null && !inputFields.ETHNICITY.isEmpty()) {
			if(LocalModelToFhirCMSUtil.containsIgnoreCase(inputFields.ETHNICITY, "Not Hispanic") || LocalModelToFhirCMSUtil.containsIgnoreCase(inputFields.ETHNICITY, "Malaysian")) {
				addEthnicity(returnDecedent, "2186-5", "", inputFields.ETHNICITY);
			}
			else if(LocalModelToFhirCMSUtil.containsIgnoreCase(inputFields.ETHNICITY, "Hispanic") || LocalModelToFhirCMSUtil.containsIgnoreCase(inputFields.ETHNICITY, "Salvadoran") ||LocalModelToFhirCMSUtil.containsIgnoreCase(inputFields.ETHNICITY, "Latino")) {
				addEthnicity(returnDecedent, "2135-2", "", inputFields.ETHNICITY);
			}
			else if(LocalModelToFhirCMSUtil.containsIgnoreCase(inputFields.ETHNICITY, "Cuban")) {
				addEthnicity(returnDecedent, "2135-2", "2182-4", inputFields.ETHNICITY);
			}
			else if(LocalModelToFhirCMSUtil.containsIgnoreCase(inputFields.ETHNICITY, "Puerto Rican")) {
				addEthnicity(returnDecedent, "2135-2", "2180-8", inputFields.ETHNICITY);
			}
			else if(LocalModelToFhirCMSUtil.containsIgnoreCase(inputFields.ETHNICITY, "Salvadoran")) {
				addEthnicity(returnDecedent, "2161-8", "2180-8", inputFields.ETHNICITY);
			}
		}
		if(inputFields.GENDER != null && !inputFields.GENDER.isEmpty()) {
			returnDecedent.setSexAtDeath(inputFields.GENDER);
		}
		if(inputFields.BIRTHDATE != null && !inputFields.BIRTHDATE.isEmpty()) {
			Date birthDate = LocalModelToFhirCMSUtil.parseDate(inputFields.BIRTHDATE);
			returnDecedent.setBirthDate(birthDate);
		}
		if(inputFields.MRNNUMBER != null && !inputFields.MRNNUMBER.isEmpty()) {
			Identifier identifier = new Identifier().setSystem("http://hl7.org/fhir/sid/us-ssn")
					.setValue(inputFields.MRNNUMBER);
			identifier.setType(new CodeableConcept().addCoding(new Coding().setCode("MR")
					.setSystem("http://terminology.hl7.org/CodeSystem/v2-0203").setDisplay("Medical Record Number")));
			returnDecedent.addIdentifier(identifier);
		}
		if(inputFields.MARITAL != null && !inputFields.MARITAL.isEmpty()) {
			CodeableConcept maritalCode = new CodeableConcept();
			Coding maritalCoding = new Coding();
			maritalCoding.setSystem("http://terminology.hl7.org/CodeSystem/v3-MaritalStatus");
			if(LocalModelToFhirCMSUtil.containsIgnoreCase(inputFields.MARITAL, "Annul")) {
				maritalCoding.setCode("A");
				maritalCoding.setDisplay("Annulled");
			}
			else if(LocalModelToFhirCMSUtil.containsIgnoreCase(inputFields.MARITAL, "Divorce")) {
				maritalCoding.setCode("D");
				maritalCoding.setDisplay("Divorced");
			}
			else if(LocalModelToFhirCMSUtil.containsIgnoreCase(inputFields.MARITAL, "Interlocutory")) {
				maritalCoding.setCode("I");
				maritalCoding.setDisplay("Interlocutory");
			}
			else if(LocalModelToFhirCMSUtil.containsIgnoreCase(inputFields.MARITAL, "Polygamous")) {
				maritalCoding.setCode("P");
				maritalCoding.setDisplay("Polygamous");
			}
			else if(LocalModelToFhirCMSUtil.containsIgnoreCase(inputFields.MARITAL, "Never Married") || LocalModelToFhirCMSUtil.containsIgnoreCase(inputFields.MARITAL, "No")) {
				maritalCoding.setCode("U");
				maritalCoding.setDisplay("unmarried");
			}
			else if(LocalModelToFhirCMSUtil.containsIgnoreCase(inputFields.MARITAL, "Domestic Partner")) {
				maritalCoding.setCode("T");
				maritalCoding.setDisplay("Domestic partner");
			}
			else if(LocalModelToFhirCMSUtil.containsIgnoreCase(inputFields.MARITAL, "Widow")) {
				maritalCoding.setCode("W");
				maritalCoding.setDisplay("Widowed");
			}
			else if(LocalModelToFhirCMSUtil.containsIgnoreCase(inputFields.MARITAL, "Married")) {
				maritalCoding.setCode("M");
				maritalCoding.setDisplay("Married");
			}
			else {
				maritalCode.setText(inputFields.MARITAL);
			}
			if(!maritalCoding.isEmpty()) {
				maritalCode.addCoding(maritalCoding);
			}
			returnDecedent.setMaritalStatus(maritalCode);
		}
		if(inputFields.POSSIBLEID != null && !inputFields.POSSIBLEID.isEmpty()) {
			returnDecedent.addIdentifier(new Identifier().setSystem("http://hl7.org/fhir/sid/us-ssn").setValue(inputFields.POSSIBLEID));
		}
		Stream<String> addressFields = Stream.of(inputFields.RESSTREET, inputFields.RESCITY, inputFields.RESCOUNTY, inputFields.RESSTATE, inputFields.RESZIP, inputFields.RESCOUNTRY);
		if(!addressFields.allMatch(x -> x == null || x.isEmpty())) {
			Address residentAddress = LocalModelToFhirCMSUtil.createAddress("", inputFields.RESSTREET,
				inputFields.RESCITY, inputFields.RESCOUNTY, inputFields.RESSTATE, inputFields.RESZIP, inputFields.RESCOUNTRY);
			residentAddress.setUse(AddressUse.HOME);
			returnDecedent.addAddress(residentAddress);
		}
		if(inputFields.LKAWHERE != null && !inputFields.LKAWHERE.isEmpty()) {
			Extension lkaExt = new Extension();
			lkaExt.setUrl("urn:temporary:code:last-known-to-be-alive-or-okay-place");
			lkaExt.setValue(new StringType(inputFields.LKAWHERE));
			returnDecedent.addExtension(lkaExt);
		}
		if(inputFields.HOSPNAME != null && !inputFields.HOSPNAME.isEmpty()) {
			Extension hospExt = new Extension();
			hospExt.setUrl("urn:temporary:code:hospital-name-decedent-was-first-taken");
			hospExt.setValue(new StringType(inputFields.HOSPNAME));
			returnDecedent.addExtension(hospExt);
		}
		return returnDecedent;
	}
	
	private Practitioner createSubmittor(DCRModelFields inputFields) {
		Practitioner returnPractitioner = new PractitionerVitalRecords();
		returnPractitioner.setId(inputFields.BASEFHIRID + "Submittor");
		if(inputFields.SUBMITTOR_NAME != null && !inputFields.SUBMITTOR_NAME.isEmpty()) {
			HumanName certName = LocalModelToFhirCMSUtil.parseHumanName(inputFields.SUBMITTOR_NAME);
			if (certName != null){
				certName.setUse(NameUse.OFFICIAL);
				returnPractitioner.addName(certName);
			}
		}
		if(inputFields.SUBMITTOR_EMAIL != null && !inputFields.SUBMITTOR_EMAIL.isEmpty()){
			returnPractitioner.addTelecom(new ContactPoint().setSystem(ContactPointSystem.EMAIL).setUse(ContactPointUse.WORK).setValue(inputFields.SUBMITTOR_EMAIL));
		}
		if(inputFields.SUBMITTOR_PHONE != null && !inputFields.SUBMITTOR_PHONE.isEmpty()){
			returnPractitioner.addTelecom(new ContactPoint().setSystem(ContactPointSystem.PHONE).setUse(ContactPointUse.WORK).setValue(inputFields.SUBMITTOR_PHONE));
		}
		if(inputFields.SUBMITTOR_FAX != null && !inputFields.SUBMITTOR_FAX.isEmpty()){
			returnPractitioner.addTelecom(new ContactPoint().setSystem(ContactPointSystem.FAX).setUse(ContactPointUse.WORK).setValue(inputFields.SUBMITTOR_FAX));
		}
		return returnPractitioner;
	}

	private PractitionerVitalRecords createPrimaryMEC(DCRModelFields inputFields) {
		PractitionerVitalRecords returnPractitioner = new PractitionerVitalRecords();
		returnPractitioner.setId(inputFields.BASEFHIRID + "Primary-Practitioner");
		if(inputFields.MENAME != null && !inputFields.MENAME.isEmpty()) {
			HumanName certName = LocalModelToFhirCMSUtil.parseHumanName(inputFields.MENAME);
			if (certName != null){
				certName.setUse(NameUse.OFFICIAL);
				returnPractitioner.addName(certName);
			}
		}
		if(inputFields.MELICENSE != null && !inputFields.MELICENSE.isEmpty()) {
			Identifier melicenseIdentifier = new Identifier();
			melicenseIdentifier.setSystem(raven_generated_systemid);
			melicenseIdentifier.setValue(inputFields.MELICENSE);
			returnPractitioner.addIdentifier(melicenseIdentifier);
		}
		if(inputFields.MEPHONE != null && !inputFields.MEPHONE.isEmpty()){
			returnPractitioner.addTelecom(new ContactPoint().setSystem(ContactPointSystem.PHONE).setUse(ContactPointUse.WORK).setValue(inputFields.MEPHONE));
		}
		Stream<String> meAddrFields = Stream.of(inputFields.ME_STREET, inputFields.ME_CITY,
				inputFields.ME_COUNTY, inputFields.ME_STATE, inputFields.ME_ZIP);
		if(!meAddrFields.allMatch(x -> x == null || x.isEmpty())) {
			Address meAddr = LocalModelToFhirCMSUtil.createAddress("", inputFields.ME_STREET, inputFields.ME_CITY,
					inputFields.ME_COUNTY, inputFields.ME_STATE, inputFields.ME_ZIP, "");
			returnPractitioner.addAddress(meAddr);
		}
		return returnPractitioner;
	}

	private PractitionerVitalRecords createCertifier(DCRModelFields inputFields) {
		PractitionerVitalRecords returnCertifier = new PractitionerVitalRecords();
		returnCertifier.setId(inputFields.BASEFHIRID + "Certifier");
		if(inputFields.CERTIFIER_NAME != null && !inputFields.CERTIFIER_NAME.isEmpty()) {
			HumanName certName = LocalModelToFhirCMSUtil.parseHumanName(inputFields.CERTIFIER_NAME);
			returnCertifier.addName(certName);
		}
		if(inputFields.CERTIFIER_IDENTIFIER != null && !inputFields.CERTIFIER_IDENTIFIER.isEmpty()) {
			Identifier certIdentifier = new Identifier();
			certIdentifier.setValue(inputFields.CERTIFIER_IDENTIFIER);
			String certIdentifierSystem = "urn:mdi:raven:provideridentifier";
			if(inputFields.CERTIFIER_IDENTIFIER_SYSTEM != null && !inputFields.CERTIFIER_IDENTIFIER_SYSTEM.isEmpty()){
				certIdentifierSystem = inputFields.CERTIFIER_IDENTIFIER_SYSTEM;
			}
			certIdentifier.setSystem(certIdentifierSystem);
			returnCertifier.addIdentifier(certIdentifier);
		}
		if(inputFields.CERTIFIER_TYPE != null && !inputFields.CERTIFIER_TYPE.isEmpty()) {
			//TODO: Add certifier qualification component that makes sense here
		}
		return returnCertifier;
	}

	private PractitionerVitalRecords updatePrimaryMECWithCertifierInformation(DCRModelFields inputFields, PractitionerVitalRecords certifierResource){
		if(inputFields.CERTIFIER_IDENTIFIER != null && !inputFields.CERTIFIER_IDENTIFIER.isEmpty()) {
			Identifier certIdentifier = new Identifier();
			certIdentifier.setValue(inputFields.CERTIFIER_IDENTIFIER);
			String certIdentifierSystem = "urn:mdi:raven:provideridentifier";
			if(inputFields.CERTIFIER_IDENTIFIER_SYSTEM != null && !inputFields.CERTIFIER_IDENTIFIER_SYSTEM.isEmpty()){
				certIdentifierSystem = inputFields.CERTIFIER_IDENTIFIER_SYSTEM;
			}
			certIdentifier.setSystem(certIdentifierSystem);
			certifierResource.addIdentifier(certIdentifier);
		}
		if(inputFields.CERTIFIER_TYPE != null && !inputFields.CERTIFIER_TYPE.isEmpty()) {
			//TODO: Add certifier qualification component that makes sense here
		}
		return certifierResource;
	}

	private PractitionerVitalRecords createPronouncer(DCRModelFields inputFields) {
		PractitionerVitalRecords returnPronouncer = new PractitionerVitalRecords();
		returnPronouncer.setId(inputFields.BASEFHIRID + "Pronouncer");
		if(inputFields.PRONOUNCERNAME != null && !inputFields.PRONOUNCERNAME.isEmpty()) {
			HumanName certName = LocalModelToFhirCMSUtil.parseHumanName(inputFields.PRONOUNCERNAME);
			returnPronouncer.addName(certName);
		}
		return returnPronouncer;
	}

	private DeathCertificationProcedure createDeathCertificationProcedure(DCRModelFields inputFields, Reference decedentReference, Reference certifierReference) {
		DeathCertificationProcedure returnCertification = new DeathCertificationProcedure();
		returnCertification.setSubject(decedentReference);
		Procedure.ProcedurePerformerComponent procedurePerformerComponent = new Procedure.ProcedurePerformerComponent();
      	procedurePerformerComponent.setActor(certifierReference);
		if(inputFields.CERTIFIER_TYPE  != null && !inputFields.CERTIFIER_TYPE.isEmpty()){
			if(inputFields.CERTIFIER_TYPE.equalsIgnoreCase("Medical Examiner/Coroner")){
				procedurePerformerComponent.setFunction(CommonUtil.findConceptFromCollectionUsingSimpleString("Medical Examiner/Coroner", ProcedureDeathCertificationUtil.certifierTypes));
			}
			else if(inputFields.CERTIFIER_TYPE.equalsIgnoreCase("Pronouncing & Certifying physician")){
				procedurePerformerComponent.setFunction(CommonUtil.findConceptFromCollectionUsingSimpleString("Pronouncing & Certifying physician", ProcedureDeathCertificationUtil.certifierTypes));
			}
			else if(inputFields.CERTIFIER_TYPE.equalsIgnoreCase("Certifying Physician")){
				procedurePerformerComponent.setFunction(CommonUtil.findConceptFromCollectionUsingSimpleString("Pronouncing & Certifying physician", ProcedureDeathCertificationUtil.certifierTypes));
			}
		}
      	procedurePerformerComponent.setFunction(CommonUtil.findConceptFromCollectionUsingSimpleString(inputFields.CERTIFIER_TYPE, CommonUtil.certifierTypeSet));
      	returnCertification.addPerformer(procedurePerformerComponent);
		returnCertification.setStatus(ProcedureStatus.NOTDONE);
		returnCertification.setId(inputFields.BASEFHIRID + "Certification");
		return returnCertification;
	}
	
	
	private TobaccoUseContributedToDeath createObservationTobaccoUseContributedToDeath(DCRModelFields inputFields, Reference decedentReference) {
		TobaccoUseContributedToDeath tobacco = new TobaccoUseContributedToDeath();
		tobacco.setStatus(ObservationStatus.PRELIMINARY);
		tobacco.setId(inputFields.BASEFHIRID + "Tobacco");
		tobacco.setSubject(decedentReference);
		tobacco.setValue(inputFields.TOBACCO);
		return tobacco;
	}
	
	private DecedentPregnancyStatus createObservationDecedentPregnancy(DCRModelFields inputFields, Reference decedentReference) {
		DecedentPregnancyStatus pregnant = new DecedentPregnancyStatus();
		pregnant.setStatus(ObservationStatus.PRELIMINARY);
		pregnant.setId(inputFields.BASEFHIRID + "Pregnancy");
		pregnant.setSubject(decedentReference);
		pregnant.setValue(inputFields.PREGNANT);
		return pregnant;
	}

	private InjuryLocation createInjuryLocation(DCRModelFields inputFields, Reference decedentReference){
		InjuryLocation injuryLocation = new InjuryLocation();
		injuryLocation.setId(inputFields.BASEFHIRID + "Injury-Location");
    	Address injuryAddress = LocalModelToFhirCMSUtil.createAddress("", inputFields.INJURYLOCATION_STREET, inputFields.INJURYLOCATION_CITY, inputFields.INJURYLOCATION_COUNTY,
		inputFields.INJURYLOCATION_STATE, inputFields.INJURYLOCATION_STATE, inputFields.INJURYLOCATION_ZIP);
    	injuryLocation.setAddress(injuryAddress);
		return injuryLocation;
	}
	
	private List<Observation> createCauseOfDeathPathway(DCRModelFields inputFields, BundleDocumentMDIDCR bundle, CompositionMDIDCR mainComposition, Reference patientReference, Reference practitionerReference) {
		List<Observation> returnList = new ArrayList<Observation>();
		List<String> causes = new ArrayList<String>(Arrays.asList(inputFields.CAUSEA,inputFields.CAUSEB,inputFields.CAUSEC,inputFields.CAUSED));
		List<String> intervals = Arrays.asList(inputFields.DURATIONA,inputFields.DURATIONB,inputFields.DURATIONC,inputFields.DURATIOND);
		//Create and add Causes to the Bundle BEFORE the CauseOfDeathPathway
		for(int i = 0; i < causes.size(); i++) {
			String cause = causes.get(i);
			String interval = (i < intervals.size()) ? intervals.get(i) : "";
			if(cause != null && !cause.isEmpty()) {
				int lineNumber = i + 1; //entry 0 = line number 1
				ObservationCauseOfDeathPart1 causeOfDeathCondition = new ObservationCauseOfDeathPart1();
				causeOfDeathCondition.setSubject(patientReference);
				causeOfDeathCondition.addPerformer(practitionerReference);
				causeOfDeathCondition.setValue(new CodeableConcept().setText(cause));
				if(interval != null && !interval.isEmpty()){
					Observation.ObservationComponentComponent component = new Observation.ObservationComponentComponent();
      				component.setCode(CauseOfDeathConditionUtil.intervalComponentCode);
      				component.setValue(new StringType(interval));
				}
				causeOfDeathCondition.setId(inputFields.BASEFHIRID+"CauseOfDeathPart1-"+i);
				causeOfDeathCondition.setStatus(ObservationStatus.PRELIMINARY);
				//CauseOfDeathPart1 VRDR model didn't specify linenumber component directly so we have to do it manually
				ObservationComponentComponent lineNumberComponent = new ObservationComponentComponent(new CodeableConcept(new Coding("http://hl7.org/fhir/us/vrdr/CodeSystem/vrdr-component-cs","lineNumber","")));
				lineNumberComponent.setValue(new IntegerType(lineNumber));
				causeOfDeathCondition.addComponent(lineNumberComponent);
				returnList.add(causeOfDeathCondition);
			}
		}
		String[] otherCauses = inputFields.OSCOND.split("[;\n]");
		List<String> listArrayOtherCauses = Arrays.asList(otherCauses);
		int i = 0;
		/* Turning off CauseofDeath part2 until they fix their tests*/
		for(String otherCause:listArrayOtherCauses) {
			if(otherCause.isEmpty()) {
				continue;
			}
			CauseOfDeathPart2 conditionContrib = new CauseOfDeathPart2();
			conditionContrib.setId(inputFields.BASEFHIRID+"CauseOfDeathPart2-"+i);
			conditionContrib.setStatus(ObservationStatus.PRELIMINARY);
			conditionContrib.setSubject(patientReference);
			conditionContrib.addPerformer(practitionerReference);
			conditionContrib.setValue(new CodeableConcept().setText(otherCause));
			returnList.add(conditionContrib);
			i++;
		}
		return returnList;
	}
	
	private MannerOfDeath createMannerOfDeath(DCRModelFields inputFields, Reference decedentReference, Reference practitionerReference) {
		MannerOfDeath manner = new MannerOfDeath();
		manner.setId(inputFields.BASEFHIRID+"MannerOfDeath");
		manner.addPerformer(practitionerReference);
		Coding mannerCoding = new Coding();
		if(inputFields.MANNER != null && !inputFields.MANNER.isEmpty()) {
			mannerCoding.setSystem("http://snomed.info/sct");
			if(LocalModelToFhirCMSUtil.containsIgnoreCase(inputFields.MANNER, "Homicide")) {
				mannerCoding.setCode("27935005");
				mannerCoding.setDisplay("Homicide");
			}
			else if(LocalModelToFhirCMSUtil.containsIgnoreCase(inputFields.MANNER, "Suicide")) {
				mannerCoding.setCode("44301001");
				mannerCoding.setDisplay("Suicide");
			}
			else if(LocalModelToFhirCMSUtil.containsIgnoreCase(inputFields.MANNER, "Accident")) {
				mannerCoding.setCode("7878000");
				mannerCoding.setDisplay("Accidental Death");
			}
			else if(LocalModelToFhirCMSUtil.containsIgnoreCase(inputFields.MANNER, "Natural")) {
				mannerCoding.setCode("38605008");
				mannerCoding.setDisplay("Natural");
			}
			else if(LocalModelToFhirCMSUtil.containsIgnoreCase(inputFields.MANNER, "Investigation")) {
				mannerCoding.setCode("185973002");
				mannerCoding.setDisplay("Patient awaiting investigation");
			}
		}
		CodeableConcept mannerCode = new CodeableConcept();
		if(!mannerCoding.isEmpty()) {
			mannerCode.addCoding(mannerCoding);
		}
		else {
			mannerCode.setText(inputFields.MANNER);
		}
		manner.setValue(mannerCode);
		manner.setSubject(decedentReference);
		return manner;
	}
	
	private InjuryIncident createInjuryIncident(DCRModelFields inputFields, Reference patientReference, Reference certifierReference){
		InjuryIncident injuryIncident = new InjuryIncident();
		injuryIncident.setId(inputFields.BASEFHIRID+"InjuryIncident");
		injuryIncident.setStatus(ObservationStatus.FINAL);
		injuryIncident.setSubject(patientReference);
		if(certifierReference != null){
			injuryIncident.addPerformer(certifierReference);
		}
		if(inputFields.CHOWNINJURY != null && !inputFields.CHOWNINJURY.isEmpty()){
			
		}
		if(inputFields.CINJDATE != null && !inputFields.CINJDATE.isEmpty()) {
			Date injDate = CommonMappingUtil.parseDateFromField(inputFields, "CINJDATE", inputFields.CINJDATE);
			if(inputFields.CINJTIME != null && !inputFields.CINJTIME.isEmpty()) {
				Date injTime = CommonMappingUtil.parseDateFromField(inputFields, "CINJTIME", inputFields.CINJTIME);
				LocalModelToFhirCMSUtil.addTimeToDate(injDate, injTime);
			}
			DateTimeType injEffectiveDT = new DateTimeType(injDate);
			injuryIncident.setEffective(injEffectiveDT);
		}

		if(inputFields.ATWORK != null && !inputFields.ATWORK.isEmpty()){
			injuryIncident.addInjuredAtWorkComponent(inputFields.ATWORK);
		}
		if(inputFields.TRANSPORTATION != null && !inputFields.TRANSPORTATION.isEmpty()){
			injuryIncident.addTransportationRoleIndicatorComponent(inputFields.TRANSPORTATION);
		}
		return injuryIncident;
	}
	
	private DeathDate createDeathDate(DCRModelFields inputFields, Reference decedentReference, Reference pronouncerReference) throws ParseException {
		DeathDate returnDeathDate = new DeathDate();
		returnDeathDate.setId(inputFields.BASEFHIRID+"DeathDate");
		returnDeathDate.setSubject(decedentReference);
		if(pronouncerReference != null && !pronouncerReference.isEmpty()){
			returnDeathDate.addPerformer(pronouncerReference);
		}
		if(inputFields.CDEATHDATE != null && !inputFields.CDEATHDATE.isEmpty()) {
			Date certDate = CommonMappingUtil.parseDateFromField(inputFields, "CDEATHDATE", inputFields.CDEATHDATE);
			if(certDate != null && inputFields.CDEATHTIME != null && !inputFields.CDEATHTIME.isEmpty()) {
				Date certTime = CommonMappingUtil.parseTimeFromField(inputFields, "CDEATHTIME", inputFields.CDEATHTIME);
				LocalModelToFhirCMSUtil.addTimeToDate(certDate, certTime);
			}
			DateTimeType certValueDT = new DateTimeType(certDate, TemporalPrecisionEnum.SECOND, TimeZone.getTimeZone(ZoneId.of("Z")));
			certValueDT.setTimeZoneZulu(true);
			returnDeathDate.setValue(certValueDT);
		}
		if(inputFields.PRNDATE != null && !inputFields.PRNDATE.isEmpty()) {
			CommonMappingUtil.parseDateTimeFromField(inputFields, "PRNDATE", inputFields.PRNDATE);
			Date prnDate = CommonMappingUtil.parseDateTimeFromField(inputFields, "PRNDATE", inputFields.PRNDATE);
			if(prnDate != null && inputFields.PRNTIME != null && !inputFields.PRNTIME.isEmpty()) {
				Date prnTime = CommonMappingUtil.parseTimeFromField(inputFields, "PRNTIME", inputFields.PRNTIME);
				LocalModelToFhirCMSUtil.addTimeToDate(prnDate, prnTime);
			}
			DateTimeType prnDT = new DateTimeType(prnDate);
			prnDT.setTimeZoneZulu(true);
			returnDeathDate.addDatePronouncedDead(prnDT);
		}
		if(inputFields.DEATHLOCATIONTYPE != null && !inputFields.DEATHLOCATIONTYPE.isEmpty()){
			
			returnDeathDate.addPlaceOfDeathComponent(inputFields.DEATHLOCATIONTYPE);
		}
		if(inputFields.CDEATHESTABLISHEMENTMETHOD != null && !inputFields.CDEATHESTABLISHEMENTMETHOD.isEmpty()){
			returnDeathDate.addMethod(inputFields.CDEATHESTABLISHEMENTMETHOD);
		}
		return returnDeathDate;
	}
	
	private DeathLocation createDeathLocation(MDIAndEDRSModelFields inputFields) {
		DeathLocation returnDeathLocation = new DeathLocation();
		returnDeathLocation.setId(inputFields.BASEFHIRID+"Death-Location");
		returnDeathLocation.setName("Death Location");
		Address deathLocationAddress = LocalModelToFhirCMSUtil.createAddress("", inputFields.DEATHLOCATION_STREET, inputFields.DEATHLOCATION_CITY, inputFields.DEATHLOCATION_COUNTY,
			inputFields.DEATHLOCATION_STATE, inputFields.DEATHLOCATION_STATE, inputFields.DEATHLOCATION_ZIP);
		returnDeathLocation.setAddress(deathLocationAddress);
		return returnDeathLocation;
	}

	private AutopsyPerformedIndicator createAutopsyPerformedIndicator(DCRModelFields inputFields,Reference decedentReference,Reference practitionerReference){
		String resultsAvailable = inputFields.AUTOPSYRESULTSAVAILABLE; //We always provide a resultsAvailable component; and say "no" when not provided by default.
		if(resultsAvailable == null || resultsAvailable.isEmpty()){
			resultsAvailable = "No";
		}
		AutopsyPerformedIndicator autopsyPerformedIndicator = new AutopsyPerformedIndicator(inputFields.AUTOPSYPERFORMED, resultsAvailable);
		autopsyPerformedIndicator.setSubject(decedentReference);
		autopsyPerformedIndicator.setId(inputFields.BASEFHIRID+"Autopsy");
		autopsyPerformedIndicator.addPerformer(practitionerReference);
		return autopsyPerformedIndicator;
	}

	private Location createAutopsyLocation(DCRModelFields inputFields) {
		Location returnAutopsyLocation = new Location();
		returnAutopsyLocation.setId(inputFields.BASEFHIRID+"Autopsy-Location");
		returnAutopsyLocation.getMeta().addProfile("http://hl7.org/fhir/us/core/StructureDefinition/us-core-location"); //Using this until we have a proper US-Core Location profile in MDI
		returnAutopsyLocation.setName(inputFields.AUTOPSY_OFFICENAME);
		returnAutopsyLocation.setStatus(LocationStatus.ACTIVE);
		Address autopsyAddress = LocalModelToFhirCMSUtil.createAddress("", inputFields.AUTOPSY_STREET,
				inputFields.AUTOPSY_CITY, inputFields.AUTOPSY_COUNTY, inputFields.AUTOPSY_STATE, inputFields.AUTOPSY_ZIP, "");
		returnAutopsyLocation.setAddress(autopsyAddress);
		return returnAutopsyLocation;
	}

	private FuneralHome createFuneralHome(DCRModelFields inputFields) {
		FuneralHome funeralHome = new FuneralHome();
		funeralHome.setId(inputFields.BASEFHIRID+"Funeral-Home");
		if(inputFields.FUNERALHOME_NAME != null && !inputFields.FUNERALHOME_NAME.isEmpty()) {
			funeralHome.setName(inputFields.FUNERALHOME_NAME);
		}
		Address funeralHomeAddress = LocalModelToFhirCMSUtil.createAddress("", inputFields.FUNERALHOME_STREET,
				inputFields.FUNERALHOME_CITY, inputFields.FUNERALHOME_COUNTY, inputFields.FUNERALHOME_STATE, inputFields.FUNERALHOME_ZIP, "");
		funeralHome.addAddress(funeralHomeAddress);
		return funeralHome;
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