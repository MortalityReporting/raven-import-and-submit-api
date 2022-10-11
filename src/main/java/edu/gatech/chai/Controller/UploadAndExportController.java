package edu.gatech.chai.Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tika.Tika;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.DetectedIssue;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.IntegerType;
import org.hl7.fhir.r4.model.MessageHeader;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.ResourceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import edu.gatech.chai.MDI.Model.MDIModelFields;
import edu.gatech.chai.Mapping.Service.MDIToMDIFhirCMSService;
import edu.gatech.chai.Mapping.Service.NightingaleSubmissionService;
import edu.gatech.chai.Mapping.Service.VitalcheckSubmissionService;
import edu.gatech.chai.Mapping.Service.XLSXToMDIFhirCMSService;
import edu.gatech.chai.Submission.Configuration.SubmissionSourcesConfiguration;
import edu.gatech.chai.Submission.Entity.PatientSubmit;
import edu.gatech.chai.Submission.Entity.SourceStatus;
import edu.gatech.chai.Submission.Entity.Status;
import edu.gatech.chai.Submission.Repository.PatientSubmitRepository;
import edu.gatech.chai.Submission.Service.SubmitBundleService;
import edu.gatech.chai.VRDR.model.DeathCertificateDocument;

@Controller
@CrossOrigin(origins = "*")
public class UploadAndExportController {
	@Autowired
	MDIToMDIFhirCMSService mappingToMDIService;
	@Autowired
	SubmitBundleService submitBundleService;
	@Autowired
	private PatientSubmitRepository patientSubmitRepository;
	@Autowired
	private XLSXToMDIFhirCMSService xLSXToMDIFhirCMSService;
	@Value("${fhircms.submit}")
	boolean submitFlag;
	
	public UploadAndExportController() {
	}
	
    @GetMapping("/")
    public String index() {
        return "index";
    }
    
    @PostMapping(value = "upload-csv-file-dataonly")
    public ResponseEntity<JsonNode> uploadCSVFileDataOnly(@RequestParam(name = "file", required = true) MultipartFile file, @RequestParam(name = "mappingType", required = false) String mappingType) throws JsonProcessingException {
    	try {
    		Map<String, Object> object = readCSVFileAndSubmitToFhirBase(file,mappingType);
    		ArrayNode VRDRBundles = (ArrayNode)object.get("bundleArray");
    		HttpHeaders responseHeaders = new HttpHeaders();
    	    responseHeaders.set("Content-Type", "application/json");
    		ResponseEntity<JsonNode> returnResponse = new ResponseEntity<JsonNode>(VRDRBundles, HttpStatus.CREATED);
    		return returnResponse;
    	} catch (IOException ex){
    		throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error reading CSV file in param 'file'");
    	} catch (ParseException e) {
    		throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error parsing received VRDR Json from fhir server");
		}
    }

    @PostMapping(value = "upload-csv-file")
    public String uploadCSVFile(@RequestParam(name = "file", required = true) MultipartFile file, @RequestParam(name = "mappingType", required = false) String mappingType, Model model) throws JsonProcessingException {
		try {
			Map<String, Object> object = readCSVFileAndSubmitToFhirBase(file, mappingType);
			List<MDIModelFields> inputFields = (List<MDIModelFields>)object.get("viewmodel");
			String prettyFhirOutput = (String) object.get("prettyFhirOutput");
			model.addAttribute("inputFields", inputFields);
			model.addAttribute("status", true);
			model.addAttribute("fhirOutput", prettyFhirOutput);
		} catch (Exception ex){
			ex.printStackTrace(System.out);
			model.addAttribute("message", "An error occurred while processing the CSV file.");
			model.addAttribute("status", false);
		}
    	return "file-upload-status";
    }

