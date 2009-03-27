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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import smartrics.rest.client.RestData;
import smartrics.rest.client.RestData.Header;

public class ContentTypeTest {
	// make sure you can get the right ContentType when header is
	// application/xml
	@Test
	public void shouldReturnCorrectTypeGivenApplicationXml() {
		// setup
		List<Header> headers = new ArrayList<Header>();
		headers.add(new RestData.Header("Content-Type", "application/xml"));
		// act & assert
		assertEquals(ContentType.XML, ContentType.parse(headers));
	}
	
	// make sure you can get the right ContentType when header is
	// application/json
	@Test
	public void shouldReturnCorrectTypeGivenApplicationJson() {
		// setup
		List<Header> headers = new ArrayList<Header>();
		headers.add(new RestData.Header("Content-Type", "application/json"));
		// act & assert
		assertEquals(ContentType.JSON, ContentType.parse(headers));
	}

	// make sure you can get the right ContentType when header is
	// text/plain
	@Test
	public void shouldReturnCorrectTypeGivenApplicationText() {
		// setup
		List<Header> headers = new ArrayList<Header>();
		headers.add(new RestData.Header("Content-Type", "text/plain"));
		// act & assert
		assertEquals(ContentType.TEXT, ContentType.parse(headers));
	}

	// make sure you can get the right ContentType when header is
	// text/plain with a charset
	@Test
	public void shouldReturnCorrectTypeGivenApplicationTextWithCharset() {
		// setup
		List<Header> headers = new ArrayList<Header>();
		headers.add(new RestData.Header("Content-Type",
				"text/plain; charset=iso-8859-1"));
		// act & assert
		assertEquals(ContentType.TEXT, ContentType.parse(headers));
	}

	// make sure you can get the UNKNOWN ContentType when header is
	// anything else
	@Test
	public void shouldReturnUnknownGivenAnythingElse() {
		// setup
		List<Header> headers = new ArrayList<Header>();
		headers.add(new RestData.Header("Content-Type", "blah/blah"));
		// act & assert
		assertEquals(ContentType.UNKNOWN, ContentType.parse(headers));
	}

	// make sure you can get the UNKNOWN ContentType when headers are empty
	@Test
	public void shouldReturnUnknownGivenEmptyHeaders() {
		// setup
		List<Header> headers = new ArrayList<Header>();
		// act & assert
		assertEquals(ContentType.UNKNOWN, ContentType.parse(headers));
	}

	// make sure you can get the UNKNOWN ContentType given more than one header
	@Test
	public void shouldReturnUnknownGivenMoreThanOneHeaders() {
		// setup
		List<Header> headers = new ArrayList<Header>();
		headers.add(new RestData.Header("Content-Type", "application/json"));
		headers.add(new RestData.Header("Content-Type", "application/json"));
		// act & assert
		assertEquals(ContentType.UNKNOWN, ContentType.parse(headers));
	}

	// make sure you can get the UNKNOWN ContentType given one header that is
	// not Content-Type:
	@Test
	public void shouldReturnUnknownGivenOneHeaderThatIsNotContentType() {
		// setup
		List<Header> headers = new ArrayList<Header>();
		headers.add(new RestData.Header("Something-Else", "application/json"));
		// act & assert
		assertEquals(ContentType.UNKNOWN, ContentType.parse(headers));
	}
}
