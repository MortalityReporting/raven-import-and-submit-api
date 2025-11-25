package edu.gatech.chai.Mapping.Util;

import java.text.ParseException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hl7.fhir.exceptions.FHIRException;
import org.hl7.fhir.r4.model.CodeType;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Enumerations.DataAbsentReasonEnumFactory;
import org.hl7.fhir.r4.model.codesystems.DataAbsentReason;

import edu.gatech.chai.MDI.Model.BaseModelFields;
import edu.gatech.chai.MDI.Model.ToxToMDIModelFields;
import edu.gatech.chai.VRDR.model.util.CommonUtil;

public class CommonMappingUtil {
	public static String trueValueRegex = "yes|true|y";
	public static String unknownValueRegex = "Unknown|Unk|\\?";
	
	public static boolean parseBoolean(String boolString) {
		Pattern r = Pattern.compile(trueValueRegex);
	    Matcher m = r.matcher(boolString.toLowerCase());
	    return m.find();
	}
	
	public static CodeableConcept parseBooleanAndCreateCode(String boolString) {
		boolean value = CommonMappingUtil.parseBoolean(boolString);
		if(value) {
			return CommonUtil.yesCode;
		}
		Pattern r = Pattern.compile(unknownValueRegex);
	    Matcher m = r.matcher(boolString.toLowerCase());
	    if(m.find()) {
	    	return CommonUtil.unknownCode;
	    }
		else {
			return CommonUtil.noCode;
		}
	}

	public static Date parseDateFromField(BaseModelFields modelFields, String fieldName, String fieldValue){
		Date returnDate = null;
		try{
			if(fieldValue != null && !fieldValue.isEmpty()){
				returnDate = LocalModelToFhirCMSUtil.parseDate(fieldValue);
			}
		}
		catch(ParseException e){
			modelFields.getErrorListForName(fieldName).add("Could not parse '"+fieldValue+"' into a date type");
		}
		return returnDate;
	}

	public static Date parseTimeFromField(BaseModelFields modelFields, String fieldName, String fieldValue){
		Date returnDate = null;
		try{
			if(fieldValue != null && !fieldValue.isEmpty()){
				returnDate = LocalModelToFhirCMSUtil.parseTime(fieldValue);
			}
		}
		catch(ParseException e){
			modelFields.getErrorListForName(fieldName).add("Could not parse '"+fieldValue+"' into a time type");
		}
		return returnDate;
	}

	public static Date parseDateTimeFromField(BaseModelFields modelFields, String fieldName, String fieldValue){
		Date returnDate = null;
		try{
			if(fieldValue != null && !fieldValue.isEmpty()){
				returnDate = LocalModelToFhirCMSUtil.parseDateAndTime(fieldValue);
			}
		}
		catch(ParseException e){
			modelFields.getErrorListForName(fieldName).add("Could not parse '"+fieldValue+"' into a datetime type");
		}
		return returnDate;
	}

	public static Extension getDataAbsentReason(String code) {
		if (code == null || code.isBlank()) {
			code = "masked";
		}

		DataAbsentReason dataAbsentReason;
		try {
			dataAbsentReason = DataAbsentReason.fromCode(code);
		} catch (FHIRException e) {
			e.printStackTrace();
			dataAbsentReason = DataAbsentReason.MASKED;
		}
			
		Extension extension = new Extension("http://hl7.org/fhir/StructureDefinition/data-absent-reason", new CodeType(dataAbsentReason.toCode()));

		return extension;
	}
}
