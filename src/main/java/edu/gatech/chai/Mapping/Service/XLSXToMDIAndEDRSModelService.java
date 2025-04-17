package edu.gatech.chai.Mapping.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.format.CellDateFormatter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import edu.gatech.chai.MDI.Model.MDIAndEDRSModelFields;

@Service
public class XLSXToMDIAndEDRSModelService {
    private static final Logger logger = LoggerFactory.getLogger(XLSXToToxToMDIModelService.class);
    private static final int FIRST_ROW = 1;
    private static String file_id = "";
    // private static final String SECTION_HEADER = "Highlighted yellow items have been changed"; //Note this will ofc change to a reasonable "section name" in the future
    private static final String ELEMENT_HEADER = "Elements";
    //Static index for the FILE ID
    private static final int FILE_ID_ROW = 2;
    private static final int FILE_ID_COL = 1;

    private static final String[] FIELDS = {"Tracking Number: Mdi Case Number", "Tracking Number: EDRS File Number"
        ,"Decedent Name", "Decedent Race", "Decedent Ethnicity", "Decedent SexAtDeath", "Decedent SSN", "Decedent Age", "Decedent DOB"
        ,"Decedent Marital status", "Decedent Residence: Street", "Decedent Residence: city","Decedent Residence: county","Decedent Residence: State, U.S. Territory or Canadian Province", "Decedent Residence: Postal Code", "Decedent Residence: Country"
        ,"Cause of Death Part I Line a", "Cause of Death Part I Line b", "Cause of Death Part I Line c", "Cause of Death Part I Line d", "Cause of Death Part I Interval, Line a"
        ,"Cause of Death Part I Interval, Line b","Cause of Death Part I Interval, Line c","Cause of Death Part I Interval, Line d","Cause of Death Part II", "Manner of Death"
        ,"Date of Injury", "Time of Injury", "Estimated Date of Injury Interval: Earliest", "Estimated Date of Injury Interval: Latest", "Did Injury Occur at Work?", "Decedent's Transportation Role During Injury"
        ,"Location of Death","Location of Death: Street","Location of Death: City","Location of Death: County","Location of Death: State, U.S. Territory or Canadian Province","Location of Death: Postal Code","Location of Death: Country",
        "Location of Injury","Place of death","Pregnancy status","Did Tobacco Use Contribute to Death?"
        ,"Decedent Date of death","Decedent Time of death","Date establishment method","Estimated Date of Death Interval: Earliest", "Estimated Date of Death Interval: Latest","Date pronounced dead","Time pronounced dead","Pronouncer of death","Place of death"
        ,"Autopsy Performed?", "Autopsy Results Available?", "Autopsy Performed Office Name", "Autopsy Performed Location: Street", "Autopsy Performed Location: City"
        ,"Autopsy Performed Location: County","Autopsy Performed Location: State, U.S. Territory or Canadian Province", "Autopsy Performed Location: Postal Code"
        ,"Did Injury Occur at Work?", "How injury occurred"
        ,"Medical Examiner Name","Medical Examiner Phone Number", "Medical Examiner License Number"
        ,"Medical Examiner Office: Street", "Medical Examiner Office: City", "Medical Examiner Office: County", "Medical Examiner Office: State, U.S. Territory or Canadian Province", "Medical Examiner Office: Postal Code"
        ,"Certifier Name", "Certifier Type", "Certifier Identifier", "Certifier Identifier System", "Case History"};
    private static final String endCapColumnHeader = "End of Cases"; //Cell text we expect at the end of the row.

