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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class StringTypeAdapterTest {

	@Test
	public void shouldEqualsTwoNullStrings() {
		assertTrue(new StringTypeAdapter().equals(null, null));
	}

	@Test
	public void shouldEqualsTwoEqualStrings() {
		assertTrue(new StringTypeAdapter().equals("a", "a"));
		assertFalse(new StringTypeAdapter().equals("a", "b"));
	}

	@Test
	public void shouldParseNullString() {
		assertNull(new StringTypeAdapter().parse("null"));
	}

	@Test
	public void shouldParseEmptyString() {
		assertEquals("", new StringTypeAdapter().parse("blank"));
	}

	@Test
	public void shouldParseAnyString() {
		assertEquals("any", new StringTypeAdapter().parse("any"));
	}

	@Test
	public void shouldConvertAnyString() {
		assertEquals("any", new StringTypeAdapter().toString("any"));
	}

	@Test
	public void shouldConvertNullString() {
		assertEquals("null", new StringTypeAdapter().toString(null));
	}

	@Test
	public void shouldConvertEmptyString() {
		assertEquals("blank", new StringTypeAdapter().toString(""));
	}

}
