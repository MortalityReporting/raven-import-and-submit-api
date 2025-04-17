package edu.gatech.chai.MDI.Model;

import com.opencsv.bean.CsvBindByName;
import java.util.Objects;

public class MDIAndEDRSModelFields extends BaseModelFields{
	@CsvBindByName
	public String BASEFHIRID = "";
	@CsvBindByName
	public String SYSTEMID = "";
	@CsvBindByName
	public String MDICASEID = "";
	@CsvBindByName
	public String EDRSCASEID = "";
	@CsvBindByName
	public String FIRSTNAME = "";
	@CsvBindByName
	public String MIDNAME = "";
	@CsvBindByName
	public String LASTNAME = "";
	@CsvBindByName
	public String SUFFIXNAME = "";
	@CsvBindByName
	public String AGE = "";
	@CsvBindByName
	public String AGEUNIT = "";
	@CsvBindByName
	public String RACE = "";
	@CsvBindByName
	public String GENDER = "";
	@CsvBindByName
	public String ETHNICITY = "";
	@CsvBindByName
	public String BIRTHDATE = "";
	@CsvBindByName
	public String MRNNUMBER = "";
	@CsvBindByName
	public String JOBTITLE = "";
	@CsvBindByName
	public String INDUSTRY = "";
	@CsvBindByName
	public String LANGUAGE = "";
	@CsvBindByName
	public String MARITAL = "";
	@CsvBindByName
	public String POSSIBLEID = "";
	@CsvBindByName
	public String CAUSEA = "";
	@CsvBindByName
	public String CAUSEB = "";
	@CsvBindByName
	public String CAUSEC = "";
	@CsvBindByName
	public String CAUSED = "";
	@CsvBindByName
	public String OSCOND = ""; // Expecting a semicolon deliniated list
	@CsvBindByName
	public String MANNER = "";
	@CsvBindByName
	public String DISPMETHOD = "";
	@CsvBindByName
	public String CHOWNINJURY = "";
	@CsvBindByName
	public String DURATIONA = "";
	@CsvBindByName
	public String DURATIONB = "";
	@CsvBindByName
	public String DURATIONC = "";
	@CsvBindByName
	public String DURATIOND = "";
	@CsvBindByName
	public String CASENOTES = "";
	@CsvBindByName
	public String ATWORK = "";
	@CsvBindByName
	public String JOBRELATED = "";
	@CsvBindByName
	public String REPORTDATE = "";
	@CsvBindByName
	public String REPORTTIME = "";
	@CsvBindByName
	public String FOUNDDATE = "";
	@CsvBindByName
	public String FOUNDTIME = "";
	@CsvBindByName
	public String CDEATHDATE = "";
	@CsvBindByName
	public String CDEATHTIME = "";
	@CsvBindByName
	public String EVENTDATE = "";
	@CsvBindByName
	public String EVENTTIME = "";
	@CsvBindByName
	public String PRNDATE = "";
	@CsvBindByName
	public String PRNTIME = "";
	@CsvBindByName
	public String EXAMDATE = "";
	@CsvBindByName
	public String CINJDATE = "";
	@CsvBindByName
	public String CINJTIME = "";
	@CsvBindByName
	public String CINJDATEEARLY = "";
	@CsvBindByName
	public String CINJDATELATE = "";
	@CsvBindByName
	public String CIDATEFLAG = "";
	@CsvBindByName
	public String CDEATHESTABLISHEMENTMETHOD = "";
	@CsvBindByName
	public String LKADATE = "";
	@CsvBindByName
	public String LKATIME = "";
	@CsvBindByName
	public String CASEYEAR = "";
	@CsvBindByName
	public String ATHOSPDATE = "";
	@CsvBindByName
	public String ATHOSPTIME = "";
	@CsvBindByName
	public String RESSTREET = "";
	@CsvBindByName
	public String RESCITY = "";
	@CsvBindByName
	public String RESCOUNTY = "";
	@CsvBindByName
	public String RESSTATE = "";
	@CsvBindByName
	public String RESZIP = "";
	@CsvBindByName
	public String RESCOUNTRY = "";
	@CsvBindByName
	public String DEATHLOCATION_STREET = "";
	@CsvBindByName
	public String DEATHLOCATION_CITY = "";
	@CsvBindByName
	public String DEATHLOCATION_COUNTY = "";
	@CsvBindByName
	public String DEATHLOCATION_STATE = "";
	@CsvBindByName
	public String DEATHLOCATION_ZIP = "";
	@CsvBindByName
	public String DEATHLOCATION_COUNTRY = "";
	@CsvBindByName
	public String DEATHLOCATIONTYPE = "";
	@CsvBindByName
	public String INJURYLOCATION = "";
	@CsvBindByName
	public String FOUNDADDR_STREET = "";
	@CsvBindByName
	public String FOUNDADDR_CITY = "";
	@CsvBindByName
	public String FOUNDADDR_COUNTY = "";
	@CsvBindByName
	public String FOUNDADDR_STATE = "";
	@CsvBindByName
	public String FOUNDADDR_ZIP = "";
	@CsvBindByName
	public String EVENTPLACE = "";
	@CsvBindByName
	public String EVENTADDR_STREET = "";
	@CsvBindByName
	public String EVENTADDR_CITY = "";
	@CsvBindByName
	public String EVENTADDR_COUNTY = "";
	@CsvBindByName
	public String EVENTADDR_STATE = "";
	@CsvBindByName
	public String EVENTADDR_ZIP = "";
	@CsvBindByName
	public String PRNPLACE = "";
	@CsvBindByName
	public String PRNSTREET = "";
	@CsvBindByName
	public String PRNCITY = "";
	@CsvBindByName
	public String PRNCOUNTY = "";
	@CsvBindByName
	public String PRNSTATE = "";
	@CsvBindByName
	public String PRNZIP = "";
	@CsvBindByName
	public String DISP_PLACE = "";
	@CsvBindByName
	public String DISP_STREET = "";
	@CsvBindByName
	public String DISP_CITY = "";
	@CsvBindByName
	public String DISP_COUNTY = "";
	@CsvBindByName
	public String DISP_STATE = "";
	@CsvBindByName
	public String DISP_ZIP = "";
	@CsvBindByName
	public String RESNAME = "";
	@CsvBindByName
	public String LKAWHERE = "";
	@CsvBindByName
	public String HOSPNAME = "";
	@CsvBindByName
	public String SCENEADDR_STREET = "";
	@CsvBindByName
	public String SCENEADDR_CITY = "";
	@CsvBindByName
	public String SCENEADDR_COUNTY = "";
	@CsvBindByName
	public String SCENEADDR_STATE = "";
	@CsvBindByName
	public String SCENEADDR_ZIP = "";
	@CsvBindByName
	public String CERTIFIER_NAME = "";
	@CsvBindByName
	public String CERTIFIER_TYPE = "";
	@CsvBindByName
	public String SURGERY = "";
	@CsvBindByName
	public String SURGDATE = "";
	@CsvBindByName
	public String SURGREASON = "";
	@CsvBindByName
	public String HCPROVIDER = "";
	@CsvBindByName
	public String AUTOPSYPERFORMED = "";
	@CsvBindByName
	public String AUTOPSYRESULTSAVAILABLE = "";
	@CsvBindByName
	public String AUTOPSY_OFFICENAME = "";
	@CsvBindByName
	public String AUTOPSY_STREET = "";
	@CsvBindByName
	public String AUTOPSY_CITY = "";
	@CsvBindByName
	public String AUTOPSY_COUNTY = "";
	@CsvBindByName
	public String AUTOPSY_STATE = "";
	@CsvBindByName
	public String AUTOPSY_ZIP = "";
	@CsvBindByName
	public String CUSTODY = "";
	@CsvBindByName
	public String PREGNANT = "";
	@CsvBindByName
	public String TOBACCO = "";
	@CsvBindByName
	public String TRANSPORTATION = "";
	@CsvBindByName
	public String MENAME = "";
	@CsvBindByName
	public String MEPHONE = "";
	@CsvBindByName
	public String MELICENSE = "";
	@CsvBindByName
	public String ME_STREET = "";
	@CsvBindByName
	public String ME_CITY = "";
	@CsvBindByName
	public String ME_COUNTY = "";
	@CsvBindByName
	public String ME_STATE = "";
	@CsvBindByName
	public String ME_ZIP = "";
	@CsvBindByName
	public String PRONOUNCERNAME = "";
	@CsvBindByName
	public String CERTIFIER_IDENTIFIER = "";
	@CsvBindByName
	public String CERTIFIER_IDENTIFIER_SYSTEM = "";


	public MDIAndEDRSModelFields() {
	}