    public List<MDIAndEDRSModelFields> convertToMDIModelFields(XSSFWorkbook workbook) throws Exception{
        List<MDIAndEDRSModelFields> returnList = new ArrayList<MDIAndEDRSModelFields>();
        XSSFSheet sheet = workbook.getSheetAt(0); //Assuming first sheet for now
        XSSFRow headerRow = sheet.getRow(0); //Header should be first row
        Cell headerElementCell = findCellFromRow(headerRow, ELEMENT_HEADER);
        if(headerElementCell == null){
            throw new Exception("Couldn't find field header cell in row 1. Are you sure the template file is the correct file?");
        }
        //Grab the file id
        file_id = sheet.getRow(FILE_ID_ROW).getCell(FILE_ID_COL).getStringCellValue();
        //Creating mapping of elements 
        int elementColumnIndex = headerElementCell.getColumnIndex();
        Map<String, Integer> fieldMap = createFieldsToRowMap(sheet, elementColumnIndex);
        int currentColumn = 1;
        Cell headerCell = sheet.getRow(0).getCell(currentColumn);
        Cell nameCell = sheet.getRow(fieldMap.get("Decedent Name")).getCell(currentColumn);
        while(!headerCell.getStringCellValue().equals(endCapColumnHeader) && !headerCell.getStringCellValue().isEmpty()){ //Check if we're at the end of the sheet
            if(nameCell != null && !nameCell.getStringCellValue().isEmpty() && headerCell.getStringCellValue().contains("Case")){ //A decedent name must be provided
                MDIAndEDRSModelFields mdiFields = convertColumnToModelFields(currentColumn, sheet, fieldMap);
                returnList.add(mdiFields);
            }
            currentColumn++;
            headerCell = sheet.getRow(0).getCell(currentColumn);
            nameCell = sheet.getRow(fieldMap.get("Decedent Name")).getCell(currentColumn);
        }
        return returnList;
    }

    public Map<String, Integer> createFieldsToRowMap(XSSFSheet sheet, int elementColumnIndex){
        Map<String, Integer> returnMap = new HashMap<String, Integer>();
        for(String fieldName:FIELDS){
            try{
                int rowOffset = findCellFromColumn(sheet, elementColumnIndex, fieldName).getRowIndex();
                returnMap.put(fieldName, Integer.valueOf(rowOffset));
            }
            catch(NullPointerException e){
                logger.debug("Couldn't find field '"+fieldName+"'.");
            }
        }
        return returnMap;
    }

