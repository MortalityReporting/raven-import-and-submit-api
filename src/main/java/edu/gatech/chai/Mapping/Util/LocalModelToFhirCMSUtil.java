package edu.gatech.chai.Mapping.Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Age;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.Bundle.BundleEntryRequestComponent;
import org.hl7.fhir.r4.model.Bundle.HTTPVerb;
import org.hl7.fhir.r4.model.Quantity.QuantityComparator;

import ca.uhn.fhir.model.dstu2.valueset.QuantityComparatorEnum;

import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Resource;

public class LocalModelToFhirCMSUtil {
	public static List<String> dateFormatStrings = Arrays.asList("yyyy-MM-dd","MM-dd-yyyy", "MM/dd/yyyy", "yyyy-MM-dd", "M/d/yy", "M/d/yyyy", "d-M-yy",
			"Mdyyyy", "Mdyy", "d-M-yyyy", "d MMMM yy", "d MMMM yyyy", "d MMMM yy zzzz",
			"d MMMM yyyy zzzz", "E, d MMM yy","E, d MMM yyyy","M-d-yy","M-d-yyyy", "MMMM DD, yy",
			"MMMM DD, yyyy", "yy", "yyyy");
	public static List<String> timeFormatStrings = Arrays.asList("hh:mm:ss a", "hh:mm a",
			"hh:mm:ss", "hh:mm","hhmm","hhmmss");
	public static String ageRegex = "(\\d+)\\s*(year|month|week|day|hour|minute)";
	public static String valueAndUnitRegex = "(\\<|\\<=|\\>=|\\>|ad)?s*(\\d+(\\.\\d+)?)\\s*(((P|T|G|M|k|h|da|d|c|m|μ|n|p)?(m|s|A|K|g|L))(\\/(P|T|G|M|k|h|da|d|c|m|μ|n|p)?(m|s|A|K|g|L))?)";
	public static List<String> nameFormatStrings = Arrays.asList("(.*),\\s{0,1}(.*)\\s(.*)", "(\\w+)\\s(\\w)[\\.]\\s(\\w+)", "(\\w+)\\s(\\w+)");
	public static String convertUnitOfMeasureStringToCode(String uomString) {
		switch(uomString) {
			case "minutes":
				return "min";
			case "hours":
				return "h";
			case "days":
				return "d";
			case "weeks":
				return "wk";
			case "months":
				return "mo";
			default:
				return "a";
		}
	}
	public static Date parseDateForYear(String yearString) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
    	sdf.setTimeZone(TimeZone.getDefault());
    	return sdf.parse(yearString);
	}
	public static Date parseDate(String dateString) throws ParseException {
		for (String formatString : dateFormatStrings)
	    {
	        try
	        {
	        	SimpleDateFormat sdf = new SimpleDateFormat(formatString);
	        	sdf.setTimeZone(TimeZone.getDefault());
	            return sdf.parse(dateString);
	        }
	        catch (ParseException e) {}
	    }
		throw new ParseException("Could not format date: "+dateString, 0);
	}
	public static Date parseTime(String timeString) throws ParseException {
		for (String formatString : timeFormatStrings)
	    {
	        try
	        {
	            SimpleDateFormat sdf = new SimpleDateFormat(formatString);
	            sdf.setTimeZone(TimeZone.getDefault());
	            return sdf.parse(timeString);
	        }
	        catch (ParseException e) {}
	    }
		throw new ParseException("Could not format time: "+timeString, 0);
	}
	public static Date addTimeToDate(Date date,String timeString) throws ParseException {
		Date timeDate = parseTime(timeString);
		date.setHours(timeDate.getHours());
		date.setMinutes(timeDate.getMinutes());
		date.setSeconds(timeDate.getSeconds());
		return date;
	}
	public static Date parseDateAndTime(String dateString, String timeString) throws ParseException {
		Date date = parseDate(dateString);
		date = addTimeToDate(date, timeString);
		return date;
	}

	public static Date parseDateAndTime(String dateAndTimeString) {
		LocalDateTime localDateTime = null;
		try{
			localDateTime = LocalDateTime.parse(dateAndTimeString);
		}
		catch (DateTimeParseException e) {}
		Date date = null;
		if (localDateTime != null){
			date = Date.from(localDateTime.toInstant(ZoneOffset.UTC));
			return date;
		}
		else{
			for(String dateString : dateFormatStrings){
				for(String timeString : timeFormatStrings){
					try{
						String dateTimeFormat = dateString + " " + timeString;
						SimpleDateFormat sdf = new SimpleDateFormat(dateTimeFormat);
	            		sdf.setTimeZone(TimeZone.getDefault());
	            		date = sdf.parse(dateAndTimeString);
						if(date != null)
							return date;
					}
					catch(ParseException e) {}
				}
			}
		}
		return null;
	}
	public static boolean containsIgnoreCase(String src, String what) {
	    final int length = what.length();
	    if (length == 0)
	        return true; // Empty string is contained

	    final char firstLo = Character.toLowerCase(what.charAt(0));
	    final char firstUp = Character.toUpperCase(what.charAt(0));

	    for (int i = src.length() - length; i >= 0; i--) {
	        // Quick check before calling the more expensive regionMatches() method:
	        final char ch = src.charAt(i);
	        if (ch != firstLo && ch != firstUp)
	            continue;

	        if (src.regionMatches(true, i, what, 0, length))
	            return true;
	    }

	    return false;
	}
	
	
	
	public static Bundle addResourceToBundle(Bundle bundle,Resource resource) {
		if(resource.getId() == null || resource.getId().isEmpty()) {
			resource.setId(new IdType(UUID.randomUUID().toString()));
		}
		BundleEntryComponent bec = new BundleEntryComponent();
		bec.setFullUrl(resource.getResourceType().name()+"/"+resource.getId());
		bec.setRequest(new BundleEntryRequestComponent().setMethod(HTTPVerb.POST));
		bec.setResource(resource);
		bundle.addEntry(bec);
		return bundle;
	}
	
	public static Age parseAge(String ageString) {
		Age returnAge = new Age();
		Pattern r = Pattern.compile(ageRegex);
	    Matcher m = r.matcher(ageString.toLowerCase());
		if(m.find()) {
			String quantity =  m.group(1);
			String type = m.group(2);
			returnAge.setValue(Double.parseDouble(quantity));
			returnAge.setSystem("http://hl7.org/fhir/ValueSet/age-units");
			switch(type) {
				case "year":
					returnAge.setUnit("a");
					returnAge.setCode("a");
					return returnAge;
				case "month":
					returnAge.setUnit("mo");
					returnAge.setCode("mo");
					return returnAge;
				case "week":
					returnAge.setUnit("wk");
					returnAge.setCode("wk");
					return returnAge;
				case "day":
					returnAge.setUnit("d");
					returnAge.setCode("d");
					return returnAge;
				case "hour":
					returnAge.setUnit("h");
					returnAge.setCode("h");
					return returnAge;
				case "minute":
					returnAge.setUnit("min");
					returnAge.setCode("min");
					return returnAge;
			}
		}

	    return null;
	}

	public static Quantity parseQuantity(String quantityString) {
		Quantity returnQuantity = new Quantity();
		Pattern r = Pattern.compile(valueAndUnitRegex, Pattern.MULTILINE);
	    Matcher m = r.matcher(quantityString);
		if(m.find()) {
			String comparatorSymbol =m.group(1);
			String decimalQuantityString =  m.group(2);
			String unitString = m.group(4);
			if(comparatorSymbol != null && !comparatorSymbol.isEmpty()){
				returnQuantity.setComparator(QuantityComparator.fromCode(comparatorSymbol));
			}
			returnQuantity.setValue(Double.parseDouble(decimalQuantityString));
			returnQuantity.setUnit(unitString);
			return returnQuantity;
		}
	    return null;
	}

	
	
	public static Address createAddress(String place, String street, String city,
			String county, String state, String zip, String country) {
		Address returnAddress = new Address();
		returnAddress.setText(place);
		returnAddress.addLine(street);
		returnAddress.setCity(city);
		returnAddress.setDistrict(county);
		returnAddress.setState(state);
		returnAddress.setPostalCode(zip);
		returnAddress.setCountry(country);
		return returnAddress;
	}

	public static HumanName parseHumanName(String name) {
		for(String nameFormat: nameFormatStrings) {
			Pattern pattern = Pattern.compile(nameFormat);
			Matcher matcher = pattern.matcher(name);
			if(matcher.find()) {
				if(matcher.groupCount() == 2){
					HumanName nameResource = new HumanName();
					nameResource.setFamily(matcher.group(2));
					nameResource.addGiven(matcher.group(1));
					return nameResource;
				}
				else if(matcher.groupCount() == 3){
					HumanName nameResource = new HumanName();
					nameResource.setFamily(matcher.group(3));
					nameResource.addGiven(matcher.group(1));
					nameResource.addGiven(matcher.group(2));
					return nameResource;
				}
			}
		}
		return null;
	}
}