	public MDIAndEDRSModelFields(String BASEFHIRID, String SYSTEMID, String MDICASEID, String EDRSCASEID, String FIRSTNAME, String MIDNAME, String LASTNAME, String SUFFIXNAME, String AGE, String AGEUNIT, String RACE, String GENDER, String ETHNICITY, String BIRTHDATE, String MRNNUMBER, String JOBTITLE, String INDUSTRY, String LANGUAGE, String MARITAL, String POSSIBLEID, String CAUSEA, String CAUSEB, String CAUSEC, String CAUSED, String OSCOND, String MANNER, String DISPMETHOD, String CHOWNINJURY, String DURATIONA, String DURATIONB, String DURATIONC, String DURATIOND, String CASENOTES, String ATWORK, String JOBRELATED, String REPORTDATE, String REPORTTIME, String FOUNDDATE, String FOUNDTIME, String CDEATHDATE, String CDEATHTIME, String EVENTDATE, String EVENTTIME, String PRNDATE, String PRNTIME, String EXAMDATE, String CINJDATE, String CINJTIME, String CINJDATEEARLY, String CINJDATELATE, String CIDATEFLAG, String CDEATHESTABLISHEMENTMETHOD, String LKADATE, String LKATIME, String CASEYEAR, String ATHOSPDATE, String ATHOSPTIME, String RESSTREET, String RESCITY, String RESCOUNTY, String RESSTATE, String RESZIP, String RESCOUNTRY, String DEATHLOCATION_STREET, String DEATHLOCATION_CITY, String DEATHLOCATION_COUNTY, String DEATHLOCATION_STATE, String DEATHLOCATION_ZIP, String DEATHLOCATION_COUNTRY, String DEATHLOCATIONTYPE, String INJURYLOCATION, String FOUNDADDR_STREET, String FOUNDADDR_CITY, String FOUNDADDR_COUNTY, String FOUNDADDR_STATE, String FOUNDADDR_ZIP, String EVENTPLACE, String EVENTADDR_STREET, String EVENTADDR_CITY, String EVENTADDR_COUNTY, String EVENTADDR_STATE, String EVENTADDR_ZIP, String PRNPLACE, String PRNSTREET, String PRNCITY, String PRNCOUNTY, String PRNSTATE, String PRNZIP, String DISP_PLACE, String DISP_STREET, String DISP_CITY, String DISP_COUNTY, String DISP_STATE, String DISP_ZIP, String RESNAME, String LKAWHERE, String HOSPNAME, String SCENEADDR_STREET, String SCENEADDR_CITY, String SCENEADDR_COUNTY, String SCENEADDR_STATE, String SCENEADDR_ZIP, String CERTIFIER_NAME, String CERTIFIER_TYPE, String SURGERY, String SURGDATE, String SURGREASON, String HCPROVIDER, String AUTOPSYPERFORMED, String AUTOPSYRESULTSAVAILABLE, String AUTOPSY_OFFICENAME, String AUTOPSY_STREET, String AUTOPSY_CITY, String AUTOPSY_COUNTY, String AUTOPSY_STATE, String AUTOPSY_ZIP, String CUSTODY, String PREGNANT, String TOBACCO, String TRANSPORTATION, String MENAME, String MEPHONE, String MELICENSE, String ME_STREET, String ME_CITY, String ME_COUNTY, String ME_STATE, String ME_ZIP, String PRONOUNCERNAME, String CERTIFIER_IDENTIFIER, String CERTIFIER_IDENTIFIER_SYSTEM) {
		this.BASEFHIRID = BASEFHIRID;
		this.SYSTEMID = SYSTEMID;
		this.MDICASEID = MDICASEID;
		this.EDRSCASEID = EDRSCASEID;
		this.FIRSTNAME = FIRSTNAME;
		this.MIDNAME = MIDNAME;
		this.LASTNAME = LASTNAME;
		this.SUFFIXNAME = SUFFIXNAME;
		this.AGE = AGE;
		this.AGEUNIT = AGEUNIT;
		this.RACE = RACE;
		this.GENDER = GENDER;
		this.ETHNICITY = ETHNICITY;
		this.BIRTHDATE = BIRTHDATE;
		this.MRNNUMBER = MRNNUMBER;
		this.JOBTITLE = JOBTITLE;
		this.INDUSTRY = INDUSTRY;
		this.LANGUAGE = LANGUAGE;
		this.MARITAL = MARITAL;
		this.POSSIBLEID = POSSIBLEID;
		this.CAUSEA = CAUSEA;
		this.CAUSEB = CAUSEB;
		this.CAUSEC = CAUSEC;
		this.CAUSED = CAUSED;
		this.OSCOND = OSCOND;
		this.MANNER = MANNER;
		this.DISPMETHOD = DISPMETHOD;
		this.CHOWNINJURY = CHOWNINJURY;
		this.DURATIONA = DURATIONA;
		this.DURATIONB = DURATIONB;
		this.DURATIONC = DURATIONC;
		this.DURATIOND = DURATIOND;
		this.CASENOTES = CASENOTES;
		this.ATWORK = ATWORK;
		this.JOBRELATED = JOBRELATED;
		this.REPORTDATE = REPORTDATE;
		this.REPORTTIME = REPORTTIME;
		this.FOUNDDATE = FOUNDDATE;
		this.FOUNDTIME = FOUNDTIME;
		this.CDEATHDATE = CDEATHDATE;
		this.CDEATHTIME = CDEATHTIME;
		this.EVENTDATE = EVENTDATE;
		this.EVENTTIME = EVENTTIME;
		this.PRNDATE = PRNDATE;
		this.PRNTIME = PRNTIME;
		this.EXAMDATE = EXAMDATE;
		this.CINJDATE = CINJDATE;
		this.CINJTIME = CINJTIME;
		this.CINJDATEEARLY = CINJDATEEARLY;
		this.CINJDATELATE = CINJDATELATE;
		this.CIDATEFLAG = CIDATEFLAG;
		this.CDEATHESTABLISHEMENTMETHOD = CDEATHESTABLISHEMENTMETHOD;
		this.LKADATE = LKADATE;
		this.LKATIME = LKATIME;
		this.CASEYEAR = CASEYEAR;
		this.ATHOSPDATE = ATHOSPDATE;
		this.ATHOSPTIME = ATHOSPTIME;
		this.RESSTREET = RESSTREET;
		this.RESCITY = RESCITY;
		this.RESCOUNTY = RESCOUNTY;
		this.RESSTATE = RESSTATE;
		this.RESZIP = RESZIP;
		this.RESCOUNTRY = RESCOUNTRY;
		this.DEATHLOCATION_STREET = DEATHLOCATION_STREET;
		this.DEATHLOCATION_CITY = DEATHLOCATION_CITY;
		this.DEATHLOCATION_COUNTY = DEATHLOCATION_COUNTY;
		this.DEATHLOCATION_STATE = DEATHLOCATION_STATE;
		this.DEATHLOCATION_ZIP = DEATHLOCATION_ZIP;
		this.DEATHLOCATION_COUNTRY = DEATHLOCATION_COUNTRY;
		this.DEATHLOCATIONTYPE = DEATHLOCATIONTYPE;
		this.INJURYLOCATION = INJURYLOCATION;
		this.FOUNDADDR_STREET = FOUNDADDR_STREET;
		this.FOUNDADDR_CITY = FOUNDADDR_CITY;
		this.FOUNDADDR_COUNTY = FOUNDADDR_COUNTY;
		this.FOUNDADDR_STATE = FOUNDADDR_STATE;
		this.FOUNDADDR_ZIP = FOUNDADDR_ZIP;
		this.EVENTPLACE = EVENTPLACE;
		this.EVENTADDR_STREET = EVENTADDR_STREET;
		this.EVENTADDR_CITY = EVENTADDR_CITY;
		this.EVENTADDR_COUNTY = EVENTADDR_COUNTY;
		this.EVENTADDR_STATE = EVENTADDR_STATE;
		this.EVENTADDR_ZIP = EVENTADDR_ZIP;
		this.PRNPLACE = PRNPLACE;
		this.PRNSTREET = PRNSTREET;
		this.PRNCITY = PRNCITY;
		this.PRNCOUNTY = PRNCOUNTY;
		this.PRNSTATE = PRNSTATE;
		this.PRNZIP = PRNZIP;
		this.DISP_PLACE = DISP_PLACE;
		this.DISP_STREET = DISP_STREET;
		this.DISP_CITY = DISP_CITY;
		this.DISP_COUNTY = DISP_COUNTY;
		this.DISP_STATE = DISP_STATE;
		this.DISP_ZIP = DISP_ZIP;
		this.RESNAME = RESNAME;
		this.LKAWHERE = LKAWHERE;
		this.HOSPNAME = HOSPNAME;
		this.SCENEADDR_STREET = SCENEADDR_STREET;
		this.SCENEADDR_CITY = SCENEADDR_CITY;
		this.SCENEADDR_COUNTY = SCENEADDR_COUNTY;
		this.SCENEADDR_STATE = SCENEADDR_STATE;
		this.SCENEADDR_ZIP = SCENEADDR_ZIP;
		this.CERTIFIER_NAME = CERTIFIER_NAME;
		this.CERTIFIER_TYPE = CERTIFIER_TYPE;
		this.SURGERY = SURGERY;
		this.SURGDATE = SURGDATE;
		this.SURGREASON = SURGREASON;
		this.HCPROVIDER = HCPROVIDER;
		this.AUTOPSYPERFORMED = AUTOPSYPERFORMED;
		this.AUTOPSYRESULTSAVAILABLE = AUTOPSYRESULTSAVAILABLE;
		this.AUTOPSY_OFFICENAME = AUTOPSY_OFFICENAME;
		this.AUTOPSY_STREET = AUTOPSY_STREET;
		this.AUTOPSY_CITY = AUTOPSY_CITY;
		this.AUTOPSY_COUNTY = AUTOPSY_COUNTY;
		this.AUTOPSY_STATE = AUTOPSY_STATE;
		this.AUTOPSY_ZIP = AUTOPSY_ZIP;
		this.CUSTODY = CUSTODY;
		this.PREGNANT = PREGNANT;
		this.TOBACCO = TOBACCO;
		this.TRANSPORTATION = TRANSPORTATION;
		this.MENAME = MENAME;
		this.MEPHONE = MEPHONE;
		this.MELICENSE = MELICENSE;
		this.ME_STREET = ME_STREET;
		this.ME_CITY = ME_CITY;
		this.ME_COUNTY = ME_COUNTY;
		this.ME_STATE = ME_STATE;
		this.ME_ZIP = ME_ZIP;
		this.PRONOUNCERNAME = PRONOUNCERNAME;
		this.CERTIFIER_IDENTIFIER = CERTIFIER_IDENTIFIER;
		this.CERTIFIER_IDENTIFIER_SYSTEM = CERTIFIER_IDENTIFIER_SYSTEM;
	}

	public String getBASEFHIRID() {
		return this.BASEFHIRID;
	}

	public void setBASEFHIRID(String BASEFHIRID) {
		this.BASEFHIRID = BASEFHIRID;
	}

	public String getSYSTEMID() {
		return this.SYSTEMID;
	}

	public void setSYSTEMID(String SYSTEMID) {
		this.SYSTEMID = SYSTEMID;
	}

	public String getMDICASEID() {
		return this.MDICASEID;
	}

	public void setMDICASEID(String MDICASEID) {
		this.MDICASEID = MDICASEID;
	}

	public String getEDRSCASEID() {
		return this.EDRSCASEID;
	}

	public void setEDRSCASEID(String EDRSCASEID) {
		this.EDRSCASEID = EDRSCASEID;
	}

	public String getFIRSTNAME() {
		return this.FIRSTNAME;
	}

	public void setFIRSTNAME(String FIRSTNAME) {
		this.FIRSTNAME = FIRSTNAME;
	}

	public String getMIDNAME() {
		return this.MIDNAME;
	}

	public void setMIDNAME(String MIDNAME) {
		this.MIDNAME = MIDNAME;
	}

	public String getLASTNAME() {
		return this.LASTNAME;
	}

	public void setLASTNAME(String LASTNAME) {
		this.LASTNAME = LASTNAME;
	}

	public String getSUFFIXNAME() {
		return this.SUFFIXNAME;
	}

	public void setSUFFIXNAME(String SUFFIXNAME) {
		this.SUFFIXNAME = SUFFIXNAME;
	}

	public String getAGE() {
		return this.AGE;
	}

	public void setAGE(String AGE) {
		this.AGE = AGE;
	}

	public String getAGEUNIT() {
		return this.AGEUNIT;
	}

	public void setAGEUNIT(String AGEUNIT) {
		this.AGEUNIT = AGEUNIT;
	}

	public String getRACE() {
		return this.RACE;
	}

	public void setRACE(String RACE) {
		this.RACE = RACE;
	}

	public String getGENDER() {
		return this.GENDER;
	}

	public void setGENDER(String GENDER) {
		this.GENDER = GENDER;
	}

	public String getETHNICITY() {
		return this.ETHNICITY;
	}

	public void setETHNICITY(String ETHNICITY) {
		this.ETHNICITY = ETHNICITY;
	}

	public String getBIRTHDATE() {
		return this.BIRTHDATE;
	}

	public void setBIRTHDATE(String BIRTHDATE) {
		this.BIRTHDATE = BIRTHDATE;
	}

	public String getMRNNUMBER() {
		return this.MRNNUMBER;
	}

	public void setMRNNUMBER(String MRNNUMBER) {
		this.MRNNUMBER = MRNNUMBER;
	}

	public String getJOBTITLE() {
		return this.JOBTITLE;
	}

	public void setJOBTITLE(String JOBTITLE) {
		this.JOBTITLE = JOBTITLE;
	}

	public String getINDUSTRY() {
		return this.INDUSTRY;
	}

	public void setINDUSTRY(String INDUSTRY) {
		this.INDUSTRY = INDUSTRY;
	}

	public String getLANGUAGE() {
		return this.LANGUAGE;
	}

	public void setLANGUAGE(String LANGUAGE) {
		this.LANGUAGE = LANGUAGE;
	}

	public String getMARITAL() {
		return this.MARITAL;
	}

	public void setMARITAL(String MARITAL) {
		this.MARITAL = MARITAL;
	}

	public String getPOSSIBLEID() {
		return this.POSSIBLEID;
	}

	public void setPOSSIBLEID(String POSSIBLEID) {
		this.POSSIBLEID = POSSIBLEID;
	}

	public String getCAUSEA() {
		return this.CAUSEA;
	}

	public void setCAUSEA(String CAUSEA) {
		this.CAUSEA = CAUSEA;
	}

	public String getCAUSEB() {
		return this.CAUSEB;
	}

	public void setCAUSEB(String CAUSEB) {
		this.CAUSEB = CAUSEB;
	}

	public String getCAUSEC() {
		return this.CAUSEC;
	}

	public void setCAUSEC(String CAUSEC) {
		this.CAUSEC = CAUSEC;
	}

	public String getCAUSED() {
		return this.CAUSED;
	}

