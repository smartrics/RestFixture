/*  Copyright 2008 Andrew Ochsner
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

import org.junit.Test;

public class JSONBodyTypeAdapterTest {
	private final JSONBodyTypeAdapter adapter = new JSONBodyTypeAdapter();
	private final String json0 = "{ \"entry\": { \"id\":\"ribbit.com:123\", \"login\":\"foo@bar.com\", \"active\":0 } }";
	private final String json1 = "{ \"id\":\"app1\", \"name\":\"something\" }";
	private final String html1 = "{&nbsp;\"id\":\"app1\",&nbsp;\"name\":\"something\"&nbsp;}";

	@Test
	public void shouldIdentifyContentObjectsWithNoBodyAsBeingEqual() {
		assertTrue(adapter.equals("no-body", "no-body"));
		assertTrue(adapter.equals("no-body", ""));
		assertTrue(adapter.equals("no-body", null));
		assertFalse(adapter.equals("{ \"id\":\"test\" }", null));
		assertFalse(adapter.equals("no-body", json0));
	}

	@Test
	public void shouldIdentifyAsEqualsIfExpectedObjectIsJSONInActual() {
		assertTrue(adapter.equals(json0, json0));
	}

	@Test
	public void shouldIdentifyAsEqualsIfExpectedObjectIsWhitespaceAgnosticJSONInActual() {
		assertTrue(adapter
				.equals(
						"{\"entry\":{\"id\":\"ribbit.com:123\",\"login\":\"foo@bar.com\",\"active\":0}}",
						json0));
	}

	@Test
	public void shouldStoreNotFoundMessageForJSONNotFoundForEqualityCheck() {
		assertFalse(adapter.equals("{\"bad\":\"json\"}", json0));
		assertEquals(1, adapter.getErrors().size());
		assertEquals("not found: '{\"bad\":\"json\"}'", adapter.getErrors()
				.get(0));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailEqualityCheckIfAnyJSONIsInvalid() {
		adapter.equals("{\"invalid\"", json0);
	}

	@Test
	public void shouldReturnItsStringRepresentationAsPrintableHTML() {
		assertEquals(html1, adapter.toString(json1));
	}

	@Test
	public void shouldReturnItsStringRepresentationAsPrintableHTMLEvenWhenEmpty() {
		assertEquals("no-body", adapter.toString(""));
	}

}
