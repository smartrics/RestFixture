package smartrics.rest.fitnesse.fixture;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import smartrics.rest.fitnesse.fixture.support.Variables;

import fit.Parse;
import fit.exception.FitFailureException;
import fit.exception.FitParseException;


public class RestFixtureTest {

	private static final String BASE_URL = "http://localhost:9090";
	private RestFixture fixture;
	private Variables variables = new Variables();

	@Before
	public void setUp(){
		fixture = new RestFixture(){
			{
				super.args = new String[]{BASE_URL};
			}
		};
		fixture.setRestClient(new MockRestClient());
		variables.clearAll();
	}

	@Test(expected = FitFailureException.class)
	public void mustNotifyCallerThatBaseUrlAsFixtureArgIsMandatory() throws FitParseException{
		fixture = new RestFixture(){
			{
				super.args = new String[]{};
			}
		};
		fixture.doCells(new Parse("<table><tr><td></td></tr></table>"));
	}

	@Test
	public void mustNotifyClientIfHTTPVerbInFirstCellIsNotSupported(){
		Parse t = createFitTestInstance(createFitTestRow("IDONTEXIST", "/uri", "", "", ""));
		fixture.doCells(t);
		assertTrue(extractCell1(t).contains("IDONTEXIST"));
		assertTrue(extractCell1(t).contains("<div class=\"fit_stacktrace\">java.lang.NoSuchMethodException"));
	}

	@Test
	public void mustExecuteVerbOnAUriWithNoExcpectationsOnRestResponseParts(){
		Parse t = createFitTestInstance(createFitTestRow("GET", "/uri", "", "", ""));
		fixture.doCells(t);
		assertAllCells(t, "GET", buildResUriLink("/uri"), "<span class=\"fit_grey\">200</span>",
				"<span class=\"fit_grey\">h1&nbsp;:&nbsp;v1<br/>h2&nbsp;:&nbsp;v2</span>",
				"<span class=\"fit_grey\">&lt;body&gt;text&lt;/body&gt;</span>");
	}

	@Test
	public void mustExecuteAnyVerbOnAnyUri() {
		Parse t = createFitTestInstance(createFitTestRow("GET", "/uri", "", "", ""));
		fixture.doCells(t);
		assertAllCells(t, "GET", buildResUriLink("/uri"), "<span class=\"fit_grey\">200</span>",
				"<span class=\"fit_grey\">h1&nbsp;:&nbsp;v1<br/>h2&nbsp;:&nbsp;v2</span>",
				"<span class=\"fit_grey\">&lt;body&gt;text&lt;/body&gt;</span>");
	}


	@Test
	public void mustMatchRequestsWithNoBodyExpressedAsNoBodyString(){
		Parse t = createFitTestInstance(createFitTestRow("DELETE", "/uri", "200", "", "no-body"));
		fixture.body("<delete/>");
		fixture.doCells(t);
		assertAllCells(t, "DELETE", buildResUriLink("/uri"), "200",
				"<span class=\"fit_grey\">h1&nbsp;:&nbsp;v1<br/>h2&nbsp;:&nbsp;v2</span>",
				"no-body");
	}

	@Test
	public void mustExecuteVerbOnAUriWithExcpectationsSetOnStatusCode() {
		// when statusCode expected matches exactly the actual, no 'actual' is displayed
		Parse t = createFitTestInstance(createFitTestRow("GET", "/uri", "200", "", ""));
		fixture.doCells(t);
		assertAllCells(t, "GET", buildResUriLink("/uri"), "200",
				"<span class=\"fit_grey\">h1&nbsp;:&nbsp;v1<br/>h2&nbsp;:&nbsp;v2</span>",
				"<span class=\"fit_grey\">&lt;body&gt;text&lt;/body&gt;</span>");
		// when statusCode expected matches exactly the actual as regex, 'actual' is displayed...
		t = createFitTestInstance(createFitTestRow("GET", "/uri", "20\\d", "", ""));
		fixture.doCells(t);
		assertAllCells(t, "GET", buildResUriLink("/uri"), "20\\d <span class=\"fit_label\">expected</span><hr>200 <span class=\"fit_label\">actual</span>",
				"<span class=\"fit_grey\">h1&nbsp;:&nbsp;v1<br/>h2&nbsp;:&nbsp;v2</span>",
				"<span class=\"fit_grey\">&lt;body&gt;text&lt;/body&gt;</span>");
		// ... unless displayActualOnRight is false
		fixture.setDisplayActualOnRight(false);
		t = createFitTestInstance(createFitTestRow("GET", "/uri", "20\\d", "", ""));
		fixture.doCells(t);
		assertAllCells(t, "GET", buildResUriLink("/uri"), "20\\d",
				"<span class=\"fit_grey\">h1&nbsp;:&nbsp;v1<br/>h2&nbsp;:&nbsp;v2</span>",
				"<span class=\"fit_grey\">&lt;body&gt;text&lt;/body&gt;</span>");
	}

