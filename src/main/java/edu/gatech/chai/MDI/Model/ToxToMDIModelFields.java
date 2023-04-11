package edu.gatech.chai.MDI.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.opencsv.bean.CsvBindByName;

public class ToxToMDIModelFields {
	@CsvBindByName
	public String TOXCASENUMBER = "";
	@CsvBindByName
	public String TOXORGNAME = "";
	@CsvBindByName
	public String TOXORDERCODE = "";
	@CsvBindByName
	public String TOXPERFORMER = "";
	@CsvBindByName
	public String TOXORGSTREET = "";
	@CsvBindByName
	public String TOXORGCITY = "";
	@CsvBindByName
	public String TOXORGCOUNTY = "";
	@CsvBindByName
	public String TOXORGSTATE = "";
	@CsvBindByName
	public String TOXORGZIP = "";
	@CsvBindByName
	public String TOXORGCOUNTRY = "";
	@CsvBindByName
	public String MDICASEID = "";
	@CsvBindByName
	public String MDICASESYSTEM = "";
	@CsvBindByName
	public String FIRSTNAME = "";
	@CsvBindByName
	public String MIDNAME = "";
	@CsvBindByName
	public String LASTNAME = "";
	@CsvBindByName
	public String SUFFIXNAME = "";
	@CsvBindByName
	public String BIRTHDATE = "";
	@CsvBindByName
	public String MECNOTES = "";
	@CsvBindByName
	public String SPECIMENCOLLECTION_DATETIME = "";
	@CsvBindByName
	public String REPORTDATE = "";
	@CsvBindByName
	public List<ToxSpecimen> SPECIMENS = new ArrayList<ToxSpecimen>();
	@CsvBindByName
	public List<ToxResult> RESULTS = new ArrayList<ToxResult>();
	@CsvBindByName
	public List<String> NOTES = new ArrayList<String>();


	public ToxToMDIModelFields() {
	}

	public ToxToMDIModelFields(String TOXCASENUMBER, String TOXORGNAME, String TOXORDERCODE, String TOXPERFORMER, String TOXORGSTREET, String TOXORGCITY, String TOXORGCOUNTY, String TOXORGSTATE, String TOXORGZIP, String TOXORGCOUNTRY, String MDICASEID, String MDICASESYSTEM, String FIRSTNAME, String MIDNAME, String LASTNAME, String SUFFIXNAME, String BIRTHDATE, String MECNOTES, String SPECIMENCOLLECTION_DATETIME, String REPORTDATE, List<ToxSpecimen> SPECIMENS, List<ToxResult> RESULTS, List<String> NOTES) {
		this.TOXCASENUMBER = TOXCASENUMBER;
		this.TOXORGNAME = TOXORGNAME;
		this.TOXORDERCODE = TOXORDERCODE;
		this.TOXPERFORMER = TOXPERFORMER;
		this.TOXORGSTREET = TOXORGSTREET;
		this.TOXORGCITY = TOXORGCITY;
		this.TOXORGCOUNTY = TOXORGCOUNTY;
		this.TOXORGSTATE = TOXORGSTATE;
		this.TOXORGZIP = TOXORGZIP;
		this.TOXORGCOUNTRY = TOXORGCOUNTRY;
		this.MDICASEID = MDICASEID;
		this.MDICASESYSTEM = MDICASESYSTEM;
		this.FIRSTNAME = FIRSTNAME;
		this.MIDNAME = MIDNAME;
		this.LASTNAME = LASTNAME;
		this.SUFFIXNAME = SUFFIXNAME;
		this.BIRTHDATE = BIRTHDATE;
		this.MECNOTES = MECNOTES;
		this.SPECIMENCOLLECTION_DATETIME = SPECIMENCOLLECTION_DATETIME;
		this.REPORTDATE = REPORTDATE;
		this.SPECIMENS = SPECIMENS;
		this.RESULTS = RESULTS;
		this.NOTES = NOTES;
	}

	public String getTOXCASENUMBER() {
		return this.TOXCASENUMBER;
	}

	public void setTOXCASENUMBER(String TOXCASENUMBER) {
		this.TOXCASENUMBER = TOXCASENUMBER;
	}

