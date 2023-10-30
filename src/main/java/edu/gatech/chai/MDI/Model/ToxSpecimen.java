package edu.gatech.chai.MDI.Model;

import java.util.Objects;

import com.opencsv.bean.CsvBindByName;

public class ToxSpecimen extends BaseModelFields{
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
	public String COLLECTED_DATETIME = "";
	@CsvBindByName
	public String RECEIPT_DATETIME = "";
	@CsvBindByName
	public String CONDITION = "";

	public ToxSpecimen() {
		super();
	}

	public ToxSpecimen(String NAME, String IDENTIFIER, String BODYSITE, String AMOUNT, String CONTAINER, String COLLECTED_DATETIME, String RECEIPT_DATETIME, String CONDITION) {
		super();
		this.NAME = NAME;
		this.IDENTIFIER = IDENTIFIER;
		this.BODYSITE = BODYSITE;
		this.AMOUNT = AMOUNT;
		this.CONTAINER = CONTAINER;
		this.COLLECTED_DATETIME = COLLECTED_DATETIME;
		this.RECEIPT_DATETIME = RECEIPT_DATETIME;
		this.CONDITION = CONDITION;
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

	public String getCOLLECTED_DATETIME() {
		return this.COLLECTED_DATETIME;
	}

	public void setCOLLECTED_DATETIME(String COLLECTED_DATETIME) {
		this.COLLECTED_DATETIME = COLLECTED_DATETIME;
	}

	public String getRECEIPT_DATETIME() {
		return this.RECEIPT_DATETIME;
	}

	public void setRECEIPT_DATETIME(String RECEIPT_DATETIME) {
		this.RECEIPT_DATETIME = RECEIPT_DATETIME;
	}

	public String getCONDITION() {
		return this.CONDITION;
	}

	public void setCONDITION(String CONDITION) {
		this.CONDITION = CONDITION;
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

	public ToxSpecimen COLLECTED_DATETIME(String COLLECTED_DATETIME) {
		setCOLLECTED_DATETIME(COLLECTED_DATETIME);
		return this;
	}

	public ToxSpecimen RECEIPT_DATETIME(String RECEIPT_DATETIME) {
		setRECEIPT_DATETIME(RECEIPT_DATETIME);
		return this;
	}

	public ToxSpecimen CONDITION(String CONDITION) {
		setCONDITION(CONDITION);
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
		return Objects.equals(NAME, toxSpecimen.NAME) && Objects.equals(IDENTIFIER, toxSpecimen.IDENTIFIER) && Objects.equals(BODYSITE, toxSpecimen.BODYSITE) && Objects.equals(AMOUNT, toxSpecimen.AMOUNT) && Objects.equals(CONTAINER, toxSpecimen.CONTAINER) && Objects.equals(COLLECTED_DATETIME, toxSpecimen.COLLECTED_DATETIME) && Objects.equals(RECEIPT_DATETIME, toxSpecimen.RECEIPT_DATETIME) && Objects.equals(CONDITION, toxSpecimen.CONDITION);
	}

	@Override
	public int hashCode() {
		return Objects.hash(NAME, IDENTIFIER, BODYSITE, AMOUNT, CONTAINER, COLLECTED_DATETIME, RECEIPT_DATETIME, CONDITION);
	}

	@Override
	public String toString() {
		return "{" +
			" NAME='" + getNAME() + "'" +
			", IDENTIFIER='" + getIDENTIFIER() + "'" +
			", BODYSITE='" + getBODYSITE() + "'" +
			", AMOUNT='" + getAMOUNT() + "'" +
			", CONTAINER='" + getCONTAINER() + "'" +
			", COLLECTED_DATETIME='" + getCOLLECTED_DATETIME() + "'" +
			", RECEIPT_DATETIME='" + getRECEIPT_DATETIME() + "'" +
			", CONDITION='" + getCONDITION() + "'" +
			"}";
	}
}