	@PostMapping(value = "upload-xlsx-file")
    public ResponseEntity<JsonNode> uploadXLSXFile(@RequestParam(name = "file", required = true) MultipartFile file) throws JsonProcessingException {
		//Read XLSX File submitted
		Tika tika = new Tika();
		String detectedType;
		XSSFWorkbook workbook = null;
		try {
			detectedType = tika.detect(file.getBytes());
			workbook = new XSSFWorkbook(file.getInputStream());
		} catch (IOException e1) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e1.getLocalizedMessage());
		}
		if(detectedType.equalsIgnoreCase("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")){
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Expected a file media type of:application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" +
			"but instead found media type of:"+detectedType);
		}

		List<MDIModelFields> mappedXLSXData;
		//Map data to internal definition
		try {
			mappedXLSXData = xLSXToMDIFhirCMSService.convertToMDIModelFields(workbook);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getLocalizedMessage());
		}
		//Setup mapper
		ObjectMapper mapper = new ObjectMapper();
		mapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
		mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		ArrayNode responseJson = mapper.createArrayNode();
		//For each mapped field
		for(MDIModelFields modelFields:mappedXLSXData){
			//Convert 
			String bundleString = "";
			try {
				bundleString = mappingToMDIService.convertToMDIString(modelFields);
			} catch (ParseException e1) {
				e1.printStackTrace();
				continue;
			}
			ObjectNode responseObject = mapper.createObjectNode();
			try {
				responseObject.set("fhirBundle", mapper.readTree(bundleString));
			} catch (IOException e1) {
				e1.printStackTrace();
				continue;
			}
			//Collect mapping of objects here.
			ObjectNode fields = mapper.createObjectNode();
			for(Field f:modelFields.getClass().getDeclaredFields()){
				String keyName = f.getName();
				ObjectNode fieldObject = mapper.createObjectNode();
				String value = "";
				if(f.getType().equals(String.class)){
					try{
						value = ((String)f.get(modelFields));
					}
					catch(IllegalArgumentException | IllegalAccessException e){
						continue;
					}
				}
				else if(f.getType().equals(Boolean.class)){
					try {
						value = Boolean.toString(((Boolean)f.get(modelFields)));
					} catch (IllegalArgumentException | IllegalAccessException e) {
						continue;
					}
				}
				
				if(value.isEmpty()){
					fieldObject.put("status", "not mapped");
				}
				else{
					fieldObject.put("status", "mapped");
				}
				fieldObject.set("FHIRResource", mapper.createObjectNode());
				fieldObject.put("value",value);
				fields.set(keyName, fieldObject);
			}
			responseObject.set("fields", fields);
			//Actually submit to the fhir server here!
			JsonNode patientInfo = submitToFhirBase(bundleString, modelFields, mapper);
			responseObject.set("fhirResponse", patientInfo);
			responseObject.put("Narrative", "");
			responseJson.add(responseObject);
		}
		//JsonNode responseJson = mapper.valueToTree(mappedXLSXData);
		HttpStatus returnStatus = HttpStatus.CREATED;
		if(mappedXLSXData.size() == 0){
			returnStatus = HttpStatus.NO_CONTENT;
		}
		return new ResponseEntity<JsonNode>(responseJson, HttpStatus.CREATED);
    }

	private JsonNode submitToFhirBase(String fhirBundleString, MDIModelFields modelFields, ObjectMapper mapper){
		ObjectNode patientInfo = mapper.createObjectNode();
		patientInfo.put("name", modelFields.FIRSTNAME + " " + modelFields.MIDNAME + " " + modelFields.LASTNAME);
		patientInfo.put("status", "Not Submitted");
		patientInfo.put("statusCode", "");
		patientInfo.put("state", "");
		try {
			ResponseEntity<String> response = submitBundleService.submitBundle(fhirBundleString);
			System.out.println("Client response body:" + response.getBody());
			patientInfo.put("statusCode", response.getStatusCode().value());
			// save users list on model
			if(response.getStatusCode() == HttpStatus.OK ) {
				patientInfo.put("status", "Success");
			}
		}
		catch (HttpStatusCodeException e) {
			patientInfo.put("status", "Error");
			patientInfo.put("statusCode", e.getRawStatusCode());
		}
		return patientInfo;
	}
    
    private Map<String, Object> readCSVFileAndSubmitToFhirBase(MultipartFile file, String mappingType) throws IOException, ParseException{
    	if(mappingType == null) {
    		mappingType = "MDI";
    	}
    	ObjectMapper mapper = new ObjectMapper();
    	ArrayNode VRDRBundles = JsonNodeFactory.instance.arrayNode();
    	// parse CSV file to create a list of `InputField` objects
        Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
        // create csv bean reader
        CsvToBean<MDIModelFields> csvToBean = new CsvToBeanBuilder(reader)
                .withType(MDIModelFields.class)
                .withIgnoreLeadingWhiteSpace(true)
                .build();

        // convert `CsvToBean` object to list of users
        List<MDIModelFields> inputFields = csvToBean.parse();
        String prettyFhirOutput = "";
        for(MDIModelFields inputField: inputFields) {
        	String jsonBundle = "";
        	if(mappingType.equalsIgnoreCase("MDI")) {
        		jsonBundle = mappingToMDIService.convertToMDIString(inputField);
        	}
        	System.out.println("JSON BUNDLE:");
        	System.out.println(jsonBundle);
        	JsonNode submitBundleNode = mapper.readTree(jsonBundle);
            if(prettyFhirOutput.isEmpty()) {
            	prettyFhirOutput = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(submitBundleNode);
            }
            // Submit to fhir server
            System.out.println(jsonBundle);
            if(submitFlag) {
                try {
	                ResponseEntity<String> response = submitBundleService.submitBundle(jsonBundle);
	                System.out.println("Client response body:" + response.getBody());
	                // save users list on model
                }
                catch (HttpStatusCodeException e) {
                	continue;
                }
				//Until we resolve what the right request from the FHIR server is, we need to not use this.
                /*try {
            		JsonNode internalVRDRNode = fhirCMSToVRDRService.pullDCDFromBaseFhirServerAsJson(inputField.SYSTEMID, inputField.CASEID);
            		VRDRBundles.add(internalVRDRNode);
            	}
            	catch (ResourceNotFoundException e) {
            		System.out.println("Error:"  + e.getLocalizedMessage() + "for patient id" + inputField.CASEID);
            		System.out.println("Could not complete the document request from the FHIR server, appending the original batch request instead");
            		VRDRBundles.add(submitBundleNode);
            	}*/
            }else {
            	VRDRBundles.add(submitBundleNode);
            }
        }
        Map<String, Object> returnMap = new HashMap<String, Object>();
        returnMap.put("viewmodel", inputFields);
        returnMap.put("bundleArray", VRDRBundles);
        returnMap.put("prettyFhirOutput", prettyFhirOutput);
        return returnMap;
    }
    
    @GetMapping("submitstatus")
    public ResponseEntity<JsonNode> submissionStatus(@RequestParam(required = true) String systemIdentifier,@RequestParam(required = true) String codeIdentifier){
    	ObjectMapper mapper = new ObjectMapper();
    	List<PatientSubmit> patientSubmitList = patientSubmitRepository.findByPatientIdentifierSystemAndPatientIdentifierCode(systemIdentifier, codeIdentifier);
    	JsonNode jsonOutput = mapper.valueToTree(patientSubmitList);
		HttpHeaders responseHeaders = new HttpHeaders();
	    responseHeaders.set("Content-Type", "application/json");
		ResponseEntity<JsonNode> returnResponse = new ResponseEntity<JsonNode>(jsonOutput, HttpStatus.OK);
		return returnResponse;
    }
    
}