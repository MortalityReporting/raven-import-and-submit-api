package edu.gatech.chai.Mapping.Service;

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

import edu.gatech.chai.MDI.Model.MDIToEDRSModelFields;
import edu.gatech.chai.MDI.Model.ToxResult;
import edu.gatech.chai.MDI.Model.ToxSpecimen;
import edu.gatech.chai.MDI.Model.ToxToMDIModelFields;

@Service
public class XLSXToToxToMDIService {
    private static final Logger logger = LoggerFactory.getLogger(XLSXToToxToMDIService.class);
    private static final DataFormatter formatter = new DataFormatter();
    private static String LABORATORY_HEADER = "Laboratory";
    private static final String[] LABORATORY_FIELDS = {"Toxicology Lab Name", "Lab Address: Street", "Lab Address: Street", "Lab Address: City", "Lab Address: County", "Lab Address: State", "Lab Address: Zip", "Laboratory Case Number", "Performer"};
    private static String DECEDENT_HEADER = "Decedent";
    private static final String[] DECEDENT_FIELDS = {"Decedent Name", "MDI Case System", "MDI Case Number", "Decedent Sex", "Decedent DOB", "ME/C Case Notes", "Date/Time of Specimen Collection", "Date/Time of Receipt", "Date of Report Issuance", "Name of Certifier"};
    private static String SPECIMEN_HEADER = "Specimens";
    private static final String[] SPECIMEN_FIELDS = {"Name", "Identifier", "Body Site", "Amount", "Date/Time Collected", "Date/Time of Receipt", "Condition", "Container", "Notes"};
    private static String RESULTS_HEADER = "Results";
    private static final String[] RESULTS_FIELDS = {"Analyte/Analysis", "Specimen", "Method", "Value", "Range", "Description"};
    private static String NOTES_HEADER = "Notes";

    private static int HEADER_COLUMN = 0;

    public List<ToxToMDIModelFields> convertToMDIModelFields(XSSFWorkbook workbook) throws Exception{
        List<ToxToMDIModelFields> returnList = new ArrayList<ToxToMDIModelFields>();
        for(int sheetIndex = 0;sheetIndex < workbook.getNumberOfSheets(); sheetIndex++){
            ToxToMDIModelFields modelFields = new ToxToMDIModelFields();
            XSSFSheet sheet = workbook.getSheetAt(sheetIndex); 
            //Laboratory fields
            Cell laboratoryHeader = findCellFromColumn(sheet, HEADER_COLUMN, LABORATORY_HEADER);
            Map<String,String> laboratoryMap = new HashMap<String,String>();
            int currentRow = laboratoryHeader.getRowIndex() + 1;
            Cell keyCell = sheet.getRow(currentRow).getCell(HEADER_COLUMN);
            String currentKey = "";
            if(keyCell != null){
                currentKey = formatter.formatCellValue(keyCell);
            }
            Cell valueCell = sheet.getRow(currentRow).getCell(HEADER_COLUMN + 1);
            String currentValue = "";
            if(valueCell != null){
                currentValue = formatter.formatCellValue(valueCell);
            }
            while(Arrays.stream(LABORATORY_FIELDS).anyMatch(currentKey::equalsIgnoreCase)){
                laboratoryMap.put(currentKey, currentValue);
                currentRow = currentRow + 1;
                keyCell = sheet.getRow(currentRow).getCell(HEADER_COLUMN);
                currentKey = "";
                if(keyCell != null){
                    currentKey = formatter.formatCellValue(keyCell);
                }
                valueCell = sheet.getRow(currentRow).getCell(HEADER_COLUMN + 1);
                currentValue = "";
                if(valueCell != null){
                     currentValue = formatter.formatCellValue(valueCell);
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
                currentKey = formatter.formatCellValue(keyCell);
            }
            valueCell = sheet.getRow(currentRow).getCell(HEADER_COLUMN + 1);
            currentValue = "";
            if(valueCell != null){
                currentValue = formatter.formatCellValue(valueCell);
            }
            while(Arrays.stream(DECEDENT_FIELDS).anyMatch(currentKey::equalsIgnoreCase)){
                decedentMap.put(currentKey, currentValue);
                currentRow = currentRow + 1;
                keyCell = sheet.getRow(currentRow).getCell(HEADER_COLUMN);
                currentKey = "";
                if(keyCell != null){
                    currentKey = formatter.formatCellValue(keyCell);
                }
                valueCell = sheet.getRow(currentRow).getCell(HEADER_COLUMN + 1);
                currentValue = "";
                if(valueCell != null){
                    currentValue = formatter.formatCellValue(valueCell);
                }
            }
            modelFields = mapDecedentFields(modelFields, decedentMap);
            //Specimen
            //Create Header Map
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
                    && !formatter.formatCellValue(specimenRow.getCell(specimenFieldMap.get("Name"))).isEmpty()
                    && !formatter.formatCellValue(specimenRow.getCell(0)).equalsIgnoreCase(RESULTS_HEADER)){
                ToxSpecimen specimen = mapSpecimen(specimenRow, specimenFieldMap);
                modelFields.SPECIMENS.add(specimen);
                specimenRow = sheet.getRow(specimenRow.getRowNum() + 1);
            }
            //Results
            //Create Result Map
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
            while(resultsRow.getCell(resultsFieldMap.get("Analyte/Analysis")) != null
                    && !formatter.formatCellValue(resultsRow.getCell(resultsFieldMap.get("Analyte/Analysis"))).isEmpty()
                    && !formatter.formatCellValue(resultsRow.getCell(0)).equalsIgnoreCase(NOTES_HEADER)){
                ToxResult result = mapResult(resultsRow, resultsFieldMap);
                modelFields.RESULTS.add(result);
                resultsRow = sheet.getRow(resultsRow.getRowNum() + 1);
            }
            //Notes
            Cell notesHeader = findCellFromColumn(sheet, HEADER_COLUMN, NOTES_HEADER);
            XSSFRow notesRow = sheet.getRow(notesHeader.getRowIndex());
            notesRow = sheet.getRow(notesRow.getRowNum() + 1);
            while(notesRow != null && notesRow.getCell(1) != null){
                String value = formatter.formatCellValue(notesRow.getCell(1));
                if(!value.isEmpty()){
                    modelFields.NOTES.add(value);
                }
                notesRow = sheet.getRow(notesRow.getRowNum() + 1);
            }
            returnList.add(modelFields);
        }
        return returnList;
    }

