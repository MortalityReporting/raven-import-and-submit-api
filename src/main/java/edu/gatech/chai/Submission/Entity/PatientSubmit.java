package edu.gatech.chai.Submission.Entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "PatientSubmit")
public class PatientSubmit implements Serializable{
	
	@EmbeddedId
	private PatientIdentifier patientIdentifier;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	//@JoinColumn(name="source_status_id")
	private List<SourceStatus> sources;
	
	public PatientSubmit() {
		super();
		patientIdentifier = new PatientIdentifier();
		sources = new ArrayList<SourceStatus>();
	}
	
	public PatientSubmit(String system, String code) {
		super();
		patientIdentifier = new PatientIdentifier(system,code);
		sources = new ArrayList<SourceStatus>();
	}

	public PatientIdentifier getPatientIdentifier() {
		return patientIdentifier;
	}

	public void setPatientIdentifier(PatientIdentifier patientIdentifier) {
		this.patientIdentifier = patientIdentifier;
	}

	public List<SourceStatus> getSources() {
		return sources;
	}

	public void setSources(List<SourceStatus> sources) {
		this.sources = sources;
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((patientIdentifier == null) ? 0 : patientIdentifier.hashCode());
		result = prime * result + ((sources == null) ? 0 : sources.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PatientSubmit other = (PatientSubmit) obj;
		if (patientIdentifier == null) {
			if (other.patientIdentifier != null)
				return false;
		} else if (!patientIdentifier.equals(other.patientIdentifier))
			return false;
		if (sources == null) {
			if (other.sources != null)
				return false;
		} else if (!sources.equals(other.sources))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PatientSubmit [patientIdentifier=" + patientIdentifier + ", sources=" + sources + "]";
	}

}
