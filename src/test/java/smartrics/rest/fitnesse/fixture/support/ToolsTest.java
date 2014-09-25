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

import org.junit.Test;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathConstants;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
    }

    @Test
    public void dualityOfToAndFromHtml() {
        String stuff = "<a> \n  </a>";
        assertEquals(stuff, Tools.fromHtml(Tools.toHtml(stuff)));
    }

    @Test
    public void shouldReadAnInputStreamToAString() {
        InputStream is = new ByteArrayInputStream("a string".getBytes());
        assertEquals("a string", Tools.getStringFromInputStream(is));
        assertEquals("", Tools.getStringFromInputStream(null));
    }

    @Test
    public void shouldThrowRTEWrappingInputStreamExceptionsWhenReadingInputStreamToAString() throws Exception {
        InputStream is = mock(InputStream.class);
        when(is.read()).thenThrow(new IOException("io error"));
        try {
            Tools.getStringFromInputStream(is);
        } catch (IllegalArgumentException e) {
            assertEquals("Unable to read from stream", e.getMessage());
        }
    }

    @Test
    public void shouldThrowWhenReadingInputStreamToAStringWhenTryingToEncodeWithUnknownEncoding() throws Exception {
        String encoding = "==========";
        InputStream is = mock(InputStream.class);
        try {
            Tools.getStringFromInputStream(is, encoding);
        } catch (IllegalArgumentException e) {
            assertEquals("Unsupported encoding: " + encoding, e.getMessage());
        }
    }

    @Test
    public void shouldWrapAStringIntoAnInputStream() {
        InputStream is = Tools.getInputStreamFromString("another string", "UTF-8");
        assertEquals("another string", Tools.getStringFromInputStream(is));
    }

    @Test
    public void shouldThrowRTEWhenWrappingAStringIntoAnInputStreamWithUnknownEncoding() {
        try {
            Tools.getInputStreamFromString("another string", "crap");
        } catch (IllegalArgumentException e) {
            assertEquals("Unsupported encoding: crap", e.getMessage());
        }
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
        Map<String, String> map = Tools.convertStringToMap("k1~v1##k2~v2", "~", "##", false);
        assertEquals(2, map.size());
        assertEquals("v2", map.get("k2"));
        assertEquals("v1", map.get("k1"));

        map = Tools.convertStringToMap("k1##k2~v2", "~", "##", false);
        assertEquals(2, map.size());
        assertEquals("", map.get("k1"));
        assertEquals("v2", map.get("k2"));
    }

    @Test
    public void shouldConvertAStringIntoAMapAndCleanHtmlTagsFromValues() {
        Map<String, String> map = Tools.convertStringToMap("k1=v1\nk2=<a href=\"2\">v2</a>", "=", "\n", true);
        assertEquals(2, map.size());
        assertEquals("v1", map.get("k1"));
        assertEquals("v2", map.get("k2"));
    }

    @Test
    public void shouldConvertAMultilineStringIntoAMap() {
        Map<String, String> map = Tools.convertStringToMap("!- k1=v1\nk2=v2-!", "=", "\n", false);
        assertEquals(2, map.size());
        assertEquals("v2", map.get("k2"));
        assertEquals("v1", map.get("k1"));
    }

    @Test
    public void shouldConvertAMultilineStringIntoAMapWhenEscapeSequenceIsNested() {
        Map<String, String> map = Tools.convertStringToMap("k1=v1 !-\n -! k2=v2", "=", "\n", false);
        assertEquals(2, map.size());
        assertEquals("v2", map.get("k2"));
        assertEquals("v1", map.get("k1"));
    }

    @Test
    public void shouldConvertAMultilineStringIntoAMapIgnoresEmptyLines() {
        Map<String, String> map = Tools.convertStringToMap("!- k1=v1\n\nk2=v2\n-!", "=", "\n", false);
        assertEquals(2, map.size());
        assertEquals("v2", map.get("k2"));
        assertEquals("v1", map.get("k1"));
        assertNull(map.get(""));
    }

    @Test
    public void shouldExtractXPathsFromXmlDocumentAsNodeLists() {
        String xml = "<a><b>test</b><c>1</c><c>2</c></a>";
        assertEquals(1, Tools.extractXPath(DEF_NS_CONTEXT, "/", xml).getLength());
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
        assertEquals("1", Tools.extractXPath("count(/)", xml, XPathConstants.STRING, "UTF-8"));
        assertEquals("2", Tools.extractXPath("count(/a/c)", xml, XPathConstants.STRING, "UTF-8"));
    }

    @Test
    public void shouldExtractXPathsFromXmlDocumentAsNumber() {
        String xml = "<a><b>test</b><c>1</c><c>2</c></a>";
        assertEquals(1.0, Tools.extractXPath("count(/a/b)", xml, XPathConstants.NUMBER, "UTF-8"));
    }

    @Test
    public void shouldExtractXPathsFromXmlDocumentAsBoolean() {
        String xml = "<a><b>test</b><c>1</c><c>2</c></a>";
        assertEquals(Boolean.TRUE, Tools.extractXPath("count(/a/c)=2", xml, XPathConstants.BOOLEAN, "UTF-8"));
    }

    @Test
    public void shouldExtractXPathsFromXmlDocumentAsNumberWithDefaultNamespace() {
        String xml = "<a xmlns='http://ns.com'><b>test</b><c>1</c></a>";
        HashMap<String, String> ns = new HashMap<String, String>();
        ns.put("def", "http://ns.com");
        assertEquals("test", Tools.extractXPath(ns, "/def:a/def:b", xml, XPathConstants.STRING, "UTF-8"));
    }

    @Test
    public void shouldExtractXPathsFromXmlDocumentAsNumberWithDefaultNamespace2() {
        String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes'?><order xmlns='x:/pcat/1/catalog/order/v5_0'><orderIdentifier><orderSchema>/pcat/1/catalog/order/v5_0</orderSchema><orderNumber>1006LXMH</orderNumber><orderVersion>0</orderVersion><orderURL>/oe-platform-app/Order/Order/Published/1006LXMH?version=1</orderURL><rootOrderNumber>1006LXMH</rootOrderNumber></orderIdentifier><detail><general><proposalNumber>8619</proposalNumber><orderStatus>InProgress</orderStatus><orderVersionStatus>Master</orderVersionStatus><orderType>Install</orderType><orderCategory>Customer</orderCategory><createDate>2013-03-19T14:27:00.744Z</createDate><createBy>l3svc.oePlatform</createBy><sourceSystem>WFOE</sourceSystem><opptyID>313841</opptyID><DiscountedMRC>3963.0146</DiscountedMRC><AmortizedNRC>4000.0000</AmortizedNRC><totalNrc>4000.0000</totalNrc><orderDate><customerSignedDate>2013-03-18T00:00:00</customerSignedDate><installDate>2013-03-19T14:54:34.230Z</installDate></orderDate></general><customer><customerNumber>1-T8BD</customerNumber><customerName>SundayUAT</customerName></customer><billingAccount><billingAccountNumber>1-T8BD</billingAccountNumber></billingAccount></detail></order>";
        HashMap<String, String> ns = new HashMap<String, String>();
        ns.put("ns", "x:/pcat/1/catalog/order/v5_0");
        assertEquals("8619", Tools.extractXPath(ns, "//ns:proposalNumber/text()", xml, XPathConstants.STRING, "UTF-8"));
    }

    
    @Test
    public void shouldExtractXPathsFromXmlDocumentAsNumberWithGenericNamespace() {
        String xml = "<?xml version='1.0' ?><a xmlns:ns1='http://ns1.com'><b>test</b><ns1:c>tada</ns1:c></a>";
        HashMap<String, String> ns = new HashMap<String, String>();
        ns.put("alias", "http://ns1.com");
        assertEquals("tada", Tools.extractXPath(ns, "/a/alias:c", xml, XPathConstants.STRING, "UTF-8"));
    }

    @Test
    public void shouldExtractXPathsFromXmlDocumentWithNestedGenericNamespace() {
        String xml = "<?xml version='1.0' ?><resource><name>a funky name</name><data>an important message</data>"
                + "<nstag xmlns:ns1='http://smartrics/ns1'><ns1:number>3</ns1:number></nstag></resource>";
        HashMap<String, String> ns = new HashMap<String, String>();
        ns.put("ns1alias", "http://smartrics/ns1");
        assertEquals("true", Tools.extractXPath(ns, "/resource/nstag/ns1alias:number[text()='3']", xml, XPathConstants.BOOLEAN, "UTF-8").toString());
    }

    @Test
    public void shouldExtractXPathsFromXmlDocumentAsNodelistWithGenericNamespace() {
        String xml = "<resource><name>test data</name><data>some data</data></resource>";
        HashMap<String, String> ns = new HashMap<String, String>();
        ns.put("ns1alias", "http://smartrics/ns1");
        NodeList nodeList = (NodeList) Tools.extractXPath(ns, "/resource/name[text()='test data']", xml, XPathConstants.NODESET, "UTF-8");
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

    @Test
    public void codeCoversionUsesCodeHtmlTag() {
        assertThat(Tools.toCode("x"), is(equalTo("<code>x</code>")));
    }

    @Test
    public void shouldWrapTextInHtmlAnchor() {
        assertThat(Tools.toHtmlLink("http://localhost:1234", "x"), is(equalTo("<a href='http://localhost:1234'>x</a>")));
    }

    @Test
    public void shouldExtractTextFromSimpleTag() {
        assertEquals("stuff", Tools.fromSimpleTag("<bob data='1'>stuff</bob>"));
    }

    @Test
    public void basicChecksOnMakeCollapsableItemWhenHtml() {
        // not much of a test, I know, but guarantees minimal info on fitnesse
        // stylesheed/js
        int id = "someContent".hashCode();
        String ret = Tools.makeToggleCollapseable("message", "someContent");
        assertTrue(ret.indexOf("javascript:toggleCollapsable('" + id) > 0);
        assertTrue(ret.indexOf("<div class='hidden' id='" + id) > 0);
    }

    @Test
    public void basicChecksOnMakeCollapsableItemWhenPlain() {
        // not much of a test, I know, but guarantees minimal info on fitnesse
        // stylesheed/js
        int id = "someContent".hashCode();
        String ret = Tools.makeToggleCollapseable("message", "someContent");
        assertTrue("Ret not containing expected 'someContent': " + ret, ret.contains("someContent"));
        assertTrue("Ret not containing expected '" + id + "': " + ret, ret.contains(Integer.toString(id)));
    }

    @Test
    public void shouldConvertJsonToXml() {
        String xml = Tools.fromJSONtoXML("{\"person\" : {\"address\" : { \"street\" : \"regent st\", \"number\" : \"1\"}, \"name\" : \"joe\", \"surname\" : \"bloggs\"} }");
        assertEquals("1", Tools.extractXPath("/person/address/number", xml, XPathConstants.STRING, "UTF-8"));
        assertEquals("regent st", Tools.extractXPath("/person/address/street", xml, XPathConstants.STRING, "UTF-8"));
        assertEquals("joe", Tools.extractXPath("/person/name", xml, XPathConstants.STRING, "UTF-8"));
        assertEquals("bloggs", Tools.extractXPath("/person/surname", xml, XPathConstants.STRING, "UTF-8"));
    }

    @Test
    public void shouldCheckIfAStringIsValidJson() {
        String jsonPart0 = "{ \"person\" : { \"name\" : ";
        String jsonPart1 = "\"Rokko\", \"age\" : \"30\" } }";
        assertFalse(Tools.isValidJson(jsonPart0));
        assertTrue(Tools.isValidJson(jsonPart0 + jsonPart1));
    }
}
