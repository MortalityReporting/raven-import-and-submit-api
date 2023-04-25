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

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import edu.gatech.chai.MDI.Model.MDIToEDRSModelFields;
import edu.gatech.chai.MDI.Model.ToxResult;
import edu.gatech.chai.MDI.Model.ToxSpecimen;
import edu.gatech.chai.MDI.Model.ToxToMDIModelFields;
import edu.gatech.chai.Mapping.Service.MDIToMDIToEDRSService;
import edu.gatech.chai.Mapping.Service.MDIToToxToMDIService;
import edu.gatech.chai.Mapping.Service.XLSXToMDIToEDRSService;
import edu.gatech.chai.Mapping.Service.XLSXToToxToMDIService;
import edu.gatech.chai.Submission.Entity.PatientSubmit;
import edu.gatech.chai.Submission.Repository.PatientSubmitRepository;
import edu.gatech.chai.Submission.Service.SubmitBundleService;

@Controller
@CrossOrigin(origins = "*")
public class UploadAndExportController {
	@Autowired
	MDIToMDIToEDRSService mDIToMDIToEDRSService;
	@Autowired
	MDIToToxToMDIService mDIToToxToMDIService;
	@Autowired
	SubmitBundleService submitBundleService;
	@Autowired
	private PatientSubmitRepository patientSubmitRepository;
	@Autowired
	private XLSXToMDIToEDRSService xLSXToMDIToEDRSService;
	@Autowired
	private XLSXToToxToMDIService xLSXToToxToMDIService;
	@Value("${fhircms.submit}")
	boolean submitFlag;
	
	private static final Logger logger = LoggerFactory.getLogger(UploadAndExportController.class);
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
			List<MDIToEDRSModelFields> inputFields = (List<MDIToEDRSModelFields>)object.get("viewmodel");
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

	@PostMapping(value = {"upload-xlsx-file"})
	public ResponseEntity<JsonNode> uploadXLSXFile(@RequestParam(name = "file", required = true) MultipartFile file, @RequestParam(name = "type", defaultValue = "mdi-to-edrs", required = false) String type) throws JsonProcessingException {
		if(type.equalsIgnoreCase("mdi-to-edrs")){
			return uploadXLSXFileForMDIToEDRS(file);
		}
		else{
			return uploadXLSXFileForToxToMDI(file);
		}
	}