    public MDIAndEDRSModelFields convertColumnToModelFields(int currentColumn, XSSFSheet sheet, Map<String, Integer> fieldMap) throws Exception{
        MDIAndEDRSModelFields returnModel = new MDIAndEDRSModelFields(); //This is going to be replaced SOON with new sheet column definitions!
        //Every model is going to have a similar base fhir id
        returnModel.setBASEFHIRID(file_id+ "-" + (currentColumn - 2) + "-");
        handleAge(returnModel, currentColumn, sheet, fieldMap);
        handleName(returnModel, currentColumn, sheet, fieldMap);
        returnModel.setATWORK(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Did Injury Occur at Work?"));
        returnModel.setAUTOPSYPERFORMED(getStringForColumnAndName(sheet, returnModel,  fieldMap, currentColumn, "Autopsy Performed?"));
        returnModel.setAUTOPSYRESULTSAVAILABLE(getStringForColumnAndName(sheet, returnModel,  fieldMap, currentColumn, "Autopsy Results Available?"));
        //New fields
        returnModel.setAUTOPSY_OFFICENAME(getStringForColumnAndName(sheet, returnModel,  fieldMap, currentColumn, "Autopsy Performed Office Name"));
        returnModel.setAUTOPSY_STREET(getStringForColumnAndName(sheet, returnModel,  fieldMap, currentColumn, "Autopsy Performed Location: Street"));
        returnModel.setAUTOPSY_CITY(getStringForColumnAndName(sheet, returnModel,  fieldMap, currentColumn, "Autopsy Performed Location: City"));
        returnModel.setAUTOPSY_COUNTY(getStringForColumnAndName(sheet, returnModel,  fieldMap, currentColumn, "Autopsy Performed Location: County"));
        returnModel.setAUTOPSY_STATE(getStringForColumnAndName(sheet, returnModel,  fieldMap, currentColumn, "Autopsy Performed Location: State, U.S. Territory or Canadian Province"));
        returnModel.setAUTOPSY_ZIP(getStringForColumnAndName(sheet, returnModel,  fieldMap, currentColumn, "Autopsy Performed Location: Postal Code"));
        returnModel.setBIRTHDATE(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Decedent DOB"));
        returnModel.setCASENOTES(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Case History"));
        returnModel.setCAUSEA(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Cause of Death Part I Line a"));
        returnModel.setCAUSEB(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Cause of Death Part I Line b"));
        returnModel.setCAUSEC(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Cause of Death Part I Line c"));
        returnModel.setCAUSED(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Cause of Death Part I Line d"));
        returnModel.setCERTIFIER_IDENTIFIER(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Certifier Identifier"));
        returnModel.setCERTIFIER_IDENTIFIER_SYSTEM(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Certifier Identifier System"));
        returnModel.setCERTIFIER_NAME(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Certifier Name"));
        returnModel.setCERTIFIER_TYPE(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Certifier Type"));
        returnModel.setCHOWNINJURY(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"How injury occurred"));
        returnModel.setCINJDATE(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Date of Injury"));
        returnModel.setCINJTIME(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Time of Injury"));
        returnModel.setDEATHLOCATION_STREET(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Location of Death: Street"));
        returnModel.setDEATHLOCATION_CITY(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Location of Death: City"));
        returnModel.setDEATHLOCATION_COUNTY(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Location of Death: County"));
        returnModel.setDEATHLOCATION_STATE(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Location of Death: State, U.S. Territory or Canadian Province"));
        returnModel.setDEATHLOCATION_ZIP(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Location of Death: Postal Code"));
        returnModel.setDEATHLOCATION_COUNTRY(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Location of Death: Country"));
        returnModel.setDEATHLOCATIONTYPE(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Place of death"));
        returnModel.setINJURYLOCATION(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Location of Injury"));
        returnModel.setCDEATHDATE(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Decedent Date of death"));
        returnModel.setCDEATHTIME(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Decedent Time of death"));
        returnModel.setCDEATHESTABLISHEMENTMETHOD(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Date establishment method"));
        returnModel.setETHNICITY(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Decedent Ethnicity"));
        returnModel.setEDRSCASEID(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Tracking Number: EDRS File Number"));
        returnModel.setDURATIONA(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Cause of Death Part I Interval, Line a"));
        returnModel.setDURATIONB(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Cause of Death Part I Interval, Line b"));
        returnModel.setDURATIONC(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Cause of Death Part I Interval, Line c"));
        returnModel.setDURATIOND(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Cause of Death Part I Interval, Line d"));
        returnModel.setGENDER(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Decedent SexAtDeath"));
        returnModel.setMANNER(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Manner of Death"));
        returnModel.setMELICENSE(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Medical Examiner License Number"));
        returnModel.setMENAME(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Medical Examiner Name"));
        returnModel.setMEPHONE(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Medical Examiner Phone Number"));
        returnModel.setME_STREET(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Medical Examiner Office: Street"));
        returnModel.setME_CITY(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Medical Examiner Office: City"));
        returnModel.setME_COUNTY(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Medical Examiner Office: County"));
        returnModel.setME_STATE(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Medical Examiner Office: State, U.S. Territory or Canadian Province"));
        returnModel.setME_ZIP(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Medical Examiner Office: Postal Code"));
        returnModel.setMDICASEID(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Tracking Number: Mdi Case Number"));
        returnModel.setMARITAL(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Decedent Marital status"));
        returnModel.setMRNNUMBER(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Decedent SSN"));
        returnModel.setOSCOND(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Cause of Death Part II"));
        returnModel.setPRNDATE(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Date pronounced dead"));
        returnModel.setPRNTIME(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Time pronounced dead"));
        returnModel.setPRONOUNCERNAME(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Pronouncer of death"));
        returnModel.setRACE(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Decedent Race"));
        returnModel.setRESSTREET(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Decedent Residence: Street"));
        returnModel.setRESCITY(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Decedent Residence: city"));
        returnModel.setRESCOUNTY(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Decedent Residence: county"));
        returnModel.setRESSTATE(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Decedent Residence: State, U.S. Territory or Canadian Province"));
        returnModel.setRESZIP(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Decedent Residence: Postal Code"));
        returnModel.setRESCOUNTRY(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Decedent Residence: Country"));
        returnModel.setPREGNANT(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Pregnancy status"));        
        returnModel.setTOBACCO(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Did Tobacco Use Contribute to Death?"));
        returnModel.setTRANSPORTATION(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Decedent's Transportation Role During Injury"));
        return returnModel;
    }

    protected MDIAndEDRSModelFields handleAge(MDIAndEDRSModelFields returnModel, int currentColumn, XSSFSheet sheet, Map<String, Integer> fieldMap){
        String ageValue = getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Decedent Age");
        ageValue.trim();
        String[] numberAndUnit = ageValue.split("\\s+"); //Looking for number + unit
        if(numberAndUnit[0] != null){
            returnModel.setAGE(numberAndUnit[0]);
        }
        else{
            returnModel.getErrorListForName("AGE").add("Could not determine age from value '"+ageValue+"'.");
        }
        if(numberAndUnit.length > 1 && numberAndUnit[1] != null){
            returnModel.setAGEUNIT(numberAndUnit[1]);
        }
        else{
            returnModel.setAGEUNIT("years");
            returnModel.getErrorListForName("AGEUNIT").add("Could not determine age unit from value '"+ageValue+"' assuming a default unit of 'years'.");
        }
        return returnModel;
    }

    protected MDIAndEDRSModelFields handleName(MDIAndEDRSModelFields returnModel, int currentColumn, XSSFSheet sheet, Map<String, Integer> fieldMap){
        String fullName = getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Decedent Name");
        Pattern suffixPattern = Pattern.compile("(?<Suffix>Jr\\.|Sr\\.|IV|III|II|)");
        Matcher suffixMatcher = suffixPattern.matcher(fullName);
        if(suffixMatcher.matches()){
            String suffixFound = suffixMatcher.group("Suffix");
            returnModel.setSUFFIXNAME(suffixFound);
            //Remove suffix from the system to make it easier to parse the first, middle, and last name
            fullName.replaceAll(suffixFound, "");
        }
        Pattern fullNamePattern = Pattern.compile("([\\w-]+)\\s+([\\w-]+)(\\s+([\\w-]+))?");
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

    public Cell findCellFromRow(XSSFRow row, String cellValue){
        Iterator<Cell> iterator = row.cellIterator();
        while(iterator.hasNext()){
            Cell cell = iterator.next();
            if(lintAndCompareStrings(cell.getStringCellValue(),cellValue)){
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
            if(cell != null && cell.getStringCellValue().equalsIgnoreCase(cellValue)){
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

    private String getStringForColumnAndName(XSSFSheet sheet, MDIAndEDRSModelFields model, Map<String, Integer> fieldMap, int columnIndex,String name){
        if(fieldMap.get(name) == null){
            model.getErrorListForName(name).add("No value found for key '"+name+"'.");
            return "";
        }
        XSSFCell targetCell = sheet.getRow(fieldMap.get(name)).getCell(columnIndex);
        //Special Handling for date cell value of "default date format" sometimes causes issues with locales so override to force a standardized mm/dd/yyyy format 
        if(targetCell.getCellStyle().getDataFormat() == 14 && targetCell.getCellType() != CellType.BLANK){
            String correctedDateFormat= "mm/dd/yyyy";
            return new CellDateFormatter(correctedDateFormat).format(targetCell.getDateCellValue());
        }
        DataFormatter formatter = new DataFormatter();
        return formatter.formatCellValue(targetCell);
    }

    private boolean lintAndCompareStrings(String left, String right){
        String leftLinted = left.trim().replaceAll("\\s+","").toLowerCase();
        String rightLinted = right.trim().replaceAll("\\s+","").toLowerCase();
        return leftLinted.contentEquals(rightLinted);
    }
}