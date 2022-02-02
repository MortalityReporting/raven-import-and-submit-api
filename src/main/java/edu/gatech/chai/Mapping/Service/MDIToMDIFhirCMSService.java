package edu.gatech.chai.Mapping.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Address.AddressUse;
import org.hl7.fhir.r4.model.Age;
import org.hl7.fhir.r4.model.Attachment;
import org.hl7.fhir.r4.model.BooleanType;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleType;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Composition.CompositionAttestationMode;
import org.hl7.fhir.r4.model.Composition.CompositionAttesterComponent;
import org.hl7.fhir.r4.model.Composition.CompositionStatus;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.DocumentReference;
import org.hl7.fhir.r4.model.DocumentReference.DocumentReferenceContentComponent;
import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.r4.model.HumanName.NameUse;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.ListResource.ListEntryComponent;
import org.hl7.fhir.r4.model.Location;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Observation.ObservationComponentComponent;
import org.hl7.fhir.r4.model.Observation.ObservationStatus;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.Procedure.ProcedureStatus;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.StringType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.uhn.fhir.parser.IParser;
import edu.gatech.chai.VRDR.context.VRDRFhirContext;
import edu.gatech.chai.VRDR.model.AutopsyPerformedIndicator;
import edu.gatech.chai.VRDR.model.CauseOfDeathCondition;
import edu.gatech.chai.VRDR.model.CauseOfDeathPathway;
import edu.gatech.chai.VRDR.model.Certifier;
import edu.gatech.chai.VRDR.model.ConditionContributingToDeath;
import edu.gatech.chai.VRDR.model.DeathCertificate;
import edu.gatech.chai.VRDR.model.DeathDate;
import edu.gatech.chai.VRDR.model.DeathLocation;
import edu.gatech.chai.VRDR.model.Decedent;
import edu.gatech.chai.VRDR.model.DecedentAge;
import edu.gatech.chai.VRDR.model.DecedentDispositionMethod;
import edu.gatech.chai.VRDR.model.DecedentUsualWork;
import edu.gatech.chai.VRDR.model.DispositionLocation;
import edu.gatech.chai.VRDR.model.ExaminerContacted;
import edu.gatech.chai.VRDR.model.InjuryIncident;
import edu.gatech.chai.VRDR.model.InjuryLocation;
import edu.gatech.chai.VRDR.model.MannerOfDeath;
import edu.gatech.chai.VRDR.model.util.CommonUtil;
import edu.gatech.chai.VRDR.model.util.DecedentUtil;
import edu.gatech.chai.VRDR.model.util.InjuryIncidentUtil;
import edu.gatech.chai.MDI.Model.MDIModelFields;
import edu.gatech.chai.MDI.context.MDIFhirContext;
import edu.gatech.chai.MDI.model.resource.CompositionMDIToEDRS;
import edu.gatech.chai.MDI.model.resource.ConditionCauseOfDeath;
import edu.gatech.chai.MDI.model.resource.ConditionOtherContributingToDeath;
import edu.gatech.chai.MDI.model.resource.DocumentReferenceMDICaseHistory;
import edu.gatech.chai.MDI.model.resource.DocumentReferenceMDICaseNotesSummary;
import edu.gatech.chai.MDI.model.resource.ListCauseOfDeathPathway;
import edu.gatech.chai.MDI.model.resource.ODHUsualWork;
import edu.gatech.chai.MDI.model.resource.ObservationDeathDate;
import edu.gatech.chai.MDI.model.resource.ObservationDeathInjuryAtWork;
import edu.gatech.chai.MDI.model.resource.ObservationDecedentPregnancy;
import edu.gatech.chai.MDI.model.resource.ObservationHowDeathInjuryOccurred;
import edu.gatech.chai.MDI.model.resource.ObservationMannerOfDeath;
import edu.gatech.chai.MDI.model.resource.ObservationTobaccoUseContributedToDeath;
import edu.gatech.chai.Mapping.Util.CommonMappingUtil;
import edu.gatech.chai.Mapping.Util.MDIToFhirCMSUtil;