	public String getTOXORGNAME() {
		return this.TOXORGNAME;
	}

	public void setTOXORGNAME(String TOXORGNAME) {
		this.TOXORGNAME = TOXORGNAME;
	}

	public String getTOXORDERCODE() {
		return this.TOXORDERCODE;
	}

	public void setTOXORDERCODE(String TOXORDERCODE) {
		this.TOXORDERCODE = TOXORDERCODE;
	}

	public String getTOXPERFORMER() {
		return this.TOXPERFORMER;
	}

	public void setTOXPERFORMER(String TOXPERFORMER) {
		this.TOXPERFORMER = TOXPERFORMER;
	}

	public String getTOXORGSTREET() {
		return this.TOXORGSTREET;
	}

	public void setTOXORGSTREET(String TOXORGSTREET) {
		this.TOXORGSTREET = TOXORGSTREET;
	}

	public String getTOXORGCITY() {
		return this.TOXORGCITY;
	}

	public void setTOXORGCITY(String TOXORGCITY) {
		this.TOXORGCITY = TOXORGCITY;
	}

	public String getTOXORGCOUNTY() {
		return this.TOXORGCOUNTY;
	}

	public void setTOXORGCOUNTY(String TOXORGCOUNTY) {
		this.TOXORGCOUNTY = TOXORGCOUNTY;
	}

	public String getTOXORGSTATE() {
		return this.TOXORGSTATE;
	}

	public void setTOXORGSTATE(String TOXORGSTATE) {
		this.TOXORGSTATE = TOXORGSTATE;
	}

	public String getTOXORGZIP() {
		return this.TOXORGZIP;
	}

	public void setTOXORGZIP(String TOXORGZIP) {
		this.TOXORGZIP = TOXORGZIP;
	}

	public String getTOXORGCOUNTRY() {
		return this.TOXORGCOUNTRY;
	}

	public void setTOXORGCOUNTRY(String TOXORGCOUNTRY) {
		this.TOXORGCOUNTRY = TOXORGCOUNTRY;
	}

	public String getMDICASEID() {
		return this.MDICASEID;
	}

	public void setMDICASEID(String MDICASEID) {
		this.MDICASEID = MDICASEID;
	}

	public String getMDICASESYSTEM() {
		return this.MDICASESYSTEM;
	}

