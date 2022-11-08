package edu.gatech.chai.Submission.Entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class PatientIdentifier implements Serializable{
	@Column(name = "system")
	private String system;
	@Column(name = "code")
	private String code;
	
	public PatientIdentifier() {
		this.system = "";
		this.code = "";
	}
	public PatientIdentifier(String system, String code) {
		super();
		this.system = system;
		this.code = code;
	}
	
	public String getSystem() {
		return system;
	}
	public void setSystem(String system) {
		this.system = system;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((system == null) ? 0 : system.hashCode());
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
		PatientIdentifier other = (PatientIdentifier) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (system == null) {
			if (other.system != null)
				return false;
		} else if (!system.equals(other.system))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PatientIdentifier [system=" + system + ", code=" + code + "]";
	}
	
}