@Service
public class MDIToMDIFhirCMSService {
	
	@Autowired
	private MDIFhirContext mdiFhirContext;
	
	public String convertToMDIString(MDIModelFields inputFields) throws ParseException {
		Bundle fullBundle = convertToMDI(inputFields);
		IParser parser = mdiFhirContext.getCtx().newJsonParser();
		String returnString = parser.encodeResourceToString(fullBundle);
		return returnString;
	}
	
	public Bundle convertToMDI(MDIModelFields inputFields) throws ParseException {
		Bundle returnBundle = new Bundle();
		returnBundle.setType(BundleType.BATCH);
		Identifier caseIdentifier = new Identifier().setSystem(inputFields.SYSTEMID);
		caseIdentifier.setValue(inputFields.CASEID);
		caseIdentifier.setType(new CodeableConcept().addCoding(new Coding().setCode("1000007").setSystem("urn:mdi:temporary:code").setDisplay("Case Number")));
		Patient patientResource = null;
		Reference patientReference = null;
		Practitioner practitionerResource = null;
		Reference practitionerReference = null;
		Reference dispositionLocationReference = null;
		// Handle Patient/Decedent
		Stream<String> decedentFields = Stream.of(inputFields.FIRSTNAME,inputFields.MIDNAME,inputFields.LASTNAME
				,inputFields.AGE,inputFields.AGEUNIT,inputFields.RACE,inputFields.GENDER
				,inputFields.ETHNICITY,inputFields.BIRTHDATE,inputFields.MRNNUMBER
				,inputFields.MARITAL,inputFields.POSSIBLEID,inputFields.RESSTREET
				,inputFields.RESCITY,inputFields.RESCOUNTY,inputFields.RESSTATE
				,inputFields.RESZIP,inputFields.RESNAME,inputFields.LKAWHERE,inputFields.HOSPNAME
				,inputFields.CASEID);
		if(!decedentFields.allMatch(x -> x == null || x.isEmpty())) {
			patientResource = createPatient(inputFields);
			patientReference = new Reference(patientResource.getId());
		}
		// Handle Practitoner/Certifier
		Stream<String> certifierFields = Stream.of(inputFields.CERTIFIER_NAME,inputFields.CERTIFIER_TYPE);
		if(!certifierFields.allMatch(x -> x == null || x.isEmpty())) {
			practitionerResource = createPractitioner(inputFields);
			practitionerReference = new Reference(practitionerResource.getId());
		}
		//Create Main Composition Section
		CompositionMDIToEDRS mainComposition = new CompositionMDIToEDRS(caseIdentifier, CompositionStatus.PRELIMINARY, new Date(), patientResource, practitionerResource);
		mainComposition.addMDICaseIdExtension(inputFields.CASEID);
		MDIToFhirCMSUtil.addResourceToBatchBundle(returnBundle, mainComposition);
		MDIToFhirCMSUtil.addResourceToBatchBundle(returnBundle, patientResource);
		MDIToFhirCMSUtil.addResourceToBatchBundle(returnBundle, practitionerResource);
		// Handle Usual Work/Employment History
		Stream<String> employmentHistoryFields = Stream.of(inputFields.JOBTITLE, inputFields.INDUSTRY);
		ODHUsualWork decedentUsualWork = null;
		if(!employmentHistoryFields.allMatch(x -> x == null || x.isEmpty())) {
			decedentUsualWork = createDecedentUsualWork(inputFields, patientReference);
			mainComposition.getDemographicsSection().addEntry(new Reference(decedentUsualWork.getId()));
			MDIToFhirCMSUtil.addResourceToBatchBundle(returnBundle, decedentUsualWork);
		}
		// Handle Death Location
		Location deathLocation = null;
		Stream<String> deathLocFields = Stream.of(inputFields.DEATHSTREET, inputFields.DEATHCITY,
				inputFields.DEATHCOUNTY, inputFields.DEATHSTATE, inputFields.DEATHZIP);
		if(!deathLocFields.allMatch(x -> x == null || x.isEmpty())) {
			deathLocation = createDeathLocation(inputFields);
			mainComposition.getCircumstancesSection().addEntry(new Reference(deathLocation.getId()));
			MDIToFhirCMSUtil.addResourceToBatchBundle(returnBundle, deathLocation);
		}
		// Handle DeathInjuryAtWork/InjuryIncident
		Stream<String> injuryIncidentFields = Stream.of(inputFields.CHOWNINJURY, inputFields.ATWORK,
				inputFields.JOBRELATED, inputFields.CINJDATE, inputFields.CINJTIME, inputFields.CIDATEFLAG,
				inputFields.CUSTODY);
		if(!injuryIncidentFields.allMatch(x -> x == null || x.isEmpty())) {
			ObservationDeathInjuryAtWork injuryIncident = createObservationHowDeathInjuryOccurred(inputFields, patientReference);
			mainComposition.getCircumstancesSection().addEntry(new Reference(injuryIncident.getId()));
			MDIToFhirCMSUtil.addResourceToBatchBundle(returnBundle, injuryIncident);
		}
		//Handle TobaccoUseContributedToDeath
		Stream<String> tobaccoFields = Stream.of(inputFields.TOBACCO);
		if(!tobaccoFields.allMatch(x -> x == null || x.isEmpty())) {
			ObservationTobaccoUseContributedToDeath tobacco = createObservationTobaccoUseContributedToDeath(inputFields, patientReference);
			mainComposition.getCircumstancesSection().addEntry(new Reference(tobacco.getId()));
			MDIToFhirCMSUtil.addResourceToBatchBundle(returnBundle, tobacco);
		}
		//Handle Decedent Pregnancy
		Stream<String> pregnantFields = Stream.of(inputFields.PREGNANT);
		if(!pregnantFields.allMatch(x -> x == null || x.isEmpty())) {
			ObservationDecedentPregnancy pregnant = createObservationDecedentPregnancy(inputFields, patientReference);
			mainComposition.getCircumstancesSection().addEntry(new Reference(pregnant.getId()));
			MDIToFhirCMSUtil.addResourceToBatchBundle(returnBundle, pregnant);
		}
		// Handle Death Date
		Stream<String> deathDateFields = Stream.of(inputFields.PRNDATE, inputFields.PRNTIME, inputFields.CDEATHFLAG,
				inputFields.CDEATHDATE, inputFields.CDEATHTIME);
		if(!deathDateFields.allMatch(x -> x == null || x.isEmpty())) {
			ObservationDeathDate deathDate = createDeathDate(inputFields, patientReference, deathLocation);
			mainComposition.getJurisdictionSection().addEntry(new Reference(deathDate.getId()));
			MDIToFhirCMSUtil.addResourceToBatchBundle(returnBundle, deathDate);
		}
		// Handle Cause Of Death Pathway
		Stream<String> causeOfDeathFields = Stream.of(inputFields.CAUSEA, inputFields.CAUSEB, inputFields.CAUSEC,
				inputFields.CAUSED,inputFields.OSCOND, inputFields.DURATIONA, inputFields.DURATIONB,
				inputFields.DURATIONC, inputFields.DURATIOND);
		if(!causeOfDeathFields.allMatch(x -> x == null || x.isEmpty())) {
			ListCauseOfDeathPathway causeOfDeathPathway = createCauseOfDeathPathway(inputFields, returnBundle, mainComposition, patientReference, practitionerReference);
			mainComposition.getCauseMannerSection().addEntry(new Reference(causeOfDeathPathway.getId()));
			MDIToFhirCMSUtil.addResourceToBatchBundle(returnBundle, causeOfDeathPathway);
		}
		// Handle Manner Of Death
		Stream<String> mannerFields = Stream.of(inputFields.MANNER);
		if(!mannerFields.allMatch(x -> x == null || x.isEmpty())) {
			ObservationMannerOfDeath manner = createMannerOfDeath(inputFields, patientReference, practitionerReference);
			mainComposition.getCauseMannerSection().addEntry(new Reference(manner.getId()));
			MDIToFhirCMSUtil.addResourceToBatchBundle(returnBundle, manner);
		}
		// Handle HowDeathInjuryOccurred
		Stream<String> deathInjuryFields = Stream.of(inputFields.CHOWNINJURY);
		if(!deathInjuryFields.allMatch(x -> x == null || x.isEmpty())) {
			ObservationHowDeathInjuryOccurred deathInjuryDescription = createHowDeathInjuryOccurred(inputFields, patientResource, practitionerResource);
			mainComposition.getCauseMannerSection().addEntry(new Reference(deathInjuryDescription.getId()));
			MDIToFhirCMSUtil.addResourceToBatchBundle(returnBundle, deathInjuryDescription);
		}
		// Handle CaseNotes
		if(inputFields.CASENOTES != null && !inputFields.CASENOTES.isEmpty()) {
			for(String caseNote: inputFields.CASENOTES.split(";")) {
				DocumentReferenceMDICaseNotesSummary caseNoteResource = createCaseNote(caseNote,patientResource);
				mainComposition.getNarrativeSection().addEntry(new Reference(caseNoteResource.getId()));
				MDIToFhirCMSUtil.addResourceToBatchBundle(returnBundle, caseNoteResource);
			}
		}
		return returnBundle;
	}
	