	@PostMapping(value = {"upload-tox-to-mdi-xlsx-file"})
	public ResponseEntity<JsonNode> uploadXLSXFileForToxToMDI(@RequestParam(name = "file", required = true) MultipartFile file) throws JsonProcessingException {
		logger.info("XLSX MDI-To-Tox Upload: Starting XLSX File Read");
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

		List<ToxToMDIModelFields> mappedXLSXData;
		//Map data to internal definition
		logger.info("XLSX MDI-To-Tox Upload: Mapping Data");
		try {
			mappedXLSXData = xLSXToToxToMDIService.convertToMDIModelFields(workbook);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getLocalizedMessage());
		}
		//Setup mapper
		ObjectMapper mapper = new ObjectMapper();
		mapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
		mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		ArrayNode responseJson = mapper.createArrayNode();
		//For each model field
		for(int i=0;i<mappedXLSXData.size();i++){
			ToxToMDIModelFields modelFields = mappedXLSXData.get(i);
			//Convert
			logger.info("XLSX MDI-To-Tox Upload: Creating FHIR Data");
			String bundleString = "";
			try {
				bundleString = mDIToToxToMDIService.convertToMDIString(modelFields, i);
			} catch (ParseException e1) {
				e1.printStackTrace();
				continue;
			}
			logger.info("XLSX MDI-To-Tox Upload: Creating Response Object");
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
			//Handle Specimen
			if(modelFields.SPECIMENS != null && !modelFields.SPECIMENS.isEmpty()){
				ArrayNode specimenArrayNode = mapper.createArrayNode();
				for(ToxSpecimen toxSpecimen: modelFields.SPECIMENS){
					ObjectNode specimenFields = mapper.createObjectNode();
					for(Field f:toxSpecimen.getClass().getDeclaredFields()){
						String keyName = f.getName();
						ObjectNode fieldObject = mapper.createObjectNode();
						String value = "";
						if(f.getType().equals(String.class)){
							try{
								value = ((String)f.get(toxSpecimen));
							}
							catch(IllegalArgumentException | IllegalAccessException e){
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
						specimenFields.set(keyName, fieldObject);
					}
					specimenArrayNode.add(specimenFields);
				}
				fields.set("SPECIMENS",specimenArrayNode);
			}
			//Handle Results
			if(modelFields.RESULTS != null && !modelFields.RESULTS.isEmpty()){
				ArrayNode resultsArrayNode = mapper.createArrayNode();
				for(ToxResult toxResults: modelFields.RESULTS){
					ObjectNode resultFields = mapper.createObjectNode();
					for(Field f:toxResults.getClass().getDeclaredFields()){
						String keyName = f.getName();
						ObjectNode fieldObject = mapper.createObjectNode();
						String value = "";
						if(f.getType().equals(String.class)){
							try{
								value = ((String)f.get(toxResults));
							}
							catch(IllegalArgumentException | IllegalAccessException e){
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
						resultFields.set(keyName, fieldObject);
					}
					resultsArrayNode.add(resultFields);
				}
				fields.set("RESULTS",resultsArrayNode);
			}
			//Handle Notes
			if(modelFields.NOTES != null && !modelFields.NOTES.isEmpty()){
				ArrayNode notesArrayNode = mapper.createArrayNode();
				for(String note: modelFields.NOTES){
					ObjectNode fieldObject = mapper.createObjectNode();
					fieldObject.put("status", "mapped");
					fieldObject.put("value", note);
					fieldObject.set("FHIRResource", mapper.createObjectNode());
				}
				fields.set("NOTES",notesArrayNode);
			}
			responseObject.set("fields", fields);
			//Actually submit to the fhir server here!
			if(submitFlag){
				logger.info("XLSX MDI-To-Tox Upload: Uploading Tox-To-MDI To FhirBase");
				JsonNode patientInfo = submitToFhirBase(bundleString, modelFields, mapper);
				responseObject.set("fhirResponse", patientInfo);
				responseObject.put("Narrative", "");
			}
			responseJson.add(responseObject);
		}
		//JsonNode responseJson = mapper.valueToTree(mappedXLSXData);
		HttpStatus returnStatus = HttpStatus.CREATED;
		if(mappedXLSXData.size() == 0){
			returnStatus = HttpStatus.NO_CONTENT;
		}
		return new ResponseEntity<JsonNode>(responseJson, returnStatus);
	}

	@PostMapping(value = {"upload-mdi-to-edrs-xlsx-file"})
    public ResponseEntity<JsonNode> uploadXLSXFileForMDIToEDRS(@RequestParam(name = "file", required = true) MultipartFile file) throws JsonProcessingException {
		//Read XLSX File submitted
		logger.info("XLSX MDI-To-EDRS Upload: Starting XLSX File Read");
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

		List<MDIToEDRSModelFields> mappedXLSXData;
		//Map data to internal definition
		try {
			mappedXLSXData = xLSXToMDIToEDRSService.convertToMDIModelFields(workbook);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getLocalizedMessage());
		}
		//Setup mapper
		ObjectMapper mapper = new ObjectMapper();
		mapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
		mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		ArrayNode responseJson = mapper.createArrayNode();
		//For each mapped field
		for(MDIToEDRSModelFields modelFields:mappedXLSXData){
			//Convert 
			String bundleString = "";
			try {
				bundleString = mDIToMDIToEDRSService.convertToMDIString(modelFields);
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
			if(submitFlag){
				JsonNode patientInfo = submitToFhirBase(bundleString, modelFields, mapper);
				responseObject.set("fhirResponse", patientInfo);
				responseObject.put("Narrative", "");
			}
			responseJson.add(responseObject);
		}
		//JsonNode responseJson = mapper.valueToTree(mappedXLSXData);
		HttpStatus returnStatus = HttpStatus.CREATED;
		if(mappedXLSXData.size() == 0){
			returnStatus = HttpStatus.NO_CONTENT;
		}
		return new ResponseEntity<JsonNode>(responseJson, returnStatus);
    }

	private JsonNode submitToFhirBase(String fhirBundleString, MDIToEDRSModelFields modelFields, ObjectMapper mapper){
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

	private JsonNode submitToFhirBase(String fhirBundleString, ToxToMDIModelFields modelFields, ObjectMapper mapper){
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
        CsvToBean<MDIToEDRSModelFields> csvToBean = new CsvToBeanBuilder(reader)
                .withType(MDIToEDRSModelFields.class)
                .withIgnoreLeadingWhiteSpace(true)
                .build();

        // convert `CsvToBean` object to list of users
        List<MDIToEDRSModelFields> inputFields = csvToBean.parse();
        String prettyFhirOutput = "";
        for(MDIToEDRSModelFields inputField: inputFields) {
        	String jsonBundle = "";
        	if(mappingType.equalsIgnoreCase("MDI")) {
        		jsonBundle = mDIToMDIToEDRSService.convertToMDIString(inputField);
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