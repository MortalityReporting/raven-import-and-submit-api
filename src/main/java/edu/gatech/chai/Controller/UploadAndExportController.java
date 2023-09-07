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

	@PostMapping(value = {"upload-xlsx-file"})
	public ResponseEntity<JsonNode> uploadXLSXFile(@RequestParam(name = "file", required = true) MultipartFile file, @RequestParam(name = "type", defaultValue = "mdi-to-edrs", required = false) String type) throws JsonProcessingException {
		if(type.equalsIgnoreCase("mdi-to-edrs")){
			return uploadXLSXFileForMDIToEDRS(file);
		}
		else{
			return uploadXLSXFileForToxToMDI(file);
		}
	}

	@PostMapping(value = {"upload-csv-file"})
	public ResponseEntity<JsonNode> uploadCSVFile(@RequestParam(name = "file", required = true) MultipartFile file, @RequestParam(name = "type", defaultValue = "mdi-to-edrs", required = false) String type) throws JsonProcessingException {
		if(type.equalsIgnoreCase("mdi-to-edrs")){
			return uploadCSVFileForMDIToEDRS(file);
		}
		else{
			return uploadCSVFileForToxToMDI(file);
		}
	}
    
    @PostMapping(value = "upload-csv-file-mdi-to-edrs")
    public ResponseEntity<JsonNode> uploadCSVFileForMDIToEDRS(@RequestParam(name = "file", required = true) MultipartFile file) throws JsonProcessingException {
		logger.info("CSV MDI-To-EDRS Upload: Collecting CSV");
		Class modelFieldsClass = MDIToEDRSModelFields.class;
		List<MDIToEDRSModelFields> inputFields = null;
    	try {
			ObjectMapper mapper = new ObjectMapper();
			ArrayNode mDIToEDRSBundles = JsonNodeFactory.instance.arrayNode();
			// parse CSV file to create a list of `InputField` objects
			Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
			// create csv bean reader
			CsvToBean<MDIToEDRSModelFields> csvToBean = new CsvToBeanBuilder(reader)
					.withType(modelFieldsClass)
					.withIgnoreLeadingWhiteSpace(true)
					.build();

			// convert `CsvToBean` object to list of users
			inputFields = csvToBean.parse();
    	} catch (IOException ex){
    		throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error reading CSV file in param 'file'");
    	}
		return convertAndSubmitMDIToEDRS(inputFields);
    }

	@PostMapping(value = "upload-csv-file-tox-to-mdi")
    public ResponseEntity<JsonNode> uploadCSVFileForToxToMDI(@RequestParam(name = "file", required = true) MultipartFile file) throws JsonProcessingException {
		logger.info("CSV MDI-To-EDRS Upload: Collecting CSV");
		Class modelFieldsClass = ToxToMDIModelFields.class;
		List<ToxToMDIModelFields> inputFields = null;
    	try {
			ObjectMapper mapper = new ObjectMapper();
			ArrayNode mDIToEDRSBundles = JsonNodeFactory.instance.arrayNode();
			// parse CSV file to create a list of `InputField` objects
			Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
			// create csv bean reader
			CsvToBean<ToxToMDIModelFields> csvToBean = new CsvToBeanBuilder(reader)
					.withType(modelFieldsClass)
					.withIgnoreLeadingWhiteSpace(true)
					.build();

			// convert `CsvToBean` object to list of users
			inputFields = csvToBean.parse();
    	} catch (IOException ex){
    		throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error reading CSV file in param 'file'");
    	}
		return convertAndSubmitToxToMDI(inputFields);
    }

	@PostMapping(value = {"upload-tox-to-mdi-xlsx-file"})
	public ResponseEntity<JsonNode> uploadXLSXFileForToxToMDI(@RequestParam(name = "file", required = true) MultipartFile file) throws JsonProcessingException {
		logger.info("XLSX MDI-To-TOX Upload: Starting XLSX File Read");
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
		logger.info("XLSX MDI-To-TOX Upload: Mapping Data");
		try {
			mappedXLSXData = xLSXToToxToMDIService.convertToMDIModelFields(workbook);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getLocalizedMessage());
		}
		return convertAndSubmitToxToMDI(mappedXLSXData);
	}

	@PostMapping(value = {"upload-mdi-to-edrs-xlsx-file"})
    public ResponseEntity<JsonNode> uploadXLSXFileForMDIToEDRS(@RequestParam(name = "file", required = true) MultipartFile file) throws JsonProcessingException {
		logger.info("XLSX MDI-To-EDRS Upload: Starting XLSX File Read");
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

		List<MDIToEDRSModelFields> mappedXLSXData;
		//Map data to internal definition
		try {
			mappedXLSXData = xLSXToMDIToEDRSService.convertToMDIModelFields(workbook);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getLocalizedMessage());
		}
		return convertAndSubmitMDIToEDRS(mappedXLSXData);
    }

	/**
	 * Converts model fields to fhri json and submits to fhirbase for MDI-To-EDRS data.
	 * Composition function that calls more base functions
	 * @param mappedData mapped model fields
	 * @return
	 */
	public ResponseEntity<JsonNode> convertAndSubmitMDIToEDRS(List<MDIToEDRSModelFields> mappedData){
		//Setup mapper
		ObjectMapper mapper = new ObjectMapper();
		mapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
		mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		ArrayNode responseJson = mapper.createArrayNode();
		//For each mapped field
		for(MDIToEDRSModelFields modelFields:mappedData){
			//Convert 
			String bundleString = "";
			logger.info("XLSX MDI-To-EDRS Upload: Creating FHIR Data");
			try {
				bundleString = mDIToMDIToEDRSService.convertToMDIString(modelFields);
			} catch (ParseException e1) {
				e1.printStackTrace();
				continue;
			}
			logger.info("XLSX MDI-To-EDRS Upload: Creating Response Object");
			ObjectNode responseObject = mapper.createObjectNode();
			try {
				responseObject.set("fhirBundle", mapper.readTree(bundleString));
			} catch (IOException e1) {
				e1.printStackTrace();
				continue;
			}
			//Collect mapping of objects here.
			ObjectNode fields = createMDIToEDRSFieldsNode(modelFields);
			responseObject.set("fields", fields);
			//Actually submit to the fhir server here!
			if(submitFlag){
				logger.info("XLSX MDI-To-EDRS Upload: Uploading Tox-To-EDRS To FhirBase");
				JsonNode responseInfo = submitToFhirBase(bundleString, modelFields, mapper);
				responseObject.set("fhirResponse", responseInfo);
				responseObject.put("Narrative", "");
			}
			responseJson.add(responseObject);
		}
		//JsonNode responseJson = mapper.valueToTree(mappedXLSXData);
		HttpStatus returnStatus = HttpStatus.CREATED;
		if(mappedData.size() == 0){
			returnStatus = HttpStatus.NO_CONTENT;
		}
		return new ResponseEntity<JsonNode>(responseJson, returnStatus);
	}

	/**
	 * Converts model fields to fhri json and submits to fhirbase for Tox-To-MDI data.
	 * Composition function that calls more base functions
	 * @param mappedData mapped model fields
	 * @return
	 */
	public ResponseEntity<JsonNode> convertAndSubmitToxToMDI(List<ToxToMDIModelFields> mappedData){
		ObjectMapper mapper = new ObjectMapper();
		mapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
		mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		ArrayNode responseJson = mapper.createArrayNode();
		//For each model field
		for(ToxToMDIModelFields modelFields:mappedData){
			//Convert
			logger.info("XLSX MDI-To-TOX Upload: Creating FHIR Data");
			String bundleString = "";
			try {
				bundleString = mDIToToxToMDIService.convertToMDIString(modelFields);
			} catch (ParseException e1) {
				e1.printStackTrace();
				continue;
			}
			logger.info("XLSX MDI-To-TOX Upload: Creating Response Object");
			ObjectNode responseObject = mapper.createObjectNode();
			try {
				responseObject.set("fhirBundle", mapper.readTree(bundleString));
			} catch (IOException e1) {
				e1.printStackTrace();
				continue;
			}
			//Collect mapping of objects here.
			ObjectNode fields = createTOXToMDIFieldsNode(modelFields);
			responseObject.set("fields", fields);
			//Actually submit to the fhir server here!
			if(submitFlag){
				logger.info("XLSX MDI-To-TOX Upload: Uploading Tox-To-MDI To FhirBase");
				JsonNode patientInfo = submitToFhirBase(bundleString, modelFields, mapper);
				responseObject.set("fhirResponse", patientInfo);
				responseObject.put("Narrative", "");
			}
			responseJson.add(responseObject);
		}
		//JsonNode responseJson = mapper.valueToTree(mappedXLSXData);
		HttpStatus returnStatus = HttpStatus.CREATED;
		if(mappedData.size() == 0){
			returnStatus = HttpStatus.NO_CONTENT;
		}
		return new ResponseEntity<JsonNode>(responseJson, returnStatus);
	}

	/**
	 * Converts an internal representation of the Tox-To-MDI CSV/ModelFields into a "fields response object"
	 * Details whether the value was mapped or not mapped, what value was captured; and if there was an issue during mapping
	 * @param modelFields
	 * @return
	 */
	public ObjectNode createTOXToMDIFieldsNode(ToxToMDIModelFields modelFields){
		ObjectMapper mapper = new ObjectMapper();
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
		return fields;
	}

	/**
	 * Converts an internal representation of the MDI-To-EDRS CSV/ModelFields into a "fields response object"
	 * Details whether the value was mapped or not mapped, what value was captured; and if there was an issue during mapping
	 * @param modelFields
	 * @return
	 */
	public ObjectNode createMDIToEDRSFieldsNode(MDIToEDRSModelFields modelFields){
		ObjectMapper mapper = new ObjectMapper();
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
		return fields;
	}
    
	/**
	 * Submits MDIToEDRS data to the FHIRBase
	 * @param fhirBundleString a json string of the bundle
	 * @param modelFields existing modelfields used to create the bundle
	 * @param mapper mapper object passed to create the response json
	 * @return JsonNode containing the patient name (as a preliminary identifier), status of submssion, statuscode, and interpreted state
	 */
	private JsonNode submitToFhirBase(String fhirBundleString, MDIToEDRSModelFields modelFields, ObjectMapper mapper){
		ObjectNode responseInfo = mapper.createObjectNode();
		responseInfo.put("name", modelFields.FIRSTNAME + " " + modelFields.MIDNAME + " " + modelFields.LASTNAME);
		responseInfo.put("status", "Not Submitted");
		responseInfo.put("statusCode", "");
		responseInfo.put("state", "");
		try {
			ResponseEntity<String> response = submitBundleService.submitBundle(fhirBundleString);
			System.out.println("Client response body:" + response.getBody());
			responseInfo.put("statusCode", response.getStatusCode().value());
			// save users list on model
			if(response.getStatusCode() == HttpStatus.OK ) {
				responseInfo.put("status", "Success");
			}
		}
		catch (HttpStatusCodeException e) {
			responseInfo.put("status", "Error");
			responseInfo.put("statusCode", e.getRawStatusCode());
		}
		return responseInfo;
	}

	/**
	 * Submits ToxToMDI data to the FHIRBase
	 * @param fhirBundleString a json string of the bundle
	 * @param modelFields existing modelfields used to create the bundle
	 * @param mapper mapper object passed to create the response json
	 * @return JsonNode containing the patient name (as a preliminary identifier), status of submssion, statuscode, and interpreted state
	 */
	private JsonNode submitToFhirBase(String fhirBundleString, ToxToMDIModelFields modelFields, ObjectMapper mapper){
		ObjectNode responseInfo = mapper.createObjectNode();
		responseInfo.put("name", modelFields.FIRSTNAME + " " + modelFields.MIDNAME + " " + modelFields.LASTNAME);
		responseInfo.put("status", "Not Submitted");
		responseInfo.put("statusCode", "");
		responseInfo.put("state", "");
		try {
			ResponseEntity<String> response = submitBundleService.submitBundle(fhirBundleString);
			System.out.println("Client response body:" + response.getBody());
			responseInfo.put("statusCode", response.getStatusCode().value());
			// save users list on model
			if(response.getStatusCode() == HttpStatus.OK ) {
				responseInfo.put("status", "Success");
			}
		}
		catch (HttpStatusCodeException e) {
			responseInfo.put("status", "Error");
			responseInfo.put("statusCode", e.getRawStatusCode());
		}
		return responseInfo;
	}
}