	/**
	 * expectations on headers are verified by checking that the expected list of headers is a
	 * subset of the actual list of headers
	 */
	@Test
	public void mustExecuteVerbOnAUriWithExcpectationsSetOnHeaders() {
		Parse t = createFitTestInstance(createFitTestRow("GET", "/uri", "200", "h1 : v1", ""));
		fixture.doCells(t);
		assertAllCells(t, "GET", buildResUriLink("/uri"), "200",
				"h1 : v1 <span class=\"fit_label\">expected</span><hr>h1&nbsp;:&nbsp;v1<br/>h2&nbsp;:&nbsp;v2 <span class=\"fit_label\">actual</span>",
				"<span class=\"fit_grey\">&lt;body&gt;text&lt;/body&gt;</span>");
		// header values should be matched using regexes
		t = createFitTestInstance(createFitTestRow("GET", "/uri", "200", "h1 : \\w\\d", ""));
		fixture.doCells(t);
		assertAllCells(t, "GET", buildResUriLink("/uri"), "200",
				"h1 : \\w\\d <span class=\"fit_label\">expected</span><hr>h1&nbsp;:&nbsp;v1<br/>h2&nbsp;:&nbsp;v2 <span class=\"fit_label\">actual</span>",
				"<span class=\"fit_grey\">&lt;body&gt;text&lt;/body&gt;</span>");
		// actual value is displayed unless displayActualOnRight is false
		fixture.setDisplayActualOnRight(false);
		t = createFitTestInstance(createFitTestRow("GET", "/uri", "200", "h1 : \\w\\d", ""));
		fixture.doCells(t);
		assertAllCells(t, "GET", buildResUriLink("/uri"), "200",
				"h1 : \\w\\d",
				"<span class=\"fit_grey\">&lt;body&gt;text&lt;/body&gt;</span>");
	}

	/**
	 * expectations on body are verified by executing xpaths on the returned body.
	 * it's assumed that the return body is XML. the expectation is matched if each
	 * xpath returns a not empty list of selected nodes
	 */
	@Test
	public void mustExecuteVerbOnAUriWithExcpectationsSetOnBody() {
		Parse t = createFitTestInstance(createFitTestRow("GET", "/uri", "200", "", "/body[text()='text']"));
		fixture.doCells(t);
		assertAllCells(t, "GET", buildResUriLink("/uri"), "200",
				"<span class=\"fit_grey\">h1&nbsp;:&nbsp;v1<br/>h2&nbsp;:&nbsp;v2</span>",
				"/body[text()='text'] <span class=\"fit_label\">expected</span><hr>&lt;body&gt;text&lt;/body&gt; <span class=\"fit_label\">actual</span>");
		// multiple xpaths can be passed separated by line.separator
		t = createFitTestInstance(createFitTestRow("GET", "/uri", "200", "", "/body[text()='text']" + System.getProperty("line.separator") + "/body"));
		fixture.doCells(t);
		assertAllCells(t, "GET", buildResUriLink("/uri"), "200",
				"<span class=\"fit_grey\">h1&nbsp;:&nbsp;v1<br/>h2&nbsp;:&nbsp;v2</span>",
				"/body[text()='text']" + System.getProperty("line.separator") + "/body <span class=\"fit_label\">expected</span><hr>&lt;body&gt;text&lt;/body&gt; <span class=\"fit_label\">actual</span>");
		// actual value is displayed unless displayActualOnRight is false
		fixture.setDisplayActualOnRight(false);
		t = createFitTestInstance(createFitTestRow("GET", "/uri", "200", "", "/body[text()='text']"));
		fixture.doCells(t);
		assertAllCells(t, "GET", buildResUriLink("/uri"), "200",
				"<span class=\"fit_grey\">h1&nbsp;:&nbsp;v1<br/>h2&nbsp;:&nbsp;v2</span>",
				"/body[text()='text']");
	}

