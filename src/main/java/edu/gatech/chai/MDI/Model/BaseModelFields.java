package edu.gatech.chai.MDI.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseModelFields {

    public Map<String, List<String>> errorSet;

    public BaseModelFields() {
        errorSet = new HashMap<String, List<String> >();
    }

    public Map<String, List<String>> getErrorSet() {
		return this.errorSet;
	}

	public void setErrorSet(Map<String, List<String>> errorSet) {
		this.errorSet = errorSet;
	}

	public List<String> getErrorListForName(String name) {
		if (!this.errorSet.containsKey(name)) {
			this.errorSet.put(name, new ArrayList<String>());
		}
		return this.errorSet.get(name);
	}

	protected void checkNullSetter(Class fieldName, String value) {
		if (value == null) {
			this.getErrorListForName(fieldName.getName()).add("No value detected");
			return;
		}
		if (value.isBlank()) {
			this.getErrorListForName(fieldName.getName()).add("Empty value detected, check for whitespace in cell");
			return;
		}
		return;
	}
}
