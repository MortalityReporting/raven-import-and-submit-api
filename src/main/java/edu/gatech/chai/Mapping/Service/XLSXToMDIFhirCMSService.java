package edu.gatech.chai.Mapping.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import edu.gatech.chai.MDI.Model.MDIModelFields;

@Service
public class XLSXToMDIFhirCMSService {
    private static final int FIRST_ROW = 1;
    // private static final String SECTION_HEADER = "Highlighted yellow items have been changed"; //Note this will ofc change to a reasonable "section name" in the future
    private static final String ELEMENT_HEADER = "Elements";

    private static final String[] FIELDS = {"Tracking Number: Mdi Case Number", "Tracking Number: EDRS File Number"
        ,"Decedent Name", "Decedent Race", "Decedent Ethnicity", "Decedent SexAtDeath", "Decedent SSN", "Decedent Age", "Decedent DOB"
        ,"Decedent Marital status", "Decedent Residence: Street", "Decedent Residence: city","Decedent Residence: county","Decedent Residence: State, U.S. Territory or Canadian Province", "Decedent Residence: Postal Code", "Decedent Residence: Country"
        ,"Cause of Death Part I Line a", "Cause of Death Part I Line b", "Cause of Death Part I Line c", "Cause of Death Part I Line d", "Cause of Death Part I Interval, Line a"
        ,"Cause of Death Part I Interval, Line b","Cause of Death Part I Interval, Line c","Cause of Death Part I Interval, Line d","Cause of Death Part II", "Manner of Death"
        ,"Date of Injury", "Time of Injury", "Estimated Date of Injury Interval: Earliest", "Estimated Date of Injury Interval: Latest", "Did Injury Occur at Work?", "Decedent's Transportation Role During Injury"
        ,"Location of Death","Location of Injury","Place of death","Pregnancy status","Did Tobacco Use Contribute to Death?"
        ,"Decedent Date of death","Decedent Time of death","Date establishment method","Estimated Date of Death Interval: Earliest", "Estimated Date of Death Interval: Latest","Date pronounced dead","Time pronounced dead","Place of death"
        ,"Autopsy Performed?", "Autopsy Results Available?"
        ,"Did Injury Occur at Work?", "How injury occurred"
        ,"Medical Examiner Name","Medical Examiner Phone Number", "Medical Examiner License Number"
        ,"Medical Examiner Office: Street", "Medical Examiner Office: City", "Medical Examiner Office: County", "Medical Examiner Office: State, U.S. Territory or Canadian Province", "Medical Examiner Office: Postal Code"
        ,"Certifier Name", "Certifier Type"};
    private static final String endCapColumnHeader = "End of Cases"; //Cell text we expect at the end of the row.

    public List<MDIModelFields> convertToMDIModelFields(XSSFWorkbook workbook) throws Exception{
        List<MDIModelFields> returnList = new ArrayList<MDIModelFields>();
        XSSFSheet sheet = workbook.getSheetAt(0); //Assuming first sheet for now
        XSSFRow headerRow = sheet.getRow(0); //Header should be first row
        Cell headerElementCell = findCellFromRow(headerRow, ELEMENT_HEADER);
        if(headerElementCell == null){
            throw new Exception("Couldn't find field header cell in row 1. Are you sure the template file is the correct file?");
        }
        int elementColumnIndex = headerElementCell.getColumnIndex();
        
        Map<String, Integer> fieldMap = createFieldsToRowMap(sheet, elementColumnIndex);
        int currentColumn = 1;
        Cell headerCell = sheet.getRow(0).getCell(currentColumn);
        Cell nameCell = sheet.getRow(fieldMap.get("Decedent Name")).getCell(currentColumn);
        while(!headerCell.getStringCellValue().equals(endCapColumnHeader) && !headerCell.getStringCellValue().isEmpty()){ //Check if we're at the end of the sheet
            if(nameCell != null && !nameCell.getStringCellValue().isEmpty() && headerCell.getStringCellValue().contains("Case")){ //A decedent name must be provided
                MDIModelFields mdiFields = convertColumnToModelFields(currentColumn, sheet, fieldMap);
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
                System.out.println("Couldn't find field '"+fieldName+"'.");
            }
        }
        return returnMap;
    }

