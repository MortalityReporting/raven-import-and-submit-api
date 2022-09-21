package edu.gatech.chai.Mapping.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Address.AddressUse;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleType;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Composition.CompositionStatus;
import org.hl7.fhir.r4.model.ContactPoint;
import org.hl7.fhir.r4.model.ContactPoint.ContactPointUse;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.HumanName.NameUse;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Location;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Observation.ObservationStatus;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.StringType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.uhn.fhir.parser.IParser;
import edu.gatech.chai.MDI.Model.MDIModelFields;
import edu.gatech.chai.MDI.context.MDIFhirContext;
import edu.gatech.chai.MDI.model.resource.BundleDocumentMDIToEDRS;
import edu.gatech.chai.MDI.model.resource.CompositionMDIToEDRS;
import edu.gatech.chai.MDI.model.resource.ObservationCauseOfDeathPart1;
import edu.gatech.chai.MDI.model.resource.ObservationContributingCauseOfDeathPart2;
import edu.gatech.chai.MDI.model.resource.ObservationDeathDate;
import edu.gatech.chai.MDI.model.resource.ObservationDecedentPregnancy;
import edu.gatech.chai.MDI.model.resource.ObservationHowDeathInjuryOccurred;
import edu.gatech.chai.MDI.model.resource.ObservationMannerOfDeath;
import edu.gatech.chai.MDI.model.resource.ObservationTobaccoUseContributedToDeath;
import edu.gatech.chai.MDI.model.resource.ProcedureDeathCertification;
import edu.gatech.chai.Mapping.Util.MDIToFhirCMSUtil;
import edu.gatech.chai.VRDR.model.util.CommonUtil;
import edu.gatech.chai.VRDR.model.util.DecedentUtil;

@Service
public class MDIToMDIFhirCMSService {
	
	@Autowired
	private MDIFhirContext mdiFhirContext;
	
	public String convertToMDIString(MDIModelFields inputFields) throws ParseException {
		Bundle fullBundle = convertToMDI(inputFields);
		return convertToMDIString(fullBundle);
	}

	public String convertToMDIString(Bundle fullBundle) throws ParseException {
		IParser parser = mdiFhirContext.getCtx().newJsonParser();
		String returnString = parser.encodeResourceToString(fullBundle);
		return returnString;
	}
	
