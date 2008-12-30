package smartrics.rest.test.fitnesse.fixture;

import fit.ActionFixture;
import fit.Fixture;

public class LetTestActionFixture extends ActionFixture {
	private String symbolName;

	public void symbolName(String name) {
		this.symbolName = name;
	}

	public String symbolValue() {
		return (String) Fixture.getSymbol(symbolName);
	}

	public void symbolValue(String val) {
		Fixture.setSymbol(symbolName, val);
	}
}
