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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.xpath.XPathConstants;

import org.junit.Test;
import org.w3c.dom.NodeList;

public class ToolsTest {
    private static Map<String, String> DEF_NS_CONTEXT = new HashMap<String, String>();

    @Test
    public void mustMatchWhenRegexIsValidAndThereIsAMatch() {
        assertTrue(Tools.regex("200", "200"));
    }

    @Test
    public void mustNotMatchWhenRegexIsValidAndThereIsNotAMatch() {
        assertFalse(Tools.regex("200", "404"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void mustNotMatchWhenRegexIsInvalidAndNotifyError() {
        Tools.regex("200", "40[]4");
        fail("Should have thrown IAE as expression is invalid");
    }

    @Test
    public void dualityOfToAndFromHtml() {
        String stuff = "<a> " + System.getProperty("line.separator") + "  </a>";
        assertEquals(stuff, Tools.fromHtml(Tools.toHtml(stuff)));
    }

    @Test
    public void shouldReadAnInputStreamToAString() {
        InputStream is = new ByteArrayInputStream("a string".getBytes());
        assertEquals("a string", Tools.getStringFromInputStream(is));
        assertEquals("", Tools.getStringFromInputStream(null));
    }

    @Test
    public void shouldWrapAStringIntoAnInputStream() {
        InputStream is = Tools.getInputStreamFromString("another string");
        assertEquals("another string", Tools.getStringFromInputStream(is));
    }

    @Test
    public void shouldConvertAMapIntoAStringRepresentation() {
        final Map<String, String> map = new HashMap<String, String>();
        map.put("k1", "v1");
        map.put("k2", "v2");
        final String nvSep = "|";
        final String entrySep = "##";
        String repr = Tools.convertMapToString(map, nvSep, entrySep);
        assertEquals("k1|v1##k2|v2", repr);
    }

    @Test
    public void shouldConvertAStringIntoAMap() {
        Map<String, String> map = Tools.convertStringToMap("k1|v1##k2|v2", "|", "##");
        assertEquals(2, map.size());
        assertEquals("v2", map.get("k2"));
        assertEquals("v1", map.get("k1"));

        map = Tools.convertStringToMap("k1##k2|v2", "|", "##");
        assertEquals(2, map.size());
        assertEquals("", map.get("k1"));
        assertEquals("v2", map.get("k2"));

    }

    @Test
    public void shouldExtractXPathsFromXmlDocumentAsNodeLists() {
        String xml = "<a><b>test</b><c>1</c><c>2</c></a>";
        assertEquals(2, Tools.extractXPath(DEF_NS_CONTEXT, "/a/c", xml).getLength());
        assertEquals(1, Tools.extractXPath(DEF_NS_CONTEXT, "/a/b[text()='test']", xml).getLength());
        assertEquals("test", Tools.extractXPath(DEF_NS_CONTEXT, "/a/b/text()", xml).item(0).getNodeValue());
        assertEquals(1, Tools.extractXPath(DEF_NS_CONTEXT, "/a[count(c)>0]", xml).getLength());
        assertEquals(3, Tools.extractXPath(DEF_NS_CONTEXT, "/a/b | /a/c | /a/X", xml).getLength());
        assertEquals(3, Tools.extractXPath(DEF_NS_CONTEXT, "/a/b | /a/c | /a/X", xml).getLength());
    }

    @Test
    public void shouldExtractXPathsFromXmlDocumentAsStrings() {
        String xml = "<a><b>test</b><c>1</c><c>2</c></a>";
        assertEquals("2", Tools.extractXPath("count(/a/c)", xml, XPathConstants.STRING));

    }

    @Test
    public void shouldExtractXPathsFromXmlDocumentAsNumber() {
        String xml = "<a><b>test</b><c>1</c><c>2</c></a>";
        assertEquals(1.0, Tools.extractXPath("count(/a/b)", xml, XPathConstants.NUMBER));

    }

    @Test
    public void shouldExtractXPathsFromXmlDocumentAsBoolean() {
        String xml = "<a><b>test</b><c>1</c><c>2</c></a>";
        assertEquals(Boolean.TRUE, Tools.extractXPath("count(/a/c)=2", xml, XPathConstants.BOOLEAN));

    }

    @Test
    public void shouldExtractXPathsFromXmlDocumentAsNumberWithDefaultNamespace() {
        String xml = "<a xmlns='http://ns.com'><b>test</b><c>1</c></a>";
        HashMap<String, String> ns = new HashMap<String, String>();
        ns.put("def", "http://ns.com");
        assertEquals("test", Tools.extractXPath(ns, "/def:a/def:b", xml, XPathConstants.STRING));
    }

    @Test
    public void shouldExtractXPathsFromXmlDocumentAsNumberWithGenericNamespace() {
        String xml = "<?xml version='1.0' ?><a xmlns:ns1='http://ns1.com'><b>test</b><ns1:c>tada</ns1:c></a>";
        HashMap<String, String> ns = new HashMap<String, String>();
        ns.put("alias", "http://ns1.com");
        assertEquals("tada", Tools.extractXPath(ns, "/a/alias:c", xml, XPathConstants.STRING));
    }

    @Test
    public void shouldExtractXPathsFromXmlDocumentAsNodelistWithGenericNamespace() {
        String xml = "<resource><name>test data</name><data>some data</data></resource>";
        HashMap<String, String> ns = new HashMap<String, String>();
        ns.put("ns1alias", "http://smartrics/ns1");
        NodeList nodeList = (NodeList) Tools.extractXPath(ns, "/resource/name[text()='test data']", xml, XPathConstants.NODESET);
        assertEquals(1, nodeList.getLength());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotifyCallerWhenXPathIsWrong() {
        Tools.extractXPath(DEF_NS_CONTEXT, "/a[text=1", "<a>1</a>");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotifyCallerWhenXmlIsWrong() {
        Tools.extractXPath(DEF_NS_CONTEXT, "/a[text()='1']", "<a>1<a>");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotifyCallerWhenXmlCannotBeParsed() {
        Tools.extractXPath(DEF_NS_CONTEXT, "/a[text()='1']", null);
    }

}