	private Patient createPatient(MDIModelFields inputFields) throws ParseException {
		Patient returnDecedent = new Patient();
		returnDecedent.setMeta(new Meta().addProfile("http://hl7.org/fhir/us/core/StructureDefinition/us-core-patient"));
		CommonUtil.setUUID(returnDecedent);
		Stream<String> caseIdFields = Stream.of(inputFields.SYSTEMID,inputFields.CASEID);
		if(!caseIdFields.allMatch(x -> x == null || x.isEmpty())) {
			Identifier identifier = new Identifier().setSystem(inputFields.SYSTEMID);
			identifier.setValue(inputFields.CASEID);
			identifier.setType(new CodeableConcept().addCoding(new Coding().setCode("1000007").setSystem("urn:mdi:temporary:code").setDisplay("Case Number")));
			returnDecedent.addIdentifier(identifier);
		}
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
		if(inputFields.CASEID != null && !inputFields.CASEID.isEmpty()) {
			Identifier caseId = new Identifier();
			caseId.setSystem("urn:mdi:temporary:code");
			caseId.setValueElement(new StringType(inputFields.CASEID));
		}
		return returnDecedent;
	}
	
	private ODHUsualWork createDecedentUsualWork(MDIModelFields inputFields, Reference decedentReference) {
		ODHUsualWork returnEmploymentHistory = new ODHUsualWork();
		CommonUtil.setUUID(returnEmploymentHistory);
		returnEmploymentHistory.setSubject(decedentReference);
		if(inputFields.JOBTITLE != null && !inputFields.JOBTITLE.isEmpty()) {
			CodeableConcept usualOccupation = new CodeableConcept();
			usualOccupation.setText(inputFields.JOBTITLE);
			returnEmploymentHistory.setValue(usualOccupation);
		}
		if(inputFields.INDUSTRY != null && !inputFields.INDUSTRY.isEmpty()) {
			CodeableConcept usualIndustry = new CodeableConcept();
			usualIndustry.setText(inputFields.INDUSTRY);
			returnEmploymentHistory.addUsualIndustry(usualIndustry);
		}
		return returnEmploymentHistory;
	}
	
