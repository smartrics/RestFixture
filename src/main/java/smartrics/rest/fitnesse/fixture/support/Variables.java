package smartrics.rest.fitnesse.fixture.support;

import java.util.HashMap;
import java.util.Map;

public class Variables {
	private static Map<String, String> locals = new HashMap<String, String>();

	public void put(String label, String val) {
		locals.put(label, val);
	}

	public String get(String label) {
		return locals.get(label);
	}

	public void clearAll(){
		locals.clear();
	}

	public String substitute(String text) {
		String textUpdatedWithVariableSubstitution = text;
		for (Map.Entry<String, String> variableEntry : locals.entrySet()) {
			String qualifiedVariableName = "%" + variableEntry.getKey() + "%";
			textUpdatedWithVariableSubstitution =
				textUpdatedWithVariableSubstitution.replaceAll(qualifiedVariableName, variableEntry.getValue());
		}
		return textUpdatedWithVariableSubstitution;
	}

}
