package smartrics.rest.fitnesse.fixture.support;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.junit.Test;
import org.w3c.dom.NodeList;

import smartrics.rest.fitnesse.fixture.support.Tools;

public class ToolsTest {
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
	public void dualityOfToAndFromHtml(){
		String stuff = "<a> " + System.getProperty("line.separator") + "  </a>";
		assertEquals(stuff, Tools.fromHtml(Tools.toHtml(stuff)));
	}

	@Test
	public void shouldReadAnInputStreamToAString(){
		InputStream is = new ByteArrayInputStream("a string".getBytes());
		assertEquals("a string", Tools.getStringFromInputStream(is));
		assertEquals("", Tools.getStringFromInputStream(null));
	}

	@Test
	public void shouldWrapAStringIntoAnInputStream(){
		InputStream is = Tools.getInputStreamFromString("another string");
		assertEquals("another string", Tools.getStringFromInputStream(is));
	}

	@Test
	public void shouldConvertAMapIntoAStringRepresentation(){
		final Map<String, String> map = new HashMap<String, String>();
		map.put("k1", "v1");
		map.put("k2", "v2");
		final String nvSep = "|";
		final String entrySep = "##";
		String repr = Tools.convertMapToString(map, nvSep, entrySep);
		assertEquals("k1|v1##k2|v2", repr);
	}

	@Test
	public void shouldConvertAStringIntoAMap(){
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
	public void shouldExtractXPathsFromXmlDocument(){
		String xml = "<a><b>test</b><c>1</c><c>2</c></a>";
		assertEquals(2, Tools.extractXPath("/a/c", xml).getLength());
		assertEquals(1, Tools.extractXPath("/a/b[text()='test']", xml).getLength());
		assertEquals("test", Tools.extractXPath("/a/b/text()", xml).item(0).getNodeValue());
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldNotifyCallerWhenXPathIsWrong(){
		Tools.extractXPath("/a[text=1", "<a>1</a>");
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldNotifyCallerWhenXmlIsWrong(){
		Tools.extractXPath("/a[text()='1']", "<a>1<a>");
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldNotifyCallerWhenXmlCannotBeParsed(){
		Tools.extractXPath("/a[text()='1']", null);
	}

}
