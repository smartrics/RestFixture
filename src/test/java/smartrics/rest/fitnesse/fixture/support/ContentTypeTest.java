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
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import smartrics.rest.client.RestData;
import smartrics.rest.client.RestData.Header;
import smartrics.rest.config.Config;

public class ContentTypeTest {

    @Before
    @After
    public void resetDefaultContentTypeMap() {
        ContentType.resetDefaultMapping();
    }

	@Test
	public void shouldReturnCorrectTypeGivenApplicationXml() {
		List<Header> headers = new ArrayList<Header>();
		headers.add(new RestData.Header("Content-Type", "application/xml"));

		assertEquals(ContentType.XML, ContentType.parse(headers));
	}
	
	@Test
	public void shouldReturnCorrectTypeGivenApplicationJson() {
		List<Header> headers = new ArrayList<Header>();
		headers.add(new RestData.Header("Content-Type", "application/json"));

		assertEquals(ContentType.JSON, ContentType.parse(headers));
	}

	@Test
	public void shouldReturnCorrectTypeGivenApplicationText() {
		List<Header> headers = new ArrayList<Header>();
		headers.add(new RestData.Header("Content-Type", "text/plain"));

		assertEquals(ContentType.TEXT, ContentType.parse(headers));
	}

	@Test
	public void shouldReturnCorrectTypeGivenApplicationTextWithCharset() {
		List<Header> headers = new ArrayList<Header>();
		headers.add(new RestData.Header("Content-Type",
				"text/plain; charset=iso-8859-1"));

        assertEquals(ContentType.TEXT, ContentType.parse(headers));
    }

    @Test
    public void shouldReturnCorrectTypeGivenApplicationBinary() {
        List<Header> headers = new ArrayList<Header>();
        headers.add(new RestData.Header("Content-Type", "application/binary"));

        assertEquals(ContentType.FILE, ContentType.parse(headers));
    }

    @Test
    public void shouldReturnDefaultGivenAnythingElse() {
        List<Header> headers = new ArrayList<Header>();
        headers.add(new RestData.Header("Content-Type", "blah/blah"));

        assertEquals(ContentType.typeFor("default"), ContentType.parse(headers));
	}

	@Test
    public void shouldReturnDefaultGivenEmptyHeaders() {
		List<Header> headers = new ArrayList<Header>();

        assertEquals(ContentType.typeFor("default"), ContentType.parse(headers));
	}

	@Test
    public void shouldReturnDefaultGivenMoreThanOneHeaders() {
		List<Header> headers = new ArrayList<Header>();
		headers.add(new RestData.Header("Content-Type", "application/json"));
		headers.add(new RestData.Header("Content-Type", "application/json"));

        assertEquals(ContentType.typeFor("default"), ContentType.parse(headers));
	}

	@Test
    public void shouldReturnDefaultGivenOneHeaderThatIsNotContentType() {
		List<Header> headers = new ArrayList<Header>();
		headers.add(new RestData.Header("Something-Else", "application/json"));

        assertEquals(ContentType.typeFor("default"), ContentType.parse(headers));
	}

    @Test
    public void shouldParseTheCharset() {
        List<Header> headers = new ArrayList<Header>();
        headers.add(new RestData.Header("Content-Type", "application/json; charset=UTF-8"));
        assertEquals(ContentType.parseCharset(headers), "UTF-8");
    }

    @Test
    public void shouldUseDefaultSystemCharsetIfCharsetNotParseableAndDefaultNotSpecifiedViaProperty() {
        List<Header> headers = new ArrayList<Header>();
        headers.add(new RestData.Header("Content-Type", "application/json"));
        Config c = Config.getConfig();
        c.add("restfixture.content.handlers.map", confMap());
        c.add("restfixture.content.default.charset", null);
        ContentType.config(c);
        assertEquals(ContentType.parseCharset(headers), Charset.defaultCharset().name());
    }

    @Test
    public void shouldUseSpecifieDefaultCharsetProperty() {
        List<Header> headers = new ArrayList<Header>();
        headers.add(new RestData.Header("Content-Type", "application/json"));
        Config c = Config.getConfig();
        c.add("restfixture.content.handlers.map", confMap());
        c.add("restfixture.content.default.charset", "MY-CHARSET");
        ContentType.config(c);
        assertEquals(ContentType.parseCharset(headers), "MY-CHARSET");
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

        List<Header> headers = new ArrayList<Header>();
        headers.add(new RestData.Header("Content-Type", "application/json"));
        assertEquals(ContentType.JSON, ContentType.parse(headers));
        headers.set(0, new RestData.Header("Content-Type", "application/xml; charset=iso12344"));
        assertEquals(ContentType.XML, ContentType.parse(headers));
        headers.set(0, new RestData.Header("Content-Type", "application/xhtml"));
        assertEquals(ContentType.XML, ContentType.parse(headers));
        headers.set(0, new RestData.Header("Content-Type", "application/my-app-xml"));
        assertEquals(ContentType.XML, ContentType.parse(headers));
        headers.set(0, new RestData.Header("Content-Type", "text/plain"));
        assertEquals(ContentType.JSON, ContentType.parse(headers));
        headers.set(0, new RestData.Header("Content-Type", "application/binary"));
        assertEquals(ContentType.FILE, ContentType.parse(headers));

        // overrides "default"
        headers.set(0, new RestData.Header("Content-Type", "unhandled"));
        assertEquals(ContentType.TEXT, ContentType.parse(headers));
    }

    private String confMap() {
        StringBuffer configEntry = new StringBuffer();
        configEntry.append("application/xml=xml<br/>\n");
        configEntry.append("default = text <br/>\n");
        configEntry.append("application/xhtml=xml<br/>\n");
        configEntry.append("application/my-app-xml=xml<br/>\n");
        configEntry.append("text/plain=json<br/>\n\n");
        configEntry.append("application/binary=file<br/>\n\n");

        return configEntry.toString();
    }
}
