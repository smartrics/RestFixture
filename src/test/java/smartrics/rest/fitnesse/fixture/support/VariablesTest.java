package smartrics.rest.fitnesse.fixture.support;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;


public class VariablesTest {

	@Before
	public void clearVariables(){
		new Variables().clearAll();
	}

	@Test
	public void variablesShoudBeStatic(){
		Variables v1 = new Variables();
		Variables v2 = new Variables();
		assertNull(v1.get("a"));
		assertNull(v2.get("a"));
		v1.put("a", "val");
		assertEquals("val", v1.get("a"));
		assertEquals("val", v2.get("a"));
	}

	@Test
	public void variablesAreSubstitutedWithCurrentValueWhenLabelsAreIdentifiedWithinPercentSymbol(){
		Variables v1 = new Variables();
		v1.put("ID", "100");
		String newText = v1.substitute("the current value of ID is %ID%.");
		assertEquals("the current value of ID is 100.", newText);
	}

	@Test
	public void variablesAreSubstitutedMultipleTimes(){
		Variables v1 = new Variables();
		v1.put("ID", "100");
		String newText = v1.substitute("first %ID%. second %ID%.");
		assertEquals("first 100. second 100.", newText);
	}

	@Test
	public void nonExistentVariablesAreNotReplaced(){
		Variables v1 = new Variables();
		v1.put("ID", "100");
		String newText = v1.substitute("non existent %XYZ%. it exists %ID%");
		assertEquals("non existent %XYZ%. it exists 100", newText);
	}

}