	@Test
	public void shoudlExecuteVerbOnAUriWithExcpectationsSetOnHeaders() {
		Parse t = createFitTestInstance(createFitTestRow("GET", "/uri", "", "", ""));
		fixture.doCells(t);
		assertEquals("GET", extractCell1(t));
		t = createFitTestInstance(createFitTestRow("POST", "/uri", "", "", ""));
		fixture.body("<post/>");
		fixture.doCells(t);
		assertEquals("POST", extractCell1(t));
		t = createFitTestInstance(createFitTestRow("DELETE", "/uri", "", "", ""));
		fixture.doCells(t);
		assertEquals("DELETE", extractCell1(t));
		t = createFitTestInstance(createFitTestRow("PUT", "/uri", "", "", ""));
		fixture.doCells(t);
		fixture.body("<put/>");
		assertEquals("PUT", extractCell1(t));
	}

	@Test
	public void mustCaptureErrorsOnExpectationsAndDisplayThemInTheSameCell(){
		Parse t = createFitTestInstance(createFitTestRow("GET", "/uri", "404", "", ""));
		fixture.doCells(t);
		assertAllCells(t, "GET", buildResUriLink("/uri"), "404 <span class=\"fit_label\">expected</span><hr>200 <span class=\"fit_label\">actual</span><hr>not&nbsp;match:&nbsp;404<br/> <span class=\"fit_label\">errors</span>",
				"<span class=\"fit_grey\">h1&nbsp;:&nbsp;v1<br/>h2&nbsp;:&nbsp;v2</span>",
				"<span class=\"fit_grey\">&lt;body&gt;text&lt;/body&gt;</span>");
	}

	/**
	 * <code>| let | content |  body | /body/text() | |</code>
	 */
	@Test
	public void mustAllowStorageInVariablesOfValuesExtractedViaXPathFromBody(){
		Parse t = createFitTestInstance(createFitTestRow("GET", "/uri", "", "", ""));
		fixture.doCells(t);
		t = createFitTestInstance(createFitTestRow("let", "content", "body", "/body/text()", "text"));
		fixture.doCells(t);
		assertAllCells(t, "let", "content", "body", "/body/text()", "text");
		assertEquals("text", new Variables().get("content"));
	}

	/**
	 * <code>| let | val | header | h1 : (\w\d) | |</code>
	 */
	@Test
	public void mustAllowStorageInVariablesOfValuesExtractedViaRegexFromHeader(){
		Parse t = createFitTestInstance(createFitTestRow("GET", "/uri", "", "", ""));
		fixture.doCells(t);
		t = createFitTestInstance(createFitTestRow("let", "val", "header", "h1:(\\w\\d)", "v1"));
		fixture.doCells(t);
		assertAllCells(t, "let", "val", "header", "h1:(\\w\\d)", "v1");
		assertEquals("v1", new Variables().get("val"));
	}

	private void assertAllCells(Parse t, String c1, String c2, String c3, String c4, String c5) {
		assertEquals(c1, extractCell1(t));
		assertEquals(c2, extractCell2(t).trim());
		assertEquals(c3, extractCell3(t).trim());
		assertEquals(c4, extractCell4(t).trim());
		assertEquals(c5, extractcell5(t).trim());
	}

	private String buildResUriLink(String resUri) {
		return "<a href='" + BASE_URL + resUri +"'>" + resUri + "</a>";
	}

	private String extractCell1(Parse p){
		return p.body;
	}

	private String extractCell2(Parse p){
		return p.more.body;
	}

	private String extractCell3(Parse p){
		return p.more.more.body;
	}

	private String extractCell4(Parse p){
		return p.more.more.more.body;
	}

	private String extractcell5(Parse p){
		return p.more.more.more.more.body;
	}

	private Parse createFitTestInstance(String ... rows) {
		Parse t = null;
		StringBuffer rBuff = new StringBuffer();
		rBuff.append("<table>");
		for(String r : rows){
			rBuff.append(r);
		}
		rBuff.append("</table>");
		try{
			t = new Parse(rBuff.toString(), new String[]{"table", "row", "col"}, 2, 0);
		} catch(FitParseException e){
			fail("Unable to build Parse object");
		}
		return t;
	}

	private String createFitTestRow(String cell1, String cell2, String cell3, String cell4, String cell5) {
		String row = String.format("<row><col>%s</col><col>%s</col><col>%s</col><col>%s</col><col>%s</col></row>",
				cell1, cell2, cell3, cell4, cell5);
		return row;
	}

}
