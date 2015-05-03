/*  Copyright 2008 Andrew Ochsner and Fabrizio Cannizzo
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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.junit.Before;
import org.junit.Test;

import smartrics.rest.fitnesse.fixture.RunnerVariablesProvider;

public class JSONBodyTypeAdapterTest {
    private JSONBodyTypeAdapter adapter;
    private static final String json0 = "{\"a\": { b: [\"12\",\"23\"], \"c\": \"XY\" } }";
    private static final String json1 = "{\"a\": \" 1&\"}";
    private static final String json2 = "{\"a\": 1, \"b\": 2 }";
    private static final List<String> xPaths = Arrays.asList("/a", "//b");
    private static final String xPathsAsString = "/a<br/>//b";
    private final RunnerVariablesProvider variablesProvider = new RunnerVariablesProvider() {
		@Override
		public Variables createRunnerVariables() {
			return null;
		}        	
    };

    @Before
    public void setUp() {
        adapter = new JSONBodyTypeAdapter(variablesProvider);
        adapter.setContext(new HashMap<String, String>());
    }

	@Test
	public void shouldIdentifyContentObjectsWithNoBodyAsBeingEqual() {
		assertTrue(adapter.equals("no-body", "no-body"));
		assertTrue(adapter.equals("no-body", ""));
		assertTrue(adapter.equals("no-body", null));
		assertFalse(adapter.equals("/a", null));
		assertFalse(adapter.equals("no-body", json0));
	}

    @Test
    public void shouldIdentifyAsEqualsIfExpectedObjectIsAJavascriptExpressionInActual() {
        assertTrue("not found simple expression", adapter.equals("jsonbody.a.b[0]==12", json0));
        assertTrue("not found simple expression", adapter.equals("jsonbody.a.b[1]==\"23\"", json0));
        assertTrue("not found two expressions", adapter.equals("jsonbody.a.b[0]=='12' && jsonbody.a.c==\"XY\"", json0));
        assertTrue("not found two expressions as list", adapter.equals(Arrays.asList("jsonbody.a.b[0]==\"12\"", "jsonbody.a.c==\"XY\""), json0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfExpectationsAreValidXPathExpression() {
        adapter.equals(Arrays.asList("/a/b[text()='zzz']", "/a/d[text()='next']", "/a/c[text()='XY']"), json0);
    }

    @Test
    public void shouldCorrectlyEvaluateExpressionsWithComparisons() {
        assertTrue("not found simple expression", adapter.equals("jsonbody.a==1\njsonbody.a<jsonbody.b", json2));
        assertEquals(0, adapter.getErrors().size());
    }

    @Test
    public void shouldStoreNotFoundMessageForEveryJSonExpressionNotFoundForEqualityCheck() {
        assertFalse(adapter.equals(Arrays.asList("jsonbody.a.b=='zzz'", "jsonbody.a.d=='next'", "jsonbody.a.c=='XY'"), json0));
        assertEquals(2, adapter.getErrors().size());
        assertEquals("not found: 'jsonbody.a.b=='zzz''", adapter.getErrors().get(0));
        assertEquals("not found: 'jsonbody.a.d=='next''", adapter.getErrors().get(1));
    }

	@Test
	public void shouldReturnItsStringRepresentationAsPrintableHTML() {
		assertEquals(json1, adapter.toString(json1));
	}

	@Test
	public void shouldReturnItsStringRepresentationAsPrintableHTMLEvenWhenEmpty() {
		assertEquals("no-body", adapter.toString(""));
	}

	@Test
    public void shoudlParseStringWithXPathInHtmlIntoAProperList() throws Exception {
        // just make sure we have the right fata
        assertTrue(xPathsAsString.indexOf("<br/>") > -1);
        assertEquals(xPaths, adapter.parse(xPathsAsString));
	}

	@Test
    public void shoudlParseNoJsonIntoAnEmptyList() throws Exception {
        // just make sure we have the right fata
        assertEquals(new Vector<String>(), adapter.parse(null));
        assertEquals(new Vector<String>(), adapter.parse("no-body"));
        assertEquals(new Vector<String>(), adapter.parse(""));
	}

    @Test
    public void shouldConvertJSONToXML() {
        assertThat(adapter.toXmlString(json1), is(equalTo("<a> 1&amp;</a>")));
    }
}