	public void setCAUSED(String CAUSED) {
		this.CAUSED = CAUSED;
	}

	public String getOSCOND() {
		return this.OSCOND;
	}

	public void setOSCOND(String OSCOND) {
		this.OSCOND = OSCOND;
	}

	public String getMANNER() {
		return this.MANNER;
	}

	public void setMANNER(String MANNER) {
		this.MANNER = MANNER;
	}

	public String getDISPMETHOD() {
		return this.DISPMETHOD;
	}

	public void setDISPMETHOD(String DISPMETHOD) {
		this.DISPMETHOD = DISPMETHOD;
	}

	public String getCHOWNINJURY() {
		return this.CHOWNINJURY;
	}

	public void setCHOWNINJURY(String CHOWNINJURY) {
		this.CHOWNINJURY = CHOWNINJURY;
	}

	public String getDURATIONA() {
		return this.DURATIONA;
	}

	public void setDURATIONA(String DURATIONA) {
		this.DURATIONA = DURATIONA;
	}

	public String getDURATIONB() {
		return this.DURATIONB;
	}

	public void setDURATIONB(String DURATIONB) {
		this.DURATIONB = DURATIONB;
	}

	public String getDURATIONC() {
		return this.DURATIONC;
	}

	public void setDURATIONC(String DURATIONC) {
		this.DURATIONC = DURATIONC;
	}

	public String getDURATIOND() {
		return this.DURATIOND;
	}

	public void setDURATIOND(String DURATIOND) {
		this.DURATIOND = DURATIOND;
	}

	public String getCASENOTES() {
		return this.CASENOTES;
	}

	public void setCASENOTES(String CASENOTES) {
		this.CASENOTES = CASENOTES;
	}

	public String getATWORK() {
		return this.ATWORK;
	}

	public void setATWORK(String ATWORK) {
		this.ATWORK = ATWORK;
	}

	public String getJOBRELATED() {
		return this.JOBRELATED;
	}

	public void setJOBRELATED(String JOBRELATED) {
		this.JOBRELATED = JOBRELATED;
	}

	public String getREPORTDATE() {
		return this.REPORTDATE;
	}

	public void setREPORTDATE(String REPORTDATE) {
		this.REPORTDATE = REPORTDATE;
	}

	public String getREPORTTIME() {
		return this.REPORTTIME;
	}

	public void setREPORTTIME(String REPORTTIME) {
		this.REPORTTIME = REPORTTIME;
	}

	public String getFOUNDDATE() {
		return this.FOUNDDATE;
	}

	public void setFOUNDDATE(String FOUNDDATE) {
		this.FOUNDDATE = FOUNDDATE;
	}

	public String getFOUNDTIME() {
		return this.FOUNDTIME;
	}

	public void setFOUNDTIME(String FOUNDTIME) {
		this.FOUNDTIME = FOUNDTIME;
	}

	public String getCDEATHDATE() {
		return this.CDEATHDATE;
	}

	public void setCDEATHDATE(String CDEATHDATE) {
		this.CDEATHDATE = CDEATHDATE;
	}

	public String getCDEATHTIME() {
		return this.CDEATHTIME;
	}

	public void setCDEATHTIME(String CDEATHTIME) {
		this.CDEATHTIME = CDEATHTIME;
	}

	public String getEVENTDATE() {
		return this.EVENTDATE;
	}

	public void setEVENTDATE(String EVENTDATE) {
		this.EVENTDATE = EVENTDATE;
	}

	public String getEVENTTIME() {
		return this.EVENTTIME;
	}

	public void setEVENTTIME(String EVENTTIME) {
		this.EVENTTIME = EVENTTIME;
	}

	public String getPRNDATE() {
		return this.PRNDATE;
	}

	public void setPRNDATE(String PRNDATE) {
		this.PRNDATE = PRNDATE;
	}

	public String getPRNTIME() {
		return this.PRNTIME;
	}

	public void setPRNTIME(String PRNTIME) {
		this.PRNTIME = PRNTIME;
	}

	public String getEXAMDATE() {
		return this.EXAMDATE;
	}

	public void setEXAMDATE(String EXAMDATE) {
		this.EXAMDATE = EXAMDATE;
	}

	public String getCINJDATE() {
		return this.CINJDATE;
	}

	public void setCINJDATE(String CINJDATE) {
		this.CINJDATE = CINJDATE;
	}

	public String getCINJTIME() {
		return this.CINJTIME;
	}

	public void setCINJTIME(String CINJTIME) {
		this.CINJTIME = CINJTIME;
	}

	public String getCINJDATEEARLY() {
		return this.CINJDATEEARLY;
	}

	public void setCINJDATEEARLY(String CINJDATEEARLY) {
		this.CINJDATEEARLY = CINJDATEEARLY;
	}

	public String getCINJDATELATE() {
		return this.CINJDATELATE;
	}

	public void setCINJDATELATE(String CINJDATELATE) {
		this.CINJDATELATE = CINJDATELATE;
	}

	public String getCIDATEFLAG() {
		return this.CIDATEFLAG;
	}

	public void setCIDATEFLAG(String CIDATEFLAG) {
		this.CIDATEFLAG = CIDATEFLAG;
	}

	public String getCDEATHESTABLISHEMENTMETHOD() {
		return this.CDEATHESTABLISHEMENTMETHOD;
	}

	public void setCDEATHESTABLISHEMENTMETHOD(String CDEATHESTABLISHEMENTMETHOD) {
		this.CDEATHESTABLISHEMENTMETHOD = CDEATHESTABLISHEMENTMETHOD;
	}

	public String getLKADATE() {
		return this.LKADATE;
	}

	public void setLKADATE(String LKADATE) {
		this.LKADATE = LKADATE;
	}

	public String getLKATIME() {
		return this.LKATIME;
	}

	public void setLKATIME(String LKATIME) {
		this.LKATIME = LKATIME;
	}

	public String getCASEYEAR() {
		return this.CASEYEAR;
	}

	public void setCASEYEAR(String CASEYEAR) {
		this.CASEYEAR = CASEYEAR;
	}

	public String getATHOSPDATE() {
		return this.ATHOSPDATE;
	}

	public void setATHOSPDATE(String ATHOSPDATE) {
		this.ATHOSPDATE = ATHOSPDATE;
	}

	public String getATHOSPTIME() {
		return this.ATHOSPTIME;
	}

	public void setATHOSPTIME(String ATHOSPTIME) {
		this.ATHOSPTIME = ATHOSPTIME;
	}

	public String getRESSTREET() {
		return this.RESSTREET;
	}

	public void setRESSTREET(String RESSTREET) {
		this.RESSTREET = RESSTREET;
	}

	public String getRESCITY() {
		return this.RESCITY;
	}

	public void setRESCITY(String RESCITY) {
		this.RESCITY = RESCITY;
	}

	public String getRESCOUNTY() {
		return this.RESCOUNTY;
	}

	public void setRESCOUNTY(String RESCOUNTY) {
		this.RESCOUNTY = RESCOUNTY;
	}

	public String getRESSTATE() {
		return this.RESSTATE;
	}

	public void setRESSTATE(String RESSTATE) {
		this.RESSTATE = RESSTATE;
	}

	public String getRESZIP() {
		return this.RESZIP;
	}

	public void setRESZIP(String RESZIP) {
		this.RESZIP = RESZIP;
	}

	public String getRESCOUNTRY() {
		return this.RESCOUNTRY;
	}

	public void setRESCOUNTRY(String RESCOUNTRY) {
		this.RESCOUNTRY = RESCOUNTRY;
	}

	public String getDEATHLOCATION_STREET() {
		return this.DEATHLOCATION_STREET;
	}

	public void setDEATHLOCATION_STREET(String DEATHLOCATION_STREET) {
		this.DEATHLOCATION_STREET = DEATHLOCATION_STREET;
	}

	public String getDEATHLOCATION_CITY() {
		return this.DEATHLOCATION_CITY;
	}

	public void setDEATHLOCATION_CITY(String DEATHLOCATION_CITY) {
		this.DEATHLOCATION_CITY = DEATHLOCATION_CITY;
	}

	public String getDEATHLOCATION_COUNTY() {
		return this.DEATHLOCATION_COUNTY;
	}

	public void setDEATHLOCATION_COUNTY(String DEATHLOCATION_COUNTY) {
		this.DEATHLOCATION_COUNTY = DEATHLOCATION_COUNTY;
	}

	public String getDEATHLOCATION_STATE() {
		return this.DEATHLOCATION_STATE;
	}

	public void setDEATHLOCATION_STATE(String DEATHLOCATION_STATE) {
		this.DEATHLOCATION_STATE = DEATHLOCATION_STATE;
	}

	public String getDEATHLOCATION_ZIP() {
		return this.DEATHLOCATION_ZIP;
	}

	public void setDEATHLOCATION_ZIP(String DEATHLOCATION_ZIP) {
		this.DEATHLOCATION_ZIP = DEATHLOCATION_ZIP;
	}

	public String getDEATHLOCATION_COUNTRY() {
		return this.DEATHLOCATION_COUNTRY;
	}

	public void setDEATHLOCATION_COUNTRY(String DEATHLOCATION_COUNTRY) {
		this.DEATHLOCATION_COUNTRY = DEATHLOCATION_COUNTRY;
	}

	public String getDEATHLOCATIONTYPE() {
		return this.DEATHLOCATIONTYPE;
	}

	public void setDEATHLOCATIONTYPE(String DEATHLOCATIONTYPE) {
		this.DEATHLOCATIONTYPE = DEATHLOCATIONTYPE;
	}

	public String getINJURYLOCATION() {
		return this.INJURYLOCATION;
	}

	public void setINJURYLOCATION(String INJURYLOCATION) {
		this.INJURYLOCATION = INJURYLOCATION;
	}

	public String getFOUNDADDR_STREET() {
		return this.FOUNDADDR_STREET;
	}

	public void setFOUNDADDR_STREET(String FOUNDADDR_STREET) {
		this.FOUNDADDR_STREET = FOUNDADDR_STREET;
	}

	public String getFOUNDADDR_CITY() {
		return this.FOUNDADDR_CITY;
	}

	public void setFOUNDADDR_CITY(String FOUNDADDR_CITY) {
		this.FOUNDADDR_CITY = FOUNDADDR_CITY;
	}

	public String getFOUNDADDR_COUNTY() {
		return this.FOUNDADDR_COUNTY;
	}

	public void setFOUNDADDR_COUNTY(String FOUNDADDR_COUNTY) {
		this.FOUNDADDR_COUNTY = FOUNDADDR_COUNTY;
	}

	public String getFOUNDADDR_STATE() {
		return this.FOUNDADDR_STATE;
	}

	public void setFOUNDADDR_STATE(String FOUNDADDR_STATE) {
		this.FOUNDADDR_STATE = FOUNDADDR_STATE;
	}

	public String getFOUNDADDR_ZIP() {
		return this.FOUNDADDR_ZIP;
	}

	public void setFOUNDADDR_ZIP(String FOUNDADDR_ZIP) {
		this.FOUNDADDR_ZIP = FOUNDADDR_ZIP;
	}

	public String getEVENTPLACE() {
		return this.EVENTPLACE;
	}

	public void setEVENTPLACE(String EVENTPLACE) {
		this.EVENTPLACE = EVENTPLACE;
	}

	public String getEVENTADDR_STREET() {
		return this.EVENTADDR_STREET;
	}

	public void setEVENTADDR_STREET(String EVENTADDR_STREET) {
		this.EVENTADDR_STREET = EVENTADDR_STREET;
	}

	public String getEVENTADDR_CITY() {
		return this.EVENTADDR_CITY;
	}

	public void setEVENTADDR_CITY(String EVENTADDR_CITY) {
		this.EVENTADDR_CITY = EVENTADDR_CITY;
	}

	public String getEVENTADDR_COUNTY() {
		return this.EVENTADDR_COUNTY;
	}

