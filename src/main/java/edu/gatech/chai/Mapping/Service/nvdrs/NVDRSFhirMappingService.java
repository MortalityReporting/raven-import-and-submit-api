package edu.gatech.chai.Mapping.Service.nvdrs;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.BooleanType;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CanonicalType;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Observation.ObservationComponentComponent;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.springframework.stereotype.Service;

import ca.uhn.fhir.util.ExtensionUtil;
import edu.gatech.model.BaseSerializedFormat;
import edu.gatech.model.CharLimitedInteger;
import edu.gatech.model.DCFormat;
import edu.gatech.model.LECMEFormat;
import edu.gatech.model.enumvalueset.OneOrZero;
import edu.gatech.model.enumvalueset.YesOrBlank;

@Service
public class NVDRSFhirMappingService {

    public NVDRSFhirMappingService(){

    }

    public String nvdrsFhirToFlatFile(Bundle bundle) throws IOException, IllegalArgumentException {
        BaseSerializedFormat intermediateFormat = createIntermediateFormat(bundle);
        return intermediateFormat.writeSerializedFormat(new StringWriter()).toString();
    }

    public BaseSerializedFormat createIntermediateFormat(Bundle bundle){
        BaseSerializedFormat intermediateFormat;
        if(!resourceContainsProfile(bundle, "http://mortalityreporting.github.io/nvdrs-ig/StructureDefinition/nvdrs-document-bundle")){
            throw new IllegalArgumentException("Could not find a bundle with a profile of 'http://mortalityreporting.github.io/nvdrs-ig/StructureDefinition/nvdrs-document-bundle'");
        }
        Composition composition = null;
        try{
            composition = (Composition)bundle.getEntryFirstRep().getResource();
            if(!resourceContainsProfile(composition, "http://mortalityreporting.github.io/nvdrs-ig/StructureDefinition/nvdrs-composition")){
                throw new IllegalArgumentException("");
            }
        }
        catch (NullPointerException | ClassCastException | IllegalArgumentException e){
            throw new IllegalArgumentException("Could not find a composition as the first entry of the bundle with a profile of 'http://mortalityreporting.github.io/nvdrs-ig/StructureDefinition/nvdrs-composition'");
        }
        Coding documentTypeCoding = composition.getType().getCodingFirstRep();
        if(documentTypeCoding.getCode().equalsIgnoreCase("dc-report")){
            intermediateFormat = createDCIntermediateFormat(bundle, composition);
        }
        else if(documentTypeCoding.getCode().equalsIgnoreCase("cme-report")){
            intermediateFormat = createLECMEIntermediateFormat(bundle, composition);
        }
        else{
            throw new IllegalArgumentException("Could not find a composition as the first entry of the bundle with a profile of 'http://mortalityreporting.github.io/nvdrs-ig/StructureDefinition/nvdrs-composition'");
        }
        return intermediateFormat;
    }

    public DCFormat createDCIntermediateFormat(Bundle bundle, Composition composition){
        DCFormat returnDCFormat = new DCFormat();
        returnDCFormat.setForceNewRecord(null);
        getExtension(bundle, "http://mortalityreporting.github.io/nvdrs-ig/StructureDefinition/nvdrs-force-new-record-extension")
            .ifPresent(e -> returnDCFormat.getForceNewRecord().setValue(((BooleanType)e.getValue()).booleanValue() ? "Y" : "N"));
        getExtension(bundle, "http://mortalityreporting.github.io/nvdrs-ig/StructureDefinition/nvdrs-overwrite-conflicts-extension")
            .ifPresent(e -> returnDCFormat.getOverwriteConflicts().setValue(((BooleanType)e.getValue()).booleanValue() ? "Y" : "N"));

        return returnDCFormat;
    }

