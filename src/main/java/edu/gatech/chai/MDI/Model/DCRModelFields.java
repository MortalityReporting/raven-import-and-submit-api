package edu.gatech.chai.MDI.Model;

import java.lang.reflect.Field;

import com.opencsv.bean.CsvBindByName;
import java.util.Objects;

public class DCRModelFields extends MDIAndEDRSModelFields{
	@CsvBindByName
	public String MESSAGE_REASON = "";
	@CsvBindByName
	public String SUBMITTOR_NAME = "";
	@CsvBindByName
	public String SUBMITTOR_PHONE = "";
	@CsvBindByName
	public String SUBMITTOR_FAX = "";
	@CsvBindByName
	public String SUBMITTOR_EMAIL = "";
	@CsvBindByName
	public String FUNERALHOME_NAME = "";
	@CsvBindByName
	public String FUNERALHOME_STREET = "";
	@CsvBindByName
	public String FUNERALHOME_CITY = "";
	@CsvBindByName
	public String FUNERALHOME_COUNTY = "";
	@CsvBindByName
	public String FUNERALHOME_STATE = "";
	@CsvBindByName
	public String FUNERALHOME_ZIP = "";
	@CsvBindByName
	public String FUNERALHOME_COUNTRY = "";
	@CsvBindByName
	public String FUNERALHOME_PHONE = "";
	@CsvBindByName
	public String FUNERALHOME_FAX = "";
	
	public DCRModelFields() {
		super();
	}

	public DCRModelFields(MDIAndEDRSModelFields parentObject){
		super();
		try{
		Class<MDIAndEDRSModelFields> clazz = MDIAndEDRSModelFields.class;
			if(clazz != null){
				Field[] fields = clazz.getDeclaredFields();
				for(Field field : fields){
					field.setAccessible(true);
					Object value = field.get(parentObject);
					field.set(this, value);
				}
			}
		}
		catch(IllegalAccessException e){
			e.printStackTrace();
		}
	}


	public DCRModelFields(String MESSAGE_REASON, String SUBMITTOR_NAME, String SUBMITTOR_PHONE, String SUBMITTOR_FAX, String SUBMITTOR_EMAIL, String FUNERALHOME_NAME, String FUNERALHOME_STREET, String FUNERALHOME_CITY, String FUNERALHOME_COUNTY, String FUNERALHOME_STATE, String FUNERALHOME_ZIP, String FUNERALHOME_COUNTRY, String FUNERALHOME_PHONE, String FUNERALHOME_FAX) {
		this.MESSAGE_REASON = MESSAGE_REASON;
		this.SUBMITTOR_NAME = SUBMITTOR_NAME;
		this.SUBMITTOR_PHONE = SUBMITTOR_PHONE;
		this.SUBMITTOR_FAX = SUBMITTOR_FAX;
		this.SUBMITTOR_EMAIL = SUBMITTOR_EMAIL;
		this.FUNERALHOME_NAME = FUNERALHOME_NAME;
		this.FUNERALHOME_STREET = FUNERALHOME_STREET;
		this.FUNERALHOME_CITY = FUNERALHOME_CITY;
		this.FUNERALHOME_COUNTY = FUNERALHOME_COUNTY;
		this.FUNERALHOME_STATE = FUNERALHOME_STATE;
		this.FUNERALHOME_ZIP = FUNERALHOME_ZIP;
		this.FUNERALHOME_COUNTRY = FUNERALHOME_COUNTRY;
		this.FUNERALHOME_PHONE = FUNERALHOME_PHONE;
		this.FUNERALHOME_FAX = FUNERALHOME_FAX;
	}

	public String getMESSAGE_REASON() {
		return this.MESSAGE_REASON;
	}

	public void setMESSAGE_REASON(String MESSAGE_REASON) {
		this.MESSAGE_REASON = MESSAGE_REASON;
	}

	public String getSUBMITTOR_NAME() {
		return this.SUBMITTOR_NAME;
	}

	public void setSUBMITTOR_NAME(String SUBMITTOR_NAME) {
		this.SUBMITTOR_NAME = SUBMITTOR_NAME;
	}

	public String getSUBMITTOR_PHONE() {
		return this.SUBMITTOR_PHONE;
	}

	public void setSUBMITTOR_PHONE(String SUBMITTOR_PHONE) {
		this.SUBMITTOR_PHONE = SUBMITTOR_PHONE;
	}

	public String getSUBMITTOR_FAX() {
		return this.SUBMITTOR_FAX;
	}

	public void setSUBMITTOR_FAX(String SUBMITTOR_FAX) {
		this.SUBMITTOR_FAX = SUBMITTOR_FAX;
	}

