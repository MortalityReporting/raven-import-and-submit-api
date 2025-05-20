package edu.gatech.chai.Mapping.Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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
import org.hl7.fhir.r4.model.CodeType;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Quantity.QuantityComparator;
import org.hl7.fhir.r4.model.Reference;

import ca.uhn.fhir.model.dstu2.valueset.QuantityComparatorEnum;

import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.UriType;

public class LocalModelToFhirCMSUtil {
	public static List<String> dateFormatStrings = Arrays.asList("yyyyyMMdd", "yyyy-MM-dd","MM-dd-yyyy", "MM/dd/yyyy", "yyyy-MM-dd", "M/d/yy", "M/d/yyyy", "d-M-yy",
			"Mdyyyy", "Mdyy", "d-M-yyyy", "d MMMM yy", "d MMMM yyyy", "d MMMM yy zzzz",
			"d MMMM yyyy zzzz", "E, d MMM yy","E, d MMM yyyy","M-d-yy","M-d-yyyy", "MMMM DD, yy",
			"MMMM DD, yyyy", "yy", "yyyy");
	public static List<String> timeFormatStrings = Arrays.asList("hh:mm:ss a", "hh:mm a",
			"hh:mm:ss", "hh:mm","hhmm","hhmmss");
	public static String ageRegex = "(\\d+)\\s*(year|month|week|day|hour|minute)";
	public static String quantityRegex = "^\\s*([<>]?=?)\\s*([+-]?\\d+(?:[.,]\\d+)?)\\s*(.*)\\s*$";
	public static List<String> nameFormatStrings = Arrays.asList("(.*),\\s{0,1}(.*)\\s(.*)", "(\\w+)\\s(\\w)[\\.]\\s(\\w+)", "(\\w+)\\s(\\w+)");
	public static Extension dataAbsentNotAskedExtension = new Extension("http://hl7.org/fhir/StructureDefinition/data-absent-reason", new CodeType("not-asked"));
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
			Date returnDate = null;
	        try
	        {
	        	SimpleDateFormat sdf = new SimpleDateFormat(formatString);
	        	sdf.setTimeZone(TimeZone.getDefault());
	            returnDate = sdf.parse(dateString);
	        }
	        catch (ParseException e) {}
			if(returnDate != null){
				if(returnDate.getYear() > 8100){
					throw new ParseException("Found a year greater than 4 digits in the date: "+dateString, 0);
				}
				return returnDate;
			}
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
	
	public static Date addTimeToDate(Date date,Date time) {
		if(date == null || time == null){
			return date;
		}
		date.setHours(time.getHours());
		date.setMinutes(time.getMinutes());
		date.setSeconds(time.getSeconds());
		return date;
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

	public static Date parseDateAndTime(String dateAndTimeString) throws ParseException{
		LocalDateTime localDateTime = null;
		Date date = null;
		try{
			localDateTime = LocalDateTime.parse(dateAndTimeString);
		}
		catch (DateTimeParseException e) {}
		if (localDateTime != null){
			date = Date.from(localDateTime.toInstant(ZoneOffset.UTC));
			return date;
		}
		else{
			List<String> dateTimeAndDateStringFormats = new ArrayList<String>();
			for(String dateString : dateFormatStrings){
				for(String timeString : timeFormatStrings){
					dateTimeAndDateStringFormats.add(dateString + " " + timeString);
				}
			}
			dateTimeAndDateStringFormats.addAll(dateFormatStrings);
			for(String dateTimeFormat:dateTimeAndDateStringFormats){
				try{
					SimpleDateFormat sdf = new SimpleDateFormat(dateTimeFormat);
					sdf.setTimeZone(TimeZone.getDefault());
					date = sdf.parse(dateAndTimeString);
					if(date != null){
						return date;
					}
				}
				catch(ParseException e){}
			}
		}
		throw new ParseException("Could not format datetime: "+dateAndTimeString, 0);
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
		Pattern r = Pattern.compile(quantityRegex, Pattern.MULTILINE);
	    Matcher m = r.matcher(quantityString);
		if(m.find()) {
            String comparatorSymbol = (m.groupCount() >= 1) ? m.group(1) : null;
            String decimalQuantityString = (m.groupCount() >= 2) ? m.group(2) : null;
            String unitString = (m.groupCount() >= 3) ? m.group(3) : null;
			if(comparatorSymbol != null && !comparatorSymbol.isEmpty()){
				returnQuantity.setComparator(QuantityComparator.fromCode(comparatorSymbol));
			}
			if(decimalQuantityString != null && !decimalQuantityString.isEmpty()){
				returnQuantity.setValue(Double.parseDouble(decimalQuantityString));
			}
			if(unitString != null && !unitString.isEmpty()){
				returnQuantity.setUnit(unitString);
			}
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

	public static Practitioner setIdentiferDataAbsentReasonNotAsked(Practitioner practitioner){
		StringType notAskedStringType = new StringType();
		notAskedStringType.addExtension(dataAbsentNotAskedExtension);
		UriType notAskedUriType = new UriType();
		notAskedUriType.addExtension(dataAbsentNotAskedExtension);
		Identifier notAskedIdentifier = new Identifier();
		notAskedIdentifier.setValueElement(notAskedStringType);
		notAskedIdentifier.setSystemElement(notAskedUriType);
		practitioner.addIdentifier(notAskedIdentifier);
		return practitioner;
	}

	public static PractitionerRole setIdentiferDataAbsentReasonNotAsked(PractitionerRole practitionerRole){
		StringType notAskedStringType = new StringType();
		notAskedStringType.addExtension(dataAbsentNotAskedExtension);
		UriType notAskedUriType = new UriType();
		notAskedUriType.addExtension(dataAbsentNotAskedExtension);
		Identifier notAskedIdentifier = new Identifier();
		notAskedIdentifier.setValueElement(notAskedStringType);
		notAskedIdentifier.setSystemElement(notAskedUriType);
		practitionerRole.addIdentifier(notAskedIdentifier);
		return practitionerRole;
	}

	public static PractitionerRole setEndpointDataAbsentReasonNotAsked(PractitionerRole practitionerRole){
		Reference notAskedEndpoint = new Reference();
		notAskedEndpoint.addExtension(dataAbsentNotAskedExtension);
		practitionerRole.addEndpoint(notAskedEndpoint);
		return practitionerRole;
	}
}