	public void setEVENTADDR_COUNTY(String EVENTADDR_COUNTY) {
		this.EVENTADDR_COUNTY = EVENTADDR_COUNTY;
	}

	public String getEVENTADDR_STATE() {
		return this.EVENTADDR_STATE;
	}

	public void setEVENTADDR_STATE(String EVENTADDR_STATE) {
		this.EVENTADDR_STATE = EVENTADDR_STATE;
	}

	public String getEVENTADDR_ZIP() {
		return this.EVENTADDR_ZIP;
	}

	public void setEVENTADDR_ZIP(String EVENTADDR_ZIP) {
		this.EVENTADDR_ZIP = EVENTADDR_ZIP;
	}

	public String getPRNPLACE() {
		return this.PRNPLACE;
	}

	public void setPRNPLACE(String PRNPLACE) {
		this.PRNPLACE = PRNPLACE;
	}

	public String getPRNSTREET() {
		return this.PRNSTREET;
	}

	public void setPRNSTREET(String PRNSTREET) {
		this.PRNSTREET = PRNSTREET;
	}

	public String getPRNCITY() {
		return this.PRNCITY;
	}

	public void setPRNCITY(String PRNCITY) {
		this.PRNCITY = PRNCITY;
	}

	public String getPRNCOUNTY() {
		return this.PRNCOUNTY;
	}

	public void setPRNCOUNTY(String PRNCOUNTY) {
		this.PRNCOUNTY = PRNCOUNTY;
	}

	public String getPRNSTATE() {
		return this.PRNSTATE;
	}

	public void setPRNSTATE(String PRNSTATE) {
		this.PRNSTATE = PRNSTATE;
	}

	public String getPRNZIP() {
		return this.PRNZIP;
	}

	public void setPRNZIP(String PRNZIP) {
		this.PRNZIP = PRNZIP;
	}

	public String getDISP_PLACE() {
		return this.DISP_PLACE;
	}

	public void setDISP_PLACE(String DISP_PLACE) {
		this.DISP_PLACE = DISP_PLACE;
	}

	public String getDISP_STREET() {
		return this.DISP_STREET;
	}

	public void setDISP_STREET(String DISP_STREET) {
		this.DISP_STREET = DISP_STREET;
	}

	public String getDISP_CITY() {
		return this.DISP_CITY;
	}

	public void setDISP_CITY(String DISP_CITY) {
		this.DISP_CITY = DISP_CITY;
	}

	public String getDISP_COUNTY() {
		return this.DISP_COUNTY;
	}

	public void setDISP_COUNTY(String DISP_COUNTY) {
		this.DISP_COUNTY = DISP_COUNTY;
	}

	public String getDISP_STATE() {
		return this.DISP_STATE;
	}

	public void setDISP_STATE(String DISP_STATE) {
		this.DISP_STATE = DISP_STATE;
	}

	public String getDISP_ZIP() {
		return this.DISP_ZIP;
	}

	public void setDISP_ZIP(String DISP_ZIP) {
		this.DISP_ZIP = DISP_ZIP;
	}

	public String getRESNAME() {
		return this.RESNAME;
	}

	public void setRESNAME(String RESNAME) {
		this.RESNAME = RESNAME;
	}

	public String getLKAWHERE() {
		return this.LKAWHERE;
	}

	public void setLKAWHERE(String LKAWHERE) {
		this.LKAWHERE = LKAWHERE;
	}

	public String getHOSPNAME() {
		return this.HOSPNAME;
	}

	public void setHOSPNAME(String HOSPNAME) {
		this.HOSPNAME = HOSPNAME;
	}

	public String getSCENEADDR_STREET() {
		return this.SCENEADDR_STREET;
	}

	public void setSCENEADDR_STREET(String SCENEADDR_STREET) {
		this.SCENEADDR_STREET = SCENEADDR_STREET;
	}

	public String getSCENEADDR_CITY() {
		return this.SCENEADDR_CITY;
	}

	public void setSCENEADDR_CITY(String SCENEADDR_CITY) {
		this.SCENEADDR_CITY = SCENEADDR_CITY;
	}

	public String getSCENEADDR_COUNTY() {
		return this.SCENEADDR_COUNTY;
	}

	public void setSCENEADDR_COUNTY(String SCENEADDR_COUNTY) {
		this.SCENEADDR_COUNTY = SCENEADDR_COUNTY;
	}

	public String getSCENEADDR_STATE() {
		return this.SCENEADDR_STATE;
	}

	public void setSCENEADDR_STATE(String SCENEADDR_STATE) {
		this.SCENEADDR_STATE = SCENEADDR_STATE;
	}

	public String getSCENEADDR_ZIP() {
		return this.SCENEADDR_ZIP;
	}

	public void setSCENEADDR_ZIP(String SCENEADDR_ZIP) {
		this.SCENEADDR_ZIP = SCENEADDR_ZIP;
	}

	public String getCERTIFIER_NAME() {
		return this.CERTIFIER_NAME;
	}

	public void setCERTIFIER_NAME(String CERTIFIER_NAME) {
		this.CERTIFIER_NAME = CERTIFIER_NAME;
	}

	public String getCERTIFIER_TYPE() {
		return this.CERTIFIER_TYPE;
	}

	public void setCERTIFIER_TYPE(String CERTIFIER_TYPE) {
		this.CERTIFIER_TYPE = CERTIFIER_TYPE;
	}

	public String getSURGERY() {
		return this.SURGERY;
	}

	public void setSURGERY(String SURGERY) {
		this.SURGERY = SURGERY;
	}

	public String getSURGDATE() {
		return this.SURGDATE;
	}

	public void setSURGDATE(String SURGDATE) {
		this.SURGDATE = SURGDATE;
	}

	public String getSURGREASON() {
		return this.SURGREASON;
	}

	public void setSURGREASON(String SURGREASON) {
		this.SURGREASON = SURGREASON;
	}

	public String getHCPROVIDER() {
		return this.HCPROVIDER;
	}

	public void setHCPROVIDER(String HCPROVIDER) {
		this.HCPROVIDER = HCPROVIDER;
	}

	public String getAUTOPSYPERFORMED() {
		return this.AUTOPSYPERFORMED;
	}

	public void setAUTOPSYPERFORMED(String AUTOPSYPERFORMED) {
		this.AUTOPSYPERFORMED = AUTOPSYPERFORMED;
	}

	public String getAUTOPSYRESULTSAVAILABLE() {
		return this.AUTOPSYRESULTSAVAILABLE;
	}

	public void setAUTOPSYRESULTSAVAILABLE(String AUTOPSYRESULTSAVAILABLE) {
		this.AUTOPSYRESULTSAVAILABLE = AUTOPSYRESULTSAVAILABLE;
	}

	public String getAUTOPSY_OFFICENAME() {
		return this.AUTOPSY_OFFICENAME;
	}

	public void setAUTOPSY_OFFICENAME(String AUTOPSY_OFFICENAME) {
		this.AUTOPSY_OFFICENAME = AUTOPSY_OFFICENAME;
	}

	public String getAUTOPSY_STREET() {
		return this.AUTOPSY_STREET;
	}

	public void setAUTOPSY_STREET(String AUTOPSY_STREET) {
		this.AUTOPSY_STREET = AUTOPSY_STREET;
	}

	public String getAUTOPSY_CITY() {
		return this.AUTOPSY_CITY;
	}

	public void setAUTOPSY_CITY(String AUTOPSY_CITY) {
		this.AUTOPSY_CITY = AUTOPSY_CITY;
	}

	public String getAUTOPSY_COUNTY() {
		return this.AUTOPSY_COUNTY;
	}

	public void setAUTOPSY_COUNTY(String AUTOPSY_COUNTY) {
		this.AUTOPSY_COUNTY = AUTOPSY_COUNTY;
	}

	public String getAUTOPSY_STATE() {
		return this.AUTOPSY_STATE;
	}

	public void setAUTOPSY_STATE(String AUTOPSY_STATE) {
		this.AUTOPSY_STATE = AUTOPSY_STATE;
	}

	public String getAUTOPSY_ZIP() {
		return this.AUTOPSY_ZIP;
	}

	public void setAUTOPSY_ZIP(String AUTOPSY_ZIP) {
		this.AUTOPSY_ZIP = AUTOPSY_ZIP;
	}

	public String getCUSTODY() {
		return this.CUSTODY;
	}

	public void setCUSTODY(String CUSTODY) {
		this.CUSTODY = CUSTODY;
	}

	public String getPREGNANT() {
		return this.PREGNANT;
	}

	public void setPREGNANT(String PREGNANT) {
		this.PREGNANT = PREGNANT;
	}

	public String getTOBACCO() {
		return this.TOBACCO;
	}

	public void setTOBACCO(String TOBACCO) {
		this.TOBACCO = TOBACCO;
	}

	public String getTRANSPORTATION() {
		return this.TRANSPORTATION;
	}

	public void setTRANSPORTATION(String TRANSPORTATION) {
		this.TRANSPORTATION = TRANSPORTATION;
	}

	public String getMENAME() {
		return this.MENAME;
	}

	public void setMENAME(String MENAME) {
		this.MENAME = MENAME;
	}

	public String getMEPHONE() {
		return this.MEPHONE;
	}

	public void setMEPHONE(String MEPHONE) {
		this.MEPHONE = MEPHONE;
	}

	public String getMELICENSE() {
		return this.MELICENSE;
	}

	public void setMELICENSE(String MELICENSE) {
		this.MELICENSE = MELICENSE;
	}

	public String getME_STREET() {
		return this.ME_STREET;
	}

	public void setME_STREET(String ME_STREET) {
		this.ME_STREET = ME_STREET;
	}

	public String getME_CITY() {
		return this.ME_CITY;
	}

	public void setME_CITY(String ME_CITY) {
		this.ME_CITY = ME_CITY;
	}

	public String getME_COUNTY() {
		return this.ME_COUNTY;
	}

	public void setME_COUNTY(String ME_COUNTY) {
		this.ME_COUNTY = ME_COUNTY;
	}

	public String getME_STATE() {
		return this.ME_STATE;
	}

	public void setME_STATE(String ME_STATE) {
		this.ME_STATE = ME_STATE;
	}

	public String getME_ZIP() {
		return this.ME_ZIP;
	}

	public void setME_ZIP(String ME_ZIP) {
		this.ME_ZIP = ME_ZIP;
	}

	public String getPRONOUNCERNAME() {
		return this.PRONOUNCERNAME;
	}

	public void setPRONOUNCERNAME(String PRONOUNCERNAME) {
		this.PRONOUNCERNAME = PRONOUNCERNAME;
	}

	public String getCERTIFIER_IDENTIFIER() {
		return this.CERTIFIER_IDENTIFIER;
	}

	public void setCERTIFIER_IDENTIFIER(String CERTIFIER_IDENTIFIER) {
		this.CERTIFIER_IDENTIFIER = CERTIFIER_IDENTIFIER;
	}

	public String getCERTIFIER_IDENTIFIER_SYSTEM() {
		return this.CERTIFIER_IDENTIFIER_SYSTEM;
	}

	public void setCERTIFIER_IDENTIFIER_SYSTEM(String CERTIFIER_IDENTIFIER_SYSTEM) {
		this.CERTIFIER_IDENTIFIER_SYSTEM = CERTIFIER_IDENTIFIER_SYSTEM;
	}

	public MDIAndEDRSModelFields BASEFHIRID(String BASEFHIRID) {
		setBASEFHIRID(BASEFHIRID);
		return this;
	}

	public MDIAndEDRSModelFields SYSTEMID(String SYSTEMID) {
		setSYSTEMID(SYSTEMID);
		return this;
	}

	public MDIAndEDRSModelFields MDICASEID(String MDICASEID) {
		setMDICASEID(MDICASEID);
		return this;
	}

