package edu.gatech.chai.Mapping.Service;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import edu.gatech.chai.MDI.Model.ToxResult;
import edu.gatech.chai.MDI.Model.ToxSpecimen;
import edu.gatech.chai.MDI.Model.ToxToMDIModelFields;

@Service
public class XLSXToToxToMDIModelService {
    private static final Logger logger = LoggerFactory.getLogger(XLSXToToxToMDIModelService.class);
    private static final DataFormatter dataFormatter = new DataFormatter();
    private static String TEMPLATE_TITLE = "Toxicology-To-MDI Template";
    private static String FILEID_HEADER = "file Id";
    private static String LABORATORY_HEADER = "Laboratory";
    private static final String[] LABORATORY_FIELDS = {"Toxicology Lab Name", "Lab Address: Street", "Lab Address: Street", "Lab Address: City", "Lab Address: County", "Lab Address: State", "Lab Address: Zip", "Laboratory Case Number", "Performer"};
    private static String DECEDENT_HEADER = "Decedent";
    private static final String[] DECEDENT_FIELDS = {"Decedent Name", "MDI Case System", "MDI Case Number", "Decedent Sex", "Decedent DOB", "ME/C Case Notes", "Date/Time of Specimen Collection", "Date/Time of Receipt", "Date of Report Issuance", "Name of Certifier"};
    private static String SPECIMEN_HEADER = "Specimens";
    private static final String[] SPECIMEN_FIELDS = {"Name", "Identifier", "Body Site", "Amount", "Date/Time Collected", "Date/Time of Receipt", "Condition", "Container", "Notes"};
    private static String RESULTS_HEADER = "Results";
    private static final String[] RESULTS_FIELDS = {"Analyte/Analysis", "Specimen", "Method", "Value", "Range", "Description"};
    private static String NOTES_HEADER = "Notes";

    private static String[] DATE_FIELDS = {"Decedent DOB", "Date of Report Issuance"};
    private static String[] DATETIME_FIELDS = {"Date/Time of Specimen Collection", "Date/Time of Receipt"};

    private static String DATE_FORMAT = "MM/dd/YYYY";
    private static String DATETIME_FORMAT = "MM/dd/YYYY hh:mm a";

    private static int HEADER_COLUMN = 0;

    public XLSXToToxToMDIModelService(){
        dataFormatter.addFormat(DATE_FORMAT, new java.text.SimpleDateFormat(DATE_FORMAT));
        dataFormatter.addFormat(DATETIME_FORMAT, new java.text.SimpleDateFormat(DATETIME_FORMAT));
    }

