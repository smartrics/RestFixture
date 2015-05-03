/*  Copyright 2008 Fabrizio Cannizzo
 *
 *  This file is part of RestFixture.
 *
 *  RestFixture (http://code.google.com/p/rest-fixture/) is free software:
 *  you can redistribute it and/or modify it under the terms of the
 *  GNU Lesser General Public License as published by the Free Software Foundation,
 *  either version 3 of the License, or (at your option) any later version.
 *
 *  RestFixture is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with RestFixture.  If not, see <http://www.gnu.org/licenses/>.
 *
 *  If you want to contact the author please leave a comment here
 *  http://smartrics.blogspot.com/2008/08/get-fitnesse-with-some-rest.html
 */
package smartrics.rest.fitnesse.fixture.support;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;


public class VariablesTest {

	@Before
	public void clearVariables(){
		new FitVariables().clearAll();
	}

	@Test
	public void variablesShoudBeStatic(){
		Variables v1 = new FitVariables();
		Variables v2 = new FitVariables();
		assertNull(v1.get("a"));
		assertNull(v2.get("a"));
		v1.put("a", "val");
		assertEquals("val", v1.get("a"));
		assertEquals("val", v2.get("a"));
	}

	@Test
	public void variablesAreSubstitutedWithCurrentValueWhenLabelsAreIdentifiedWithinPercentSymbol(){
		Variables v1 = new FitVariables();
		v1.put("ID", "100");
		String newText = v1.substitute("the current value of ID is %ID%.");
		assertEquals("the current value of ID is 100.", newText);
	}

	@Test
	public void variablesAreSubstitutedMultipleTimes(){
		Variables v1 = new FitVariables();
		v1.put("ID", "100");
		String newText = v1.substitute("first %ID%. second %ID%.");
		assertEquals("first 100. second 100.", newText);
	}

    @Test
    public void nonExistentVariablesAreNotReplaced() {
        Variables v1 = new FitVariables();
        v1.put("ID", "100");
        String newText = v1.substitute("non existent %XYZ%. it exists %ID%");
        assertEquals("non existent %XYZ%. it exists 100", newText);
    }

    @Test
    public void variablesContainingNullAreSubWithStringNullByDefault() {
    	Config.getConfig().clear();
        Variables v1 = new FitVariables();
        v1.put("ID", null);
        String newText = v1.substitute("null is '%ID%'");
        assertEquals("null is 'null'", newText);
    }

    @Test
    public void variablesContainingNullAreSubWithValueSuppliedViaConfig() {
        Config c = Config.getConfig();
        c.add("restfixture.null.value.representation", "this-is-null-value");
        Variables v1 = new FitVariables(c);
        v1.put("ID", null);
        String newText = v1.substitute("null is '%ID%'");
        assertEquals("null is 'this-is-null-value'", newText);
    }

    @Test
    public void variablesContainingNullAreSubWithEmptyValueSuppliedViaConfig() {
        Config c = Config.getConfig();
        c.add("restfixture.null.value.representation", "");
        Variables v1 = new FitVariables(c);
        v1.put("ID", null);
        String newText = v1.substitute("null is '%ID%'");
        assertEquals("null is ''", newText);
    }

}
