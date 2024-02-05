package edu.gatech.chai.MDI.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.opencsv.bean.CsvBindByName;

public class ToxToMDIModelFields extends BaseModelFields{
	//File Id
	@CsvBindByName
	public String FILEID = "";
	//Laboratory Section
	@CsvBindByName
	public String TOXCASENUMBER = "";
	@CsvBindByName
	public String TOXORG_NAME = "";
	@CsvBindByName
	public String TOXORDERCODE = "";
	@CsvBindByName
	public String TOXPERFORMER = "";
	@CsvBindByName
	public String TOXORG_STREET = "";
	@CsvBindByName
	public String TOXORG_CITY = "";
	@CsvBindByName
	public String TOXORG_COUNTY = "";
	@CsvBindByName
	public String TOXORG_STATE = "";
	@CsvBindByName
	public String TOXORG_ZIP = "";
	@CsvBindByName
	public String TOXORG_COUNTRY = "";
	//Decedent Section
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
	public String RECEIPT_DATETIME = "";
	@CsvBindByName
	public String REPORTISSUANCE_DATETIME = "";
	//Agency Section
	@CsvBindByName
	public String AGENCY_NAME = "";
	@CsvBindByName
	public String AGENCY_STREET = "";
	@CsvBindByName
	public String AGENCY_CITY = "";
	@CsvBindByName
	public String AGENCY_COUNTY = "";
	@CsvBindByName
	public String AGENCY_STATE = "";
	@CsvBindByName
	public String AGENCY_ZIP = "";
	@CsvBindByName
	public String CORONER_NAME = "";
	@CsvBindByName
	public String INVESTIGATOR = "";
	//Specimens Section
	@CsvBindByName
	public List<ToxSpecimen> SPECIMENS = new ArrayList<ToxSpecimen>();
	//Results Section
	@CsvBindByName
	public List<ToxResult> RESULTS = new ArrayList<ToxResult>();
	//Notes Section
	@CsvBindByName
	public List<String> NOTES = new ArrayList<String>();

	public ToxToMDIModelFields() {
		super();
	}