    public List<ToxToMDIModelFields> convertToMDIModelFields(XSSFWorkbook workbook) throws Exception{
        List<ToxToMDIModelFields> returnList = new ArrayList<ToxToMDIModelFields>();
        for(int sheetIndex = 0;sheetIndex < workbook.getNumberOfSheets(); sheetIndex++){
            XSSFSheet sheet = workbook.getSheetAt(sheetIndex);
            if(sheet.getRow(0) == null || 
            !dataFormatter.formatCellValue(sheet.getRow(0).getCell(0)).equalsIgnoreCase(TEMPLATE_TITLE)){
                continue;
            }
            ToxToMDIModelFields modelFields = new ToxToMDIModelFields();
            
            //File Id
            Cell fileId = findCellFromColumn(sheet, HEADER_COLUMN, FILEID_HEADER);
            Cell fileIdValueCell = sheet.getRow(fileId.getRowIndex() + 1).getCell(HEADER_COLUMN);
            if(!dataFormatter.formatCellValue(fileIdValueCell).isEmpty()){
                modelFields.FILEID = dataFormatter.formatCellValue(fileIdValueCell);
            }

            //Laboratory fields
            Cell laboratoryHeader = findCellFromColumn(sheet, HEADER_COLUMN, LABORATORY_HEADER);
            Map<String,String> laboratoryMap = new HashMap<String,String>();
            int currentRow = laboratoryHeader.getRowIndex() + 1;
            Cell keyCell = sheet.getRow(currentRow).getCell(HEADER_COLUMN);
            String currentKey = "";
            if(keyCell != null){
                currentKey = dataFormatter.formatCellValue(keyCell);
            }
            Cell valueCell = sheet.getRow(currentRow).getCell(HEADER_COLUMN + 1);
            String currentValue = "";
            if(valueCell != null){
                currentValue = dataFormatter.formatCellValue(valueCell);
            }
            while(Arrays.stream(LABORATORY_FIELDS).anyMatch(currentKey::equalsIgnoreCase)){
                laboratoryMap.put(currentKey, currentValue);
                currentRow = currentRow + 1;
                keyCell = sheet.getRow(currentRow).getCell(HEADER_COLUMN);
                currentKey = "";
                if(keyCell != null){
                    currentKey = dataFormatter.formatCellValue(keyCell);
                }
                valueCell = sheet.getRow(currentRow).getCell(HEADER_COLUMN + 1);
                currentValue = "";
                if(valueCell != null){
                     currentValue = interpretCellValue(valueCell, currentKey, modelFields);
                }
            }
            modelFields = mapLaboratoryFields(modelFields, laboratoryMap);

            //Decedent Fields
            Cell decedentHeader = findCellFromColumn(sheet, HEADER_COLUMN, DECEDENT_HEADER);
            Map<String,String> decedentMap = new HashMap<String,String>();
            currentRow = decedentHeader.getRowIndex() + 1;
            keyCell = sheet.getRow(currentRow).getCell(HEADER_COLUMN);
            currentKey = "";
            if(keyCell != null){
                currentKey = dataFormatter.formatCellValue(keyCell);
            }
            valueCell = sheet.getRow(currentRow).getCell(HEADER_COLUMN + 1);
            currentValue = "";
            if(valueCell != null){
                currentValue = dataFormatter.formatCellValue(valueCell);
            }
            while(Arrays.stream(DECEDENT_FIELDS).anyMatch(currentKey::equalsIgnoreCase)){
                decedentMap.put(currentKey, currentValue);
                currentRow = currentRow + 1;
                keyCell = sheet.getRow(currentRow).getCell(HEADER_COLUMN);
                currentKey = "";
                if(keyCell != null){
                    currentKey = dataFormatter.formatCellValue(keyCell);
                }
                valueCell = sheet.getRow(currentRow).getCell(HEADER_COLUMN + 1);
                currentValue = "";
                if(valueCell != null){
                    currentValue = interpretCellValue(valueCell, currentKey, modelFields);
                }
            }
            modelFields = mapDecedentFields(modelFields, decedentMap);

            //Specimen
            //Create Specimen Header Map
            Cell specimenHeader = findCellFromColumn(sheet, HEADER_COLUMN, SPECIMEN_HEADER);
            XSSFRow specimenRow = sheet.getRow(specimenHeader.getRowIndex());
            Map<String,Integer> specimenFieldMap = new HashMap<String,Integer>();
            for(String specimenField:SPECIMEN_FIELDS){
                Cell specimenFieldCell = findCellFromRow(specimenRow, specimenField);
                if(specimenFieldCell != null){
                    specimenFieldMap.put(specimenField, Integer.valueOf(specimenFieldCell.getColumnIndex()));
                }
            }
            //Create a specimen per entry
            specimenRow = sheet.getRow(specimenRow.getRowNum() + 1);
            while(specimenRow.getCell(specimenFieldMap.get("Name")) != null 
                    && !dataFormatter.formatCellValue(specimenRow.getCell(specimenFieldMap.get("Name"))).isEmpty()
                    && !dataFormatter.formatCellValue(specimenRow.getCell(0)).equalsIgnoreCase(RESULTS_HEADER)){
                ToxSpecimen specimen = mapSpecimen(specimenRow, specimenFieldMap);
                modelFields.SPECIMENS.add(specimen);
                specimenRow = sheet.getRow(specimenRow.getRowNum() + 1);
            }

            //Results
            //Create Result Header Map
            Cell resultsHeader = findCellFromColumn(sheet, HEADER_COLUMN, RESULTS_HEADER);
            XSSFRow resultsRow = sheet.getRow(resultsHeader.getRowIndex());
            Map<String,Integer> resultsFieldMap = new HashMap<String,Integer>();
            for(String resultsField:RESULTS_FIELDS){
                Cell resultsFieldCell = findCellFromRow(resultsRow, resultsField);
                if(resultsFieldCell != null){
                    resultsFieldMap.put(resultsField, Integer.valueOf(resultsFieldCell.getColumnIndex()));
                }
            }
            //Create a result per entry
            resultsRow = sheet.getRow(resultsRow.getRowNum() + 1);
            while(resultsRow != null
                    && resultsRow.getCell(resultsFieldMap.get("Analyte/Analysis")) != null
                    && !dataFormatter.formatCellValue(resultsRow.getCell(resultsFieldMap.get("Analyte/Analysis"))).isEmpty()
                    && !dataFormatter.formatCellValue(resultsRow.getCell(0)).equalsIgnoreCase(NOTES_HEADER)){
                ToxResult result = mapResult(resultsRow, resultsFieldMap);
                result.RECORD_DATE = modelFields.REPORTISSUANCE_DATETIME;
                modelFields.RESULTS.add(result);
                resultsRow = sheet.getRow(resultsRow.getRowNum() + 1);
            }
            //Notes
            Cell notesHeader = findCellFromColumn(sheet, HEADER_COLUMN, NOTES_HEADER);
            XSSFRow notesRow = sheet.getRow(notesHeader.getRowIndex());
            notesRow = sheet.getRow(notesRow.getRowNum() + 1);
            while(notesRow != null && notesRow.getCell(1) != null){
                String value = dataFormatter.formatCellValue(notesRow.getCell(1));
                if(!value.isEmpty()){
                    modelFields.NOTES.add(value);
                }
                notesRow = sheet.getRow(notesRow.getRowNum() + 1);
            }
            returnList.add(modelFields);
        }
        return returnList;
    }

