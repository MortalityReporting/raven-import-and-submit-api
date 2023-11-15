package edu.gatech.chai.Mapping.Service;

import java.text.ParseException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
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
import org.hl7.fhir.r4.model.Composition.SectionComponent;
import org.hl7.fhir.r4.model.ContactPoint;
import org.hl7.fhir.r4.model.ContactPoint.ContactPointSystem;
import org.hl7.fhir.r4.model.ContactPoint.ContactPointUse;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.HumanName.NameUse;
import org.hl7.fhir.r4.model.Location.LocationStatus;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Location;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Observation.ObservationStatus;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.Procedure.ProcedureStatus;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.StringType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import ca.uhn.fhir.parser.IParser;
import edu.gatech.chai.MDI.Model.MDIAndEDRSModelFields;
import edu.gatech.chai.MDI.context.MDIFhirContext;
import edu.gatech.chai.MDI.model.resource.BundleDocumentMDIAndEDRS;
import edu.gatech.chai.MDI.model.resource.CompositionMDIAndEDRS;
import edu.gatech.chai.MDI.model.resource.LocationDeath;
import edu.gatech.chai.MDI.model.resource.LocationInjury;
import edu.gatech.chai.MDI.model.resource.ObservationAutopsyPerformedIndicator;
import edu.gatech.chai.MDI.model.resource.ObservationCauseOfDeathPart1;
import edu.gatech.chai.MDI.model.resource.ObservationContributingCauseOfDeathPart2;
import edu.gatech.chai.MDI.model.resource.ObservationDeathDate;
import edu.gatech.chai.MDI.model.resource.ObservationDecedentPregnancy;
import edu.gatech.chai.MDI.model.resource.ObservationHowDeathInjuryOccurred;
import edu.gatech.chai.MDI.model.resource.ObservationMannerOfDeath;
import edu.gatech.chai.MDI.model.resource.ObservationTobaccoUseContributedToDeath;
import edu.gatech.chai.MDI.model.resource.ProcedureDeathCertification;
import edu.gatech.chai.Mapping.Util.LocalModelToFhirCMSUtil;
import edu.gatech.chai.VRDR.model.util.DecedentUtil;

@Service
public class LocalToMDIAndEDRSService {
	
	@Value("${raven_generated_systemid}")
	private String raven_generated_systemid;
	@Autowired
	private MDIFhirContext mdiFhirContext;
	
	public String convertToMDIString(MDIAndEDRSModelFields inputFields) throws ParseException {
		Bundle fullBundle = convertToMDI(inputFields);
		return convertToMDIString(fullBundle);
	}

	public String convertToMDIString(Bundle fullBundle) throws ParseException {
		IParser parser = mdiFhirContext.getCtx().newJsonParser();
		String returnString = parser.encodeResourceToString(fullBundle);
		return returnString;
	}
	