	public void setMDICASESYSTEM(String MDICASESYSTEM) {
		this.MDICASESYSTEM = MDICASESYSTEM;
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

	public String getBIRTHDATE() {
		return this.BIRTHDATE;
	}

	public void setBIRTHDATE(String BIRTHDATE) {
		this.BIRTHDATE = BIRTHDATE;
	}

	public String getMECNOTES() {
		return this.MECNOTES;
	}

	public void setMECNOTES(String MECNOTES) {
		this.MECNOTES = MECNOTES;
	}

	public String getSPECIMENCOLLECTION_DATETIME() {
		return this.SPECIMENCOLLECTION_DATETIME;
	}

	public void setSPECIMENCOLLECTION_DATETIME(String SPECIMENCOLLECTION_DATETIME) {
		this.SPECIMENCOLLECTION_DATETIME = SPECIMENCOLLECTION_DATETIME;
	}

	public String getREPORTDATE() {
		return this.REPORTDATE;
	}

	public void setREPORTDATE(String REPORTDATE) {
		this.REPORTDATE = REPORTDATE;
	}

	public List<ToxSpecimen> getSPECIMENS() {
		return this.SPECIMENS;
	}

	public void setSPECIMENS(List<ToxSpecimen> SPECIMENS) {
		this.SPECIMENS = SPECIMENS;
	}

	public List<ToxResult> getRESULTS() {
		return this.RESULTS;
	}

	public void setRESULTS(List<ToxResult> RESULTS) {
		this.RESULTS = RESULTS;
	}

	public List<String> getNOTES() {
		return this.NOTES;
	}

	public void setNOTES(List<String> NOTES) {
		this.NOTES = NOTES;
	}

	public ToxToMDIModelFields TOXCASENUMBER(String TOXCASENUMBER) {
		setTOXCASENUMBER(TOXCASENUMBER);
		return this;
	}

	public ToxToMDIModelFields TOXORGNAME(String TOXORGNAME) {
		setTOXORGNAME(TOXORGNAME);
		return this;
	}

	public ToxToMDIModelFields TOXORDERCODE(String TOXORDERCODE) {
		setTOXORDERCODE(TOXORDERCODE);
		return this;
	}

	public ToxToMDIModelFields TOXPERFORMER(String TOXPERFORMER) {
		setTOXPERFORMER(TOXPERFORMER);
		return this;
	}

	public ToxToMDIModelFields TOXORGSTREET(String TOXORGSTREET) {
		setTOXORGSTREET(TOXORGSTREET);
		return this;
	}

	public ToxToMDIModelFields TOXORGCITY(String TOXORGCITY) {
		setTOXORGCITY(TOXORGCITY);
		return this;
	}

	public ToxToMDIModelFields TOXORGCOUNTY(String TOXORGCOUNTY) {
		setTOXORGCOUNTY(TOXORGCOUNTY);
		return this;
	}

	public ToxToMDIModelFields TOXORGSTATE(String TOXORGSTATE) {
		setTOXORGSTATE(TOXORGSTATE);
		return this;
	}

	public ToxToMDIModelFields TOXORGZIP(String TOXORGZIP) {
		setTOXORGZIP(TOXORGZIP);
		return this;
	}

	public ToxToMDIModelFields TOXORGCOUNTRY(String TOXORGCOUNTRY) {
		setTOXORGCOUNTRY(TOXORGCOUNTRY);
		return this;
	}

	public ToxToMDIModelFields MDICASEID(String MDICASEID) {
		setMDICASEID(MDICASEID);
		return this;
	}

	public ToxToMDIModelFields MDICASESYSTEM(String MDICASESYSTEM) {
		setMDICASESYSTEM(MDICASESYSTEM);
		return this;
	}

	public ToxToMDIModelFields FIRSTNAME(String FIRSTNAME) {
		setFIRSTNAME(FIRSTNAME);
		return this;
	}

	public ToxToMDIModelFields MIDNAME(String MIDNAME) {
		setMIDNAME(MIDNAME);
		return this;
	}

	public ToxToMDIModelFields LASTNAME(String LASTNAME) {
		setLASTNAME(LASTNAME);
		return this;
	}

	public ToxToMDIModelFields SUFFIXNAME(String SUFFIXNAME) {
		setSUFFIXNAME(SUFFIXNAME);
		return this;
	}

	public ToxToMDIModelFields BIRTHDATE(String BIRTHDATE) {
		setBIRTHDATE(BIRTHDATE);
		return this;
	}

	public ToxToMDIModelFields MECNOTES(String MECNOTES) {
		setMECNOTES(MECNOTES);
		return this;
	}

	public ToxToMDIModelFields SPECIMENCOLLECTION_DATETIME(String SPECIMENCOLLECTION_DATETIME) {
		setSPECIMENCOLLECTION_DATETIME(SPECIMENCOLLECTION_DATETIME);
		return this;
	}

	public ToxToMDIModelFields REPORTDATE(String REPORTDATE) {
		setREPORTDATE(REPORTDATE);
		return this;
	}

	public ToxToMDIModelFields SPECIMENS(List<ToxSpecimen> SPECIMENS) {
		setSPECIMENS(SPECIMENS);
		return this;
	}

	public ToxToMDIModelFields RESULTS(List<ToxResult> RESULTS) {
		setRESULTS(RESULTS);
		return this;
	}

	public ToxToMDIModelFields NOTES(List<String> NOTES) {
		setNOTES(NOTES);
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof ToxToMDIModelFields)) {
			return false;
		}
		ToxToMDIModelFields toxToMDIModelFields = (ToxToMDIModelFields) o;
		return Objects.equals(TOXCASENUMBER, toxToMDIModelFields.TOXCASENUMBER) && Objects.equals(TOXORGNAME, toxToMDIModelFields.TOXORGNAME) && Objects.equals(TOXORDERCODE, toxToMDIModelFields.TOXORDERCODE) && Objects.equals(TOXPERFORMER, toxToMDIModelFields.TOXPERFORMER) && Objects.equals(TOXORGSTREET, toxToMDIModelFields.TOXORGSTREET) && Objects.equals(TOXORGCITY, toxToMDIModelFields.TOXORGCITY) && Objects.equals(TOXORGCOUNTY, toxToMDIModelFields.TOXORGCOUNTY) && Objects.equals(TOXORGSTATE, toxToMDIModelFields.TOXORGSTATE) && Objects.equals(TOXORGZIP, toxToMDIModelFields.TOXORGZIP) && Objects.equals(TOXORGCOUNTRY, toxToMDIModelFields.TOXORGCOUNTRY) && Objects.equals(MDICASEID, toxToMDIModelFields.MDICASEID) && Objects.equals(MDICASESYSTEM, toxToMDIModelFields.MDICASESYSTEM) && Objects.equals(FIRSTNAME, toxToMDIModelFields.FIRSTNAME) && Objects.equals(MIDNAME, toxToMDIModelFields.MIDNAME) && Objects.equals(LASTNAME, toxToMDIModelFields.LASTNAME) && Objects.equals(SUFFIXNAME, toxToMDIModelFields.SUFFIXNAME) && Objects.equals(BIRTHDATE, toxToMDIModelFields.BIRTHDATE) && Objects.equals(MECNOTES, toxToMDIModelFields.MECNOTES) && Objects.equals(SPECIMENCOLLECTION_DATETIME, toxToMDIModelFields.SPECIMENCOLLECTION_DATETIME) && Objects.equals(REPORTDATE, toxToMDIModelFields.REPORTDATE) && Objects.equals(SPECIMENS, toxToMDIModelFields.SPECIMENS) && Objects.equals(RESULTS, toxToMDIModelFields.RESULTS) && Objects.equals(NOTES, toxToMDIModelFields.NOTES);
	}

	@Override
	public int hashCode() {
		return Objects.hash(TOXCASENUMBER, TOXORGNAME, TOXORDERCODE, TOXPERFORMER, TOXORGSTREET, TOXORGCITY, TOXORGCOUNTY, TOXORGSTATE, TOXORGZIP, TOXORGCOUNTRY, MDICASEID, MDICASESYSTEM, FIRSTNAME, MIDNAME, LASTNAME, SUFFIXNAME, BIRTHDATE, MECNOTES, SPECIMENCOLLECTION_DATETIME, REPORTDATE, SPECIMENS, RESULTS, NOTES);
	}

	@Override
	public String toString() {
		return "{" +
			" TOXCASENUMBER='" + getTOXCASENUMBER() + "'" +
			", TOXORGNAME='" + getTOXORGNAME() + "'" +
			", TOXORDERCODE='" + getTOXORDERCODE() + "'" +
			", TOXPERFORMER='" + getTOXPERFORMER() + "'" +
			", TOXORGSTREET='" + getTOXORGSTREET() + "'" +
			", TOXORGCITY='" + getTOXORGCITY() + "'" +
			", TOXORGCOUNTY='" + getTOXORGCOUNTY() + "'" +
			", TOXORGSTATE='" + getTOXORGSTATE() + "'" +
			", TOXORGZIP='" + getTOXORGZIP() + "'" +
			", TOXORGCOUNTRY='" + getTOXORGCOUNTRY() + "'" +
			", MDICASEID='" + getMDICASEID() + "'" +
			", MDICASESYSTEM='" + getMDICASESYSTEM() + "'" +
			", FIRSTNAME='" + getFIRSTNAME() + "'" +
			", MIDNAME='" + getMIDNAME() + "'" +
			", LASTNAME='" + getLASTNAME() + "'" +
			", SUFFIXNAME='" + getSUFFIXNAME() + "'" +
			", BIRTHDATE='" + getBIRTHDATE() + "'" +
			", MECNOTES='" + getMECNOTES() + "'" +
			", SPECIMENCOLLECTION_DATETIME='" + getSPECIMENCOLLECTION_DATETIME() + "'" +
			", REPORTDATE='" + getREPORTDATE() + "'" +
			", SPECIMENS='" + getSPECIMENS() + "'" +
			", RESULTS='" + getRESULTS() + "'" +
			", NOTES='" + getNOTES() + "'" +
			"}";
	}	
}