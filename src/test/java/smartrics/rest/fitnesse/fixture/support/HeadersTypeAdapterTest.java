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
import java.util.Collection;

import org.junit.Test;

import smartrics.rest.client.RestData.Header;

public class HeadersTypeAdapterTest {

	private final HeadersTypeAdapter adapter = new HeadersTypeAdapter();
    private final String ls = "\n";
	private final Header h0 = new Header("n0", "v0");
	private final Header h1 = new Header("n1", "v1");
	private final Header h = new Header("n", "v");
	private final Header h2 = new Header("n1", "v1");
	private final Header h3 = new Header("n3", "v3");
    private final Header h4 = new Header("n", "http://something:port/blah:blah");
    private final Collection<Header> expected = Arrays.asList(h, h2, h3);
    private final Collection<Header> expectedWithColonsInValues = Arrays.asList(h4, h2);
    private final Header h5actual = new Header("content-type", "application/my-xml;charset=UTF-8");
    private final Header h5expected = new Header("content-type", "application/my-xml;.+");
    private final Collection<Header> expectedWithSemiColonsInValues = Arrays.asList(h5expected);
    private final Collection<Header> actualWithSemiColonsInValues = Arrays.asList(h5actual);
	private final Collection<Header> actualSubset = Arrays.asList(h, h2);
	private final Collection<Header> actualSame = Arrays.asList(h, h2, h3);
	private final Collection<Header> actualSuperset = Arrays.asList(h0, h1, h, h2, h3);
	private final String headersAsHtmlString = " n : v <br/> n1: v1 <br  /> n3 :v3 ";
	private final String headersAsOutputString = "n : v" + ls + "n1 : v1" + ls + "n3 : v3";
	private final String headersAsHtmlStringWithColonsInValue = " n : http://something:port/blah:blah <br/> n1: v1 ";

	@Test
	public void shouldIdentifyContentObjectsWithNoBodyAsBeingEqual(){
		assertFalse(adapter.equals(null, null));
	}

	@Test
	public void actualHeadersMustBeASupersetOfExpectedHeaders(){
		assertTrue(adapter.equals(expected, actualSame));
		assertTrue(adapter.equals(expected, actualSuperset));
	}

	@Test
	public void actualHeadersShouldntBeASupersetOfExpectedHeaders(){
		assertFalse(adapter.equals(expected, actualSubset));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldParseHeadersInHtmlFormatAsProperListOfHeaders(){
		try{
			Collection<Header> result = (Collection<Header>)adapter.parse(headersAsHtmlString);
			assertEquals(expected, result);
		} catch(Exception e){
			fail();
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldParseHeadersInHtmlFormatWithMultipleColonsAsProperListOfHeaders() {
		try {
			Collection<Header> result = (Collection<Header>) adapter
					.parse(headersAsHtmlStringWithColonsInValue);
			assertEquals(expectedWithColonsInValues, result);
		} catch (Exception e) {
			fail();
		}
	}
	@Test(expected=IllegalArgumentException.class)
	public void shouldNotifyClientOfBadHeaderSyntax(){
		try {
			adapter.parse("a:b<br /> c;d");
		} catch (Exception e) {
			if(e instanceof RuntimeException){
				throw (RuntimeException) e;
			}
			fail("should have thrown the exception");
		}
	}

    @Test
    public void shouldTransformACollectionOfHeadersIntoAHtmlString() {
        String result = adapter.toString(expected);
        assertEquals(headersAsOutputString, result);
    }

    @Test
    public void shouldMatchHeadersWithRegex() {
        boolean res = adapter.equals(expectedWithSemiColonsInValues, actualWithSemiColonsInValues);
        assertTrue("regex didn't match: " + h5expected, res);
    }

}
