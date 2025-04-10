package edu.gatech.chai.Mapping.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.format.CellDateFormatter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.gatech.chai.MDI.Model.DCRModelFields;
import edu.gatech.chai.MDI.Model.MDIAndEDRSModelFields;


@Service
public class XLSXToDCRModelService{
    @Autowired
    XLSXToMDIAndEDRSModelService xLSXToMDIAndEDRSModelService;
    
    private static final Logger logger = LoggerFactory.getLogger(XLSXToToxToMDIModelService.class);
    private static final String ELEMENT_HEADER = "Elements";

    private static final String[] FIELDS = {"Message Reason", "Submittor Name", "Submittor Email"
        ,"Funeral Home Name", "Funeral Home Address: Street", "Funeral Home Address: City", "Funeral Home Address: County"
        ,"Funeral Home Address: State, U.S. Territory or Canadian Province", "Funeral Home Address: Country", "Funeral Home Phone Number"
        ,"Funeral Home Fax Number"};
    private static final String endCapColumnHeader = "End of Cases"; //Cell text we expect at the end of the row.

    public List<DCRModelFields> convertToMDIModelFields(XSSFWorkbook workbook) throws Exception{
        List<DCRModelFields> returnList = new ArrayList<DCRModelFields>();
        List<MDIAndEDRSModelFields> parentList = xLSXToMDIAndEDRSModelService.convertToMDIModelFields(workbook); //Get a set of MDI and EDRS models to extend upon for DCR
        XSSFSheet sheet = workbook.getSheetAt(1); //Assuming second sheet for DCR for now
        XSSFRow headerRow = sheet.getRow(0); //Header should be first row
        Cell headerElementCell = findCellFromRow(headerRow, ELEMENT_HEADER);
        if(headerElementCell == null){
            throw new Exception("Couldn't find field header cell in row 1. Are you sure the template file is the correct file?");
        }
        //Creating mapping of elements 
        int elementColumnIndex = headerElementCell.getColumnIndex();
        Map<String, Integer> fieldMap = createFieldsToRowMap(sheet, elementColumnIndex);
        int currentColumn = 1;
        int currentMDICaseInList = 0;
        Cell headerCell = sheet.getRow(0).getCell(currentColumn);
        while(!headerCell.getStringCellValue().equals(endCapColumnHeader) && !headerCell.getStringCellValue().isEmpty() && currentMDICaseInList < parentList.size()){ //Check if we're at the end of the sheet
            if(headerCell.getStringCellValue().contains("Case")){ //A decedent name must be provided
                DCRModelFields returnModel = new DCRModelFields(parentList.get(currentMDICaseInList)); //Create a DCR model based on the MDIandEDRS model from the other service
                returnModel = convertColumnToModelFields(returnModel, currentColumn, sheet, fieldMap);
                returnList.add(returnModel);
                currentMDICaseInList++;
            }
            currentColumn++;
            headerCell = sheet.getRow(0).getCell(currentColumn);
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

    public DCRModelFields convertColumnToModelFields(DCRModelFields returnModel,int currentColumn, XSSFSheet sheet, Map<String, Integer> fieldMap) throws Exception{ 
        returnModel.setMESSAGE_REASON(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Message Reason"));
        returnModel.setSUBMITTOR_NAME(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Submittor Name"));
        returnModel.setSUBMITTOR_PHONE(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Submittor Phone Number"));
        returnModel.setSUBMITTOR_FAX(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Submittor Fax Number"));
        returnModel.setSUBMITTOR_EMAIL(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Submittor Email"));
        returnModel.setFUNERALHOME_NAME(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Funeral Home Name"));
        returnModel.setFUNERALHOME_STREET(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Funeral Home Address: Street"));
        returnModel.setFUNERALHOME_CITY(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Funeral Home Address: City"));
        returnModel.setFUNERALHOME_COUNTY(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Funeral Home Address: County"));
        returnModel.setFUNERALHOME_STATE(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Funeral Home Address: State, U.S. Territory or Canadian Province"));
        returnModel.setFUNERALHOME_ZIP(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Funeral Home Address: Postal Code"));
        returnModel.setFUNERALHOME_COUNTRY(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Funeral Home Address: Country"));
        returnModel.setFUNERALHOME_PHONE(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Funeral Home Phone Number"));
        returnModel.setFUNERALHOME_FAX(getStringForColumnAndName(sheet, returnModel, fieldMap,currentColumn,"Funeral Home Fax Number"));
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
        DataFormatter formatter = new DataFormatter();
        XSSFCell targetCell = sheet.getRow(fieldMap.get(name)).getCell(columnIndex);
        //Special Handling for date cell value of "default date format" sometimes causes issues with locales so override to force a standardized mm/dd/yyyy format 
        if(targetCell.getCellStyle().getDataFormat() == 14){
            String correctedDateFormat= "mm/dd/yyyy";
            return new CellDateFormatter(correctedDateFormat).format(targetCell.getDateCellValue());
        }
        return formatter.formatCellValue(targetCell);
    }

    private boolean lintAndCompareStrings(String left, String right){
        String leftLinted = left.trim().replaceAll("\\s+","").toLowerCase();
        String rightLinted = right.trim().replaceAll("\\s+","").toLowerCase();
        return leftLinted.contentEquals(rightLinted);
    }
}