	public Bundle convertToMDI(MDIAndEDRSModelFields inputFields) throws ParseException {
		BundleDocumentMDIAndEDRS returnBundle = new BundleDocumentMDIAndEDRS();
		returnBundle.setId(inputFields.BASEFHIRID + "MDIAndEDRS-Document-Bundle");
		Date now = new Date();
		returnBundle.setTimestamp(now);
		//Assigning a raven generated system identifier
		returnBundle.setIdentifier(new Identifier().setSystem("urn:raven:temporary").setValue(Long.toString(now.getTime())));
		returnBundle.setType(BundleType.BATCH);
		Identifier caseIdentifier = new Identifier().setSystem(inputFields.SYSTEMID);
		caseIdentifier.setValue("TestIdentifier");
		caseIdentifier.setType(new CodeableConcept().addCoding(new Coding().setCode("1000007").setSystem("urn:temporary:code").setDisplay("Case Number")));
		
		CompositionMDIAndEDRS mainComposition = returnBundle.getCompositionMDIAndEDRS();
		mainComposition.setTitle("Raven generated MDI-And-EDRS Document");
		//Some bookkeeping to set the mainComposition id
		mainComposition.setId(inputFields.BASEFHIRID + "MDIAndEDRS-Composition");
		returnBundle.getEntryFirstRep().setFullUrl(mainComposition.getId());
		mainComposition.setIdentifier(caseIdentifier);
		mainComposition.setStatus(CompositionStatus.PRELIMINARY);
		mainComposition.setDate(new Date());
		if(inputFields.MDICASEID != null && !inputFields.MDICASEID.isEmpty()){
			mainComposition.addMDICaseIdExtension(raven_generated_systemid, inputFields.MDICASEID);
		}
		if(inputFields.EDRSCASEID != null && !inputFields.EDRSCASEID.isEmpty()){
			mainComposition.addEDRSCaseIdExtension(raven_generated_systemid, inputFields.EDRSCASEID);
		}
		//Since we're creating a batch bundle we need to set the request type on all resources. However this one is handled special from "addResourceToBatchBundle"
		returnBundle.getEntryFirstRep().setRequest(new BundleEntryRequestComponent().setMethod(HTTPVerb.POST));

		Patient patientResource = null;
		Reference patientReference = null;
		Practitioner primaryMECResource = null;
		Reference primaryMECReference = null;
		Practitioner certifierResource = null;
		Reference certifierReference = null;
		Practitioner pronouncerResource = null;
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
			patientResource = createPatient(inputFields);
			patientReference = new Reference("Patient/"+patientResource.getId());
			mainComposition.setSubject(patientReference);
			LocalModelToFhirCMSUtil.addResourceToBundle(returnBundle, patientResource);
		}
		// Handle Primary Medical Examiner/Coroner
		Stream<String> primaryMECFields = Stream.of(inputFields.MENAME,inputFields.MELICENSE,inputFields.MEPHONE,
			inputFields.ME_STREET,inputFields.ME_CITY,inputFields.ME_COUNTY,inputFields.ME_STATE,inputFields.ME_ZIP);
		if(!primaryMECFields.allMatch(x -> x == null || x.isEmpty())) {
			primaryMECResource = createPrimaryMEC(inputFields);
			primaryMECReference = new Reference("Practitioner/"+primaryMECResource.getId());
			mainComposition.addAuthor(primaryMECReference);
			LocalModelToFhirCMSUtil.addResourceToBundle(returnBundle, primaryMECResource);
		}
		// Handle Certifier
		Stream<String> certifierFields = Stream.of(inputFields.CERTIFIER_NAME,inputFields.CERTIFIER_TYPE);
		if(!certifierFields.allMatch(x -> x == null || x.isEmpty())) {
			//Special handling if the Primary Medical Examiner/Coroner is the same as the Certifier
			if(inputFields.MENAME.equalsIgnoreCase(inputFields.CERTIFIER_NAME)){
				certifierResource = primaryMECResource;
			}
			else{
				certifierResource = createCertifier(inputFields);
				LocalModelToFhirCMSUtil.addResourceToBundle(returnBundle, certifierResource);
			}
			certifierReference = new Reference("Practitioner/"+certifierResource.getId());
			mainComposition.addAttester(certifierReference);
			ProcedureDeathCertification deathCertification = createDeathCertification(inputFields, patientReference, certifierReference);
			LocalModelToFhirCMSUtil.addResourceToBundle(returnBundle, deathCertification);
			mainComposition.getJurisdictionSection().addEntry(new Reference("Procedure/"+deathCertification.getId()));
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
				LocalModelToFhirCMSUtil.addResourceToBundle(returnBundle, pronouncerResource);
			}
			pronouncerReference = new Reference("Practitioner/"+pronouncerResource.getId());
		}
		// Handle Death Certification
		ProcedureDeathCertification deathCertification = createDeathCertification(inputFields, patientReference, certifierReference);
		LocalModelToFhirCMSUtil.addResourceToBundle(returnBundle, deathCertification);
		mainComposition.getJurisdictionSection().addEntry(new Reference("Procedure/"+deathCertification.getId()));
		// Handle Death Location
		LocationDeath deathLocation = null;
		Stream<String> deathLocFields = Stream.of(inputFields.DEATHLOCATION);
		if(!deathLocFields.allMatch(x -> x == null || x.isEmpty())) {
			deathLocation = createDeathLocation(inputFields);
			mainComposition.getCircumstancesSection().addEntry(new Reference("Location/"+deathLocation.getId()));
			LocalModelToFhirCMSUtil.addResourceToBundle(returnBundle, deathLocation);
		}
		//Handle TobaccoUseContributedToDeath
		Stream<String> tobaccoFields = Stream.of(inputFields.TOBACCO);
		if(!tobaccoFields.allMatch(x -> x == null || x.isEmpty())) {
			ObservationTobaccoUseContributedToDeath tobacco = createObservationTobaccoUseContributedToDeath(inputFields, patientReference);
			mainComposition.getCircumstancesSection().addEntry(new Reference("Observation/"+tobacco.getId()));
			LocalModelToFhirCMSUtil.addResourceToBundle(returnBundle, tobacco);
		}
		//Handle Decedent Pregnancy
		Stream<String> pregnantFields = Stream.of(inputFields.PREGNANT);
		if(!pregnantFields.allMatch(x -> x == null || x.isEmpty())) {
			ObservationDecedentPregnancy pregnant = createObservationDecedentPregnancy(inputFields, patientReference);
			mainComposition.getCircumstancesSection().addEntry(new Reference("Observation/"+pregnant.getId()));
			LocalModelToFhirCMSUtil.addResourceToBundle(returnBundle, pregnant);
		}
		//Handle Injury Location
		Stream<String> injuryLocationFields = Stream.of(inputFields.INJURYLOCATION);
		if(!injuryLocationFields.allMatch(x -> x == null || x.isEmpty())) {
			LocationInjury injuryLocation = createInjuryLocation(inputFields, patientReference);
			mainComposition.getCircumstancesSection().addEntry(new Reference("Location/"+injuryLocation.getId()));
			LocalModelToFhirCMSUtil.addResourceToBundle(returnBundle, injuryLocation);
		}
		// Handle Death Date
		Stream<String> deathDateFields = Stream.of(inputFields.PRNDATE, inputFields.PRNTIME,
				inputFields.CDEATHDATE, inputFields.CDEATHTIME);
		if(!deathDateFields.allMatch(x -> x == null || x.isEmpty())) {
			ObservationDeathDate deathDate = createDeathDate(inputFields, patientReference, pronouncerReference);
			mainComposition.getJurisdictionSection().addEntry(new Reference("Observation/"+deathDate.getId()));
			LocalModelToFhirCMSUtil.addResourceToBundle(returnBundle, deathDate);
		}
		// Handle Cause Of Death Pathway
		Stream<String> causeOfDeathFields = Stream.of(inputFields.CAUSEA, inputFields.CAUSEB, inputFields.CAUSEC,
				inputFields.CAUSED,inputFields.OSCOND, inputFields.DURATIONA, inputFields.DURATIONB,
				inputFields.DURATIONC, inputFields.DURATIOND);
		if(!causeOfDeathFields.allMatch(x -> x == null || x.isEmpty())) {
			//THis list contains CauseOfDeathPart1 and CauseOfDeathPart2 resources
			List<Observation> causeOfDeathPathway = createCauseOfDeathPathway(inputFields, returnBundle, mainComposition, patientResource, certifierResource);
			for(Observation cause:causeOfDeathPathway){
				mainComposition.getCauseMannerSection().addEntry(new Reference("Observation/"+cause.getId()));
				LocalModelToFhirCMSUtil.addResourceToBundle(returnBundle, cause);
			}
		}

		// Handle Manner Of Death
		Stream<String> mannerFields = Stream.of(inputFields.MANNER);
		if(!mannerFields.allMatch(x -> x == null || x.isEmpty())) {
			ObservationMannerOfDeath manner = createMannerOfDeath(inputFields, patientReference, primaryMECReference);
			mainComposition.getCauseMannerSection().addEntry(new Reference("Observation/"+manner.getId()));
			LocalModelToFhirCMSUtil.addResourceToBundle(returnBundle, manner);
		}
		// Handle HowDeathInjuryOccurred
		Stream<String> deathInjuryFields = Stream.of(inputFields.CHOWNINJURY, inputFields.CINJDATE, inputFields.CINJTIME, inputFields.INJURYLOCATION,
				inputFields.ATWORK,inputFields.TRANSPORTATION);
		if(!deathInjuryFields.allMatch(x -> x == null || x.isEmpty())) {
			ObservationHowDeathInjuryOccurred deathInjuryDescription = createHowDeathInjuryOccurred(inputFields, patientResource, certifierResource);
			mainComposition.getCauseMannerSection().addEntry(new Reference("Observation/"+deathInjuryDescription.getId()));
			LocalModelToFhirCMSUtil.addResourceToBundle(returnBundle, deathInjuryDescription);
		}
		SectionComponent medicationHistorySection = mainComposition.getMedicalHistorySection();
		medicationHistorySection.setEmptyReason(new CodeableConcept().addCoding(new Coding("http://terminology.hl7.org/CodeSystem/list-empty-reason","unavailable","Unavailable")));
		//Handle Autopsy
		SectionComponent examAutopsySection = mainComposition.getExamAutopsySection();
		//Handle Autopsy Indicator
		Stream<String> autopsyIdenticatorFields = Stream.of(inputFields.AUTOPSYPERFORMED, inputFields.AUTOPSYRESULTSAVAILABLE);
		if(!autopsyIdenticatorFields.allMatch(x -> x == null || x.isEmpty())) {
			ObservationAutopsyPerformedIndicator autopsyPerformedIndicator = createAutopsyPerformedIndicator(inputFields, patientReference, primaryMECReference);
			examAutopsySection.addEntry(new Reference("Observation/"+autopsyPerformedIndicator.getId()));
			LocalModelToFhirCMSUtil.addResourceToBundle(returnBundle, autopsyPerformedIndicator);
		}
		//Hand Autopsy Location
		Stream<String> autopsyLocationFields = Stream.of(inputFields.AUTOPSY_OFFICENAME, inputFields.AUTOPSY_STREET, inputFields.AUTOPSY_CITY, inputFields.AUTOPSY_COUNTY, inputFields.AUTOPSY_STATE, inputFields.AUTOPSY_ZIP);
		if(!autopsyLocationFields.allMatch(x -> x == null || x.isEmpty())) {
			Location autopsyPerformedLocation = createAutopsyLocation(inputFields);
			examAutopsySection.addEntry(new Reference("Location/"+autopsyPerformedLocation.getId()));
			LocalModelToFhirCMSUtil.addResourceToBundle(returnBundle, autopsyPerformedLocation);
		}
		return returnBundle;
	}
	
	private Patient createPatient(MDIAndEDRSModelFields inputFields) throws ParseException {
		Patient returnDecedent = new Patient();
		returnDecedent.setMeta(new Meta().addProfile("http://hl7.org/fhir/us/core/StructureDefinition/us-core-patient"));
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
			if(LocalModelToFhirCMSUtil.containsIgnoreCase(inputFields.GENDER, "Female")) {
				returnDecedent.setGender(AdministrativeGender.FEMALE);
			}
			else if(LocalModelToFhirCMSUtil.containsIgnoreCase(inputFields.GENDER, "Male")) {
				returnDecedent.setGender(AdministrativeGender.MALE);
			}
			else{
				returnDecedent.setGender(AdministrativeGender.UNKNOWN);
			}
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
		Address residentAddress = LocalModelToFhirCMSUtil.createAddress("", inputFields.RESSTREET,
				inputFields.RESCITY, inputFields.RESCOUNTY, inputFields.RESSTATE, inputFields.RESZIP, inputFields.RESCOUNTRY);
		residentAddress.setUse(AddressUse.HOME);
		returnDecedent.addAddress(residentAddress);
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
	
	private Practitioner createPrimaryMEC(MDIAndEDRSModelFields inputFields) {
		Practitioner returnPractitioner = new Practitioner();
		returnPractitioner.setMeta(new Meta().addProfile("http://hl7.org/fhir/us/core/StructureDefinition/us-core-practitioner"));
		returnPractitioner.setId(inputFields.BASEFHIRID + "Practitioner");
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

	private Practitioner createCertifier(MDIAndEDRSModelFields inputFields) {
		Practitioner returnCertifier = new Practitioner();
		returnCertifier.setMeta(new Meta().addProfile("http://hl7.org/fhir/us/core/StructureDefinition/us-core-practitioner"));
		returnCertifier.setId(inputFields.BASEFHIRID + "Certifier");
		if(inputFields.CERTIFIER_NAME != null && !inputFields.CERTIFIER_NAME.isEmpty()) {
			HumanName certName = LocalModelToFhirCMSUtil.parseHumanName(inputFields.CERTIFIER_NAME);
			returnCertifier.addName(certName);
		}
		if(inputFields.CERTIFIER_TYPE != null && !inputFields.CERTIFIER_TYPE.isEmpty()) {
			//TODO: Add certifier qualification component that makes sense here
		}
		return returnCertifier;
	}

	private Practitioner createPronouncer(MDIAndEDRSModelFields inputFields) {
		Practitioner returnPronouncer = new Practitioner();
		returnPronouncer.setMeta(new Meta().addProfile("http://hl7.org/fhir/us/core/StructureDefinition/us-core-practitioner"));
		returnPronouncer.setId(inputFields.BASEFHIRID + "Pronouncer");
		if(inputFields.PRONOUNCERNAME != null && !inputFields.PRONOUNCERNAME.isEmpty()) {
			HumanName certName = LocalModelToFhirCMSUtil.parseHumanName(inputFields.PRONOUNCERNAME);
			returnPronouncer.addName(certName);
		}
		return returnPronouncer;
	}

	private ProcedureDeathCertification createDeathCertification(MDIAndEDRSModelFields inputFields, Reference decedentReference, Reference certifierReference) {
		ProcedureDeathCertification returnCertification = new ProcedureDeathCertification(decedentReference, certifierReference, inputFields.CERTIFIER_TYPE);
		returnCertification.setStatus(ProcedureStatus.NOTDONE);
		returnCertification.setId(inputFields.BASEFHIRID + "Certification");
		return returnCertification;
	}
	
	
	private ObservationTobaccoUseContributedToDeath createObservationTobaccoUseContributedToDeath(MDIAndEDRSModelFields inputFields, Reference decedentReference) throws ParseException {
		ObservationTobaccoUseContributedToDeath tobacco = new ObservationTobaccoUseContributedToDeath();
		tobacco.setStatus(ObservationStatus.PRELIMINARY);
		tobacco.setId(inputFields.BASEFHIRID + "Tobacco");
		tobacco.setSubject(decedentReference);
		tobacco.setValue(inputFields.TOBACCO);
		return tobacco;
	}
	
	private ObservationDecedentPregnancy createObservationDecedentPregnancy(MDIAndEDRSModelFields inputFields, Reference decedentReference) throws ParseException {
		ObservationDecedentPregnancy pregnant = new ObservationDecedentPregnancy();
		pregnant.setStatus(ObservationStatus.PRELIMINARY);
		pregnant.setId(inputFields.BASEFHIRID + "Pregnancy");
		pregnant.setSubject(decedentReference);
		pregnant.setValue(inputFields.PREGNANT);
		return pregnant;
	}

	private LocationInjury createInjuryLocation(MDIAndEDRSModelFields inputFields, Reference decedentReference){
		LocationInjury injuryLocation = new LocationInjury();
		injuryLocation.setId(inputFields.BASEFHIRID + "Injury-Location");
    	injuryLocation.setName(inputFields.INJURYLOCATION);
    	injuryLocation.setAddress(new Address().setText(inputFields.INJURYLOCATION));
		return injuryLocation;
	}
	
	private List<Observation> createCauseOfDeathPathway(MDIAndEDRSModelFields inputFields, Bundle bundle, CompositionMDIAndEDRS mainComposition, Patient patientResource, Practitioner practitionerResource) {
		List<Observation> returnList = new ArrayList<Observation>();
		List<String> causes = new ArrayList<String>(Arrays.asList(inputFields.CAUSEA,inputFields.CAUSEB,inputFields.CAUSEC,inputFields.CAUSED));
		List<String> intervals = Arrays.asList(inputFields.DURATIONA,inputFields.DURATIONB,inputFields.DURATIONC,inputFields.DURATIOND);
		//Create and add Causes to the Bundle BEFORE the CauseOfDeathPathway
		for(int i = 0; i < causes.size(); i++) {
			String cause = causes.get(i);
			String interval = (i < intervals.size()) ? intervals.get(i) : "";
			if(cause != null && !cause.isEmpty()) {
				int lineNumber = i + 1; //entry 0 = line number 1
				ObservationCauseOfDeathPart1 causeOfDeathCondition = new ObservationCauseOfDeathPart1(patientResource, practitionerResource, cause, lineNumber, interval);
				causeOfDeathCondition.setId(inputFields.BASEFHIRID+"CauseOfDeathPart1-"+i);
				causeOfDeathCondition.setStatus(ObservationStatus.PRELIMINARY);
				returnList.add(causeOfDeathCondition);
			}
		}
		String[] otherCauses = inputFields.OSCOND.split("[;\n]");
		List<String> listArrayOtherCauses = Arrays.asList(otherCauses);
		int i = 0;
		for(String otherCause:listArrayOtherCauses) {
			if(otherCause.isEmpty()) {
				continue;
			}
			ObservationContributingCauseOfDeathPart2 conditionContrib = new ObservationContributingCauseOfDeathPart2(patientResource, practitionerResource, otherCause);
			conditionContrib.setId(inputFields.BASEFHIRID+"CauseOfDeathPart2-"+i);
			conditionContrib.setStatus(ObservationStatus.PRELIMINARY);
			returnList.add(conditionContrib);
			i++;
		}
		return returnList;
	}
	
	private ObservationMannerOfDeath createMannerOfDeath(MDIAndEDRSModelFields inputFields, Reference decedentReference, Reference practitionerReference) {
		ObservationMannerOfDeath manner = new ObservationMannerOfDeath();
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
	
	private ObservationHowDeathInjuryOccurred createHowDeathInjuryOccurred(MDIAndEDRSModelFields inputFields, Patient patientResource, Practitioner practitionerResource) throws ParseException {
		ObservationHowDeathInjuryOccurred injuryDescription = new ObservationHowDeathInjuryOccurred(patientResource, practitionerResource, inputFields.CHOWNINJURY);
		injuryDescription.setId(inputFields.BASEFHIRID+"InjuryDescription");
		injuryDescription.setStatus(ObservationStatus.FINAL);

		if(inputFields.CINJDATE != null && !inputFields.CINJDATE.isEmpty()) {
			Date injDate = LocalModelToFhirCMSUtil.parseDate(inputFields.CINJDATE);
			if(inputFields.CINJTIME != null && !inputFields.CINJTIME.isEmpty()) {
				LocalModelToFhirCMSUtil.addTimeToDate(injDate, inputFields.CINJTIME);
			}
			DateTimeType injEffectiveDT = new DateTimeType(injDate);
			injuryDescription.setEffective(injEffectiveDT);
		}

		if(inputFields.INJURYLOCATION != null && !inputFields.INJURYLOCATION.isEmpty()) {
			injuryDescription.addPlaceOfInjury(inputFields.INJURYLOCATION);
		}

		if(inputFields.ATWORK != null && !inputFields.ATWORK.isEmpty()){
			injuryDescription.addWorkInjuryIndicator(inputFields.ATWORK);
		}
		if(inputFields.TRANSPORTATION != null && !inputFields.TRANSPORTATION.isEmpty()){
			injuryDescription.addTransportationRole(inputFields.TRANSPORTATION);
		}
		return injuryDescription;
	}
	
	private Observation createFoundObs(MDIAndEDRSModelFields inputFields, Reference decedentReference) throws ParseException {
		Observation foundObs = new Observation();
		foundObs.setId(inputFields.BASEFHIRID+"FoundObs");
		foundObs.setStatus(ObservationStatus.FINAL);
		foundObs.setSubject(decedentReference);
		foundObs.setCode(new CodeableConcept().addCoding(new Coding(
				"urn:temporary:code", "1000001", "Date and Time found dead, unconcious and in distress")));
		if(inputFields.FOUNDDATE != null && !inputFields.FOUNDDATE.isEmpty()) {
			Date reportDate = LocalModelToFhirCMSUtil.parseDate(inputFields.FOUNDDATE);
			if(inputFields.FOUNDTIME != null && !inputFields.FOUNDTIME.isEmpty()) {
				LocalModelToFhirCMSUtil.addTimeToDate(reportDate, inputFields.FOUNDTIME);
			}
			foundObs.setValue(new DateTimeType(reportDate).setTimeZoneZulu(true));
		}
		return foundObs;
	}
	
	private ObservationDeathDate createDeathDate(MDIAndEDRSModelFields inputFields, Reference decedentReference, Reference pronouncerReference) throws ParseException {
		ObservationDeathDate returnDeathDate = new ObservationDeathDate();
		returnDeathDate.setId(inputFields.BASEFHIRID+"DeathDate");
		returnDeathDate.setSubject(decedentReference);
		if(pronouncerReference != null && !pronouncerReference.isEmpty()){
			returnDeathDate.addPerformer(pronouncerReference);
		}
		if(inputFields.CDEATHDATE != null && !inputFields.CDEATHDATE.isEmpty()) {
			Date certDate = LocalModelToFhirCMSUtil.parseDate(inputFields.CDEATHDATE);
			if(inputFields.CDEATHTIME != null && !inputFields.CDEATHTIME.isEmpty()) {
				LocalModelToFhirCMSUtil.addTimeToDate(certDate, inputFields.CDEATHTIME);
			}
			DateTimeType certValueDT = new DateTimeType(certDate, TemporalPrecisionEnum.SECOND, TimeZone.getTimeZone(ZoneId.of("Z")));
			certValueDT.setTimeZoneZulu(true);
			returnDeathDate.setValue(certValueDT);
		}
		if(inputFields.PRNDATE != null && !inputFields.PRNDATE.isEmpty()) {
			Date prnDate = LocalModelToFhirCMSUtil.parseDate(inputFields.PRNDATE);
			if(inputFields.PRNTIME != null && !inputFields.PRNTIME.isEmpty()) {
				LocalModelToFhirCMSUtil.addTimeToDate(prnDate, inputFields.PRNTIME);
			}
			DateTimeType prnDT = new DateTimeType(prnDate);
			prnDT.setTimeZoneZulu(true);
			returnDeathDate.addDatePronouncedDead(prnDT);
		}
		if(inputFields.DEATHLOCATIONTYPE != null && !inputFields.DEATHLOCATIONTYPE.isEmpty()){
			returnDeathDate.addPlaceOfDeath(inputFields.DEATHLOCATIONTYPE);
		}
		if(inputFields.CDEATHESTABLISHEMENTMETHOD != null && !inputFields.CDEATHESTABLISHEMENTMETHOD.isEmpty()){
			returnDeathDate.setEstablishmentMethod(inputFields.CDEATHESTABLISHEMENTMETHOD);
		}
		return returnDeathDate;
	}
	
	private LocationDeath createDeathLocation(MDIAndEDRSModelFields inputFields) {
		LocationDeath returnDeathLocation = new LocationDeath();
		returnDeathLocation.setId(inputFields.BASEFHIRID+"Death-Location");
		returnDeathLocation.setName(inputFields.DEATHLOCATION);
		returnDeathLocation.setAddress(new Address().setText(inputFields.DEATHLOCATION));
		return returnDeathLocation;
	}

	private ObservationAutopsyPerformedIndicator createAutopsyPerformedIndicator(MDIAndEDRSModelFields inputFields,Reference decedentReference,Reference practitionerReference){
		String resultsAvailable = inputFields.AUTOPSYRESULTSAVAILABLE; //We always provide a resultsAvailable component; and say "no" when not provided by default.
		if(resultsAvailable == null || resultsAvailable.isEmpty()){
			resultsAvailable = "No";
		}
		ObservationAutopsyPerformedIndicator autopsyPerformedIndicator = new ObservationAutopsyPerformedIndicator(decedentReference, inputFields.AUTOPSYPERFORMED, resultsAvailable);
		autopsyPerformedIndicator.setId(inputFields.BASEFHIRID+"Autopsy");
		autopsyPerformedIndicator.addPerformer(practitionerReference);
		return autopsyPerformedIndicator;
	}

	private Location createAutopsyLocation(MDIAndEDRSModelFields inputFields) {
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