	public ToxToMDIModelFields(String FILEID, String TOXCASENUMBER, String TOXORGNAME, String TOXORDERCODE, String TOXPERFORMER, String TOXORGSTREET, String TOXORGCITY, String TOXORGCOUNTY, String TOXORGSTATE, String TOXORGZIP, String TOXORGCOUNTRY, String MDICASEID, String MDICASESYSTEM, String FIRSTNAME, String MIDNAME, String LASTNAME, String DECEDENTSEX, String SUFFIXNAME, String BIRTHDATE, String MECNOTES, String SPECIMENCOLLECTION_DATETIME, String RECEIPT_DATETIME, String REPORTDATE, List<ToxSpecimen> SPECIMENS, List<ToxResult> RESULTS, List<String> NOTES) {
		super();
		this.FILEID = FILEID;
		this.TOXCASENUMBER = TOXCASENUMBER;
		this.TOXORG_NAME = TOXORGNAME;
		this.TOXORDERCODE = TOXORDERCODE;
		this.TOXPERFORMER = TOXPERFORMER;
		this.TOXORG_STREET = TOXORGSTREET;
		this.TOXORG_CITY = TOXORGCITY;
		this.TOXORG_COUNTY = TOXORGCOUNTY;
		this.TOXORG_STATE = TOXORGSTATE;
		this.TOXORG_ZIP = TOXORGZIP;
		this.TOXORG_COUNTRY = TOXORGCOUNTRY;
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
		this.RECEIPT_DATETIME = RECEIPT_DATETIME;
		this.REPORTISSUANCE_DATETIME = REPORTDATE;
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

    public String getTOXORG_NAME() {
        return TOXORG_NAME;
    }

    public String getTOXORDERCODE() {
        return TOXORDERCODE;
    }

    public String getTOXPERFORMER() {
        return TOXPERFORMER;
    }

    public String getTOXORG_STREET() {
        return TOXORG_STREET;
    }

    public String getTOXORG_CITY() {
        return TOXORG_CITY;
    }

    public String getTOXORG_COUNTY() {
        return TOXORG_COUNTY;
    }

    public String getTOXORG_STATE() {
        return TOXORG_STATE;
    }

    public String getTOXORG_ZIP() {
        return TOXORG_ZIP;
    }

    public String getTOXORG_COUNTRY() {
        return TOXORG_COUNTRY;
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

	public String getRECEIPT_DATETIME() {
        return RECEIPT_DATETIME;
    }

    public String getREPORTISSUANCE_DATETIME() {
        return REPORTISSUANCE_DATETIME;
    }
	//@CsvBindByName
	//public String AGENCY_NAME = "";
	//@CsvBindByName
	//public String AGENCY_STREET = "";
	//@CsvBindByName
	//public String AGENCY_CITY = "";
	//@CsvBindByName
	//public String AGENCY_COUNTY = "";
	//@CsvBindByName
	//public String AGENCY_STATE = "";
	//@CsvBindByName
	//public String AGENCY_ZIP = "";
	//@CsvBindByName
	//public String CORONER_NAME = "";
	//@CsvBindByName
	//public String INVESTIGATOR = "";

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
	
	public void setTOXORG_NAME(String TOXORGNAME) {
		this.checkNullSetter(this.TOXORG_NAME.getClass(), TOXORGNAME);
		this.TOXORG_NAME = TOXORGNAME;
	}
	
	public void setTOXORDERCODE(String TOXORDERCODE) {
		this.checkNullSetter(this.TOXORDERCODE.getClass(), TOXORDERCODE);
		this.TOXORDERCODE = TOXORDERCODE;
	}
	
	public void setTOXPERFORMER(String TOXPERFORMER) {
		this.checkNullSetter(this.TOXPERFORMER.getClass(), TOXPERFORMER);
		this.TOXPERFORMER = TOXPERFORMER;
	}
	
	public void setTOXORG_STREET(String TOXORGSTREET) {
		this.checkNullSetter(this.TOXORG_STREET.getClass(), TOXORGSTREET);
		this.TOXORG_STREET = TOXORGSTREET;
	}
	
	public void setTOXORG_CITY(String TOXORGCITY) {
		this.checkNullSetter(this.TOXORG_CITY.getClass(), TOXORGCITY);
		this.TOXORG_CITY = TOXORGCITY;
	}
	
	public void setTOXORG_COUNTY(String TOXORGCOUNTY) {
		this.checkNullSetter(this.TOXORG_COUNTY.getClass(), TOXORGCOUNTY);
		this.TOXORG_COUNTY = TOXORGCOUNTY;
	}
	
	public void setTOXORG_STATE(String TOXORGSTATE) {
		this.checkNullSetter(this.TOXORG_STATE.getClass(), TOXORGSTATE);
		this.TOXORG_STATE = TOXORGSTATE;
	}
	
	public void setTOXORG_ZIP(String TOXORGZIP) {
		this.checkNullSetter(this.TOXORG_ZIP.getClass(), TOXORGZIP);
		this.TOXORG_ZIP = TOXORGZIP;
	}
	
	public void setTOXORG_COUNTRY(String TOXORGCOUNTRY) {
		this.checkNullSetter(this.TOXORG_COUNTRY.getClass(), TOXORGCOUNTRY);
		this.TOXORG_COUNTRY = TOXORGCOUNTRY;
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

	public void setRECEIPT_DATETIME(String RECEIPT_DATETIME) {
		this.checkNullSetter(this.RECEIPT_DATETIME.getClass(), RECEIPT_DATETIME);
		this.RECEIPT_DATETIME = RECEIPT_DATETIME;
	}
	
	public void setREPORTISSUANCE_DATETIME(String REPORTDATE) {
		this.checkNullSetter(this.REPORTISSUANCE_DATETIME.getClass(), REPORTDATE);
		this.REPORTISSUANCE_DATETIME = REPORTDATE;
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
		return Objects.equals(FILEID, toxToMDIModelFields.FILEID) && Objects.equals(TOXCASENUMBER, toxToMDIModelFields.TOXCASENUMBER) && Objects.equals(TOXORG_NAME, toxToMDIModelFields.TOXORG_NAME) && Objects.equals(TOXORDERCODE, toxToMDIModelFields.TOXORDERCODE) && Objects.equals(TOXPERFORMER, toxToMDIModelFields.TOXPERFORMER) && Objects.equals(TOXORG_STREET, toxToMDIModelFields.TOXORG_STREET) && Objects.equals(TOXORG_CITY, toxToMDIModelFields.TOXORG_CITY) && Objects.equals(TOXORG_COUNTY, toxToMDIModelFields.TOXORG_COUNTY) && Objects.equals(TOXORG_STATE, toxToMDIModelFields.TOXORG_STATE) && Objects.equals(TOXORG_ZIP, toxToMDIModelFields.TOXORG_ZIP) && Objects.equals(TOXORG_COUNTRY, toxToMDIModelFields.TOXORG_COUNTRY) && Objects.equals(MDICASEID, toxToMDIModelFields.MDICASEID) && Objects.equals(MDICASESYSTEM, toxToMDIModelFields.MDICASESYSTEM) && Objects.equals(FIRSTNAME, toxToMDIModelFields.FIRSTNAME) && Objects.equals(MIDNAME, toxToMDIModelFields.MIDNAME) && Objects.equals(LASTNAME, toxToMDIModelFields.LASTNAME) && Objects.equals(SUFFIXNAME, toxToMDIModelFields.SUFFIXNAME) && Objects.equals(BIRTHDATE, toxToMDIModelFields.BIRTHDATE) && Objects.equals(MECNOTES, toxToMDIModelFields.MECNOTES) && Objects.equals(SPECIMENCOLLECTION_DATETIME, toxToMDIModelFields.SPECIMENCOLLECTION_DATETIME) && Objects.equals(RECEIPT_DATETIME, toxToMDIModelFields.RECEIPT_DATETIME) && Objects.equals(REPORTISSUANCE_DATETIME, toxToMDIModelFields.REPORTISSUANCE_DATETIME) && Objects.equals(SPECIMENS, toxToMDIModelFields.SPECIMENS) && Objects.equals(RESULTS, toxToMDIModelFields.RESULTS) && Objects.equals(NOTES, toxToMDIModelFields.NOTES);
	}

	@Override
	public int hashCode() {
		return Objects.hash(FILEID, TOXCASENUMBER, TOXORG_NAME, TOXORDERCODE, TOXPERFORMER, TOXORG_STREET, TOXORG_CITY, TOXORG_COUNTY, TOXORG_STATE, TOXORG_ZIP, TOXORG_COUNTRY, MDICASEID, MDICASESYSTEM, FIRSTNAME, MIDNAME, LASTNAME, SUFFIXNAME, BIRTHDATE, MECNOTES, SPECIMENCOLLECTION_DATETIME, RECEIPT_DATETIME, REPORTISSUANCE_DATETIME, SPECIMENS, RESULTS, NOTES);
	}

	@Override
	public String toString() {
		return "{" +
			" FILEID='" + getFILEID() + "'" +
			", TOXCASENUMBER='" + getTOXCASENUMBER() + "'" +
			", TOXORGNAME='" + getTOXORG_NAME() + "'" +
			", TOXORDERCODE='" + getTOXORDERCODE() + "'" +
			", TOXPERFORMER='" + getTOXPERFORMER() + "'" +
			", TOXORGSTREET='" + getTOXORG_STREET() + "'" +
			", TOXORGCITY='" + getTOXORG_CITY() + "'" +
			", TOXORGCOUNTY='" + getTOXORG_COUNTY() + "'" +
			", TOXORGSTATE='" + getTOXORG_STATE() + "'" +
			", TOXORGZIP='" + getTOXORG_ZIP() + "'" +
			", TOXORGCOUNTRY='" + getTOXORG_COUNTRY() + "'" +
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
			", RECEIPT_DATETIME='" + getRECEIPT_DATETIME() + "'" +
			", REPORTDATE='" + getREPORTISSUANCE_DATETIME() + "'" +
			", SPECIMENS='" + getSPECIMENS() + "'" +
			", RESULTS='" + getRESULTS() + "'" +
			", NOTES='" + getNOTES() + "'" +
			"}";
	}
	
}