	public String getSUBMITTOR_EMAIL() {
		return this.SUBMITTOR_EMAIL;
	}

	public void setSUBMITTOR_EMAIL(String SUBMITTOR_EMAIL) {
		this.SUBMITTOR_EMAIL = SUBMITTOR_EMAIL;
	}

	public String getFUNERALHOME_NAME() {
		return this.FUNERALHOME_NAME;
	}

	public void setFUNERALHOME_NAME(String FUNERALHOME_NAME) {
		this.FUNERALHOME_NAME = FUNERALHOME_NAME;
	}

	public String getFUNERALHOME_STREET() {
		return this.FUNERALHOME_STREET;
	}

	public void setFUNERALHOME_STREET(String FUNERALHOME_STREET) {
		this.FUNERALHOME_STREET = FUNERALHOME_STREET;
	}

	public String getFUNERALHOME_CITY() {
		return this.FUNERALHOME_CITY;
	}

	public void setFUNERALHOME_CITY(String FUNERALHOME_CITY) {
		this.FUNERALHOME_CITY = FUNERALHOME_CITY;
	}

	public String getFUNERALHOME_COUNTY() {
		return this.FUNERALHOME_COUNTY;
	}

	public void setFUNERALHOME_COUNTY(String FUNERALHOME_COUNTY) {
		this.FUNERALHOME_COUNTY = FUNERALHOME_COUNTY;
	}

	public String getFUNERALHOME_STATE() {
		return this.FUNERALHOME_STATE;
	}

	public void setFUNERALHOME_STATE(String FUNERALHOME_STATE) {
		this.FUNERALHOME_STATE = FUNERALHOME_STATE;
	}

	public String getFUNERALHOME_ZIP() {
		return this.FUNERALHOME_ZIP;
	}

	public void setFUNERALHOME_ZIP(String FUNERALHOME_ZIP) {
		this.FUNERALHOME_ZIP = FUNERALHOME_ZIP;
	}

	public String getFUNERALHOME_COUNTRY() {
		return this.FUNERALHOME_COUNTRY;
	}

	public void setFUNERALHOME_COUNTRY(String FUNERALHOME_COUNTRY) {
		this.FUNERALHOME_COUNTRY = FUNERALHOME_COUNTRY;
	}

	public String getFUNERALHOME_PHONE() {
		return this.FUNERALHOME_PHONE;
	}

	public void setFUNERALHOME_PHONE(String FUNERALHOME_PHONE) {
		this.FUNERALHOME_PHONE = FUNERALHOME_PHONE;
	}

	public String getFUNERALHOME_FAX() {
		return this.FUNERALHOME_FAX;
	}

	public void setFUNERALHOME_FAX(String FUNERALHOME_FAX) {
		this.FUNERALHOME_FAX = FUNERALHOME_FAX;
	}

	public DCRModelFields MESSAGE_REASON(String MESSAGE_REASON) {
		setMESSAGE_REASON(MESSAGE_REASON);
		return this;
	}

	public DCRModelFields SUBMITTOR_NAME(String SUBMITTOR_NAME) {
		setSUBMITTOR_NAME(SUBMITTOR_NAME);
		return this;
	}

	public DCRModelFields SUBMITTOR_PHONE(String SUBMITTOR_PHONE) {
		setSUBMITTOR_PHONE(SUBMITTOR_PHONE);
		return this;
	}

	public DCRModelFields SUBMITTOR_FAX(String SUBMITTOR_FAX) {
		setSUBMITTOR_FAX(SUBMITTOR_FAX);
		return this;
	}

	public DCRModelFields SUBMITTOR_EMAIL(String SUBMITTOR_EMAIL) {
		setSUBMITTOR_EMAIL(SUBMITTOR_EMAIL);
		return this;
	}

	public DCRModelFields FUNERALHOME_NAME(String FUNERALHOME_NAME) {
		setFUNERALHOME_NAME(FUNERALHOME_NAME);
		return this;
	}

	public DCRModelFields FUNERALHOME_STREET(String FUNERALHOME_STREET) {
		setFUNERALHOME_STREET(FUNERALHOME_STREET);
		return this;
	}

	public DCRModelFields FUNERALHOME_CITY(String FUNERALHOME_CITY) {
		setFUNERALHOME_CITY(FUNERALHOME_CITY);
		return this;
	}

	public DCRModelFields FUNERALHOME_COUNTY(String FUNERALHOME_COUNTY) {
		setFUNERALHOME_COUNTY(FUNERALHOME_COUNTY);
		return this;
	}

