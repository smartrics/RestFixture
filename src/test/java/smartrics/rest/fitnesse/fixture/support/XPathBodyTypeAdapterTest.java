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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.junit.Test;

public class XPathBodyTypeAdapterTest {

    private final BodyTypeAdapter adapter = new XPathBodyTypeAdapter();
    private final String xml0 = "<a><b>12</b><b>23</b><c>XY</c></a>";
    private final String html1 = "&lt;a&gt;&nbsp;1&amp;&lt;/a&gt;";
    private final String xml1 = "<a> 1&</a>";
    private final List<String> xPaths = Arrays.asList("/a", "//b");
    private final String xPathsAsString = "/a<br/>//b";

    @Test
    public void shouldIdentifyContentObjectsWithNoBodyAsBeingEqual() {
	assertTrue(adapter.equals("no-body", "no-body"));
	assertTrue(adapter.equals("no-body", ""));
	assertTrue(adapter.equals("no-body", null));
	assertFalse(adapter.equals("/a", null));
	assertFalse(adapter.equals("no-body", xml0));
    }

    @Test
    public void shouldIdentifyAsEqualsIfExpectedObjectIsAListOfXPathsAvailableInActual() {
	assertTrue(adapter.equals(Arrays.asList("/a/b[text()='12']"), xml0));
	assertTrue(adapter.equals(Arrays.asList("/a/b[text()='12']", "/a/c[text()='XY']"), xml0));
    }

    @Test
    public void shouldStoreNotFoundMessageForEveryExpressionNotFoundForEqualityCheck() {
	assertFalse(adapter.equals(Arrays.asList("/a/b[text()='zzz']", "/a/d[text()='next']", "/a/c[text()='XY']"),
		xml0));
	assertEquals(2, adapter.getErrors().size());
	assertEquals("not found: '/a/b[text()='zzz']'", adapter.getErrors().get(0));
	assertEquals("not found: '/a/d[text()='next']'", adapter.getErrors().get(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailEqualityCheckIfAnyExpressionIsInvalid() {
	adapter.equals(Arrays.asList("invalid xpath", "/a/c[text()='XY']"), xml0);
    }

    @Test
    public void shouldReturnItsStringRepresentationAsPrintableHTML() {
	assertEquals(html1, adapter.toString(xml1));
    }

    @Test
    public void shouldReturnItsStringRepresentationAsPrintableHTMLEvenWhenEmpty() {
	assertEquals("no-body", adapter.toString(""));
    }

    @Test
    public void shoudlParseStringWithXPathInHtmlIntoAProperList() {
	try {
	    // just make sure we have the right fata
	    assertTrue(xPathsAsString.indexOf("<br/>") > -1);
	    assertEquals(xPaths, adapter.parse(xPathsAsString));
	} catch (Exception e) {
	    fail("should have not raised an exception");
	}
    }

    @Test
    public void shoudlParseNoXPathsIntoAnEmptyList() {
	try {
	    // just make sure we have the right fata
	    assertEquals(new Vector<String>(), adapter.parse(null));
	    assertEquals(new Vector<String>(), adapter.parse(""));
	    assertEquals(new Vector<String>(), adapter.parse("no-body"));
	} catch (Exception e) {
	    fail("should have not raised an exception");
	}
    }

}