    public MDIModelFields convertColumnToModelFields(int currentColumn, XSSFSheet sheet, Map<String, Integer> fieldMap){
        MDIModelFields returnModel = new MDIModelFields(); //This is going to be replaced SOON with new sheet column definitions!
        handleAge(returnModel, currentColumn, sheet, fieldMap);
        handleName(returnModel, currentColumn, sheet, fieldMap);
        returnModel.setAUTOPSYPERFORMED(getStringForColumnAndName(sheet, fieldMap, currentColumn, "Autopsy Performed?"));
        returnModel.setAUTOPSYRESULTSAVAILABLE(getStringForColumnAndName(sheet, fieldMap, currentColumn, "Autopsy Results Available?"));
        returnModel.setATWORK(getStringForColumnAndName(sheet,fieldMap,currentColumn,"Did Injury Occur at Work?"));
        returnModel.setBIRTHDATE(getStringForColumnAndName(sheet,fieldMap,currentColumn,"Decedent DOB"));
        returnModel.setCASENOTES(getStringForColumnAndName(sheet,fieldMap,currentColumn,"Case History"));
        returnModel.setCAUSEA(getStringForColumnAndName(sheet,fieldMap,currentColumn,"Cause of Death Part I Line a"));
        returnModel.setCAUSEB(getStringForColumnAndName(sheet,fieldMap,currentColumn,"Cause of Death Part I Line b"));
        returnModel.setCAUSEC(getStringForColumnAndName(sheet,fieldMap,currentColumn,"Cause of Death Part I Line c"));
        returnModel.setCAUSED(getStringForColumnAndName(sheet,fieldMap,currentColumn,"Cause of Death Part I Line d"));
        returnModel.setCERTIFIER_NAME(getStringForColumnAndName(sheet,fieldMap,currentColumn,"Certifier Name"));
        returnModel.setCERTIFIER_TYPE(getStringForColumnAndName(sheet,fieldMap,currentColumn,"Certifier Type"));
        returnModel.setCHOWNINJURY(getStringForColumnAndName(sheet,fieldMap,currentColumn,"How injury occurred"));
        returnModel.setCINJDATE(getStringForColumnAndName(sheet,fieldMap,currentColumn,"Date of Injury"));
        returnModel.setCINJTIME(getStringForColumnAndName(sheet,fieldMap,currentColumn,"Time of Injury"));
        //New Fields
        returnModel.setDEATHLOCATION(getStringForColumnAndName(sheet,fieldMap,currentColumn,"Location of Death"));
        returnModel.setDEATHLOCATIONTYPE(getStringForColumnAndName(sheet,fieldMap,currentColumn,"Place of death"));
        returnModel.setINJURYLOCATION(getStringForColumnAndName(sheet,fieldMap,currentColumn,"Location of Injury"));

        returnModel.setCDEATHDATE(getStringForColumnAndName(sheet,fieldMap,currentColumn,"Decedent Date of death"));
        returnModel.setCDEATHTIME(getStringForColumnAndName(sheet,fieldMap,currentColumn,"Decedent Time of death"));
        returnModel.setCDEATHESTABLISHEMENTMETHOD(getStringForColumnAndName(sheet,fieldMap,currentColumn,"Date establishment method"));
        returnModel.setETHNICITY(getStringForColumnAndName(sheet,fieldMap,currentColumn,"Decedent Ethnicity"));
        returnModel.setEDRSCASEID(getStringForColumnAndName(sheet,fieldMap,currentColumn,"Tracking Number: EDRS File Number"));
        returnModel.setDURATIONA(getStringForColumnAndName(sheet,fieldMap,currentColumn,"Cause of Death Part I Interval, Line a"));
        returnModel.setDURATIONB(getStringForColumnAndName(sheet,fieldMap,currentColumn,"Cause of Death Part I Interval, Line b"));
        returnModel.setDURATIONC(getStringForColumnAndName(sheet,fieldMap,currentColumn,"Cause of Death Part I Interval, Line c"));
        returnModel.setDURATIOND(getStringForColumnAndName(sheet,fieldMap,currentColumn,"Cause of Death Part I Interval, Line d"));
        returnModel.setETHNICITY(getStringForColumnAndName(sheet,fieldMap,currentColumn,"Decedent Ethnicity"));
        returnModel.setGENDER(getStringForColumnAndName(sheet,fieldMap,currentColumn,"Decedent SexAtDeath"));
        returnModel.setMANNER(getStringForColumnAndName(sheet,fieldMap,currentColumn,"Manner of Death"));
        returnModel.setMELICENSE(getStringForColumnAndName(sheet,fieldMap,currentColumn,"Medical Examiner License Number"));
        returnModel.setMENAME(getStringForColumnAndName(sheet,fieldMap,currentColumn,"Medical Examiner Name"));
        returnModel.setMEPHONE(getStringForColumnAndName(sheet,fieldMap,currentColumn,"Medical Examiner Phone Number"));
        returnModel.setME_STREET(getStringForColumnAndName(sheet,fieldMap,currentColumn,"Medical Examiner Office: Street"));
        returnModel.setME_CITY(getStringForColumnAndName(sheet,fieldMap,currentColumn,"Medical Examiner Office: City"));
        returnModel.setME_COUNTY(getStringForColumnAndName(sheet,fieldMap,currentColumn,"Medical Examiner Office: County"));
        returnModel.setME_STATE(getStringForColumnAndName(sheet,fieldMap,currentColumn,"Medical Examiner Office: State, U.S. Territory or Canadian Province"));
        returnModel.setME_ZIP(getStringForColumnAndName(sheet,fieldMap,currentColumn,"Medical Examiner Office: Postal Code"));
        returnModel.setMDICASEID(getStringForColumnAndName(sheet,fieldMap,currentColumn,"Tracking Number: Mdi Case Number"));
        returnModel.setMARITAL(getStringForColumnAndName(sheet,fieldMap,currentColumn,"Decedent Marital status"));
        returnModel.setMRNNUMBER(getStringForColumnAndName(sheet,fieldMap,currentColumn,"Decedent SSN"));
        returnModel.setOSCOND(getStringForColumnAndName(sheet,fieldMap,currentColumn,"Cause of Death Part II"));
        returnModel.setPRNDATE(getStringForColumnAndName(sheet,fieldMap,currentColumn,"Date pronounced dead"));
        returnModel.setPRNTIME(getStringForColumnAndName(sheet,fieldMap,currentColumn,"Time pronounced dead"));
        returnModel.setRACE(getStringForColumnAndName(sheet,fieldMap,currentColumn,"Decedent Race"));
        returnModel.setRESSTREET(getStringForColumnAndName(sheet,fieldMap,currentColumn,"Decedent Residence: Street"));
        returnModel.setRESCITY(getStringForColumnAndName(sheet,fieldMap,currentColumn,"Decedent Residence: city"));
        returnModel.setRESCOUNTY(getStringForColumnAndName(sheet,fieldMap,currentColumn,"Decedent Residence: county"));
        returnModel.setRESSTATE(getStringForColumnAndName(sheet,fieldMap,currentColumn,"Decedent Residence: State, U.S. Territory or Canadian Province"));
        returnModel.setRESZIP(getStringForColumnAndName(sheet,fieldMap,currentColumn,"Decedent Residence: Postal Code"));
        returnModel.setRESCOUNTRY(getStringForColumnAndName(sheet,fieldMap,currentColumn,"Decedent Residence: Country"));
        returnModel.setPREGNANT(getStringForColumnAndName(sheet,fieldMap,currentColumn,"Pregnancy status"));        
        returnModel.setTOBACCO(getStringForColumnAndName(sheet,fieldMap,currentColumn,"Did Tobacco Use Contribute to Death?"));
        returnModel.setTRANSPORTATION(getStringForColumnAndName(sheet,fieldMap,currentColumn,"Decedent's Transportation Role During Injury"));
        return returnModel;
    }

