/*  Copyright 2015 Fabrizio Cannizzo
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

import java.net.URL;

import org.junit.Test;

public class UrlTest {
	@Test(expected=IllegalArgumentException.class)
	public void mustRejectEmptyStringUriRepresentations() {
		new Url("");
	}
	@Test(expected=IllegalArgumentException.class)
	public void mustRejectNullStringUriRepresentations() {
		new Url(null);
	}

	@Test(expected=IllegalArgumentException.class)
	public void mustRejectMalformedUrls(){
		new Url("aaa");
	}

	@Test(expected=IllegalArgumentException.class)
	public void mustRejectUrlsWithNoHost(){
		new Url("http:///a");
	}

	@Test
	public void mustAcceptWellFormedUrl(){
		String url = "http://a.com";
		Url u = new Url(url);
		assertEquals(url, u.toString());
	}

	@Test
	public void tailingSlashOnAUrlIsOptional(){
		Url u1 = new Url("http://a.com/");
		Url u2 = new Url("http://a.com");
		assertEquals(u1.toString(), u2.toString());
	}

	@Test
	public void mustBuildUrlTailingPathToBaseUrl(){
		Url u = new Url("http://a.com");
		URL fullUrl1 = u.buildURL("/path");
		URL fullUrl2 = u.buildURL("path");
		assertEquals("http://a.com/path", fullUrl1.toExternalForm());
		assertEquals("http://a.com/path", fullUrl2.toExternalForm());
	}

	@Test(expected=IllegalArgumentException.class)
	public void mustRejectNullPaths(){
		Url u = new Url("http://a.com");
		u.buildURL(null);
	}
	
    @Test
    public void mustExtractPartsIfUrlIsComplete() {
        Url u = new Url("http://a.com/resource");
        assertEquals("/resource", u.getResource());
        assertEquals("http://a.com", u.getBaseUrl());
    }

    @Test
    public void mustExtractPartsIfUrlIsCompleteWithNoPathTerminatingWithSlash() {
        Url u = new Url("http://a.com/");
        assertEquals("http://a.com", u.getBaseUrl());
        assertEquals("/", u.getResource());
    }

    @Test
    public void mustExtractPartsIfUrlIsCompleteWithNoPathTerminatingWithoutSlash() {
        Url u = new Url("http://a.com");
        assertEquals("http://a.com", u.getBaseUrl());
        assertEquals("/", u.getResource());
    }

}