	public Bundle convertToMDI(MDIModelFields inputFields) throws ParseException {
		BundleDocumentMDIToEDRS returnBundle = new BundleDocumentMDIToEDRS();
		CommonUtil.setUUID(returnBundle);
		returnBundle.setType(BundleType.BATCH);
		Identifier caseIdentifier = new Identifier().setSystem(inputFields.SYSTEMID);
		caseIdentifier.setValue("TestIdentifier");
		caseIdentifier.setType(new CodeableConcept().addCoding(new Coding().setCode("1000007").setSystem("urn:mdi:temporary:code").setDisplay("Case Number")));
		
		CompositionMDIToEDRS mainComposition = returnBundle.getCompositionMDIToEDRS();
		//Some bookkeeping to set the mainComposition id
		CommonUtil.setUUID(mainComposition);
		returnBundle.getEntryFirstRep().setFullUrl(mainComposition.getId());
		mainComposition.setIdentifier(caseIdentifier);
		mainComposition.setStatus(CompositionStatus.PRELIMINARY);
		mainComposition.setDate(new Date());
		if(inputFields.MDICASEID != null && !inputFields.MDICASEID.isEmpty()){
			mainComposition.addMDICaseIdExtension(inputFields.MDICASEID);
		}
		if(inputFields.EDRSCASEID != null && !inputFields.EDRSCASEID.isEmpty()){
			mainComposition.addEDRSCaseIdExtension(inputFields.EDRSCASEID);
		}
		//MDIToFhirCMSUtil.addResourceToBatchBundle(returnBundle, mainComposition);

		Patient patientResource = null;
		Reference patientReference = null;
		Practitioner practitionerResource = null;
		Reference practitionerReference = null;
		Practitioner certifierResource = null;
		Reference certifierReference = null;
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
			MDIToFhirCMSUtil.addResourceToBatchBundle(returnBundle, patientResource);
		}
		// Handle Practitoner/Certifier
		Stream<String> practitionerFields = Stream.of(inputFields.MENAME,inputFields.MELICENSE,inputFields.MEPHONE,
			inputFields.ME_STREET,inputFields.ME_CITY,inputFields.ME_COUNTY,inputFields.ME_STATE,inputFields.ME_ZIP);
		if(!practitionerFields.allMatch(x -> x == null || x.isEmpty())) {
			practitionerResource = createPractitioner(inputFields);
			practitionerReference = new Reference("Practitioner/"+practitionerResource.getId());
			mainComposition.addAuthor(practitionerReference);
			MDIToFhirCMSUtil.addResourceToBatchBundle(returnBundle, practitionerResource);
		}
		Stream<String> certifierFields = Stream.of(inputFields.CERTIFIER_NAME,inputFields.CERTIFIER_TYPE);
		if(!certifierFields.allMatch(x -> x == null || x.isEmpty())) {
			//Special handling if the practitioner is the same as the certifier
			if(inputFields.MENAME.equalsIgnoreCase(inputFields.CERTIFIER_NAME)){
				certifierResource = practitionerResource;
			}
			else{
				certifierResource = createCertifier(inputFields);
				MDIToFhirCMSUtil.addResourceToBatchBundle(returnBundle, certifierResource);
			}
			certifierReference = new Reference("Practitioner/"+certifierResource.getId());
			mainComposition.addAttester(certifierReference);
			ProcedureDeathCertification deathCertification = createDeathCertification(inputFields, patientReference, certifierReference);
			MDIToFhirCMSUtil.addResourceToBatchBundle(returnBundle, deathCertification);
			mainComposition.getJurisdictionSection().addEntry(new Reference("Procedure/"+deathCertification));
		}
		
