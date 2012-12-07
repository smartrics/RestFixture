/*  Copyright 2011 Fabrizio Cannizzo
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

import java.nio.charset.Charset;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import smartrics.rest.client.RestData;
import smartrics.rest.client.RestResponse;

public class ContentTypeTest {

    @Before
    @After
    public void resetDefaultContentTypeMap() {
    	RestData.DEFAULT_ENCODING = "UTF-8";
        ContentType.resetDefaultMapping();
    }

	@Test
	public void shouldReturnCorrectTypeGivenApplicationXml() {
		RestData d = new RestResponse();
		d.addHeader("Content-Type", "application/xml");
		assertEquals(ContentType.XML, ContentType.parse(d.getContentType()));
	}
	
	@Test
	public void shouldReturnCorrectTypeGivenApplicationJson() {
		RestData d = new RestResponse();
		d.addHeader("Content-Type", "application/json");
		assertEquals(ContentType.JSON, ContentType.parse(d.getContentType()));
	}

	@Test
	public void shouldReturnCorrectTypeGivenApplicationText() {
		RestData d = new RestResponse();
		d.addHeader("Content-Type", "text/plain");

		assertEquals(ContentType.TEXT, ContentType.parse(d.getContentType()));
	}

	@Test
	public void shouldReturnCorrectTypeAndCharsetGivenApplicationTextWithCharset() {
		RestData d = new RestResponse();
		d.addHeader("Content-Type", "text/plain; charset= iso-8859-1");
		assertEquals(ContentType.TEXT, ContentType.parse(d.getContentType()));
		assertEquals("iso-8859-1", d.getCharset());
	}

	@Test
    public void shouldReturnDefaultGivenAnythingElse() {
		RestData d = new RestResponse();
		d.addHeader("Content-Type", "bla/bla");
        assertEquals(ContentType.typeFor("default"), ContentType.parse(d.getContentType()));
	}

	@Test
    public void shouldReturnDefaultGivenEmptyHeaders() {
		RestData d = new RestResponse();
        assertEquals(ContentType.typeFor("default"), ContentType.parse(d.getContentType()));
	}

    @Test
    public void shouldUseDefaultSystemCharsetIfCharsetNotParseableAndDefaultNotSpecifiedViaProperty() {
        Config c = Config.getConfig();
        c.add("restfixture.content.handlers.map", confMap());
        c.add("restfixture.content.default.charset", null);
        ContentType.config(c);
        assertEquals(RestData.DEFAULT_ENCODING, Charset.defaultCharset().name());
    }

    @Test
    public void shouldUseSpecifieDefaultCharsetProperty() {
        Config c = Config.getConfig();
        c.add("restfixture.content.handlers.map", confMap());
        c.add("restfixture.content.default.charset", "MY-CHARSET");
        ContentType.config(c);
        assertEquals(RestData.DEFAULT_ENCODING, "MY-CHARSET");
    }

    @Test
    public void shouldSetupInternalStateFromConfig() {
        StringBuffer configEntry = new StringBuffer();
        configEntry.append("application/x-html=xml<br/>\n");
        Config c = Config.getConfig();
        c.add("restfixture.content.handlers.map", configEntry.toString());
        ContentType.config(c);
        assertEquals(ContentType.XML, ContentType.typeFor("application/x-html"));
        assertEquals(ContentType.XML, ContentType.typeFor("default"));
    }

    @Test
    public void shouldSetupTheContentTypeToAdaptersMapViaConfig() {

        Config c = Config.getConfig();
        c.add("restfixture.content.handlers.map", confMap());
        c.add("restfixture.content.default.charset", "MY-CHARSET");
        ContentType.config(c);

        assertEquals(ContentType.JSON, ContentType.parse("application/json"));
        assertEquals(ContentType.XML, ContentType.parse("application/xml; charset=iso12344"));
        assertEquals(ContentType.XML, ContentType.parse("application/xhtml"));
        assertEquals(ContentType.XML, ContentType.parse("application/my-app-xml"));
        assertEquals(ContentType.JSON, ContentType.parse("text/plain"));

        // overrides "default"
        assertEquals(ContentType.TEXT, ContentType.parse("unhandled"));
    }

    private String confMap() {
        StringBuffer configEntry = new StringBuffer();
        configEntry.append("application/xml=xml<br/>\n");
        configEntry.append("default = text <br/>\n");
        configEntry.append("application/xhtml=xml<br/>\n");
        configEntry.append("application/my-app-xml=xml<br/>\n");
        configEntry.append("text/plain=json<br/>\n\n");

        return configEntry.toString();
    }
}