    public ToxToMDIModelFields mapLaboratoryFields(ToxToMDIModelFields modelFields, Map<String,String> decedentMap){
        Optional.ofNullable(decedentMap.get("Toxicology Lab Name")).ifPresent(x -> modelFields.TOXORGNAME = x);
        Optional.ofNullable(decedentMap.get("Lab Address: Street")).ifPresent(x -> modelFields.TOXORGSTREET = x);
        Optional.ofNullable(decedentMap.get("Lab Address: County")).ifPresent(x -> modelFields.TOXORGCOUNTY = x);
        Optional.ofNullable(decedentMap.get("Lab Address: State")).ifPresent(x -> modelFields.TOXORGSTATE = x);
        Optional.ofNullable(decedentMap.get("Lab Address: Zip")).ifPresent(x -> modelFields.TOXORGZIP = x);
        Optional.ofNullable(decedentMap.get("Lab Case Number")).ifPresent(x -> modelFields.TOXCASENUMBER = x);
        Optional.ofNullable(decedentMap.get("Performer")).ifPresent(x -> modelFields.TOXPERFORMER = x);
        return modelFields;
    }

    public ToxToMDIModelFields mapDecedentFields(ToxToMDIModelFields modelFields, Map<String,String> decedentMap){
        //private static final String[] DECEDENT_FIELDS = {"Decedent Name", "MDI Case System", "MDI Case Number", "Decedent DOB", "ME/C Case Notes", "Date/Time of Specimen Collection", "Date/Time of Receipt", "Date of Report Issuance", "Name of Certifier"};
        Optional.ofNullable(decedentMap.get("Decedent Name")).ifPresent(fullName -> {
            try {
                handleName(modelFields, fullName);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });
        Optional.ofNullable(decedentMap.get("MDI Case System")).ifPresent(x -> modelFields.MDICASESYSTEM = x);
        Optional.ofNullable(decedentMap.get("MDI Case Number")).ifPresent(x -> modelFields.MDICASEID = x);
        Optional.ofNullable(decedentMap.get("Decedent DOB")).ifPresent(x -> modelFields.BIRTHDATE = x);
        Optional.ofNullable(decedentMap.get("ME/C Case Notes")).ifPresent(x -> modelFields.MECNOTES = x);
        Optional.ofNullable(decedentMap.get("Date/Time of Specimen Collection")).ifPresent(x -> modelFields.SPECIMENCOLLECTION_DATETIME = x);
        //Optional.ofNullable(decedentMap.get("Date/Time of Receipt")).ifPresent(x -> modelFields.rece = x); TODO: Map receipt time
        Optional.ofNullable(decedentMap.get("Date of Report Issuance")).ifPresent(x -> modelFields.REPORTDATE = x);
        //Optional.ofNullable(decedentMap.get("Name of Certifier")).ifPresent(x -> modelFields. = x); TODO Map Certifier
        return modelFields;
    }

    public ToxSpecimen mapSpecimen(XSSFRow specimenRow, Map<String,Integer> specimenFieldMap){
        ToxSpecimen specimen = new ToxSpecimen();
        Optional.ofNullable(specimenFieldMap.get("Name")).ifPresent(x -> specimen.NAME = formatter.formatCellValue(specimenRow.getCell(x)));
        Optional.ofNullable(specimenFieldMap.get("Identifier")).ifPresent(x -> specimen.IDENTIFIER = formatter.formatCellValue(specimenRow.getCell(x)));
        Optional.ofNullable(specimenFieldMap.get("Body Site")).ifPresent(x -> specimen.BODYSITE = formatter.formatCellValue(specimenRow.getCell(x)));
        Optional.ofNullable(specimenFieldMap.get("Amount")).ifPresent(x -> specimen.AMOUNT = formatter.formatCellValue(specimenRow.getCell(x)));
        Optional.ofNullable(specimenFieldMap.get("Date/Time of Receipt")).ifPresent(x -> specimen.RECEIPT_DATETIME = formatter.formatCellValue(specimenRow.getCell(x)));
        Optional.ofNullable(specimenFieldMap.get("Date/Time Collected")).ifPresent(x -> specimen.COLLECTED_DATETIME = formatter.formatCellValue(specimenRow.getCell(x)));
        Optional.ofNullable(specimenFieldMap.get("Condition")).ifPresent(x -> specimen.CONDITION = formatter.formatCellValue(specimenRow.getCell(x)));
        return specimen;
    }

    public ToxResult mapResult(XSSFRow specimenRow, Map<String,Integer> resultFieldMap){
        //private static final String[] RESULTS_FIELDS = {"Analyte/Analysis", "Specimen", "Method", "Value", "Range", "Description"};
        ToxResult result = new ToxResult();
        Optional.ofNullable(resultFieldMap.get("Analyte/Analysis")).ifPresent(x -> result.ANALYSIS = formatter.formatCellValue(specimenRow.getCell(x)));
        Optional.ofNullable(resultFieldMap.get("Specimen")).ifPresent(x -> result.SPECIMEN = formatter.formatCellValue(specimenRow.getCell(x)));
        Optional.ofNullable(resultFieldMap.get("Method")).ifPresent(x -> result.METHOD = formatter.formatCellValue(specimenRow.getCell(x)));
        Optional.ofNullable(resultFieldMap.get("Value")).ifPresent(x -> result.VALUE = formatter.formatCellValue(specimenRow.getCell(x)));
        //Optional.ofNullable(resultFieldMap.get("Range")).ifPresent(x -> result = formatter.formatCellValue(specimenRow.getCell(x))); TODO: Add Range
        //Optional.ofNullable(resultFieldMap.get("Description")).ifPresent(x -> result. = formatter.formatCellValue(specimenRow.getCell(x))); TODO: Add Description
        return result;
    }

    protected ToxToMDIModelFields handleName(ToxToMDIModelFields returnModel, String fullName) throws Exception{
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
            throw new Exception("Unable to capture the name components of name '"+fullName+"'.");
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
            if(lintAndCompareStrings(formatter.formatCellValue(cell),cellValue)){
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
            if(cell != null && formatter.formatCellValue(cell).equalsIgnoreCase(cellValue)){
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
}