		// Handle Death Location
		Location deathLocation = null;
		Stream<String> deathLocFields = Stream.of(inputFields.DEATHLOCATION);
		if(!deathLocFields.allMatch(x -> x == null || x.isEmpty())) {
			deathLocation = createDeathLocation(inputFields);
			mainComposition.getCircumstancesSection().addEntry(new Reference("Location/"+deathLocation.getId()));
			MDIToFhirCMSUtil.addResourceToBatchBundle(returnBundle, deathLocation);
		}
		//Handle TobaccoUseContributedToDeath
		Stream<String> tobaccoFields = Stream.of(inputFields.TOBACCO);
		if(!tobaccoFields.allMatch(x -> x == null || x.isEmpty())) {
			ObservationTobaccoUseContributedToDeath tobacco = createObservationTobaccoUseContributedToDeath(inputFields, patientReference);
			mainComposition.getCircumstancesSection().addEntry(new Reference("Observation/"+tobacco.getId()));
			MDIToFhirCMSUtil.addResourceToBatchBundle(returnBundle, tobacco);
		}
		//Handle Decedent Pregnancy
		Stream<String> pregnantFields = Stream.of(inputFields.PREGNANT);
		if(!pregnantFields.allMatch(x -> x == null || x.isEmpty())) {
			ObservationDecedentPregnancy pregnant = createObservationDecedentPregnancy(inputFields, patientReference);
			mainComposition.getCircumstancesSection().addEntry(new Reference("Observation/"+pregnant.getId()));
			MDIToFhirCMSUtil.addResourceToBatchBundle(returnBundle, pregnant);
		}
		//Handle Injury Location
		Stream<String> injuryLocationFields = Stream.of(inputFields.INJURYLOCATION);
		if(!injuryLocationFields.allMatch(x -> x == null || x.isEmpty())) {
			Location injuryLocation = createInjuryLocation(inputFields, patientReference);
			mainComposition.getCircumstancesSection().addEntry(new Reference("Location/"+injuryLocation.getId()));
			MDIToFhirCMSUtil.addResourceToBatchBundle(returnBundle, injuryLocation);
		}
		//Handle HowDeathInjuryOccured
		Stream<String> injuryIncidentFields = Stream.of(inputFields.CHOWNINJURY, inputFields.ATWORK,
				inputFields.TRANSPORTATION);
		if(!injuryIncidentFields.allMatch(x -> x == null || x.isEmpty())) {
			ObservationHowDeathInjuryOccurred howDeathInjuryOccurred = createObservationHowDeathInjuryOccurred(inputFields, patientResource, practitionerResource);
			mainComposition.getCircumstancesSection().addEntry(new Reference("Observation/"+howDeathInjuryOccurred.getId()));
			MDIToFhirCMSUtil.addResourceToBatchBundle(returnBundle, howDeathInjuryOccurred);
		}
		// Handle Death Date
		Stream<String> deathDateFields = Stream.of(inputFields.PRNDATE, inputFields.PRNTIME,
				inputFields.CDEATHDATE, inputFields.CDEATHTIME);
		if(!deathDateFields.allMatch(x -> x == null || x.isEmpty())) {
			ObservationDeathDate deathDate = createDeathDate(inputFields, patientReference, deathLocation);
			mainComposition.getJurisdictionSection().addEntry(new Reference("Observation/"+deathDate.getId()));
			MDIToFhirCMSUtil.addResourceToBatchBundle(returnBundle, deathDate);
		}
		// Handle Cause Of Death Pathway
		Stream<String> causeOfDeathFields = Stream.of(inputFields.CAUSEA, inputFields.CAUSEB, inputFields.CAUSEC,
				inputFields.CAUSED,inputFields.OSCOND, inputFields.DURATIONA, inputFields.DURATIONB,
				inputFields.DURATIONC, inputFields.DURATIOND);
		if(!causeOfDeathFields.allMatch(x -> x == null || x.isEmpty())) {
			//THis list contains CauseOfDeathPart1 and CauseOfDeathPart2 resources
			List<Observation> causeOfDeathPathway = createCauseOfDeathPathway(inputFields, returnBundle, mainComposition, patientResource, practitionerResource);
			for(Observation cause:causeOfDeathPathway){
				mainComposition.getCauseMannerSection().addEntry(new Reference("Observation/"+cause.getId()));
				MDIToFhirCMSUtil.addResourceToBatchBundle(returnBundle, cause);
			}
		}