	private Practitioner createPractitioner(MDIModelFields inputFields) {
		Practitioner returnPractitioner = new Practitioner();
		returnPractitioner.setMeta(new Meta().addProfile("http://hl7.org/fhir/us/core/StructureDefinition/us-core-practitioner"));
		CommonUtil.setUUID(returnPractitioner);
		if(inputFields.CERTIFIER_NAME != null && !inputFields.CERTIFIER_NAME.isEmpty()) {
			HumanName certName = MDIToFhirCMSUtil.parseHumanName(inputFields.CERTIFIER_NAME);
			returnPractitioner.addName(certName);
		}
		if(inputFields.CERTIFIER_TYPE != null && !inputFields.CERTIFIER_TYPE.isEmpty()) {
			//TODO: Add certifier qualification component that makes sense here
		}
		return returnPractitioner;
	}
	
	private ObservationDeathInjuryAtWork createObservationHowDeathInjuryOccurred(MDIModelFields inputFields, Reference decedentReference) throws ParseException {
		ObservationDeathInjuryAtWork returnIncident = new ObservationDeathInjuryAtWork();
		CommonUtil.setUUID(returnIncident);
		returnIncident.setSubject(decedentReference);
		if(inputFields.ATWORK !=  null && !inputFields.ATWORK.isEmpty()) {
			returnIncident.setValue(CommonUtil.findConceptFromCollectionUsingSimpleString(inputFields.ATWORK, CommonUtil.yesNoNASet));
			if(inputFields.JOBRELATED !=  null && !inputFields.JOBRELATED.isEmpty()) {
				//Search for the atWork component we just made
				for (ObservationComponentComponent component: returnIncident.getComponent()) {
					if(component.getCode().equalsShallow(InjuryIncidentUtil.componentInjuryAtWorkCode)) {
						Extension jobRelatedExtension = new Extension();
						jobRelatedExtension.setUrl("urn:mdi:temporary:code:constitute-osha-injury-at-work");
						BooleanType value = new BooleanType(CommonMappingUtil.parseBoolean(inputFields.JOBRELATED));
						jobRelatedExtension.setValue(value);
						component.addModifierExtension(jobRelatedExtension);
					}
				}
			}
		}
		if(inputFields.CINJDATE != null && !inputFields.CINJDATE.isEmpty()) {
			Date reportDate = MDIToFhirCMSUtil.parseDate(inputFields.CINJDATE);
			if(inputFields.CINJTIME != null && !inputFields.CINJTIME.isEmpty()) {
				MDIToFhirCMSUtil.addTimeToDate(reportDate, inputFields.CINJTIME);
			}
			returnIncident.setEffective(new DateTimeType(reportDate));
		}
		if(inputFields.CIDATEFLAG != null && !inputFields.CIDATEFLAG.isEmpty()) {
			Extension qualificationExtension = new Extension();
			qualificationExtension.setUrl("urn:mdi:temporary:code:qualification-of-injury-date");
			qualificationExtension.setValue(new StringType(inputFields.CIDATEFLAG));
			returnIncident.addExtension(qualificationExtension);
		}
		if(inputFields.CUSTODY !=  null && !inputFields.CUSTODY.isEmpty()) {
			ObservationComponentComponent custodyComp = new ObservationComponentComponent();
			custodyComp.setCode(new CodeableConcept().addCoding(new Coding("urn:mdi:temporary:code","100002","Death in custody")));
			custodyComp.setValue(new BooleanType(CommonMappingUtil.parseBoolean(inputFields.CUSTODY)));
			returnIncident.addComponent(custodyComp);
		}
		return returnIncident;
	}
	