	public MDIAndEDRSModelFields EDRSCASEID(String EDRSCASEID) {
		setEDRSCASEID(EDRSCASEID);
		return this;
	}

	public MDIAndEDRSModelFields FIRSTNAME(String FIRSTNAME) {
		setFIRSTNAME(FIRSTNAME);
		return this;
	}

	public MDIAndEDRSModelFields MIDNAME(String MIDNAME) {
		setMIDNAME(MIDNAME);
		return this;
	}

	public MDIAndEDRSModelFields LASTNAME(String LASTNAME) {
		setLASTNAME(LASTNAME);
		return this;
	}

	public MDIAndEDRSModelFields SUFFIXNAME(String SUFFIXNAME) {
		setSUFFIXNAME(SUFFIXNAME);
		return this;
	}

	public MDIAndEDRSModelFields AGE(String AGE) {
		setAGE(AGE);
		return this;
	}

	public MDIAndEDRSModelFields AGEUNIT(String AGEUNIT) {
		setAGEUNIT(AGEUNIT);
		return this;
	}

	public MDIAndEDRSModelFields RACE(String RACE) {
		setRACE(RACE);
		return this;
	}

	public MDIAndEDRSModelFields GENDER(String GENDER) {
		setGENDER(GENDER);
		return this;
	}

	public MDIAndEDRSModelFields ETHNICITY(String ETHNICITY) {
		setETHNICITY(ETHNICITY);
		return this;
	}

	public MDIAndEDRSModelFields BIRTHDATE(String BIRTHDATE) {
		setBIRTHDATE(BIRTHDATE);
		return this;
	}

	public MDIAndEDRSModelFields MRNNUMBER(String MRNNUMBER) {
		setMRNNUMBER(MRNNUMBER);
		return this;
	}

	public MDIAndEDRSModelFields JOBTITLE(String JOBTITLE) {
		setJOBTITLE(JOBTITLE);
		return this;
	}

	public MDIAndEDRSModelFields INDUSTRY(String INDUSTRY) {
		setINDUSTRY(INDUSTRY);
		return this;
	}

	public MDIAndEDRSModelFields LANGUAGE(String LANGUAGE) {
		setLANGUAGE(LANGUAGE);
		return this;
	}

	public MDIAndEDRSModelFields MARITAL(String MARITAL) {
		setMARITAL(MARITAL);
		return this;
	}

	public MDIAndEDRSModelFields POSSIBLEID(String POSSIBLEID) {
		setPOSSIBLEID(POSSIBLEID);
		return this;
	}

	public MDIAndEDRSModelFields CAUSEA(String CAUSEA) {
		setCAUSEA(CAUSEA);
		return this;
	}

	public MDIAndEDRSModelFields CAUSEB(String CAUSEB) {
		setCAUSEB(CAUSEB);
		return this;
	}

	public MDIAndEDRSModelFields CAUSEC(String CAUSEC) {
		setCAUSEC(CAUSEC);
		return this;
	}

	public MDIAndEDRSModelFields CAUSED(String CAUSED) {
		setCAUSED(CAUSED);
		return this;
	}

	public MDIAndEDRSModelFields OSCOND(String OSCOND) {
		setOSCOND(OSCOND);
		return this;
	}

	public MDIAndEDRSModelFields MANNER(String MANNER) {
		setMANNER(MANNER);
		return this;
	}

	public MDIAndEDRSModelFields DISPMETHOD(String DISPMETHOD) {
		setDISPMETHOD(DISPMETHOD);
		return this;
	}

	public MDIAndEDRSModelFields CHOWNINJURY(String CHOWNINJURY) {
		setCHOWNINJURY(CHOWNINJURY);
		return this;
	}

	public MDIAndEDRSModelFields DURATIONA(String DURATIONA) {
		setDURATIONA(DURATIONA);
		return this;
	}

	public MDIAndEDRSModelFields DURATIONB(String DURATIONB) {
		setDURATIONB(DURATIONB);
		return this;
	}

	public MDIAndEDRSModelFields DURATIONC(String DURATIONC) {
		setDURATIONC(DURATIONC);
		return this;
	}

	public MDIAndEDRSModelFields DURATIOND(String DURATIOND) {
		setDURATIOND(DURATIOND);
		return this;
	}

	public MDIAndEDRSModelFields CASENOTES(String CASENOTES) {
		setCASENOTES(CASENOTES);
		return this;
	}

	public MDIAndEDRSModelFields ATWORK(String ATWORK) {
		setATWORK(ATWORK);
		return this;
	}

	public MDIAndEDRSModelFields JOBRELATED(String JOBRELATED) {
		setJOBRELATED(JOBRELATED);
		return this;
	}

	public MDIAndEDRSModelFields REPORTDATE(String REPORTDATE) {
		setREPORTDATE(REPORTDATE);
		return this;
	}

	public MDIAndEDRSModelFields REPORTTIME(String REPORTTIME) {
		setREPORTTIME(REPORTTIME);
		return this;
	}

	public MDIAndEDRSModelFields FOUNDDATE(String FOUNDDATE) {
		setFOUNDDATE(FOUNDDATE);
		return this;
	}

	public MDIAndEDRSModelFields FOUNDTIME(String FOUNDTIME) {
		setFOUNDTIME(FOUNDTIME);
		return this;
	}

	public MDIAndEDRSModelFields CDEATHDATE(String CDEATHDATE) {
		setCDEATHDATE(CDEATHDATE);
		return this;
	}

	public MDIAndEDRSModelFields CDEATHTIME(String CDEATHTIME) {
		setCDEATHTIME(CDEATHTIME);
		return this;
	}

	public MDIAndEDRSModelFields EVENTDATE(String EVENTDATE) {
		setEVENTDATE(EVENTDATE);
		return this;
	}

	public MDIAndEDRSModelFields EVENTTIME(String EVENTTIME) {
		setEVENTTIME(EVENTTIME);
		return this;
	}

	public MDIAndEDRSModelFields PRNDATE(String PRNDATE) {
		setPRNDATE(PRNDATE);
		return this;
	}

	public MDIAndEDRSModelFields PRNTIME(String PRNTIME) {
		setPRNTIME(PRNTIME);
		return this;
	}

	public MDIAndEDRSModelFields EXAMDATE(String EXAMDATE) {
		setEXAMDATE(EXAMDATE);
		return this;
	}

	public MDIAndEDRSModelFields CINJDATE(String CINJDATE) {
		setCINJDATE(CINJDATE);
		return this;
	}

	public MDIAndEDRSModelFields CINJTIME(String CINJTIME) {
		setCINJTIME(CINJTIME);
		return this;
	}

	public MDIAndEDRSModelFields CINJDATEEARLY(String CINJDATEEARLY) {
		setCINJDATEEARLY(CINJDATEEARLY);
		return this;
	}

	public MDIAndEDRSModelFields CINJDATELATE(String CINJDATELATE) {
		setCINJDATELATE(CINJDATELATE);
		return this;
	}

	public MDIAndEDRSModelFields CIDATEFLAG(String CIDATEFLAG) {
		setCIDATEFLAG(CIDATEFLAG);
		return this;
	}

	public MDIAndEDRSModelFields CDEATHESTABLISHEMENTMETHOD(String CDEATHESTABLISHEMENTMETHOD) {
		setCDEATHESTABLISHEMENTMETHOD(CDEATHESTABLISHEMENTMETHOD);
		return this;
	}

	public MDIAndEDRSModelFields LKADATE(String LKADATE) {
		setLKADATE(LKADATE);
		return this;
	}

	public MDIAndEDRSModelFields LKATIME(String LKATIME) {
		setLKATIME(LKATIME);
		return this;
	}

	public MDIAndEDRSModelFields CASEYEAR(String CASEYEAR) {
		setCASEYEAR(CASEYEAR);
		return this;
	}

	public MDIAndEDRSModelFields ATHOSPDATE(String ATHOSPDATE) {
		setATHOSPDATE(ATHOSPDATE);
		return this;
	}

	public MDIAndEDRSModelFields ATHOSPTIME(String ATHOSPTIME) {
		setATHOSPTIME(ATHOSPTIME);
		return this;
	}

	public MDIAndEDRSModelFields RESSTREET(String RESSTREET) {
		setRESSTREET(RESSTREET);
		return this;
	}

	public MDIAndEDRSModelFields RESCITY(String RESCITY) {
		setRESCITY(RESCITY);
		return this;
	}

	public MDIAndEDRSModelFields RESCOUNTY(String RESCOUNTY) {
		setRESCOUNTY(RESCOUNTY);
		return this;
	}

	public MDIAndEDRSModelFields RESSTATE(String RESSTATE) {
		setRESSTATE(RESSTATE);
		return this;
	}

	public MDIAndEDRSModelFields RESZIP(String RESZIP) {
		setRESZIP(RESZIP);
		return this;
	}

	public MDIAndEDRSModelFields RESCOUNTRY(String RESCOUNTRY) {
		setRESCOUNTRY(RESCOUNTRY);
		return this;
	}

	public MDIAndEDRSModelFields DEATHLOCATION_STREET(String DEATHLOCATION_STREET) {
		setDEATHLOCATION_STREET(DEATHLOCATION_STREET);
		return this;
	}

	public MDIAndEDRSModelFields DEATHLOCATION_CITY(String DEATHLOCATION_CITY) {
		setDEATHLOCATION_CITY(DEATHLOCATION_CITY);
		return this;
	}

	public MDIAndEDRSModelFields DEATHLOCATION_COUNTY(String DEATHLOCATION_COUNTY) {
		setDEATHLOCATION_COUNTY(DEATHLOCATION_COUNTY);
		return this;
	}

	public MDIAndEDRSModelFields DEATHLOCATION_STATE(String DEATHLOCATION_STATE) {
		setDEATHLOCATION_STATE(DEATHLOCATION_STATE);
		return this;
	}

	public MDIAndEDRSModelFields DEATHLOCATION_ZIP(String DEATHLOCATION_ZIP) {
		setDEATHLOCATION_ZIP(DEATHLOCATION_ZIP);
		return this;
	}

	public MDIAndEDRSModelFields DEATHLOCATION_COUNTRY(String DEATHLOCATION_COUNTRY) {
		setDEATHLOCATION_COUNTRY(DEATHLOCATION_COUNTRY);
		return this;
	}

	public MDIAndEDRSModelFields DEATHLOCATIONTYPE(String DEATHLOCATIONTYPE) {
		setDEATHLOCATIONTYPE(DEATHLOCATIONTYPE);
		return this;
	}

	public MDIAndEDRSModelFields INJURYLOCATION(String INJURYLOCATION) {
		setINJURYLOCATION(INJURYLOCATION);
		return this;
	}

	public MDIAndEDRSModelFields FOUNDADDR_STREET(String FOUNDADDR_STREET) {
		setFOUNDADDR_STREET(FOUNDADDR_STREET);
		return this;
	}

	public MDIAndEDRSModelFields FOUNDADDR_CITY(String FOUNDADDR_CITY) {
		setFOUNDADDR_CITY(FOUNDADDR_CITY);
		return this;
	}

	public MDIAndEDRSModelFields FOUNDADDR_COUNTY(String FOUNDADDR_COUNTY) {
		setFOUNDADDR_COUNTY(FOUNDADDR_COUNTY);
		return this;
	}

	public MDIAndEDRSModelFields FOUNDADDR_STATE(String FOUNDADDR_STATE) {
		setFOUNDADDR_STATE(FOUNDADDR_STATE);
		return this;
	}

	public MDIAndEDRSModelFields FOUNDADDR_ZIP(String FOUNDADDR_ZIP) {
		setFOUNDADDR_ZIP(FOUNDADDR_ZIP);
		return this;
	}

	public MDIAndEDRSModelFields EVENTPLACE(String EVENTPLACE) {
		setEVENTPLACE(EVENTPLACE);
		return this;
	}