		// Handle Manner Of Death
		Stream<String> mannerFields = Stream.of(inputFields.MANNER);
		if(!mannerFields.allMatch(x -> x == null || x.isEmpty())) {
			ObservationMannerOfDeath manner = createMannerOfDeath(inputFields, patientReference, practitionerReference);
			mainComposition.getCauseMannerSection().addEntry(new Reference("Observation/"+manner.getId()));
			MDIToFhirCMSUtil.addResourceToBatchBundle(returnBundle, manner);
		}
		// Handle HowDeathInjuryOccurred
		Stream<String> deathInjuryFields = Stream.of(inputFields.CHOWNINJURY);
		if(!deathInjuryFields.allMatch(x -> x == null || x.isEmpty())) {
			ObservationHowDeathInjuryOccurred deathInjuryDescription = createHowDeathInjuryOccurred(inputFields, patientResource, practitionerResource);
			mainComposition.getCauseMannerSection().addEntry(new Reference("Observation/"+deathInjuryDescription.getId()));
			MDIToFhirCMSUtil.addResourceToBatchBundle(returnBundle, deathInjuryDescription);
		}
		// Handle CaseNotes
		/*if(inputFields.CASENOTES != null && !inputFields.CASENOTES.isEmpty()) {
			for(String caseNote: inputFields.CASENOTES.split(";")) {
				DocumentReferenceMDICaseNotesSummary caseNoteResource = createCaseNote(caseNote,patientResource);
				mainComposition.getNarrativeSection().addEntry(new Reference("DocumentReference/"+caseNoteResource.getId()));
				MDIToFhirCMSUtil.addResourceToBatchBundle(returnBundle, caseNoteResource);
			}
		}*/
		return returnBundle;
	}
	
	private Patient createPatient(MDIModelFields inputFields) throws ParseException {
		Patient returnDecedent = new Patient();
		returnDecedent.setMeta(new Meta().addProfile("http://hl7.org/fhir/us/core/StructureDefinition/us-core-patient"));
		CommonUtil.setUUID(returnDecedent);
		/*Stream<String> caseIdFields = Stream.of(inputFields.SYSTEMID,inputFields.CASEID);
		if(!caseIdFields.allMatch(x -> x == null || x.isEmpty())) {
			Identifier identifier = new Identifier().setSystem(inputFields.SYSTEMID);
			identifier.setValue(inputFields.CASEID);
			identifier.setType(new CodeableConcept().addCoding(new Coding().setCode("1000007").setSystem("urn:mdi:temporary:code").setDisplay("Case Number")));
			returnDecedent.addIdentifier(identifier);
		}*/
		Stream<String> nameFields = Stream.of(inputFields.FIRSTNAME,inputFields.LASTNAME,inputFields.MIDNAME);
		if(!nameFields.allMatch(x -> x == null || x.isEmpty())) {
			HumanName name = new HumanName();
			name.addGiven(inputFields.FIRSTNAME);
			name.setFamily(inputFields.LASTNAME);
			name.addGiven(inputFields.MIDNAME);
			name.setUse(NameUse.OFFICIAL);
			returnDecedent.addName(name);
		}
		if(inputFields.RACE != null && !inputFields.RACE.isEmpty()) {
			if(MDIToFhirCMSUtil.containsIgnoreCase(inputFields.RACE, "White")) {
				addRace(returnDecedent, "2106-3", "", inputFields.RACE);
			}
			else if(MDIToFhirCMSUtil.containsIgnoreCase(inputFields.RACE, "Hawaiian") || MDIToFhirCMSUtil.containsIgnoreCase(inputFields.RACE, "Pacific")) {
				addRace(returnDecedent, "2076-8", "", inputFields.RACE);
			}
			else if(MDIToFhirCMSUtil.containsIgnoreCase(inputFields.RACE, "Asian")) {
				addRace(returnDecedent, "2028-9", "", inputFields.RACE);
			}
			else if(MDIToFhirCMSUtil.containsIgnoreCase(inputFields.RACE, "Indian") || MDIToFhirCMSUtil.containsIgnoreCase(inputFields.RACE, "Native")) {
				addRace(returnDecedent, "1002-5", "", inputFields.RACE);
			}
			else if(MDIToFhirCMSUtil.containsIgnoreCase(inputFields.RACE, "Black")) {
				addRace(returnDecedent, "2054-5", "", inputFields.RACE);
			}
		}
		if(inputFields.ETHNICITY != null && !inputFields.ETHNICITY.isEmpty()) {
			if(MDIToFhirCMSUtil.containsIgnoreCase(inputFields.ETHNICITY, "Not Hispanic") || MDIToFhirCMSUtil.containsIgnoreCase(inputFields.ETHNICITY, "Malaysian")) {
				addEthnicity(returnDecedent, "2186-5", "", inputFields.ETHNICITY);
			}
			else if(MDIToFhirCMSUtil.containsIgnoreCase(inputFields.ETHNICITY, "Hispanic") || MDIToFhirCMSUtil.containsIgnoreCase(inputFields.ETHNICITY, "Salvadoran") ||MDIToFhirCMSUtil.containsIgnoreCase(inputFields.ETHNICITY, "Latino")) {
				addEthnicity(returnDecedent, "2135-2", "", inputFields.ETHNICITY);
			}
			else if(MDIToFhirCMSUtil.containsIgnoreCase(inputFields.ETHNICITY, "Cuban")) {
				addEthnicity(returnDecedent, "2135-2", "2182-4", inputFields.ETHNICITY);
			}
			else if(MDIToFhirCMSUtil.containsIgnoreCase(inputFields.ETHNICITY, "Puerto Rican")) {
				addEthnicity(returnDecedent, "2135-2", "2180-8", inputFields.ETHNICITY);
			}
			else if(MDIToFhirCMSUtil.containsIgnoreCase(inputFields.ETHNICITY, "Salvadoran")) {
				addEthnicity(returnDecedent, "2161-8", "2180-8", inputFields.ETHNICITY);
			}
		}
		if(inputFields.GENDER != null && !inputFields.GENDER.isEmpty()) {
			if(MDIToFhirCMSUtil.containsIgnoreCase(inputFields.GENDER, "Female")) {
				returnDecedent.setGender(AdministrativeGender.FEMALE);
			}
			else if(MDIToFhirCMSUtil.containsIgnoreCase(inputFields.GENDER, "Male")) {
				returnDecedent.setGender(AdministrativeGender.MALE);
			}
			else{
				returnDecedent.setGender(AdministrativeGender.UNKNOWN);
			}
		}
		if(inputFields.BIRTHDATE != null && !inputFields.BIRTHDATE.isEmpty()) {
			Date birthDate = MDIToFhirCMSUtil.parseDate(inputFields.BIRTHDATE);
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
			if(MDIToFhirCMSUtil.containsIgnoreCase(inputFields.MARITAL, "Annul")) {
				maritalCoding.setCode("A");
				maritalCoding.setDisplay("Annulled");
			}
			else if(MDIToFhirCMSUtil.containsIgnoreCase(inputFields.MARITAL, "Divorce")) {
				maritalCoding.setCode("D");
				maritalCoding.setDisplay("Divorced");
			}
			else if(MDIToFhirCMSUtil.containsIgnoreCase(inputFields.MARITAL, "Interlocutory")) {
				maritalCoding.setCode("I");
				maritalCoding.setDisplay("Interlocutory");
			}
			else if(MDIToFhirCMSUtil.containsIgnoreCase(inputFields.MARITAL, "Polygamous")) {
				maritalCoding.setCode("P");
				maritalCoding.setDisplay("Polygamous");
			}
			else if(MDIToFhirCMSUtil.containsIgnoreCase(inputFields.MARITAL, "Never Married") || MDIToFhirCMSUtil.containsIgnoreCase(inputFields.MARITAL, "No")) {
				maritalCoding.setCode("U");
				maritalCoding.setDisplay("unmarried");
			}
			else if(MDIToFhirCMSUtil.containsIgnoreCase(inputFields.MARITAL, "Domestic Partner")) {
				maritalCoding.setCode("T");
				maritalCoding.setDisplay("Domestic partner");
			}
			else if(MDIToFhirCMSUtil.containsIgnoreCase(inputFields.MARITAL, "Widow")) {
				maritalCoding.setCode("W");
				maritalCoding.setDisplay("Widowed");
			}
			else if(MDIToFhirCMSUtil.containsIgnoreCase(inputFields.MARITAL, "Married")) {
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
		Address residentAddress = MDIToFhirCMSUtil.createAddress("", inputFields.RESSTREET,
				inputFields.RESCITY, inputFields.RESCOUNTY, inputFields.RESSTATE, inputFields.RESZIP);
		if(inputFields.RESNAME != null && !inputFields.RESNAME.isEmpty()) {
			Extension resNameExt = new Extension();
			resNameExt.setUrl("urn:oid:2.16.840.1.113883.11.20.9.49");
			Extension resNameTextExt = new Extension();
			resNameTextExt.setUrl("Text");
			resNameTextExt.setValue(new StringType(inputFields.RESNAME));
			resNameExt.addExtension(resNameTextExt);
			residentAddress.addExtension(resNameTextExt);
		}
		residentAddress.setUse(AddressUse.HOME);
		returnDecedent.addAddress(residentAddress);
		if(inputFields.LKAWHERE != null && !inputFields.LKAWHERE.isEmpty()) {
			Extension lkaExt = new Extension();
			lkaExt.setUrl("urn:mdi:temporary:code:last-known-to-be-alive-or-okay-place");
			lkaExt.setValue(new StringType(inputFields.LKAWHERE));
			returnDecedent.addExtension(lkaExt);
		}
		if(inputFields.HOSPNAME != null && !inputFields.HOSPNAME.isEmpty()) {
			Extension hospExt = new Extension();
			hospExt.setUrl("urn:mdi:temporary:code:hospital-name-decedent-was-first-taken");
			hospExt.setValue(new StringType(inputFields.HOSPNAME));
			returnDecedent.addExtension(hospExt);
		}
		return returnDecedent;
	}
	
	private Practitioner createPractitioner(MDIModelFields inputFields) {
		Practitioner returnPractitioner = new Practitioner();
		returnPractitioner.setMeta(new Meta().addProfile("http://hl7.org/fhir/us/core/StructureDefinition/us-core-practitioner"));
		CommonUtil.setUUID(returnPractitioner);
		if(inputFields.MENAME != null && !inputFields.MENAME.isEmpty()) {
			HumanName certName = MDIToFhirCMSUtil.parseHumanName(inputFields.MENAME);
			if (certName != null){
				certName.setUse(NameUse.OFFICIAL);
				returnPractitioner.addName(certName);
			}
		}
		if(inputFields.MELICENSE != null && !inputFields.MELICENSE.isEmpty()) {
			Identifier melicenseIdentifier = new Identifier();
			melicenseIdentifier.setValue(inputFields.MELICENSE);
			returnPractitioner.addIdentifier(melicenseIdentifier);
		}
		if(inputFields.MEPHONE != null && !inputFields.MEPHONE.isEmpty()){
			returnPractitioner.addTelecom(new ContactPoint().setUse(ContactPointUse.WORK).setValue(inputFields.MEPHONE));
		}
		Stream<String> meAddrFields = Stream.of(inputFields.ME_STREET, inputFields.ME_CITY,
				inputFields.ME_COUNTY, inputFields.ME_STATE, inputFields.ME_ZIP);
		if(!meAddrFields.allMatch(x -> x == null || x.isEmpty())) {
			Address meAddr = MDIToFhirCMSUtil.createAddress("", inputFields.ME_STREET, inputFields.ME_CITY,
					inputFields.ME_COUNTY, inputFields.ME_STATE, inputFields.ME_ZIP);
			returnPractitioner.addAddress(meAddr);
		}
		return returnPractitioner;
	}

	private Practitioner createCertifier(MDIModelFields inputFields) {
		Practitioner returnCertifier = new Practitioner();
		returnCertifier.setMeta(new Meta().addProfile("http://hl7.org/fhir/us/core/StructureDefinition/us-core-practitioner"));
		CommonUtil.setUUID(returnCertifier);
		if(inputFields.CERTIFIER_NAME != null && !inputFields.CERTIFIER_NAME.isEmpty()) {
			HumanName certName = MDIToFhirCMSUtil.parseHumanName(inputFields.CERTIFIER_NAME);
			returnCertifier.addName(certName);
		}
		if(inputFields.CERTIFIER_TYPE != null && !inputFields.CERTIFIER_TYPE.isEmpty()) {
			//TODO: Add certifier qualification component that makes sense here
		}
		return returnCertifier;
	}

	private ProcedureDeathCertification createDeathCertification(MDIModelFields inputFields, Reference decedentReference, Reference certifierReference) {
		ProcedureDeathCertification returnCertification = new ProcedureDeathCertification(decedentReference, certifierReference, inputFields.CERTIFIER_TYPE);
		CommonUtil.setUUID(returnCertification);
		return returnCertification;
	}
	
	private ObservationHowDeathInjuryOccurred createObservationHowDeathInjuryOccurred(MDIModelFields inputFields, Patient decedent, Practitioner practitioner) throws ParseException {
		ObservationHowDeathInjuryOccurred returnObservation = new ObservationHowDeathInjuryOccurred(decedent, practitioner, inputFields.CHOWNINJURY, "", inputFields.ATWORK, inputFields.TRANSPORTATION);
		CommonUtil.setUUID(returnObservation);
		return returnObservation;
	}
	
	private ObservationTobaccoUseContributedToDeath createObservationTobaccoUseContributedToDeath(MDIModelFields inputFields, Reference decedentReference) throws ParseException {
		ObservationTobaccoUseContributedToDeath tobacco = new ObservationTobaccoUseContributedToDeath();
		tobacco.setStatus(ObservationStatus.PRELIMINARY);
		CommonUtil.setUUID(tobacco);
		tobacco.setSubject(decedentReference);
		tobacco.setValue(inputFields.TOBACCO);
		return tobacco;
	}
	
	private ObservationDecedentPregnancy createObservationDecedentPregnancy(MDIModelFields inputFields, Reference decedentReference) throws ParseException {
		ObservationDecedentPregnancy pregnant = new ObservationDecedentPregnancy();
		pregnant.setStatus(ObservationStatus.PRELIMINARY);
		CommonUtil.setUUID(pregnant);
		pregnant.setSubject(decedentReference);
		pregnant.setValue(inputFields.PREGNANT);
		return pregnant;
	}

	private Location createInjuryLocation(MDIModelFields inputFields, Reference decedentReference){
		Location injuryLocation = new Location();
    	CommonUtil.setUUID(injuryLocation);
    	injuryLocation.getMeta().addProfile("http://hl7.org/fhir/us/core/StructureDefinition/us-core-location");
    	injuryLocation.setName(inputFields.INJURYLOCATION);
    	injuryLocation.setAddress(new Address().setText(inputFields.INJURYLOCATION));
		return injuryLocation;
	}
	
	private List<Observation> createCauseOfDeathPathway(MDIModelFields inputFields, Bundle bundle, CompositionMDIToEDRS mainComposition, Patient patientResource, Practitioner practitionerResource) {
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
				CommonUtil.setUUID(causeOfDeathCondition);
				returnList.add(causeOfDeathCondition);
			}
		}
		String[] otherCauses = inputFields.OSCOND.split("[;\n]");
		List<String> listArrayOtherCauses = Arrays.asList(otherCauses);
		for(String otherCause:listArrayOtherCauses) {
			if(otherCause.isEmpty()) {
				continue;
			}
			ObservationContributingCauseOfDeathPart2 conditionContrib = new ObservationContributingCauseOfDeathPart2(patientResource, practitionerResource, otherCause);
			CommonUtil.setUUID(conditionContrib);
			returnList.add(conditionContrib);
		}
		return returnList;
	}
	
	private ObservationMannerOfDeath createMannerOfDeath(MDIModelFields inputFields, Reference decedentReference, Reference practitionerReference) {
		ObservationMannerOfDeath manner = new ObservationMannerOfDeath();
		CommonUtil.setUUID(manner);
		manner.addPerformer(practitionerReference);
		Coding mannerCoding = new Coding();
		if(inputFields.MANNER != null && !inputFields.MANNER.isEmpty()) {
			mannerCoding.setSystem("http://snomed.info/sct");
			if(MDIToFhirCMSUtil.containsIgnoreCase(inputFields.MANNER, "Homicide")) {
				mannerCoding.setCode("27935005");
				mannerCoding.setDisplay("Homicide");
			}
			else if(MDIToFhirCMSUtil.containsIgnoreCase(inputFields.MANNER, "Suicide")) {
				mannerCoding.setCode("44301001");
				mannerCoding.setDisplay("Suicide");
			}
			else if(MDIToFhirCMSUtil.containsIgnoreCase(inputFields.MANNER, "Accident")) {
				mannerCoding.setCode("7878000");
				mannerCoding.setDisplay("Accidental Death");
			}
			else if(MDIToFhirCMSUtil.containsIgnoreCase(inputFields.MANNER, "Natural")) {
				mannerCoding.setCode("38605008");
				mannerCoding.setDisplay("Natural");
			}
			else if(MDIToFhirCMSUtil.containsIgnoreCase(inputFields.MANNER, "Investigation")) {
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
	
	private ObservationHowDeathInjuryOccurred createHowDeathInjuryOccurred(MDIModelFields inputFields, Patient patientResource, Practitioner practitionerResource) {
		ObservationHowDeathInjuryOccurred injuryDescription = new ObservationHowDeathInjuryOccurred(patientResource, practitionerResource, inputFields.CHOWNINJURY);
		CommonUtil.setUUID(injuryDescription);
		return injuryDescription;
	}

	/*private DocumentReferenceMDICaseNotesSummary createCaseNote(String caseNoteString, Patient patient) {
		DocumentReferenceMDICaseNotesSummary caseNote = new DocumentReferenceMDICaseNotesSummary(patient);
		CommonUtil.setUUID(caseNote);
		caseNote.setDate(new Date());
		DocumentReferenceContentComponent contentComponent = new DocumentReferenceContentComponent();
		Attachment attachment = new Attachment();
		attachment.setContentType("text/plain");
		attachment.setLanguage("en-US");
		attachment.setData(caseNoteString.getBytes());
		contentComponent.setAttachment(attachment);
		caseNote.addContent(contentComponent);
		return caseNote;
	}*/
	
	private Observation createFoundObs(MDIModelFields inputFields, Reference decedentReference) throws ParseException {
		Observation foundObs = new Observation();
		CommonUtil.setUUID(foundObs);
		foundObs.setStatus(ObservationStatus.FINAL);
		foundObs.setSubject(decedentReference);
		foundObs.setCode(new CodeableConcept().addCoding(new Coding(
				"urn:mdi:temporary:code", "1000001", "Date and Time found dead, unconcious and in distress")));
		if(inputFields.FOUNDDATE != null && !inputFields.FOUNDDATE.isEmpty()) {
			Date reportDate = MDIToFhirCMSUtil.parseDate(inputFields.FOUNDDATE);
			if(inputFields.FOUNDTIME != null && !inputFields.FOUNDTIME.isEmpty()) {
				MDIToFhirCMSUtil.addTimeToDate(reportDate, inputFields.FOUNDTIME);
			}
			foundObs.setValue(new DateTimeType(reportDate));
		}
		return foundObs;
	}
	
	private ObservationDeathDate createDeathDate(MDIModelFields inputFields, Reference decedentReference, Location location) throws ParseException {
		ObservationDeathDate returnDeathDate = new ObservationDeathDate();
		CommonUtil.setUUID(returnDeathDate);
		returnDeathDate.setSubject(decedentReference);
		if(inputFields.CDEATHDATE != null && !inputFields.CDEATHDATE.isEmpty()) {
			Date certDate = MDIToFhirCMSUtil.parseDate(inputFields.CDEATHDATE);
			if(inputFields.CDEATHTIME != null && !inputFields.CDEATHTIME.isEmpty()) {
				MDIToFhirCMSUtil.addTimeToDate(certDate, inputFields.CDEATHTIME);
			}
			DateTimeType certValueDT = new DateTimeType(certDate);
			returnDeathDate.setEffective(new DateTimeType(new Date()));
			returnDeathDate.setValue(certValueDT);
		}
		if(inputFields.PRNDATE != null && !inputFields.PRNDATE.isEmpty()) {
			Date prnDate = MDIToFhirCMSUtil.parseDate(inputFields.PRNDATE);
			if(inputFields.PRNTIME != null && !inputFields.PRNTIME.isEmpty()) {
				MDIToFhirCMSUtil.addTimeToDate(prnDate, inputFields.PRNTIME);
			}
			DateTimeType prnDT = new DateTimeType(prnDate);
			returnDeathDate.addDatePronouncedDead(prnDT);
		}
		return returnDeathDate;
	}
	
	private Location createDeathLocation(MDIModelFields inputFields) {
		Location returnDeathLocation = new Location();
		returnDeathLocation.setMeta(new Meta().addProfile("http://hl7.org/fhir/us/core/StructureDefinition/us-core-location"));
		CommonUtil.setUUID(returnDeathLocation);
		returnDeathLocation.setAddress(new Address().setText(inputFields.DEATHLOCATION));
		return returnDeathLocation;
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