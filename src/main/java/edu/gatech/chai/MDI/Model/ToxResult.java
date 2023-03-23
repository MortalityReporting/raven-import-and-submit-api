package edu.gatech.chai.MDI.Model;

import java.util.Objects;

import com.opencsv.bean.CsvBindByName;

public class ToxResult {
	@CsvBindByName
	public String ANALYSIS = "";
	@CsvBindByName
	public String SPECIMEN = "";
	@CsvBindByName
	public String METHOD = "";
	@CsvBindByName
	public String VALUE = "";
	@CsvBindByName
	public String CONTAINER = "";
	@CsvBindByName
	public String COLLECTED_DATE = "";
	@CsvBindByName
	public String COLLECTED_TIME = "";
	@CsvBindByName
	public String RECORD_DATE = "";
	@CsvBindByName
	public String RECORD_TIME = "";
	

	public ToxResult() {
	}

	public ToxResult(String ANALYSIS, String SPECIMEN, String METHOD, String VALUE, String CONTAINER, String COLLECTED_DATE, String COLLECTED_TIME, String RECORD_DATE, String RECORD_TIME) {
		this.ANALYSIS = ANALYSIS;
		this.SPECIMEN = SPECIMEN;
		this.METHOD = METHOD;
		this.VALUE = VALUE;
		this.CONTAINER = CONTAINER;
		this.COLLECTED_DATE = COLLECTED_DATE;
		this.COLLECTED_TIME = COLLECTED_TIME;
		this.RECORD_DATE = RECORD_DATE;
		this.RECORD_TIME = RECORD_TIME;
	}

	public String getANALYSIS() {
		return this.ANALYSIS;
	}

	public void setANALYSIS(String ANALYSIS) {
		this.ANALYSIS = ANALYSIS;
	}

	public String getSPECIMEN() {
		return this.SPECIMEN;
	}

	public void setSPECIMEN(String SPECIMEN) {
		this.SPECIMEN = SPECIMEN;
	}

	public String getMETHOD() {
		return this.METHOD;
	}

	public void setMETHOD(String METHOD) {
		this.METHOD = METHOD;
	}

	public String getVALUE() {
		return this.VALUE;
	}

	public void setVALUE(String VALUE) {
		this.VALUE = VALUE;
	}

	public String getCONTAINER() {
		return this.CONTAINER;
	}

	public void setCONTAINER(String CONTAINER) {
		this.CONTAINER = CONTAINER;
	}

	public String getCOLLECTED_DATE() {
		return this.COLLECTED_DATE;
	}

	public void setCOLLECTED_DATE(String COLLECTED_DATE) {
		this.COLLECTED_DATE = COLLECTED_DATE;
	}

	public String getCOLLECTED_TIME() {
		return this.COLLECTED_TIME;
	}

	public void setCOLLECTED_TIME(String COLLECTED_TIME) {
		this.COLLECTED_TIME = COLLECTED_TIME;
	}

	public String getRECORD_DATE() {
		return this.RECORD_DATE;
	}

	public void setRECORD_DATE(String RECORD_DATE) {
		this.RECORD_DATE = RECORD_DATE;
	}

	public String getRECORD_TIME() {
		return this.RECORD_TIME;
	}

	public void setRECORD_TIME(String RECORD_TIME) {
		this.RECORD_TIME = RECORD_TIME;
	}

	public ToxResult ANALYSIS(String ANALYSIS) {
		setANALYSIS(ANALYSIS);
		return this;
	}

	public ToxResult SPECIMEN(String SPECIMEN) {
		setSPECIMEN(SPECIMEN);
		return this;
	}

	public ToxResult METHOD(String METHOD) {
		setMETHOD(METHOD);
		return this;
	}

	public ToxResult VALUE(String VALUE) {
		setVALUE(VALUE);
		return this;
	}

	public ToxResult CONTAINER(String CONTAINER) {
		setCONTAINER(CONTAINER);
		return this;
	}

	public ToxResult COLLECTED_DATE(String COLLECTED_DATE) {
		setCOLLECTED_DATE(COLLECTED_DATE);
		return this;
	}

	public ToxResult COLLECTED_TIME(String COLLECTED_TIME) {
		setCOLLECTED_TIME(COLLECTED_TIME);
		return this;
	}

	public ToxResult RECORD_DATE(String RECORD_DATE) {
		setRECORD_DATE(RECORD_DATE);
		return this;
	}

	public ToxResult RECORD_TIME(String RECORD_TIME) {
		setRECORD_TIME(RECORD_TIME);
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof ToxResult)) {
			return false;
		}
		ToxResult toxResult = (ToxResult) o;
		return Objects.equals(ANALYSIS, toxResult.ANALYSIS) && Objects.equals(SPECIMEN, toxResult.SPECIMEN) && Objects.equals(METHOD, toxResult.METHOD) && Objects.equals(VALUE, toxResult.VALUE) && Objects.equals(CONTAINER, toxResult.CONTAINER) && Objects.equals(COLLECTED_DATE, toxResult.COLLECTED_DATE) && Objects.equals(COLLECTED_TIME, toxResult.COLLECTED_TIME) && Objects.equals(RECORD_DATE, toxResult.RECORD_DATE) && Objects.equals(RECORD_TIME, toxResult.RECORD_TIME);
	}

	@Override
	public int hashCode() {
		return Objects.hash(ANALYSIS, SPECIMEN, METHOD, VALUE, CONTAINER, COLLECTED_DATE, COLLECTED_TIME, RECORD_DATE, RECORD_TIME);
	}

	@Override
	public String toString() {
		return "{" +
			" ANALYSIS='" + getANALYSIS() + "'" +
			", SPECIMEN='" + getSPECIMEN() + "'" +
			", METHOD='" + getMETHOD() + "'" +
			", VALUE='" + getVALUE() + "'" +
			", CONTAINER='" + getCONTAINER() + "'" +
			", COLLECTED_DATE='" + getCOLLECTED_DATE() + "'" +
			", COLLECTED_TIME='" + getCOLLECTED_TIME() + "'" +
			", RECORD_DATE='" + getRECORD_DATE() + "'" +
			", RECORD_TIME='" + getRECORD_TIME() + "'" +
			"}";
	}
	
	
}