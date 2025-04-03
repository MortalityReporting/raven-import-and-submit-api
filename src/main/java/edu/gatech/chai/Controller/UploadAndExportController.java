package edu.gatech.chai.Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
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

import edu.gatech.chai.MDI.Model.DCRModelFields;
import edu.gatech.chai.MDI.Model.MDIAndEDRSModelFields;
import edu.gatech.chai.MDI.Model.ToxResult;
import edu.gatech.chai.MDI.Model.ToxSpecimen;
import edu.gatech.chai.MDI.Model.ToxToMDIModelFields;
import edu.gatech.chai.Mapping.Service.LocalToDCRService;
import edu.gatech.chai.Mapping.Service.LocalToMDIAndEDRSService;
import edu.gatech.chai.Mapping.Service.LocalToToxToMDIService;
import edu.gatech.chai.Mapping.Service.XLSXToDCRModelService;
import edu.gatech.chai.Mapping.Service.XLSXToMDIAndEDRSModelService;
import edu.gatech.chai.Mapping.Service.XLSXToToxToMDIModelService;
import edu.gatech.chai.Submission.Repository.PatientSubmitRepository;
import edu.gatech.chai.Submission.Service.SubmitBundleService;

@Controller
@CrossOrigin(origins = "*")
public class UploadAndExportController {
	@Autowired
	LocalToMDIAndEDRSService mDIAndMDIToEDRSService;
	@Autowired
	LocalToToxToMDIService mDIToToxToMDIService;
	@Autowired
	LocalToDCRService mDIToDCRService;
	@Autowired
	SubmitBundleService submitBundleService;
	@Autowired
	private XLSXToMDIAndEDRSModelService xLSXToMDIToEDRSService;
	@Autowired
	private XLSXToToxToMDIModelService xLSXToToxToMDIService;
	@Autowired
	private XLSXToDCRModelService xLSXToDCRModelService;
	@Value("${fhircms.submit}")
	boolean submitFlag;

	private static final Logger logger = LoggerFactory.getLogger(UploadAndExportController.class);

	public UploadAndExportController() {
	}