	private ObservationTobaccoUseContributedToDeath createObservationTobaccoUseContributedToDeath(MDIModelFields inputFields, Reference decedentReference) throws ParseException {
		ObservationTobaccoUseContributedToDeath tobacco = new ObservationTobaccoUseContributedToDeath();
		CommonUtil.setUUID(tobacco);
		tobacco.setSubject(decedentReference);
		tobacco.setValue(inputFields.TOBACCO);
		return tobacco;
	}
	
	private ObservationDecedentPregnancy createObservationDecedentPregnancy(MDIModelFields inputFields, Reference decedentReference) throws ParseException {
		ObservationDecedentPregnancy pregnant = new ObservationDecedentPregnancy();
		CommonUtil.setUUID(pregnant);
		pregnant.setSubject(decedentReference);
		pregnant.setValue(inputFields.PREGNANT);
		return pregnant;
	}
	
	private ListCauseOfDeathPathway createCauseOfDeathPathway(MDIModelFields inputFields, Bundle bundle, CompositionMDIToEDRS mainComposition, Reference patientReference, Reference practitionerReference) {
		ListCauseOfDeathPathway returnCoDPathway = new ListCauseOfDeathPathway();
		CommonUtil.setUUID(returnCoDPathway);
		returnCoDPathway.setSubject(patientReference);
		returnCoDPathway.setSource(practitionerReference);
		List<String> causes = new ArrayList<String>(Arrays.asList(inputFields.CAUSEA,inputFields.CAUSEB,inputFields.CAUSEC,inputFields.CAUSED));
		List<String> durations = Arrays.asList(inputFields.DURATIONA,inputFields.DURATIONB,inputFields.DURATIONC,inputFields.DURATIOND);
		//Create and add Causes to the Bundle BEFORE the CauseOfDeathPathway
		for(int i = 0; i < causes.size(); i++) {
			String cause = causes.get(i);
			String duration = (i < durations.size()) ? durations.get(i) : "";
			if(cause != null && !cause.isEmpty()) {
				ConditionCauseOfDeath causeOfDeathCondition = new ConditionCauseOfDeath();
				if(patientReference != null) {
					causeOfDeathCondition.setSubject(patientReference);
				}
				if(practitionerReference != null) {
					causeOfDeathCondition.setAsserter(practitionerReference);
				}
				causeOfDeathCondition.setCode(new CodeableConcept().setText(cause));
				if(!duration.isEmpty()) {
					Age durationAge = MDIToFhirCMSUtil.parseAge(duration);
					if(durationAge != null) {
						causeOfDeathCondition.setOnset(durationAge);
					}
					else {
						causeOfDeathCondition.setOnset(new StringType(duration));
					}
				}
				MDIToFhirCMSUtil.addResourceToBatchBundle(bundle, causeOfDeathCondition);
				mainComposition.getCauseMannerSection().addEntry(new Reference(causeOfDeathCondition.getId()));
				returnCoDPathway.addEntry(new ListEntryComponent().setItem(new Reference(causeOfDeathCondition.getId())));
			}
		}
		String[] otherCauses = inputFields.OSCOND.split(";");
		List<String> listArrayOtherCauses = Arrays.asList(otherCauses);
		for(String otherCause:listArrayOtherCauses) {
			if(otherCause.isEmpty()) {
				continue;
			}
			ConditionOtherContributingToDeath conditionContrib = new ConditionOtherContributingToDeath();
			if(patientReference != null) {
				conditionContrib.setSubject(patientReference);
			}
			if(practitionerReference != null) {
				conditionContrib.setAsserter(practitionerReference);
			}
			conditionContrib.setCode(new CodeableConcept().setText(otherCause));
			mainComposition.getCauseMannerSection().addEntry(new Reference(conditionContrib.getId()));
			MDIToFhirCMSUtil.addResourceToBatchBundle(bundle, conditionContrib);
		}
		return returnCoDPathway;
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

	private DocumentReferenceMDICaseNotesSummary createCaseNote(String caseNoteString, Patient patient) {
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
	}
	
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
		Location returnDeathLocation = new Location(); //TODO: Add US Core location support to MDI Library
		returnDeathLocation.setMeta(new Meta().addProfile("http://hl7.org/fhir/us/core/StructureDefinition/us-core-location"));
		CommonUtil.setUUID(returnDeathLocation);
		Stream<String> deathAddrFields = Stream.of(inputFields.DEATHSTREET, inputFields.DEATHCITY,
				inputFields.DEATHCOUNTY, inputFields.DEATHSTATE, inputFields.DEATHZIP);
		if(!deathAddrFields.allMatch(x -> x == null || x.isEmpty())) {
			Address deathAddr = MDIToFhirCMSUtil.createAddress(inputFields.DEATHPLACE, inputFields.DEATHSTREET, inputFields.DEATHCITY,
					inputFields.DEATHCOUNTY, inputFields.DEATHSTATE, inputFields.DEATHZIP);
			returnDeathLocation.setName(inputFields.DEATHSTREET);
			returnDeathLocation.setAddress(deathAddr);
		}
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