    public LECMEFormat createLECMEIntermediateFormat(Bundle bundle, Composition composition){
        LECMEFormat returnLECMEFormat = new LECMEFormat();
        //Control Section
        getExtension(composition, "http://mortalityreporting.github.io/nvdrs-ig/StructureDefinition/nvdrs-force-new-record-extension")
            .ifPresent(e -> returnLECMEFormat.getForceNewRecord().setValue(
                ((BooleanType)e.getValue()).booleanValue() ? YesOrBlank.YES : YesOrBlank.BLANK));
        getExtension(composition, "http://mortalityreporting.github.io/nvdrs-ig/StructureDefinition/nvdrs-overwrite-conflicts-extension")
            .ifPresent(e -> returnLECMEFormat.getOverwriteConflicts().setValue(
                ((BooleanType)e.getValue()).booleanValue() ? YesOrBlank.YES : YesOrBlank.BLANK));
        getExtension(composition, "http://mortalityreporting.github.io/nvdrs-ig/StructureDefinition/nvdrs-incident-year-extension")
            .ifPresent(e -> returnLECMEFormat.getIncidentYear().setValue(
                new CharLimitedInteger(((DateType)e.getValue()).getYear(), 4)));

        if(composition.getIdentifier().getSystem() != null && composition.getIdentifier().getSystem().equalsIgnoreCase("urn:vdrs:nvdrs:incidentnumber")){
            returnLECMEFormat.getIncidentNumber().setValue(new CharLimitedInteger(Integer.parseInt(composition.getIdentifier().getValue()), 4));
        }
        getExtensions(composition, "http://mortalityreporting.github.io/nvdrs-ig/StructureDefinition/identifier-extension")
            .stream().forEach(e -> {
                Identifier i = (Identifier)e.getValue();
                if (i.getSystem().equalsIgnoreCase("urn:vdrs:nvdrs:last4dc")){
                    returnLECMEFormat.getLastFourDCNumber().setValue(i.getValueElement().asStringValue());
                }
                else if(i.getSystem().equalsIgnoreCase("urn:vdrs:nvdrs:last4cme")){
                    returnLECMEFormat.getLastFourCMENumber().setValue(i.getValueElement().asStringValue());
                }
                else if(i.getSystem().equalsIgnoreCase("urn:vdrs:nvdrs:incidentnumber")){
                    returnLECMEFormat.getIncidentNumber().setValue(
                        new CharLimitedInteger(
                            Integer.parseInt(i.getValueElement().asStringValue()), 4
                            )
                        );
                }
                else if(i.getSystem().equalsIgnoreCase("urn:vdrs:nvdrs:victim")){
                    returnLECMEFormat.getIncidentNumber().setValue(
                        new CharLimitedInteger(
                            Integer.parseInt(i.getValueElement().asStringValue()), 4
                            )
                        );
                }
            });

            Optional<Resource> weaponType = findFirstResourceWithProfile(bundle, "http://mortalityreporting.github.io/nvdrs-ig/ValueSet/nvdrs-weapon-type-vs");
            if(weaponType.isPresent()){
                Observation weaponTypeObs = (Observation)weaponType.get();
                returnLECMEFormat.getWeaponType().setValue(weaponTypeObs.getValueCodeableConcept().getCodingFirstRep().getCode());
            }

            Optional<Resource> firearm = findFirstResourceWithProfile(bundle, "http://mortalityreporting.github.io/nvdrs-ig/StructureDefinition/nvdrs-firearm");
            if(firearm.isPresent()){
                Observation firearmObs = (Observation)firearm.get();
                getExtension(composition, "http://mortalityreporting.github.io/nvdrs-ig/StructureDefinition/nvdrs-firearm-stolen-extension")
                    .ifPresent(e -> returnLECMEFormat.getFirearmStolen().setValue(
                        ((CodeableConcept)e.getValue()).getCodingFirstRep().getCode().equalsIgnoreCase("Y") ? OneOrZero.ONE : OneOrZero.ZERO));
                getExtension(composition, "http://mortalityreporting.github.io/nvdrs-ig/StructureDefinition/nvdrs-gun-stored-loaded-extension")
                    .ifPresent(e -> returnLECMEFormat.getGunLoaded().setValue(
                            ((CodeableConcept)e.getValue()).getCodingFirstRep().getCode().equalsIgnoreCase("Y") ? OneOrZero.ONE : OneOrZero.ZERO));
                getExtension(composition, "http://mortalityreporting.github.io/nvdrs-ig/StructureDefinition/nvdrs-gun-stored-locked-extension")
                    .ifPresent(e -> returnLECMEFormat.getGunStoredLocked().setValue(
                            ((CodeableConcept)e.getValue()).getCodingFirstRep().getCode().equalsIgnoreCase("Y") ? new CharLimitedInteger(1, 1) : new CharLimitedInteger(0, 1)));
                
            }
        return returnLECMEFormat;
    }

    public boolean resourceContainsProfile(IBaseResource resource, String profileUrl){
        return resource.getMeta().getProfile().stream()
            .anyMatch(canonicalType -> canonicalType.getValueAsString().equalsIgnoreCase(profileUrl));
    }

    public Optional<Extension> getExtension(Resource resource, String extensionUrl){
        Extension returnExtension = (Extension)ExtensionUtil.getExtensionByUrl(resource, extensionUrl);
        if(returnExtension != null){
            return Optional.of(returnExtension);
        }
        return Optional.empty();
    }

    public List<Extension> getExtensions(Resource resource,String extensionUrl){
        
        return resource.getMeta().getExtension().stream()
            .filter(e -> e.getUrlElement().equals(extensionUrl)).collect(Collectors.toList());
    }

    public Optional<Resource> findFirstResourceWithProfile(Bundle bundle, String profileUrl){
        Optional<BundleEntryComponent> bec = bundle.getEntry().stream()
            .filter(e -> 
                e.getResource().getMeta().getProfile().stream().anyMatch(c -> c.getValueAsString().equalsIgnoreCase(profileUrl)))
                .findFirst();
        if(bec.isPresent()){
            return Optional.of(bec.get().getResource());
        }
        return Optional.empty();
    }

    public Optional<ObservationComponentComponent> findObservationComponentWithCode(Observation observation, String componentCode){
        return observation.getComponent().stream()
            .filter( c -> 
                c.getCode().getCodingFirstRep().getCode().equalsIgnoreCase(componentCode)).findFirst();
    }
}