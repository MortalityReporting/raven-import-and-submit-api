package edu.gatech.chai.Controller;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.InvalidParameterException;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ca.uhn.fhir.parser.IParser;
import edu.gatech.chai.MDI.context.MDIFhirContext;
import edu.gatech.chai.Mapping.Service.nvdrs.NVDRSFhirMappingService;
import edu.gatech.cli.NVDRSFlatFileRunner;
import edu.gatech.mapping.JsonMapping;
import edu.gatech.model.BaseSerializedFormat;
import edu.gatech.model.DCFormat;
import edu.gatech.model.LECMEFormat;


@RestController
@RequestMapping("/nvdrs")
@CrossOrigin(origins = "*")
public class NVDRSTranslationController {
    
    @Autowired
    public MDIFhirContext mdiFhirContext;
    public IParser fhirJsonParser;
    @Autowired
    public NVDRSFhirMappingService nvdrsFhirMappingService;

    @Autowired
    public NVDRSTranslationController(MDIFhirContext mdiFhirContext) {
        this.mdiFhirContext = mdiFhirContext;
        fhirJsonParser = mdiFhirContext.ctx.newJsonParser();
	}

    @GetMapping("json-intermediate/example")
     public JsonNode createExample(@RequestParam(defaultValue = "LECME") String type) {
        BaseSerializedFormat exampleModel = new NVDRSFlatFileRunner().createExampleNVDRSFile(type);
        JsonMapping mapping = new JsonMapping();
        JsonNode returnNode = mapping.mapModelToJson(exampleModel);
        return returnNode;
    }

    @PostMapping("json-intermediate/translate-from-flatfile")
     public JsonNode createExample(@RequestParam(defaultValue = "LECME") String type, @RequestBody String flatfileString) throws IOException, RuntimeException {
        JsonMapping mapping = new JsonMapping();
        BaseSerializedFormat baseModel;
        if(type.equalsIgnoreCase("LECME")){
            baseModel = new LECMEFormat();
        }
        else{
            baseModel = new DCFormat();
        }
        baseModel.readSerializedFormat(new StringReader(flatfileString));
        JsonNode returnNode = mapping.mapModelToJson(baseModel);
        return returnNode;
    }

    @PostMapping(value = "flatfile/generate", produces = MediaType.TEXT_PLAIN_VALUE)
     public String createExample(@RequestParam(required = true) String format, @RequestParam(defaultValue = "LECME") String type, @RequestBody(required = true) String bodyString) throws IllegalArgumentException, IOException {
        if(format.equalsIgnoreCase("fhir")){
            IBaseResource nvdrsResource = fhirJsonParser.parseResource(bodyString);
            if(!(nvdrsResource instanceof Bundle)){
                throw new InvalidParameterException("Expected a fhir R4 Bundle but instead found the fhirType:" + nvdrsResource.fhirType());
            }
            Bundle nvdrsBundle = (Bundle)nvdrsResource;
            String flatfileSerialString = nvdrsFhirMappingService.nvdrsFhirToFlatFile(nvdrsBundle);
            return flatfileSerialString;

        }
        else if(format.equalsIgnoreCase("json-intermediate")){
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonRootNode = mapper.readTree(bodyString);
            String flatfileSerialString = JsonMapping.mapJsonToSerialField(jsonRootNode);
            return flatfileSerialString;
        }
        else{
            throw new InvalidParameterException("format string of '"+format+"' found. Only values of ['fhir','json-intermediate'] are supported.");
        }
    }
}