	public MDIAndEDRSModelFields EVENTADDR_STREET(String EVENTADDR_STREET) {
		setEVENTADDR_STREET(EVENTADDR_STREET);
		return this;
	}

	public MDIAndEDRSModelFields EVENTADDR_CITY(String EVENTADDR_CITY) {
		setEVENTADDR_CITY(EVENTADDR_CITY);
		return this;
	}

	public MDIAndEDRSModelFields EVENTADDR_COUNTY(String EVENTADDR_COUNTY) {
		setEVENTADDR_COUNTY(EVENTADDR_COUNTY);
		return this;
	}

	public MDIAndEDRSModelFields EVENTADDR_STATE(String EVENTADDR_STATE) {
		setEVENTADDR_STATE(EVENTADDR_STATE);
		return this;
	}

	public MDIAndEDRSModelFields EVENTADDR_ZIP(String EVENTADDR_ZIP) {
		setEVENTADDR_ZIP(EVENTADDR_ZIP);
		return this;
	}

	public MDIAndEDRSModelFields PRNPLACE(String PRNPLACE) {
		setPRNPLACE(PRNPLACE);
		return this;
	}

	public MDIAndEDRSModelFields PRNSTREET(String PRNSTREET) {
		setPRNSTREET(PRNSTREET);
		return this;
	}

	public MDIAndEDRSModelFields PRNCITY(String PRNCITY) {
		setPRNCITY(PRNCITY);
		return this;
	}

	public MDIAndEDRSModelFields PRNCOUNTY(String PRNCOUNTY) {
		setPRNCOUNTY(PRNCOUNTY);
		return this;
	}

	public MDIAndEDRSModelFields PRNSTATE(String PRNSTATE) {
		setPRNSTATE(PRNSTATE);
		return this;
	}

	public MDIAndEDRSModelFields PRNZIP(String PRNZIP) {
		setPRNZIP(PRNZIP);
		return this;
	}

	public MDIAndEDRSModelFields DISP_PLACE(String DISP_PLACE) {
		setDISP_PLACE(DISP_PLACE);
		return this;
	}

	public MDIAndEDRSModelFields DISP_STREET(String DISP_STREET) {
		setDISP_STREET(DISP_STREET);
		return this;
	}

	public MDIAndEDRSModelFields DISP_CITY(String DISP_CITY) {
		setDISP_CITY(DISP_CITY);
		return this;
	}

	public MDIAndEDRSModelFields DISP_COUNTY(String DISP_COUNTY) {
		setDISP_COUNTY(DISP_COUNTY);
		return this;
	}

	public MDIAndEDRSModelFields DISP_STATE(String DISP_STATE) {
		setDISP_STATE(DISP_STATE);
		return this;
	}

	public MDIAndEDRSModelFields DISP_ZIP(String DISP_ZIP) {
		setDISP_ZIP(DISP_ZIP);
		return this;
	}

	public MDIAndEDRSModelFields RESNAME(String RESNAME) {
		setRESNAME(RESNAME);
		return this;
	}

	public MDIAndEDRSModelFields LKAWHERE(String LKAWHERE) {
		setLKAWHERE(LKAWHERE);
		return this;
	}

	public MDIAndEDRSModelFields HOSPNAME(String HOSPNAME) {
		setHOSPNAME(HOSPNAME);
		return this;
	}

	public MDIAndEDRSModelFields SCENEADDR_STREET(String SCENEADDR_STREET) {
		setSCENEADDR_STREET(SCENEADDR_STREET);
		return this;
	}

	public MDIAndEDRSModelFields SCENEADDR_CITY(String SCENEADDR_CITY) {
		setSCENEADDR_CITY(SCENEADDR_CITY);
		return this;
	}

	public MDIAndEDRSModelFields SCENEADDR_COUNTY(String SCENEADDR_COUNTY) {
		setSCENEADDR_COUNTY(SCENEADDR_COUNTY);
		return this;
	}

	public MDIAndEDRSModelFields SCENEADDR_STATE(String SCENEADDR_STATE) {
		setSCENEADDR_STATE(SCENEADDR_STATE);
		return this;
	}

	public MDIAndEDRSModelFields SCENEADDR_ZIP(String SCENEADDR_ZIP) {
		setSCENEADDR_ZIP(SCENEADDR_ZIP);
		return this;
	}

	public MDIAndEDRSModelFields CERTIFIER_NAME(String CERTIFIER_NAME) {
		setCERTIFIER_NAME(CERTIFIER_NAME);
		return this;
	}

	public MDIAndEDRSModelFields CERTIFIER_TYPE(String CERTIFIER_TYPE) {
		setCERTIFIER_TYPE(CERTIFIER_TYPE);
		return this;
	}

	public MDIAndEDRSModelFields SURGERY(String SURGERY) {
		setSURGERY(SURGERY);
		return this;
	}

	public MDIAndEDRSModelFields SURGDATE(String SURGDATE) {
		setSURGDATE(SURGDATE);
		return this;
	}

	public MDIAndEDRSModelFields SURGREASON(String SURGREASON) {
		setSURGREASON(SURGREASON);
		return this;
	}

	public MDIAndEDRSModelFields HCPROVIDER(String HCPROVIDER) {
		setHCPROVIDER(HCPROVIDER);
		return this;
	}

	public MDIAndEDRSModelFields AUTOPSYPERFORMED(String AUTOPSYPERFORMED) {
		setAUTOPSYPERFORMED(AUTOPSYPERFORMED);
		return this;
	}

	public MDIAndEDRSModelFields AUTOPSYRESULTSAVAILABLE(String AUTOPSYRESULTSAVAILABLE) {
		setAUTOPSYRESULTSAVAILABLE(AUTOPSYRESULTSAVAILABLE);
		return this;
	}

	public MDIAndEDRSModelFields AUTOPSY_OFFICENAME(String AUTOPSY_OFFICENAME) {
		setAUTOPSY_OFFICENAME(AUTOPSY_OFFICENAME);
		return this;
	}

	public MDIAndEDRSModelFields AUTOPSY_STREET(String AUTOPSY_STREET) {
		setAUTOPSY_STREET(AUTOPSY_STREET);
		return this;
	}

	public MDIAndEDRSModelFields AUTOPSY_CITY(String AUTOPSY_CITY) {
		setAUTOPSY_CITY(AUTOPSY_CITY);
		return this;
	}

	public MDIAndEDRSModelFields AUTOPSY_COUNTY(String AUTOPSY_COUNTY) {
		setAUTOPSY_COUNTY(AUTOPSY_COUNTY);
		return this;
	}

	public MDIAndEDRSModelFields AUTOPSY_STATE(String AUTOPSY_STATE) {
		setAUTOPSY_STATE(AUTOPSY_STATE);
		return this;
	}

	public MDIAndEDRSModelFields AUTOPSY_ZIP(String AUTOPSY_ZIP) {
		setAUTOPSY_ZIP(AUTOPSY_ZIP);
		return this;
	}

	public MDIAndEDRSModelFields CUSTODY(String CUSTODY) {
		setCUSTODY(CUSTODY);
		return this;
	}

	public MDIAndEDRSModelFields PREGNANT(String PREGNANT) {
		setPREGNANT(PREGNANT);
		return this;
	}

	public MDIAndEDRSModelFields TOBACCO(String TOBACCO) {
		setTOBACCO(TOBACCO);
		return this;
	}

	public MDIAndEDRSModelFields TRANSPORTATION(String TRANSPORTATION) {
		setTRANSPORTATION(TRANSPORTATION);
		return this;
	}

	public MDIAndEDRSModelFields MENAME(String MENAME) {
		setMENAME(MENAME);
		return this;
	}

	public MDIAndEDRSModelFields MEPHONE(String MEPHONE) {
		setMEPHONE(MEPHONE);
		return this;
	}

	public MDIAndEDRSModelFields MELICENSE(String MELICENSE) {
		setMELICENSE(MELICENSE);
		return this;
	}

	public MDIAndEDRSModelFields ME_STREET(String ME_STREET) {
		setME_STREET(ME_STREET);
		return this;
	}

	public MDIAndEDRSModelFields ME_CITY(String ME_CITY) {
		setME_CITY(ME_CITY);
		return this;
	}

	public MDIAndEDRSModelFields ME_COUNTY(String ME_COUNTY) {
		setME_COUNTY(ME_COUNTY);
		return this;
	}

	public MDIAndEDRSModelFields ME_STATE(String ME_STATE) {
		setME_STATE(ME_STATE);
		return this;
	}

	public MDIAndEDRSModelFields ME_ZIP(String ME_ZIP) {
		setME_ZIP(ME_ZIP);
		return this;
	}

	public MDIAndEDRSModelFields PRONOUNCERNAME(String PRONOUNCERNAME) {
		setPRONOUNCERNAME(PRONOUNCERNAME);
		return this;
	}

	public MDIAndEDRSModelFields CERTIFIER_IDENTIFIER(String CERTIFIER_IDENTIFIER) {
		setCERTIFIER_IDENTIFIER(CERTIFIER_IDENTIFIER);
		return this;
	}

