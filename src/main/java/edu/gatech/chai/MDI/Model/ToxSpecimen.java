package edu.gatech.chai.MDI.Model;

import java.util.Objects;

import com.opencsv.bean.CsvBindByName;

public class ToxSpecimen {
	@CsvBindByName
	public String NAME = "";
	@CsvBindByName
	public String IDENTIFIER = "";
	@CsvBindByName
	public String BODYSITE = "";
	@CsvBindByName
	public String AMOUNT = "";
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

	public ToxSpecimen() {
	}

	public ToxSpecimen(String NAME, String IDENTIFIER, String BODYSITE, String AMOUNT, String CONTAINER, String COLLECTED_DATE, String COLLECTED_TIME, String RECORD_DATE, String RECORD_TIME) {
		this.NAME = NAME;
		this.IDENTIFIER = IDENTIFIER;
		this.BODYSITE = BODYSITE;
		this.AMOUNT = AMOUNT;
		this.CONTAINER = CONTAINER;
		this.COLLECTED_DATE = COLLECTED_DATE;
		this.COLLECTED_TIME = COLLECTED_TIME;
		this.RECORD_DATE = RECORD_DATE;
		this.RECORD_TIME = RECORD_TIME;
	}

	public String getNAME() {
		return this.NAME;
	}

	public void setNAME(String NAME) {
		this.NAME = NAME;
	}

	public String getIDENTIFIER() {
		return this.IDENTIFIER;
	}

	public void setIDENTIFIER(String IDENTIFIER) {
		this.IDENTIFIER = IDENTIFIER;
	}

	public String getBODYSITE() {
		return this.BODYSITE;
	}

	public void setBODYSITE(String BODYSITE) {
		this.BODYSITE = BODYSITE;
	}

	public String getAMOUNT() {
		return this.AMOUNT;
	}

	public void setAMOUNT(String AMOUNT) {
		this.AMOUNT = AMOUNT;
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

	public ToxSpecimen NAME(String NAME) {
		setNAME(NAME);
		return this;
	}

	public ToxSpecimen IDENTIFIER(String IDENTIFIER) {
		setIDENTIFIER(IDENTIFIER);
		return this;
	}

	public ToxSpecimen BODYSITE(String BODYSITE) {
		setBODYSITE(BODYSITE);
		return this;
	}

	public ToxSpecimen AMOUNT(String AMOUNT) {
		setAMOUNT(AMOUNT);
		return this;
	}

	public ToxSpecimen CONTAINER(String CONTAINER) {
		setCONTAINER(CONTAINER);
		return this;
	}

	public ToxSpecimen COLLECTED_DATE(String COLLECTED_DATE) {
		setCOLLECTED_DATE(COLLECTED_DATE);
		return this;
	}

	public ToxSpecimen COLLECTED_TIME(String COLLECTED_TIME) {
		setCOLLECTED_TIME(COLLECTED_TIME);
		return this;
	}

	public ToxSpecimen RECORD_DATE(String RECORD_DATE) {
		setRECORD_DATE(RECORD_DATE);
		return this;
	}

	public ToxSpecimen RECORD_TIME(String RECORD_TIME) {
		setRECORD_TIME(RECORD_TIME);
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof ToxSpecimen)) {
			return false;
		}
		ToxSpecimen toxSpecimen = (ToxSpecimen) o;
		return Objects.equals(NAME, toxSpecimen.NAME) && Objects.equals(IDENTIFIER, toxSpecimen.IDENTIFIER) && Objects.equals(BODYSITE, toxSpecimen.BODYSITE) && Objects.equals(AMOUNT, toxSpecimen.AMOUNT) && Objects.equals(CONTAINER, toxSpecimen.CONTAINER) && Objects.equals(COLLECTED_DATE, toxSpecimen.COLLECTED_DATE) && Objects.equals(COLLECTED_TIME, toxSpecimen.COLLECTED_TIME) && Objects.equals(RECORD_DATE, toxSpecimen.RECORD_DATE) && Objects.equals(RECORD_TIME, toxSpecimen.RECORD_TIME);
	}

	@Override
	public int hashCode() {
		return Objects.hash(NAME, IDENTIFIER, BODYSITE, AMOUNT, CONTAINER, COLLECTED_DATE, COLLECTED_TIME, RECORD_DATE, RECORD_TIME);
	}

	@Override
	public String toString() {
		return "{" +
			" NAME='" + getNAME() + "'" +
			", IDENTIFIER='" + getIDENTIFIER() + "'" +
			", BODYSITE='" + getBODYSITE() + "'" +
			", AMOUNT='" + getAMOUNT() + "'" +
			", CONTAINER='" + getCONTAINER() + "'" +
			", COLLECTED_DATE='" + getCOLLECTED_DATE() + "'" +
			", COLLECTED_TIME='" + getCOLLECTED_TIME() + "'" +
			", RECORD_DATE='" + getRECORD_DATE() + "'" +
			", RECORD_TIME='" + getRECORD_TIME() + "'" +
			"}";
	}
	
	
}