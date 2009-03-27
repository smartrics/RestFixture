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

import org.junit.Test;

public class TextBodyTypeAdapterTest {

	private final TextBodyTypeAdapter adapter = new TextBodyTypeAdapter();

	@Test
	public void shouldParseTextAsTrimmedStrings() {
		assertEquals("abc 123", adapter.parse(" abc 123 "));
		assertEquals("null", adapter.parse(null));
	}

	@Test
	public void shouldRenderCellContentAsStrings(){
		assertEquals("abc123", adapter.toString("abc123"));
		assertEquals("no-body", adapter.toString(" "));
		assertEquals("no-body", adapter.toString(null));
		assertEquals("&lt;fred/&gt;", adapter.toString("<fred/>"));
	}

	@Test
	public void shouldNotEqualiseIfOneHasCRLF() {
		assertFalse(adapter.equals("abc123", "abc\r\n123"));
	}

	@Test
	public void shouldEqualiseIfBothHaveCRLF() {
		assertTrue(adapter.equals("abc\r\n123", "abc\r\n123"));
	}

	@Test
	public void shouldNotEqualiseIfExpectedOrActualAreNull(){
		assertFalse(adapter.equals(null, "abc123"));
		assertFalse(adapter.equals("sgsgd", null));
	}

	@Test
	public void whenExpectedIsNotMatchedAnErrorShouldBeAdded(){
		adapter.equals("xyz", "abc");
		assertEquals(1, adapter.getErrors().size());
		assertEquals("not match: xyz", adapter.getErrors().get(0));
	}
}