	public DCRModelFields FUNERALHOME_STATE(String FUNERALHOME_STATE) {
		setFUNERALHOME_STATE(FUNERALHOME_STATE);
		return this;
	}

	public DCRModelFields FUNERALHOME_ZIP(String FUNERALHOME_ZIP) {
		setFUNERALHOME_ZIP(FUNERALHOME_ZIP);
		return this;
	}

	public DCRModelFields FUNERALHOME_COUNTRY(String FUNERALHOME_COUNTRY) {
		setFUNERALHOME_COUNTRY(FUNERALHOME_COUNTRY);
		return this;
	}

	public DCRModelFields FUNERALHOME_PHONE(String FUNERALHOME_PHONE) {
		setFUNERALHOME_PHONE(FUNERALHOME_PHONE);
		return this;
	}

	public DCRModelFields FUNERALHOME_FAX(String FUNERALHOME_FAX) {
		setFUNERALHOME_FAX(FUNERALHOME_FAX);
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof DCRModelFields)) {
			return false;
		}
		DCRModelFields dCRModelFields = (DCRModelFields) o;
		return Objects.equals(MESSAGE_REASON, dCRModelFields.MESSAGE_REASON) && Objects.equals(SUBMITTOR_NAME, dCRModelFields.SUBMITTOR_NAME) && Objects.equals(SUBMITTOR_PHONE, dCRModelFields.SUBMITTOR_PHONE) && Objects.equals(SUBMITTOR_FAX, dCRModelFields.SUBMITTOR_FAX) && Objects.equals(SUBMITTOR_EMAIL, dCRModelFields.SUBMITTOR_EMAIL) && Objects.equals(FUNERALHOME_NAME, dCRModelFields.FUNERALHOME_NAME) && Objects.equals(FUNERALHOME_STREET, dCRModelFields.FUNERALHOME_STREET) && Objects.equals(FUNERALHOME_CITY, dCRModelFields.FUNERALHOME_CITY) && Objects.equals(FUNERALHOME_COUNTY, dCRModelFields.FUNERALHOME_COUNTY) && Objects.equals(FUNERALHOME_STATE, dCRModelFields.FUNERALHOME_STATE) && Objects.equals(FUNERALHOME_ZIP, dCRModelFields.FUNERALHOME_ZIP) && Objects.equals(FUNERALHOME_COUNTRY, dCRModelFields.FUNERALHOME_COUNTRY) && Objects.equals(FUNERALHOME_PHONE, dCRModelFields.FUNERALHOME_PHONE) && Objects.equals(FUNERALHOME_FAX, dCRModelFields.FUNERALHOME_FAX);
	}

	@Override
	public int hashCode() {
		return Objects.hash(MESSAGE_REASON, SUBMITTOR_NAME, SUBMITTOR_PHONE, SUBMITTOR_FAX, SUBMITTOR_EMAIL, FUNERALHOME_NAME, FUNERALHOME_STREET, FUNERALHOME_CITY, FUNERALHOME_COUNTY, FUNERALHOME_STATE, FUNERALHOME_ZIP, FUNERALHOME_COUNTRY, FUNERALHOME_PHONE, FUNERALHOME_FAX);
	}

	@Override
	public String toString() {
		return "{" +
			" MESSAGE_REASON='" + getMESSAGE_REASON() + "'" +
			", SUBMITTOR_NAME='" + getSUBMITTOR_NAME() + "'" +
			", SUBMITTOR_PHONE='" + getSUBMITTOR_PHONE() + "'" +
			", SUBMITTOR_FAX='" + getSUBMITTOR_FAX() + "'" +
			", SUBMITTOR_EMAIL='" + getSUBMITTOR_EMAIL() + "'" +
			", FUNERALHOME_NAME='" + getFUNERALHOME_NAME() + "'" +
			", FUNERALHOME_STREET='" + getFUNERALHOME_STREET() + "'" +
			", FUNERALHOME_CITY='" + getFUNERALHOME_CITY() + "'" +
			", FUNERALHOME_COUNTY='" + getFUNERALHOME_COUNTY() + "'" +
			", FUNERALHOME_STATE='" + getFUNERALHOME_STATE() + "'" +
			", FUNERALHOME_ZIP='" + getFUNERALHOME_ZIP() + "'" +
			", FUNERALHOME_COUNTRY='" + getFUNERALHOME_COUNTRY() + "'" +
			", FUNERALHOME_PHONE='" + getFUNERALHOME_PHONE() + "'" +
			", FUNERALHOME_FAX='" + getFUNERALHOME_FAX() + "'" +
			"}";
	}

	
}