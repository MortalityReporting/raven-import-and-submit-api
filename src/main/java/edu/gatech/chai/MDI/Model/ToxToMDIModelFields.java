package edu.gatech.chai.MDI.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.opencsv.bean.CsvBindByName;

public class ToxToMDIModelFields extends BaseModelFields{
	@CsvBindByName
	public String FILEID = "";
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
	public String DECEDENTSEX = "";
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
		super();
	}

	public ToxToMDIModelFields(String FILEID, String TOXCASENUMBER, String TOXORGNAME, String TOXORDERCODE, String TOXPERFORMER, String TOXORGSTREET, String TOXORGCITY, String TOXORGCOUNTY, String TOXORGSTATE, String TOXORGZIP, String TOXORGCOUNTRY, String MDICASEID, String MDICASESYSTEM, String FIRSTNAME, String MIDNAME, String LASTNAME, String DECEDENTSEX, String SUFFIXNAME, String BIRTHDATE, String MECNOTES, String SPECIMENCOLLECTION_DATETIME, String REPORTDATE, List<ToxSpecimen> SPECIMENS, List<ToxResult> RESULTS, List<String> NOTES) {
		super();
		this.FILEID = FILEID;
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
		this.DECEDENTSEX = DECEDENTSEX;
		this.SUFFIXNAME = SUFFIXNAME;
		this.BIRTHDATE = BIRTHDATE;
		this.MECNOTES = MECNOTES;
		this.SPECIMENCOLLECTION_DATETIME = SPECIMENCOLLECTION_DATETIME;
		this.REPORTDATE = REPORTDATE;
		this.SPECIMENS = SPECIMENS;
		this.RESULTS = RESULTS;
		this.NOTES = NOTES;
	}

	// Getters
	public String getFILEID() {
        return FILEID;
    }

    public String getTOXCASENUMBER() {
        return TOXCASENUMBER;
    }

    public String getTOXORGNAME() {
        return TOXORGNAME;
    }

    public String getTOXORDERCODE() {
        return TOXORDERCODE;
    }

    public String getTOXPERFORMER() {
        return TOXPERFORMER;
    }

    public String getTOXORGSTREET() {
        return TOXORGSTREET;
    }

    public String getTOXORGCITY() {
        return TOXORGCITY;
    }

    public String getTOXORGCOUNTY() {
        return TOXORGCOUNTY;
    }

    public String getTOXORGSTATE() {
        return TOXORGSTATE;
    }

    public String getTOXORGZIP() {
        return TOXORGZIP;
    }

    public String getTOXORGCOUNTRY() {
        return TOXORGCOUNTRY;
    }

    public String getMDICASEID() {
        return MDICASEID;
    }

    public String getMDICASESYSTEM() {
        return MDICASESYSTEM;
    }

    public String getFIRSTNAME() {
        return FIRSTNAME;
    }

    public String getMIDNAME() {
        return MIDNAME;
    }

    public String getLASTNAME() {
        return LASTNAME;
    }

	public String getDECEDENTSEX() {
        return DECEDENTSEX;
    }

    public String getSUFFIXNAME() {
        return SUFFIXNAME;
    }

    public String getBIRTHDATE() {
        return BIRTHDATE;
    }

    public String getMECNOTES() {
        return MECNOTES;
    }

    public String getSPECIMENCOLLECTION_DATETIME() {
        return SPECIMENCOLLECTION_DATETIME;
    }

    public String getREPORTDATE() {
        return REPORTDATE;
    }

    public List<ToxSpecimen> getSPECIMENS() {
        return SPECIMENS;
    }

    public List<ToxResult> getRESULTS() {
        return RESULTS;
    }

    public List<String> getNOTES() {
        return NOTES;
    }

    // Setters
	public void setFILEID(String FILEID) {
		this.checkNullSetter(this.FILEID.getClass(), FILEID);
		this.FILEID = FILEID;
	}

    public void setTOXCASENUMBER(String TOXCASENUMBER) {
		this.checkNullSetter(this.TOXCASENUMBER.getClass(), TOXCASENUMBER);
		this.TOXCASENUMBER = TOXCASENUMBER;
	}
	
	public void setTOXORGNAME(String TOXORGNAME) {
		this.checkNullSetter(this.TOXORGNAME.getClass(), TOXORGNAME);
		this.TOXORGNAME = TOXORGNAME;
	}
	
	public void setTOXORDERCODE(String TOXORDERCODE) {
		this.checkNullSetter(this.TOXORDERCODE.getClass(), TOXORDERCODE);
		this.TOXORDERCODE = TOXORDERCODE;
	}
	
	public void setTOXPERFORMER(String TOXPERFORMER) {
		this.checkNullSetter(this.TOXPERFORMER.getClass(), TOXPERFORMER);
		this.TOXPERFORMER = TOXPERFORMER;
	}
	
	public void setTOXORGSTREET(String TOXORGSTREET) {
		this.checkNullSetter(this.TOXORGSTREET.getClass(), TOXORGSTREET);
		this.TOXORGSTREET = TOXORGSTREET;
	}
	
	public void setTOXORGCITY(String TOXORGCITY) {
		this.checkNullSetter(this.TOXORGCITY.getClass(), TOXORGCITY);
		this.TOXORGCITY = TOXORGCITY;
	}
	
	public void setTOXORGCOUNTY(String TOXORGCOUNTY) {
		this.checkNullSetter(this.TOXORGCOUNTY.getClass(), TOXORGCOUNTY);
		this.TOXORGCOUNTY = TOXORGCOUNTY;
	}
	
	public void setTOXORGSTATE(String TOXORGSTATE) {
		this.checkNullSetter(this.TOXORGSTATE.getClass(), TOXORGSTATE);
		this.TOXORGSTATE = TOXORGSTATE;
	}
	
	public void setTOXORGZIP(String TOXORGZIP) {
		this.checkNullSetter(this.TOXORGZIP.getClass(), TOXORGZIP);
		this.TOXORGZIP = TOXORGZIP;
	}
	
	public void setTOXORGCOUNTRY(String TOXORGCOUNTRY) {
		this.checkNullSetter(this.TOXORGCOUNTRY.getClass(), TOXORGCOUNTRY);
		this.TOXORGCOUNTRY = TOXORGCOUNTRY;
	}
	
	public void setMDICASEID(String MDICASEID) {
		this.checkNullSetter(this.MDICASEID.getClass(), MDICASEID);
		this.MDICASEID = MDICASEID;
	}
	
	public void setMDICASESYSTEM(String MDICASESYSTEM) {
		this.checkNullSetter(this.MDICASESYSTEM.getClass(), MDICASESYSTEM);
		this.MDICASESYSTEM = MDICASESYSTEM;
	}
	
	public void setFIRSTNAME(String FIRSTNAME) {
		this.checkNullSetter(this.FIRSTNAME.getClass(), FIRSTNAME);
		this.FIRSTNAME = FIRSTNAME;
	}
	
	public void setMIDNAME(String MIDNAME) {
		this.checkNullSetter(this.MIDNAME.getClass(), MIDNAME);
		this.MIDNAME = MIDNAME;
	}
	
	public void setLASTNAME(String LASTNAME) {
		this.checkNullSetter(this.LASTNAME.getClass(), LASTNAME);
		this.LASTNAME = LASTNAME;
	}

	public String setDECEDENTSEX(String DECEDENTSEX) {
		this.checkNullSetter(this.DECEDENTSEX.getClass(), DECEDENTSEX);
        return DECEDENTSEX;
    }
	
	public void setSUFFIXNAME(String SUFFIXNAME) {
		this.checkNullSetter(this.SUFFIXNAME.getClass(), SUFFIXNAME);
		this.SUFFIXNAME = SUFFIXNAME;
	}
	
	public void setBIRTHDATE(String BIRTHDATE) {
		this.checkNullSetter(this.BIRTHDATE.getClass(), BIRTHDATE);
		this.BIRTHDATE = BIRTHDATE;
	}
	
	public void setMECNOTES(String MECNOTES) {
		this.checkNullSetter(this.MECNOTES.getClass(), MECNOTES);
		this.MECNOTES = MECNOTES;
	}
	
	public void setSPECIMENCOLLECTION_DATETIME(String SPECIMENCOLLECTION_DATETIME) {
		this.checkNullSetter(this.SPECIMENCOLLECTION_DATETIME.getClass(), SPECIMENCOLLECTION_DATETIME);
		this.SPECIMENCOLLECTION_DATETIME = SPECIMENCOLLECTION_DATETIME;
	}
	
	public void setREPORTDATE(String REPORTDATE) {
		this.checkNullSetter(this.REPORTDATE.getClass(), REPORTDATE);
		this.REPORTDATE = REPORTDATE;
	}
	
	public void setSPECIMENS(List<ToxSpecimen> SPECIMENS) {
		this.SPECIMENS = SPECIMENS;
	}
	
	public void setRESULTS(List<ToxResult> RESULTS) {
		this.RESULTS = RESULTS;
	}
	
	public void setNOTES(List<String> NOTES) {
		this.NOTES = NOTES;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof ToxToMDIModelFields)) {
			return false;
		}
		ToxToMDIModelFields toxToMDIModelFields = (ToxToMDIModelFields) o;
		return Objects.equals(FILEID, toxToMDIModelFields.FILEID) && Objects.equals(TOXCASENUMBER, toxToMDIModelFields.TOXCASENUMBER) && Objects.equals(TOXORGNAME, toxToMDIModelFields.TOXORGNAME) && Objects.equals(TOXORDERCODE, toxToMDIModelFields.TOXORDERCODE) && Objects.equals(TOXPERFORMER, toxToMDIModelFields.TOXPERFORMER) && Objects.equals(TOXORGSTREET, toxToMDIModelFields.TOXORGSTREET) && Objects.equals(TOXORGCITY, toxToMDIModelFields.TOXORGCITY) && Objects.equals(TOXORGCOUNTY, toxToMDIModelFields.TOXORGCOUNTY) && Objects.equals(TOXORGSTATE, toxToMDIModelFields.TOXORGSTATE) && Objects.equals(TOXORGZIP, toxToMDIModelFields.TOXORGZIP) && Objects.equals(TOXORGCOUNTRY, toxToMDIModelFields.TOXORGCOUNTRY) && Objects.equals(MDICASEID, toxToMDIModelFields.MDICASEID) && Objects.equals(MDICASESYSTEM, toxToMDIModelFields.MDICASESYSTEM) && Objects.equals(FIRSTNAME, toxToMDIModelFields.FIRSTNAME) && Objects.equals(MIDNAME, toxToMDIModelFields.MIDNAME) && Objects.equals(LASTNAME, toxToMDIModelFields.LASTNAME) && Objects.equals(SUFFIXNAME, toxToMDIModelFields.SUFFIXNAME) && Objects.equals(BIRTHDATE, toxToMDIModelFields.BIRTHDATE) && Objects.equals(MECNOTES, toxToMDIModelFields.MECNOTES) && Objects.equals(SPECIMENCOLLECTION_DATETIME, toxToMDIModelFields.SPECIMENCOLLECTION_DATETIME) && Objects.equals(REPORTDATE, toxToMDIModelFields.REPORTDATE) && Objects.equals(SPECIMENS, toxToMDIModelFields.SPECIMENS) && Objects.equals(RESULTS, toxToMDIModelFields.RESULTS) && Objects.equals(NOTES, toxToMDIModelFields.NOTES);
	}

	@Override
	public int hashCode() {
		return Objects.hash(FILEID, TOXCASENUMBER, TOXORGNAME, TOXORDERCODE, TOXPERFORMER, TOXORGSTREET, TOXORGCITY, TOXORGCOUNTY, TOXORGSTATE, TOXORGZIP, TOXORGCOUNTRY, MDICASEID, MDICASESYSTEM, FIRSTNAME, MIDNAME, LASTNAME, SUFFIXNAME, BIRTHDATE, MECNOTES, SPECIMENCOLLECTION_DATETIME, REPORTDATE, SPECIMENS, RESULTS, NOTES);
	}

	@Override
	public String toString() {
		return "{" +
			" FILEID='" + getFILEID() + "'" +
			", TOXCASENUMBER='" + getTOXCASENUMBER() + "'" +
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
			", DECEDENTSEX='" + getDECEDENTSEX() + "'" +
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