    public ToxToMDIModelFields mapLaboratoryFields(ToxToMDIModelFields modelFields, Map<String,String> laboratoryMap){
        Optional.ofNullable(laboratoryMap.get("Toxicology Lab Name")).ifPresent(x -> modelFields.TOXORGNAME = x);
        Optional.ofNullable(laboratoryMap.get("Lab Address: Street")).ifPresent(x -> modelFields.TOXORGSTREET = x);
        Optional.ofNullable(laboratoryMap.get("Lab Address: County")).ifPresent(x -> modelFields.TOXORGCOUNTY = x);
        Optional.ofNullable(laboratoryMap.get("Lab Address: State")).ifPresent(x -> modelFields.TOXORGSTATE = x);
        Optional.ofNullable(laboratoryMap.get("Lab Address: Zip")).ifPresent(x -> modelFields.TOXORGZIP = x);
        Optional.ofNullable(laboratoryMap.get("Laboratory Case Number")).ifPresent(x -> modelFields.TOXCASENUMBER = x);
        Optional.ofNullable(laboratoryMap.get("Performer")).ifPresent(x -> modelFields.TOXPERFORMER = x);
        return modelFields;
    }

    public ToxToMDIModelFields mapDecedentFields(ToxToMDIModelFields modelFields, Map<String,String> decedentMap){
        Optional.ofNullable(decedentMap.get("Decedent Name")).ifPresent(fullName -> {
            try {
                handleName(modelFields, fullName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        Optional.ofNullable(decedentMap.get("MDI Case System")).ifPresent(x -> modelFields.MDICASESYSTEM = x);
        Optional.ofNullable(decedentMap.get("MDI Case Number")).ifPresent(x -> modelFields.MDICASEID = x);
        Optional.ofNullable(decedentMap.get("Decedent Sex")).ifPresent(x -> modelFields.DECEDENTSEX = x);
        Optional.ofNullable(decedentMap.get("Decedent DOB")).ifPresent(x -> modelFields.BIRTHDATE = x);
        Optional.ofNullable(decedentMap.get("ME/C Case Notes")).ifPresent(x -> modelFields.MECNOTES = x);
        Optional.ofNullable(decedentMap.get("Date/Time of Specimen Collection")).ifPresent(x -> modelFields.SPECIMENCOLLECTION_DATETIME = x);
        Optional.ofNullable(decedentMap.get("Date/Time of Receipt")).ifPresent(x -> modelFields.RECEIPT_DATETIME = x);
        Optional.ofNullable(decedentMap.get("Date of Report Issuance")).ifPresent(x -> modelFields.REPORTISSUANCE_DATETIME = x);
        //Optional.ofNullable(decedentMap.get("Name of Certifier")).ifPresent(x -> modelFields. = x); TODO Map Certifier
        return modelFields;
    }

    public ToxSpecimen mapSpecimen(XSSFRow specimenRow, Map<String,Integer> specimenFieldMap){
        ToxSpecimen specimen = new ToxSpecimen();
        Optional.ofNullable(specimenFieldMap.get("Name")).ifPresent(x -> specimen.NAME = dataFormatter.formatCellValue(specimenRow.getCell(x)));
        Optional.ofNullable(specimenFieldMap.get("Identifier")).ifPresent(x -> specimen.IDENTIFIER = dataFormatter.formatCellValue(specimenRow.getCell(x)));
        Optional.ofNullable(specimenFieldMap.get("Body Site")).ifPresent(x -> specimen.BODYSITE = dataFormatter.formatCellValue(specimenRow.getCell(x)));
        Optional.ofNullable(specimenFieldMap.get("Amount")).ifPresent(x -> specimen.AMOUNT = dataFormatter.formatCellValue(specimenRow.getCell(x)));
        Optional.ofNullable(specimenFieldMap.get("Date/Time of Receipt")).ifPresent(x -> specimen.RECEIPT_DATETIME = dataFormatter.formatCellValue(specimenRow.getCell(x)));
        Optional.ofNullable(specimenFieldMap.get("Date/Time Collected")).ifPresent(x -> specimen.COLLECTED_DATETIME = dataFormatter.formatCellValue(specimenRow.getCell(x)));
        Optional.ofNullable(specimenFieldMap.get("Condition")).ifPresent(x -> specimen.CONDITION = dataFormatter.formatCellValue(specimenRow.getCell(x)));
        return specimen;
    }

    public ToxResult mapResult(XSSFRow specimenRow, Map<String,Integer> resultFieldMap){
        //private static final String[] RESULTS_FIELDS = {"Analyte/Analysis", "Specimen", "Method", "Value", "Range", "Description"};
        ToxResult result = new ToxResult();
        Optional.ofNullable(resultFieldMap.get("Analyte/Analysis")).ifPresent(x -> result.ANALYSIS = dataFormatter.formatCellValue(specimenRow.getCell(x)));
        Optional.ofNullable(resultFieldMap.get("Specimen")).ifPresent(x -> result.SPECIMEN = dataFormatter.formatCellValue(specimenRow.getCell(x)));
        Optional.ofNullable(resultFieldMap.get("Method")).ifPresent(x -> result.METHOD = dataFormatter.formatCellValue(specimenRow.getCell(x)));
        Optional.ofNullable(resultFieldMap.get("Value")).ifPresent(x -> result.VALUE = dataFormatter.formatCellValue(specimenRow.getCell(x)));
        //Optional.ofNullable(resultFieldMap.get("Range")).ifPresent(x -> result = formatter.formatCellValue(specimenRow.getCell(x))); TODO: Add Range
        //Optional.ofNullable(resultFieldMap.get("Description")).ifPresent(x -> result. = formatter.formatCellValue(specimenRow.getCell(x))); TODO: Add Description
        return result;
    }

    protected ToxToMDIModelFields handleName(ToxToMDIModelFields returnModel, String fullName){
        Pattern suffixPattern = Pattern.compile("(?<Suffix>Jr\\.|Sr\\.|IV|III|II|)");
        Matcher suffixMatcher = suffixPattern.matcher(fullName);
        if(suffixMatcher.matches()){
            String suffixFound = suffixMatcher.group("Suffix");
            returnModel.setSUFFIXNAME(suffixFound);
            //Remove suffix from the system to make it easier to parse the first, middle, and last name
            fullName.replaceAll(suffixFound, "");
        }
        Pattern fullNamePattern = Pattern.compile("([\\w-]+)\\s+([\\.\\w-]+)(\\s+([\\w-]+))?");
        Matcher fullNameMatcher = fullNamePattern.matcher(fullName);
        if(fullNameMatcher.groupCount() == 4 && fullNameMatcher.matches()){
            if(fullNameMatcher.group(3) != null){
                returnModel.setFIRSTNAME(fullNameMatcher.group(1));
                returnModel.setMIDNAME(fullNameMatcher.group(2));
                returnModel.setLASTNAME(fullNameMatcher.group(3));
            }
            else{
                returnModel.setFIRSTNAME(fullNameMatcher.group(1));
                returnModel.setLASTNAME(fullNameMatcher.group(2));
            }
        }
        else{
            returnModel.getErrorListForName("FIRSTNAME").add("Error parsing Name '"+fullName+"' into a first name component.");
            returnModel.getErrorListForName("LASTNAME").add("Error parsing Name '"+fullName+"' into a last name component.");
            returnModel.getErrorListForName("MIDNAME").add("Error parsing Name '"+fullName+"' into a middle name component.");
        }
        return returnModel;
    }

    public Cell findCellFromSheet(XSSFSheet sheet, String cellValue){
        for(int rowIndex=0;rowIndex<sheet.getLastRowNum();rowIndex++){
            XSSFRow row = sheet.getRow(rowIndex);
            Cell returnCell = findCellFromRow(row, cellValue);
            if(returnCell != null){
                return returnCell;
            }
        }
        return null;
    }

    public Cell findCellFromRow(XSSFRow row, String cellValue){
        Iterator<Cell> iterator = row.cellIterator();
        while(iterator.hasNext()){
            Cell cell = iterator.next();
            if(lintAndCompareStrings(dataFormatter.formatCellValue(cell),cellValue)){
                return cell;
            }
        }
        return null;
    }

    public Cell findCellFromColumn(XSSFSheet sheet, int columnIndex, String cellValue){
        return findCellFromColumn(sheet, columnIndex, cellValue, 0);
    }

    public Cell findCellFromColumn(XSSFSheet sheet, int columnIndex, String cellValue, int rowOffset){
        Iterator<Row> iterator = sheet.rowIterator();
        for(int i=0;i<rowOffset;i++){
            iterator.next();
        }
        while(iterator.hasNext()){
            Row row = iterator.next();
            Cell cell = row.getCell(columnIndex);
            if(cell != null && dataFormatter.formatCellValue(cell).equalsIgnoreCase(cellValue)){
                return cell;
            }
        }
        return null;
    }

    public String mergeSectionAndFieldName(String sectionName, String fieldName){
        return sectionName + "_" + fieldName;
    }

    public Integer getValueFromFieldMap(Map<String, Integer> fieldsToRowMap, String sectionName, String fieldName){
        String mapKey = mergeSectionAndFieldName(sectionName, fieldName);
        return fieldsToRowMap.get(mapKey);

    }

    private String getStringForColumnAndName(XSSFSheet sheet, Map<String, Integer> fieldMap, int columnIndex,String name){
        if(fieldMap.get(name) == null){
            return "";
        }
        DataFormatter formatter = new DataFormatter();
        return formatter.formatCellValue(sheet.getRow(fieldMap.get(name)).getCell(columnIndex));
    }

    private boolean lintAndCompareStrings(String left, String right){
        String leftLinted = left.trim().replaceAll("\\s+","").toLowerCase();
        String rightLinted = right.trim().replaceAll("\\s+","").toLowerCase();
        return leftLinted.contentEquals(rightLinted);
    }

    public String interpretCellValue(Cell valueCell, String currentKey, ToxToMDIModelFields modelFields){
        String currentValue = "";
        if(Arrays.asList(DATE_FIELDS).contains(currentKey)){
            currentValue = parseCellDateOrDateTimeWithFormat(valueCell, modelFields, currentKey, DATE_FORMAT);
        }
        else if(Arrays.asList(DATETIME_FIELDS).contains(currentKey)){
            currentValue = parseCellDateOrDateTimeWithFormat(valueCell, modelFields, currentKey, DATETIME_FORMAT);
        }
        else{
            currentValue = dataFormatter.formatCellValue(valueCell);
        } 
        currentValue = dataFormatter.formatCellValue(valueCell);
        return currentValue;
    }

    public String parseCellDateOrDateTimeWithFormat(Cell valueCell, ToxToMDIModelFields modelFields, String fieldName, String format){
        String returnValue = "";
        try{
            returnValue = DateTimeFormatter.ofPattern(format).format(
            valueCell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }
        catch(IllegalStateException e){
            modelFields.getErrorListForName(fieldName).add("Could not parse the value '"+valueCell.getStringCellValue()+"' with format '"+format+"'");
        }
        return returnValue;
    }
}