	public MDIAndEDRSModelFields CERTIFIER_IDENTIFIER_SYSTEM(String CERTIFIER_IDENTIFIER_SYSTEM) {
		setCERTIFIER_IDENTIFIER_SYSTEM(CERTIFIER_IDENTIFIER_SYSTEM);
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof MDIAndEDRSModelFields)) {
			return false;
		}
		MDIAndEDRSModelFields mDIAndEDRSModelFields = (MDIAndEDRSModelFields) o;
		return Objects.equals(BASEFHIRID, mDIAndEDRSModelFields.BASEFHIRID) && Objects.equals(SYSTEMID, mDIAndEDRSModelFields.SYSTEMID) && Objects.equals(MDICASEID, mDIAndEDRSModelFields.MDICASEID) && Objects.equals(EDRSCASEID, mDIAndEDRSModelFields.EDRSCASEID) && Objects.equals(FIRSTNAME, mDIAndEDRSModelFields.FIRSTNAME) && Objects.equals(MIDNAME, mDIAndEDRSModelFields.MIDNAME) && Objects.equals(LASTNAME, mDIAndEDRSModelFields.LASTNAME) && Objects.equals(SUFFIXNAME, mDIAndEDRSModelFields.SUFFIXNAME) && Objects.equals(AGE, mDIAndEDRSModelFields.AGE) && Objects.equals(AGEUNIT, mDIAndEDRSModelFields.AGEUNIT) && Objects.equals(RACE, mDIAndEDRSModelFields.RACE) && Objects.equals(GENDER, mDIAndEDRSModelFields.GENDER) && Objects.equals(ETHNICITY, mDIAndEDRSModelFields.ETHNICITY) && Objects.equals(BIRTHDATE, mDIAndEDRSModelFields.BIRTHDATE) && Objects.equals(MRNNUMBER, mDIAndEDRSModelFields.MRNNUMBER) && Objects.equals(JOBTITLE, mDIAndEDRSModelFields.JOBTITLE) && Objects.equals(INDUSTRY, mDIAndEDRSModelFields.INDUSTRY) && Objects.equals(LANGUAGE, mDIAndEDRSModelFields.LANGUAGE) && Objects.equals(MARITAL, mDIAndEDRSModelFields.MARITAL) && Objects.equals(POSSIBLEID, mDIAndEDRSModelFields.POSSIBLEID) && Objects.equals(CAUSEA, mDIAndEDRSModelFields.CAUSEA) && Objects.equals(CAUSEB, mDIAndEDRSModelFields.CAUSEB) && Objects.equals(CAUSEC, mDIAndEDRSModelFields.CAUSEC) && Objects.equals(CAUSED, mDIAndEDRSModelFields.CAUSED) && Objects.equals(OSCOND, mDIAndEDRSModelFields.OSCOND) && Objects.equals(MANNER, mDIAndEDRSModelFields.MANNER) && Objects.equals(DISPMETHOD, mDIAndEDRSModelFields.DISPMETHOD) && Objects.equals(CHOWNINJURY, mDIAndEDRSModelFields.CHOWNINJURY) && Objects.equals(DURATIONA, mDIAndEDRSModelFields.DURATIONA) && Objects.equals(DURATIONB, mDIAndEDRSModelFields.DURATIONB) && Objects.equals(DURATIONC, mDIAndEDRSModelFields.DURATIONC) && Objects.equals(DURATIOND, mDIAndEDRSModelFields.DURATIOND) && Objects.equals(CASENOTES, mDIAndEDRSModelFields.CASENOTES) && Objects.equals(ATWORK, mDIAndEDRSModelFields.ATWORK) && Objects.equals(JOBRELATED, mDIAndEDRSModelFields.JOBRELATED) && Objects.equals(REPORTDATE, mDIAndEDRSModelFields.REPORTDATE) && Objects.equals(REPORTTIME, mDIAndEDRSModelFields.REPORTTIME) && Objects.equals(FOUNDDATE, mDIAndEDRSModelFields.FOUNDDATE) && Objects.equals(FOUNDTIME, mDIAndEDRSModelFields.FOUNDTIME) && Objects.equals(CDEATHDATE, mDIAndEDRSModelFields.CDEATHDATE) && Objects.equals(CDEATHTIME, mDIAndEDRSModelFields.CDEATHTIME) && Objects.equals(EVENTDATE, mDIAndEDRSModelFields.EVENTDATE) && Objects.equals(EVENTTIME, mDIAndEDRSModelFields.EVENTTIME) && Objects.equals(PRNDATE, mDIAndEDRSModelFields.PRNDATE) && Objects.equals(PRNTIME, mDIAndEDRSModelFields.PRNTIME) && Objects.equals(EXAMDATE, mDIAndEDRSModelFields.EXAMDATE) && Objects.equals(CINJDATE, mDIAndEDRSModelFields.CINJDATE) && Objects.equals(CINJTIME, mDIAndEDRSModelFields.CINJTIME) && Objects.equals(CINJDATEEARLY, mDIAndEDRSModelFields.CINJDATEEARLY) && Objects.equals(CINJDATELATE, mDIAndEDRSModelFields.CINJDATELATE) && Objects.equals(CIDATEFLAG, mDIAndEDRSModelFields.CIDATEFLAG) && Objects.equals(CDEATHESTABLISHEMENTMETHOD, mDIAndEDRSModelFields.CDEATHESTABLISHEMENTMETHOD) && Objects.equals(LKADATE, mDIAndEDRSModelFields.LKADATE) && Objects.equals(LKATIME, mDIAndEDRSModelFields.LKATIME) && Objects.equals(CASEYEAR, mDIAndEDRSModelFields.CASEYEAR) && Objects.equals(ATHOSPDATE, mDIAndEDRSModelFields.ATHOSPDATE) && Objects.equals(ATHOSPTIME, mDIAndEDRSModelFields.ATHOSPTIME) && Objects.equals(RESSTREET, mDIAndEDRSModelFields.RESSTREET) && Objects.equals(RESCITY, mDIAndEDRSModelFields.RESCITY) && Objects.equals(RESCOUNTY, mDIAndEDRSModelFields.RESCOUNTY) && Objects.equals(RESSTATE, mDIAndEDRSModelFields.RESSTATE) && Objects.equals(RESZIP, mDIAndEDRSModelFields.RESZIP) && Objects.equals(RESCOUNTRY, mDIAndEDRSModelFields.RESCOUNTRY) && Objects.equals(DEATHLOCATION_STREET, mDIAndEDRSModelFields.DEATHLOCATION_STREET) && Objects.equals(DEATHLOCATION_CITY, mDIAndEDRSModelFields.DEATHLOCATION_CITY) && Objects.equals(DEATHLOCATION_COUNTY, mDIAndEDRSModelFields.DEATHLOCATION_COUNTY) && Objects.equals(DEATHLOCATION_STATE, mDIAndEDRSModelFields.DEATHLOCATION_STATE) && Objects.equals(DEATHLOCATION_ZIP, mDIAndEDRSModelFields.DEATHLOCATION_ZIP) && Objects.equals(DEATHLOCATION_COUNTRY, mDIAndEDRSModelFields.DEATHLOCATION_COUNTRY) && Objects.equals(DEATHLOCATIONTYPE, mDIAndEDRSModelFields.DEATHLOCATIONTYPE) && Objects.equals(INJURYLOCATION, mDIAndEDRSModelFields.INJURYLOCATION) && Objects.equals(FOUNDADDR_STREET, mDIAndEDRSModelFields.FOUNDADDR_STREET) && Objects.equals(FOUNDADDR_CITY, mDIAndEDRSModelFields.FOUNDADDR_CITY) && Objects.equals(FOUNDADDR_COUNTY, mDIAndEDRSModelFields.FOUNDADDR_COUNTY) && Objects.equals(FOUNDADDR_STATE, mDIAndEDRSModelFields.FOUNDADDR_STATE) && Objects.equals(FOUNDADDR_ZIP, mDIAndEDRSModelFields.FOUNDADDR_ZIP) && Objects.equals(EVENTPLACE, mDIAndEDRSModelFields.EVENTPLACE) && Objects.equals(EVENTADDR_STREET, mDIAndEDRSModelFields.EVENTADDR_STREET) && Objects.equals(EVENTADDR_CITY, mDIAndEDRSModelFields.EVENTADDR_CITY) && Objects.equals(EVENTADDR_COUNTY, mDIAndEDRSModelFields.EVENTADDR_COUNTY) && Objects.equals(EVENTADDR_STATE, mDIAndEDRSModelFields.EVENTADDR_STATE) && Objects.equals(EVENTADDR_ZIP, mDIAndEDRSModelFields.EVENTADDR_ZIP) && Objects.equals(PRNPLACE, mDIAndEDRSModelFields.PRNPLACE) && Objects.equals(PRNSTREET, mDIAndEDRSModelFields.PRNSTREET) && Objects.equals(PRNCITY, mDIAndEDRSModelFields.PRNCITY) && Objects.equals(PRNCOUNTY, mDIAndEDRSModelFields.PRNCOUNTY) && Objects.equals(PRNSTATE, mDIAndEDRSModelFields.PRNSTATE) && Objects.equals(PRNZIP, mDIAndEDRSModelFields.PRNZIP) && Objects.equals(DISP_PLACE, mDIAndEDRSModelFields.DISP_PLACE) && Objects.equals(DISP_STREET, mDIAndEDRSModelFields.DISP_STREET) && Objects.equals(DISP_CITY, mDIAndEDRSModelFields.DISP_CITY) && Objects.equals(DISP_COUNTY, mDIAndEDRSModelFields.DISP_COUNTY) && Objects.equals(DISP_STATE, mDIAndEDRSModelFields.DISP_STATE) && Objects.equals(DISP_ZIP, mDIAndEDRSModelFields.DISP_ZIP) && Objects.equals(RESNAME, mDIAndEDRSModelFields.RESNAME) && Objects.equals(LKAWHERE, mDIAndEDRSModelFields.LKAWHERE) && Objects.equals(HOSPNAME, mDIAndEDRSModelFields.HOSPNAME) && Objects.equals(SCENEADDR_STREET, mDIAndEDRSModelFields.SCENEADDR_STREET) && Objects.equals(SCENEADDR_CITY, mDIAndEDRSModelFields.SCENEADDR_CITY) && Objects.equals(SCENEADDR_COUNTY, mDIAndEDRSModelFields.SCENEADDR_COUNTY) && Objects.equals(SCENEADDR_STATE, mDIAndEDRSModelFields.SCENEADDR_STATE) && Objects.equals(SCENEADDR_ZIP, mDIAndEDRSModelFields.SCENEADDR_ZIP) && Objects.equals(CERTIFIER_NAME, mDIAndEDRSModelFields.CERTIFIER_NAME) && Objects.equals(CERTIFIER_TYPE, mDIAndEDRSModelFields.CERTIFIER_TYPE) && Objects.equals(SURGERY, mDIAndEDRSModelFields.SURGERY) && Objects.equals(SURGDATE, mDIAndEDRSModelFields.SURGDATE) && Objects.equals(SURGREASON, mDIAndEDRSModelFields.SURGREASON) && Objects.equals(HCPROVIDER, mDIAndEDRSModelFields.HCPROVIDER) && Objects.equals(AUTOPSYPERFORMED, mDIAndEDRSModelFields.AUTOPSYPERFORMED) && Objects.equals(AUTOPSYRESULTSAVAILABLE, mDIAndEDRSModelFields.AUTOPSYRESULTSAVAILABLE) && Objects.equals(AUTOPSY_OFFICENAME, mDIAndEDRSModelFields.AUTOPSY_OFFICENAME) && Objects.equals(AUTOPSY_STREET, mDIAndEDRSModelFields.AUTOPSY_STREET) && Objects.equals(AUTOPSY_CITY, mDIAndEDRSModelFields.AUTOPSY_CITY) && Objects.equals(AUTOPSY_COUNTY, mDIAndEDRSModelFields.AUTOPSY_COUNTY) && Objects.equals(AUTOPSY_STATE, mDIAndEDRSModelFields.AUTOPSY_STATE) && Objects.equals(AUTOPSY_ZIP, mDIAndEDRSModelFields.AUTOPSY_ZIP) && Objects.equals(CUSTODY, mDIAndEDRSModelFields.CUSTODY) && Objects.equals(PREGNANT, mDIAndEDRSModelFields.PREGNANT) && Objects.equals(TOBACCO, mDIAndEDRSModelFields.TOBACCO) && Objects.equals(TRANSPORTATION, mDIAndEDRSModelFields.TRANSPORTATION) && Objects.equals(MENAME, mDIAndEDRSModelFields.MENAME) && Objects.equals(MEPHONE, mDIAndEDRSModelFields.MEPHONE) && Objects.equals(MELICENSE, mDIAndEDRSModelFields.MELICENSE) && Objects.equals(ME_STREET, mDIAndEDRSModelFields.ME_STREET) && Objects.equals(ME_CITY, mDIAndEDRSModelFields.ME_CITY) && Objects.equals(ME_COUNTY, mDIAndEDRSModelFields.ME_COUNTY) && Objects.equals(ME_STATE, mDIAndEDRSModelFields.ME_STATE) && Objects.equals(ME_ZIP, mDIAndEDRSModelFields.ME_ZIP) && Objects.equals(PRONOUNCERNAME, mDIAndEDRSModelFields.PRONOUNCERNAME) && Objects.equals(CERTIFIER_IDENTIFIER, mDIAndEDRSModelFields.CERTIFIER_IDENTIFIER) && Objects.equals(CERTIFIER_IDENTIFIER_SYSTEM, mDIAndEDRSModelFields.CERTIFIER_IDENTIFIER_SYSTEM);
	}

	@Override
	public int hashCode() {
		return Objects.hash(BASEFHIRID, SYSTEMID, MDICASEID, EDRSCASEID, FIRSTNAME, MIDNAME, LASTNAME, SUFFIXNAME, AGE, AGEUNIT, RACE, GENDER, ETHNICITY, BIRTHDATE, MRNNUMBER, JOBTITLE, INDUSTRY, LANGUAGE, MARITAL, POSSIBLEID, CAUSEA, CAUSEB, CAUSEC, CAUSED, OSCOND, MANNER, DISPMETHOD, CHOWNINJURY, DURATIONA, DURATIONB, DURATIONC, DURATIOND, CASENOTES, ATWORK, JOBRELATED, REPORTDATE, REPORTTIME, FOUNDDATE, FOUNDTIME, CDEATHDATE, CDEATHTIME, EVENTDATE, EVENTTIME, PRNDATE, PRNTIME, EXAMDATE, CINJDATE, CINJTIME, CINJDATEEARLY, CINJDATELATE, CIDATEFLAG, CDEATHESTABLISHEMENTMETHOD, LKADATE, LKATIME, CASEYEAR, ATHOSPDATE, ATHOSPTIME, RESSTREET, RESCITY, RESCOUNTY, RESSTATE, RESZIP, RESCOUNTRY, DEATHLOCATION_STREET, DEATHLOCATION_CITY, DEATHLOCATION_COUNTY, DEATHLOCATION_STATE, DEATHLOCATION_ZIP, DEATHLOCATION_COUNTRY, DEATHLOCATIONTYPE, INJURYLOCATION, FOUNDADDR_STREET, FOUNDADDR_CITY, FOUNDADDR_COUNTY, FOUNDADDR_STATE, FOUNDADDR_ZIP, EVENTPLACE, EVENTADDR_STREET, EVENTADDR_CITY, EVENTADDR_COUNTY, EVENTADDR_STATE, EVENTADDR_ZIP, PRNPLACE, PRNSTREET, PRNCITY, PRNCOUNTY, PRNSTATE, PRNZIP, DISP_PLACE, DISP_STREET, DISP_CITY, DISP_COUNTY, DISP_STATE, DISP_ZIP, RESNAME, LKAWHERE, HOSPNAME, SCENEADDR_STREET, SCENEADDR_CITY, SCENEADDR_COUNTY, SCENEADDR_STATE, SCENEADDR_ZIP, CERTIFIER_NAME, CERTIFIER_TYPE, SURGERY, SURGDATE, SURGREASON, HCPROVIDER, AUTOPSYPERFORMED, AUTOPSYRESULTSAVAILABLE, AUTOPSY_OFFICENAME, AUTOPSY_STREET, AUTOPSY_CITY, AUTOPSY_COUNTY, AUTOPSY_STATE, AUTOPSY_ZIP, CUSTODY, PREGNANT, TOBACCO, TRANSPORTATION, MENAME, MEPHONE, MELICENSE, ME_STREET, ME_CITY, ME_COUNTY, ME_STATE, ME_ZIP, PRONOUNCERNAME, CERTIFIER_IDENTIFIER, CERTIFIER_IDENTIFIER_SYSTEM);
	}

	@Override
	public String toString() {
		return "{" +
			" BASEFHIRID='" + getBASEFHIRID() + "'" +
			", SYSTEMID='" + getSYSTEMID() + "'" +
			", MDICASEID='" + getMDICASEID() + "'" +
			", EDRSCASEID='" + getEDRSCASEID() + "'" +
			", FIRSTNAME='" + getFIRSTNAME() + "'" +
			", MIDNAME='" + getMIDNAME() + "'" +
			", LASTNAME='" + getLASTNAME() + "'" +
			", SUFFIXNAME='" + getSUFFIXNAME() + "'" +
			", AGE='" + getAGE() + "'" +
			", AGEUNIT='" + getAGEUNIT() + "'" +
			", RACE='" + getRACE() + "'" +
			", GENDER='" + getGENDER() + "'" +
			", ETHNICITY='" + getETHNICITY() + "'" +
			", BIRTHDATE='" + getBIRTHDATE() + "'" +
			", MRNNUMBER='" + getMRNNUMBER() + "'" +
			", JOBTITLE='" + getJOBTITLE() + "'" +
			", INDUSTRY='" + getINDUSTRY() + "'" +
			", LANGUAGE='" + getLANGUAGE() + "'" +
			", MARITAL='" + getMARITAL() + "'" +
			", POSSIBLEID='" + getPOSSIBLEID() + "'" +
			", CAUSEA='" + getCAUSEA() + "'" +
			", CAUSEB='" + getCAUSEB() + "'" +
			", CAUSEC='" + getCAUSEC() + "'" +
			", CAUSED='" + getCAUSED() + "'" +
			", OSCOND='" + getOSCOND() + "'" +
			", MANNER='" + getMANNER() + "'" +
			", DISPMETHOD='" + getDISPMETHOD() + "'" +
			", CHOWNINJURY='" + getCHOWNINJURY() + "'" +
			", DURATIONA='" + getDURATIONA() + "'" +
			", DURATIONB='" + getDURATIONB() + "'" +
			", DURATIONC='" + getDURATIONC() + "'" +
			", DURATIOND='" + getDURATIOND() + "'" +
			", CASENOTES='" + getCASENOTES() + "'" +
			", ATWORK='" + getATWORK() + "'" +
			", JOBRELATED='" + getJOBRELATED() + "'" +
			", REPORTDATE='" + getREPORTDATE() + "'" +
			", REPORTTIME='" + getREPORTTIME() + "'" +
			", FOUNDDATE='" + getFOUNDDATE() + "'" +
			", FOUNDTIME='" + getFOUNDTIME() + "'" +
			", CDEATHDATE='" + getCDEATHDATE() + "'" +
			", CDEATHTIME='" + getCDEATHTIME() + "'" +
			", EVENTDATE='" + getEVENTDATE() + "'" +
			", EVENTTIME='" + getEVENTTIME() + "'" +
			", PRNDATE='" + getPRNDATE() + "'" +
			", PRNTIME='" + getPRNTIME() + "'" +
			", EXAMDATE='" + getEXAMDATE() + "'" +
			", CINJDATE='" + getCINJDATE() + "'" +
			", CINJTIME='" + getCINJTIME() + "'" +
			", CINJDATEEARLY='" + getCINJDATEEARLY() + "'" +
			", CINJDATELATE='" + getCINJDATELATE() + "'" +
			", CIDATEFLAG='" + getCIDATEFLAG() + "'" +
			", CDEATHESTABLISHEMENTMETHOD='" + getCDEATHESTABLISHEMENTMETHOD() + "'" +
			", LKADATE='" + getLKADATE() + "'" +
			", LKATIME='" + getLKATIME() + "'" +
			", CASEYEAR='" + getCASEYEAR() + "'" +
			", ATHOSPDATE='" + getATHOSPDATE() + "'" +
			", ATHOSPTIME='" + getATHOSPTIME() + "'" +
			", RESSTREET='" + getRESSTREET() + "'" +
			", RESCITY='" + getRESCITY() + "'" +
			", RESCOUNTY='" + getRESCOUNTY() + "'" +
			", RESSTATE='" + getRESSTATE() + "'" +
			", RESZIP='" + getRESZIP() + "'" +
			", RESCOUNTRY='" + getRESCOUNTRY() + "'" +
			", DEATHLOCATION_STREET='" + getDEATHLOCATION_STREET() + "'" +
			", DEATHLOCATION_CITY='" + getDEATHLOCATION_CITY() + "'" +
			", DEATHLOCATION_COUNTY='" + getDEATHLOCATION_COUNTY() + "'" +
			", DEATHLOCATION_STATE='" + getDEATHLOCATION_STATE() + "'" +
			", DEATHLOCATION_ZIP='" + getDEATHLOCATION_ZIP() + "'" +
			", DEATHLOCATION_COUNTRY='" + getDEATHLOCATION_COUNTRY() + "'" +
			", DEATHLOCATIONTYPE='" + getDEATHLOCATIONTYPE() + "'" +
			", INJURYLOCATION='" + getINJURYLOCATION() + "'" +
			", FOUNDADDR_STREET='" + getFOUNDADDR_STREET() + "'" +
			", FOUNDADDR_CITY='" + getFOUNDADDR_CITY() + "'" +
			", FOUNDADDR_COUNTY='" + getFOUNDADDR_COUNTY() + "'" +
			", FOUNDADDR_STATE='" + getFOUNDADDR_STATE() + "'" +
			", FOUNDADDR_ZIP='" + getFOUNDADDR_ZIP() + "'" +
			", EVENTPLACE='" + getEVENTPLACE() + "'" +
			", EVENTADDR_STREET='" + getEVENTADDR_STREET() + "'" +
			", EVENTADDR_CITY='" + getEVENTADDR_CITY() + "'" +
			", EVENTADDR_COUNTY='" + getEVENTADDR_COUNTY() + "'" +
			", EVENTADDR_STATE='" + getEVENTADDR_STATE() + "'" +
			", EVENTADDR_ZIP='" + getEVENTADDR_ZIP() + "'" +
			", PRNPLACE='" + getPRNPLACE() + "'" +
			", PRNSTREET='" + getPRNSTREET() + "'" +
			", PRNCITY='" + getPRNCITY() + "'" +
			", PRNCOUNTY='" + getPRNCOUNTY() + "'" +
			", PRNSTATE='" + getPRNSTATE() + "'" +
			", PRNZIP='" + getPRNZIP() + "'" +
			", DISP_PLACE='" + getDISP_PLACE() + "'" +
			", DISP_STREET='" + getDISP_STREET() + "'" +
			", DISP_CITY='" + getDISP_CITY() + "'" +
			", DISP_COUNTY='" + getDISP_COUNTY() + "'" +
			", DISP_STATE='" + getDISP_STATE() + "'" +
			", DISP_ZIP='" + getDISP_ZIP() + "'" +
			", RESNAME='" + getRESNAME() + "'" +
			", LKAWHERE='" + getLKAWHERE() + "'" +
			", HOSPNAME='" + getHOSPNAME() + "'" +
			", SCENEADDR_STREET='" + getSCENEADDR_STREET() + "'" +
			", SCENEADDR_CITY='" + getSCENEADDR_CITY() + "'" +
			", SCENEADDR_COUNTY='" + getSCENEADDR_COUNTY() + "'" +
			", SCENEADDR_STATE='" + getSCENEADDR_STATE() + "'" +
			", SCENEADDR_ZIP='" + getSCENEADDR_ZIP() + "'" +
			", CERTIFIER_NAME='" + getCERTIFIER_NAME() + "'" +
			", CERTIFIER_TYPE='" + getCERTIFIER_TYPE() + "'" +
			", SURGERY='" + getSURGERY() + "'" +
			", SURGDATE='" + getSURGDATE() + "'" +
			", SURGREASON='" + getSURGREASON() + "'" +
			", HCPROVIDER='" + getHCPROVIDER() + "'" +
			", AUTOPSYPERFORMED='" + getAUTOPSYPERFORMED() + "'" +
			", AUTOPSYRESULTSAVAILABLE='" + getAUTOPSYRESULTSAVAILABLE() + "'" +
			", AUTOPSY_OFFICENAME='" + getAUTOPSY_OFFICENAME() + "'" +
			", AUTOPSY_STREET='" + getAUTOPSY_STREET() + "'" +
			", AUTOPSY_CITY='" + getAUTOPSY_CITY() + "'" +
			", AUTOPSY_COUNTY='" + getAUTOPSY_COUNTY() + "'" +
			", AUTOPSY_STATE='" + getAUTOPSY_STATE() + "'" +
			", AUTOPSY_ZIP='" + getAUTOPSY_ZIP() + "'" +
			", CUSTODY='" + getCUSTODY() + "'" +
			", PREGNANT='" + getPREGNANT() + "'" +
			", TOBACCO='" + getTOBACCO() + "'" +
			", TRANSPORTATION='" + getTRANSPORTATION() + "'" +
			", MENAME='" + getMENAME() + "'" +
			", MEPHONE='" + getMEPHONE() + "'" +
			", MELICENSE='" + getMELICENSE() + "'" +
			", ME_STREET='" + getME_STREET() + "'" +
			", ME_CITY='" + getME_CITY() + "'" +
			", ME_COUNTY='" + getME_COUNTY() + "'" +
			", ME_STATE='" + getME_STATE() + "'" +
			", ME_ZIP='" + getME_ZIP() + "'" +
			", PRONOUNCERNAME='" + getPRONOUNCERNAME() + "'" +
			", CERTIFIER_IDENTIFIER='" + getCERTIFIER_IDENTIFIER() + "'" +
			", CERTIFIER_IDENTIFIER_SYSTEM='" + getCERTIFIER_IDENTIFIER_SYSTEM() + "'" +
			"}";
	}

}