    protected MDIModelFields handleAge(MDIModelFields returnModel, int currentColumn, XSSFSheet sheet, Map<String, Integer> fieldMap){
        String ageValue = getStringForColumnAndName(sheet,fieldMap,currentColumn,"Decedent Age");
        ageValue.trim();
        String[] numberAndUnit = ageValue.split("\\s+"); //Looking for number + unit
        if(numberAndUnit[0] != null){
            returnModel.setAGE(numberAndUnit[0]);
        }
        if(numberAndUnit.length > 1 && numberAndUnit[1] != null){
            returnModel.setAGEUNIT(numberAndUnit[1]);
        }
        else{
            returnModel.setAGEUNIT("years");
        }
        return returnModel;
    }

    protected MDIModelFields handleName(MDIModelFields returnModel, int currentColumn, XSSFSheet sheet, Map<String, Integer> fieldMap){
        String fullName = getStringForColumnAndName(sheet,fieldMap,currentColumn,"Decedent Name");
        Pattern pattern = Pattern.compile("(\\w+)\\s+(\\w+)(\\s+(\\w+))?");
        Matcher matcher = pattern.matcher(fullName);
        if(matcher.groupCount() == 4){
            matcher.matches();
            if(matcher.group(3) != null){
                returnModel.setFIRSTNAME(matcher.group(1));
                returnModel.setMIDNAME(matcher.group(2));
                returnModel.setLASTNAME(matcher.group(3));
            }
            else{
                returnModel.setFIRSTNAME(matcher.group(1));
                returnModel.setLASTNAME(matcher.group(2));
            }
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

    private String getStringForColumnAndName(XSSFSheet sheet, Map<String, Integer> fieldMap, int columnIndex,String name){
        if(fieldMap.get(name) == null){
            System.out.println("Couldn't find key: '"+name+"' in excel sheet.");
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