	@PostMapping(value = "upload-csv-file")
	public ResponseEntity<JsonNode> uploadCSVFileDataOnly(
			@RequestParam(name = "file", required = true) MultipartFile file,
			@RequestParam(name = "mappingType", required = false) String mappingType) throws JsonProcessingException {
		try {
			logger.info("Running upload-csv-file-dataonly");
			ResponseEntity<JsonNode> returnResponse = readCSVFileAndSubmitToFhirBase(file, mappingType);
			return returnResponse;
		} catch (IOException ex) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Error reading CSV file in param 'file'");
		} catch (ParseException e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Error parsing received VRDR Json from fhir server");
		}
	}

	@PostMapping(value = { "upload-xlsx-file" })
	public ResponseEntity<JsonNode> uploadXLSXFile(@RequestParam(name = "file", required = true) MultipartFile file,
			@RequestParam(name = "type", defaultValue = "mdi-and-edrs", required = false) String type)
			throws JsonProcessingException {
		if (type.equalsIgnoreCase("mdi-and-edrs")) {
			return uploadXLSXFileForMDIToEDRS(file);
		} else if (type.equalsIgnoreCase("tox-to-mdi")){
			return uploadXLSXFileForToxToMDI(file);
		}
		else if (type.equalsIgnoreCase("dcr")){
			return uploadXLSXFileForDCR(file);
		}
		else{
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid 'type' param of '"+type+"' expected a value of 'mdi-and-edrs' or 'tox-to-mdi' or 'dcr'");
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
			bundleString = mDIToToxToMDIService.convertToMDIString(modelFields, i);
			logger.info("XLSX MDI-To-Tox Upload: Creating Response Object");
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
				logger.info("XLSX TOX-To-MDI Upload: Uploading Tox-To-EDRS To FhirBase");
				JsonNode responseInfo = submitToFhirBase(bundleString, modelFields, mapper);
				responseObject.set("fhirResponse", responseInfo);
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
		logger.info("XLSX MDI-and-EDRS Upload: Starting XLSX File Read");
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

		List<MDIAndEDRSModelFields> mappedXLSXData;
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
		for(MDIAndEDRSModelFields modelFields:mappedXLSXData){
			//Convert 
			String bundleString = "";
			try {
				bundleString = mDIAndMDIToEDRSService.convertToMDIString(modelFields);
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
			ObjectNode fields = createMDIAndEDRSFieldsNode(modelFields);
			responseObject.set("fields", fields);
			//Actually submit to the fhir server here!
			if(submitFlag){
				logger.info("XLSX MDI-and-EDRS Upload: Uploading Tox-To-EDRS To FhirBase");
				JsonNode responseInfo = submitToFhirBase(bundleString, modelFields, mapper);
				responseObject.set("fhirResponse", responseInfo);
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

	@PostMapping(value = {"upload-dcr-xlsx-file"})
    public ResponseEntity<JsonNode> uploadXLSXFileForDCR(@RequestParam(name = "file", required = true) MultipartFile file) throws JsonProcessingException {
		//Read XLSX File submitted
		logger.info("XLSX DCR Upload: Starting XLSX File Read");
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

		List<DCRModelFields> mappedXLSXData;
		//Map data to internal definition
		try {
			mappedXLSXData = xLSXToDCRModelService.convertToMDIModelFields(workbook);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getLocalizedMessage());
		}
		//Setup mapper
		ObjectMapper mapper = new ObjectMapper();
		mapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
		mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		ArrayNode responseJson = mapper.createArrayNode();
		//For each mapped field
		for(DCRModelFields modelFields:mappedXLSXData){
			//Convert 
			String bundleString = "";
			try {
				bundleString = mDIToDCRService.convertToMDIString(modelFields);
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
			ObjectNode fields = createMDIAndEDRSFieldsNode(modelFields);
			responseObject.set("fields", fields);
			//Actually submit to the fhir server here!
			if(submitFlag){
				logger.info("XLSX DCR Upload: Uploading Tox-To-EDRS To FhirBase");
				JsonNode responseInfo = submitToFhirBase(bundleString, modelFields, mapper);
				responseObject.set("fhirResponse", responseInfo);
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

	private JsonNode submitToFhirBase(String fhirBundleString, MDIAndEDRSModelFields modelFields, ObjectMapper mapper) {
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
			if (response.getStatusCode() == HttpStatus.OK) {
				patientInfo.put("status", "Success");
			}
		} catch (HttpStatusCodeException e) {
			patientInfo.put("status", "Error");
			patientInfo.put("statusCode", e.getRawStatusCode());
		}
		return patientInfo;
	}

	private JsonNode submitToFhirBase(String fhirBundleString, ToxToMDIModelFields modelFields, ObjectMapper mapper) {
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
			if (response.getStatusCode() == HttpStatus.OK) {
				patientInfo.put("status", "Success");
			}
		} catch (HttpStatusCodeException e) {
			patientInfo.put("status", "Error");
			patientInfo.put("statusCode", e.getRawStatusCode());
		}
		return patientInfo;
	}

	/**
	 * Converts an internal representation of the Tox-to-MDI CSV/ModelFields into a
	 * "fields response object"
	 * Details whether the value was mapped or not mapped, what value was captured;
	 * and if there was an issue during mapping
	 * 
	 * @param modelFields
	 * @return
	 */
	public ObjectNode createTOXToMDIFieldsNode(ToxToMDIModelFields modelFields) {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode fields = mapper.createObjectNode();
		// Handle non-specimen, non results, non notes object
		for (Field f : modelFields.getClass().getDeclaredFields()) {
			String keyName = f.getName();
			ObjectNode fieldObject = mapper.createObjectNode();
			String value = "";
			if(keyName.equalsIgnoreCase("SPECIMEN") || keyName.equalsIgnoreCase("RESULTS") || keyName.equalsIgnoreCase("NOTES")){
				continue;
			}
			if (f.getType().equals(String.class)) {
				try {
					value = ((String) f.get(modelFields));
				} catch (IllegalArgumentException | IllegalAccessException e) {
					continue;
				}
			} else if (f.getType().equals(Boolean.class)) {
				try {
					value = Boolean.toString(((Boolean) f.get(modelFields)));
				} catch (IllegalArgumentException | IllegalAccessException e) {
					continue;
				}
			}
			if (value.isEmpty()) {
				fieldObject.put("status", "not mapped");
			} else {
				fieldObject.put("status", "mapped");
			}
			fieldObject.put("value", value);
			List<String> errorList = modelFields.getErrorListForName(f.getName());
			if (!errorList.isEmpty()) {
				fieldObject.set("errors", mapper.valueToTree(errorList));
				fieldObject.put("status", "not mapped");
			}
			fields.set(keyName, fieldObject);
		}
		// Handle Specimen
		if (modelFields.SPECIMENS != null && !modelFields.SPECIMENS.isEmpty()) {
			ArrayNode specimenArrayNode = mapper.createArrayNode();
			for (ToxSpecimen toxSpecimen : modelFields.SPECIMENS) {
				ObjectNode specimenFields = mapper.createObjectNode();
				for (Field f : toxSpecimen.getClass().getDeclaredFields()) {
					String keyName = f.getName();
					ObjectNode fieldObject = mapper.createObjectNode();
					String value = "";
					if (f.getType().equals(String.class)) {
						try {
							value = ((String) f.get(toxSpecimen));
						} catch (IllegalArgumentException | IllegalAccessException e) {
							continue;
						}
					}
					if (value.isEmpty()) {
						fieldObject.put("status", "not mapped");
					} else {
						fieldObject.put("status", "mapped");
					}
					fieldObject.set("FHIRResource", mapper.createObjectNode());
					fieldObject.put("value", value);
					List<String> errorList = toxSpecimen.getErrorListForName(f.getName());
					if (!errorList.isEmpty()) {
						fieldObject.set("errors", mapper.valueToTree(errorList));
						fieldObject.put("status", "not mapped");
					}
					specimenFields.set(keyName, fieldObject);
				}
				specimenArrayNode.add(specimenFields);
			}
			fields.set("SPECIMENS", specimenArrayNode);
		}
		// Handle Results
		if (modelFields.RESULTS != null && !modelFields.RESULTS.isEmpty()) {
			ArrayNode resultsArrayNode = mapper.createArrayNode();
			for (ToxResult toxResult : modelFields.RESULTS) {
				ObjectNode resultFields = mapper.createObjectNode();
				for (Field f : toxResult.getClass().getDeclaredFields()) {
					String keyName = f.getName();
					ObjectNode fieldObject = mapper.createObjectNode();
					String value = "";
					if (f.getType().equals(String.class)) {
						try {
							value = ((String) f.get(toxResult));
						} catch (IllegalArgumentException | IllegalAccessException e) {
							continue;
						}
					}
					if (value.isEmpty()) {
						fieldObject.put("status", "not mapped");
					} else {
						fieldObject.put("status", "mapped");
					}
					fieldObject.set("FHIRResource", mapper.createObjectNode());
					fieldObject.put("value", value);
					List<String> errorList = toxResult.getErrorListForName(f.getName());
					if (!errorList.isEmpty()) {
						fieldObject.set("errors", mapper.valueToTree(errorList));
						fieldObject.put("status", "not mapped");
					}
					resultFields.set(keyName, fieldObject);
				}
				resultsArrayNode.add(resultFields);
			}
			fields.set("RESULTS", resultsArrayNode);
		}
		// Handle Notes
		if (modelFields.NOTES != null && !modelFields.NOTES.isEmpty()) {
			ArrayNode notesArrayNode = mapper.createArrayNode();
			for (String note : modelFields.NOTES) {
				ObjectNode fieldObject = mapper.createObjectNode();
				fieldObject.put("status", "mapped");
				fieldObject.put("value", note);
				fieldObject.set("FHIRResource", mapper.createObjectNode());
			}
			fields.set("NOTES", notesArrayNode);
		}
		return fields;
	}

	/**
	 * Converts an internal representation of the MDI-To-EDRS CSV/ModelFields into a
	 * "fields response object"
	 * Details whether the value was mapped or not mapped, what value was captured;
	 * and if there was an issue during mapping
	 * 
	 * @param modelFields
	 * @return
	 */
	public ObjectNode createMDIAndEDRSFieldsNode(MDIAndEDRSModelFields modelFields) {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode fields = mapper.createObjectNode();
		for (Field f : modelFields.getClass().getDeclaredFields()) {
			String keyName = f.getName();
			ObjectNode fieldObject = mapper.createObjectNode();
			String value = "";
			if (f.getType().equals(String.class)) {
				try {
					value = ((String) f.get(modelFields));
				} catch (IllegalArgumentException | IllegalAccessException e) {
					continue;
				}
			} else if (f.getType().equals(Boolean.class)) {
				try {
					value = Boolean.toString(((Boolean) f.get(modelFields)));
				} catch (IllegalArgumentException | IllegalAccessException e) {
					continue;
				}
			}
			if (value.isEmpty()) {
				fieldObject.put("status", "not mapped");
			} else {
				fieldObject.put("status", "mapped");
			}
			fieldObject.set("FHIRResource", mapper.createObjectNode());
			fieldObject.put("value", value);
			List<String> errorList = modelFields.getErrorListForName(f.getName());
			if (!errorList.isEmpty()) {
				fieldObject.put("status", "not mapped");
				fieldObject.set("errors", mapper.valueToTree(errorList));
			}
			fields.set(keyName, fieldObject);
		}
		return fields;
	}

	private ResponseEntity<JsonNode> readCSVFileAndSubmitToFhirBase(MultipartFile file, String mappingType)
			throws IOException, ParseException {
		if (mappingType == null) {
			mappingType = "MDI";
		}
		ArrayNode VRDRBundles = JsonNodeFactory.instance.arrayNode();
		// parse CSV file to create a list of `InputField` objects
		Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
		// create csv bean reader
		CsvToBean<MDIAndEDRSModelFields> csvToBean = new CsvToBeanBuilder(reader)
				.withType(MDIAndEDRSModelFields.class)
				.withIgnoreLeadingWhiteSpace(true)
				.build();

		// convert `CsvToBean` object to list of users
		List<MDIAndEDRSModelFields> inputFields = csvToBean.parse();
		//Setup mapper
		ObjectMapper mapper = new ObjectMapper();
		mapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
		mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		ArrayNode responseJson = mapper.createArrayNode();
		//For each mapped field
		for(MDIAndEDRSModelFields modelFields:inputFields){
			//Convert 
			String bundleString = "";
			try {
				bundleString = mDIAndMDIToEDRSService.convertToMDIString(modelFields);
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
			ObjectNode fields = createMDIAndEDRSFieldsNode(modelFields);
			responseObject.set("fields", fields);
			//Actually submit to the fhir server here!
			if(submitFlag){
				logger.info("XLSX TOX-To-MDI Upload: Uploading Tox-To-EDRS To FhirBase");
				JsonNode responseInfo = submitToFhirBase(bundleString, modelFields, mapper);
				responseObject.set("fhirResponse", responseInfo);
				responseObject.put("Narrative", "");
			}
			responseJson.add(responseObject);
		}
		//JsonNode responseJson = mapper.valueToTree(mappedXLSXData);
		HttpStatus returnStatus = HttpStatus.CREATED;
		if(inputFields.size() == 0){
			returnStatus = HttpStatus.NO_CONTENT;
		}
		return new ResponseEntity<JsonNode>(responseJson, returnStatus);
	}

}