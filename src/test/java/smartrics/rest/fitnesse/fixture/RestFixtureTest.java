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
package smartrics.rest.fitnesse.fixture;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import smartrics.rest.client.RestRequest;
import smartrics.rest.client.RestResponse;
import smartrics.rest.fitnesse.fixture.support.Variables;
import fit.Parse;
import fit.exception.FitFailureException;
import fit.exception.FitParseException;

public class RestFixtureTest {

	private static final String BASE_URL = "http://localhost:9090";
	private RestFixture fixture;
	private final Variables variables = new Variables();
	private RestFixtureTestHelper helper;

	@Before
	public void setUp() {
		helper = new RestFixtureTestHelper();
		fixture = new RestFixture() {
			{
				super.args = new String[] { BASE_URL };
			}
		};
		fixture.setRestClient(new MockRestClient());
		variables.clearAll();
	}

	@Test(expected = FitFailureException.class)
	public void mustNotifyCallerThatBaseUrlAsFixtureArgIsMandatory()
			throws FitParseException {
		fixture = new RestFixture() {
			{
				super.args = new String[] {};
			}
		};
		fixture.doCells(new Parse("<table><tr><td></td></tr></table>"));
	}

	@Test
	public void mustNotifyClientIfHTTPVerbInFirstCellIsNotSupported() {
		Parse t = helper.createFitTestInstance(helper.createFitTestRow(
				"IDONTEXIST", "/uri",
				"", "", ""));
		fixture.doCells(t);
		assertTrue(extractCell1(t).contains("IDONTEXIST"));
		assertTrue(extractCell1(t)
				.contains(
						"<div class=\"fit_stacktrace\">java.lang.NoSuchMethodException"));
	}

	@Test
	public void mustExecuteVerbOnAUriWithNoExcpectationsOnRestResponseParts() {
		Parse t = helper.createFitTestInstance(helper.createFitTestRow("GET",
				"/uri",
				"", "",
				""));
		fixture.doCells(t);
		assertAllCells(
				t,
				"GET",
				buildResUriLink("/uri"),
				"<span class=\"fit_grey\">200</span>",
				"<span class=\"fit_grey\">h1&nbsp;:&nbsp;v1<br/>h2&nbsp;:&nbsp;v2<br/>Content-Type&nbsp;:&nbsp;application/xml</span>",
				"<span class=\"fit_grey\">&lt;body&gt;text&lt;/body&gt;</span>");
	}

	@Test
	public void mustExecuteAnyVerbOnAnyUri() {
		Parse t = helper.createFitTestInstance(helper.createFitTestRow("GET",
				"/uri",
				"", "",
				""));
		fixture.doCells(t);
		assertAllCells(
				t,
				"GET",
				buildResUriLink("/uri"),
				"<span class=\"fit_grey\">200</span>",
				"<span class=\"fit_grey\">h1&nbsp;:&nbsp;v1<br/>h2&nbsp;:&nbsp;v2<br/>Content-Type&nbsp;:&nbsp;application/xml</span>",
				"<span class=\"fit_grey\">&lt;body&gt;text&lt;/body&gt;</span>");
	}

	@Test
	public void mustMatchRequestsWithNoBodyExpressedAsNoBodyString() {
		Parse t = helper.createFitTestInstance(helper.createFitTestRow(
				"DELETE",
				"/uri",
				"200", "", "no-body"));
		fixture.body("<delete/>");
		fixture.doCells(t);
		assertAllCells(
				t,
				"DELETE",
				buildResUriLink("/uri"),
				"200",
				"<span class=\"fit_grey\">h1&nbsp;:&nbsp;v1<br/>h2&nbsp;:&nbsp;v2<br/>Content-Type&nbsp;:&nbsp;application/xml</span>",
				"no-body");
	}

	@Test
	public void mustExecuteVerbOnAUriWithExcpectationsSetOnStatusCode() {
		// when statusCode expected matches exactly the actual, no 'actual' is
		// displayed
		Parse t = helper.createFitTestInstance(helper.createFitTestRow("GET",
				"/uri",
				"200",
				"", ""));
		fixture.doCells(t);
		assertAllCells(
				t,
				"GET",
				buildResUriLink("/uri"),
				"200",
				"<span class=\"fit_grey\">h1&nbsp;:&nbsp;v1<br/>h2&nbsp;:&nbsp;v2<br/>Content-Type&nbsp;:&nbsp;application/xml</span>",
				"<span class=\"fit_grey\">&lt;body&gt;text&lt;/body&gt;</span>");
		// when statusCode expected matches exactly the actual as regex,
		// 'actual' is displayed...
		t = helper.createFitTestInstance(helper.createFitTestRow("GET", "/uri",
				"20\\d", "",
				""));
		fixture.doCells(t);
		assertAllCells(
				t,
				"GET",
				buildResUriLink("/uri"),
				"20\\d <span class=\"fit_label\">expected</span><hr>200 <span class=\"fit_label\">actual</span>",
				"<span class=\"fit_grey\">h1&nbsp;:&nbsp;v1<br/>h2&nbsp;:&nbsp;v2<br/>Content-Type&nbsp;:&nbsp;application/xml</span>",
				"<span class=\"fit_grey\">&lt;body&gt;text&lt;/body&gt;</span>");
		// ... unless displayActualOnRight is false
		fixture.setDisplayActualOnRight(false);
		t = helper.createFitTestInstance(helper.createFitTestRow("GET", "/uri",
				"20\\d", "",
				""));
		fixture.doCells(t);
		assertAllCells(
				t,
				"GET",
				buildResUriLink("/uri"),
				"20\\d",
				"<span class=\"fit_grey\">h1&nbsp;:&nbsp;v1<br/>h2&nbsp;:&nbsp;v2<br/>Content-Type&nbsp;:&nbsp;application/xml</span>",
				"<span class=\"fit_grey\">&lt;body&gt;text&lt;/body&gt;</span>");
	}

	/**
	 * expectations on headers are verified by checking that the expected list
	 * of headers is a subset of the actual list of headers
	 */
	@Test
	public void mustExecuteVerbOnAUriWithExcpectationsSetOnHeaders() {
		Parse t = helper.createFitTestInstance(helper.createFitTestRow("GET",
				"/uri",
				"200",
				"h1 : v1", ""));
		fixture.doCells(t);
		assertAllCells(
				t,
				"GET",
				buildResUriLink("/uri"),
				"200",
				"h1 : v1 <span class=\"fit_label\">expected</span><hr>h1&nbsp;:&nbsp;v1<br/>h2&nbsp;:&nbsp;v2<br/>Content-Type&nbsp;:&nbsp;application/xml <span class=\"fit_label\">actual</span>",
				"<span class=\"fit_grey\">&lt;body&gt;text&lt;/body&gt;</span>");
		// header values should be matched using regexes
		t = helper.createFitTestInstance(helper.createFitTestRow("GET", "/uri",
				"200",
				"h1 : \\w\\d", ""));
		fixture.doCells(t);
		assertAllCells(
				t,
				"GET",
				buildResUriLink("/uri"),
				"200",
				"h1 : \\w\\d <span class=\"fit_label\">expected</span><hr>h1&nbsp;:&nbsp;v1<br/>h2&nbsp;:&nbsp;v2<br/>Content-Type&nbsp;:&nbsp;application/xml <span class=\"fit_label\">actual</span>",
				"<span class=\"fit_grey\">&lt;body&gt;text&lt;/body&gt;</span>");
		// actual value is displayed unless displayActualOnRight is false
		fixture.setDisplayActualOnRight(false);
		t = helper.createFitTestInstance(helper.createFitTestRow("GET", "/uri",
				"200",
				"h1 : \\w\\d", ""));
		fixture.doCells(t);
		assertAllCells(t, "GET", buildResUriLink("/uri"), "200", "h1 : \\w\\d",
				"<span class=\"fit_grey\">&lt;body&gt;text&lt;/body&gt;</span>");
	}

	/**
	 * expectations on body are verified by executing xpaths on the returned
	 * body. it's assumed that the return body is XML. the expectation is
	 * matched if each xpath returns a not empty list of selected nodes
	 */
	@Test
	public void mustExecuteVerbOnAUriWithExcpectationsSetOnBody() {
		Parse t = helper.createFitTestInstance(helper.createFitTestRow("GET",
				"/uri",
				"200",
				"", "/body[text()='text']"));
		fixture.doCells(t);
		assertAllCells(
				t,
				"GET",
				buildResUriLink("/uri"),
				"200",
				"<span class=\"fit_grey\">h1&nbsp;:&nbsp;v1<br/>h2&nbsp;:&nbsp;v2<br/>Content-Type&nbsp;:&nbsp;application/xml</span>",
				"/body[text()='text'] <span class=\"fit_label\">expected</span><hr>&lt;body&gt;text&lt;/body&gt; <span class=\"fit_label\">actual</span>");
		// multiple xpaths can be passed separated by line.separator
		t = helper.createFitTestInstance(helper.createFitTestRow("GET", "/uri",
				"200",
				"",
				"/body[text()='text']" + System.getProperty("line.separator")
						+ "/body"));
		fixture.doCells(t);
		assertAllCells(
				t,
				"GET",
				buildResUriLink("/uri"),
				"200",
				"<span class=\"fit_grey\">h1&nbsp;:&nbsp;v1<br/>h2&nbsp;:&nbsp;v2<br/>Content-Type&nbsp;:&nbsp;application/xml</span>",
				"/body[text()='text']"
						+ System.getProperty("line.separator")
						+ "/body <span class=\"fit_label\">expected</span><hr>&lt;body&gt;text&lt;/body&gt; <span class=\"fit_label\">actual</span>");
		// actual value is displayed unless displayActualOnRight is false
		fixture.setDisplayActualOnRight(false);
		t = helper.createFitTestInstance(helper.createFitTestRow("GET", "/uri",
				"200",
				"",
				"/body[text()='text']"));
		fixture.doCells(t);
		assertAllCells(
				t,
				"GET",
				buildResUriLink("/uri"),
				"200",
				"<span class=\"fit_grey\">h1&nbsp;:&nbsp;v1<br/>h2&nbsp;:&nbsp;v2<br/>Content-Type&nbsp;:&nbsp;application/xml</span>",
				"/body[text()='text']");
	}

	/**
	 * expectations on body that parse into JSON will be processed w/ the JSON
	 * body adapter
	 */
	@Test
	public void mustExecuteVerbOnAUriWithExcpectationsSetOnBodyInJSON() {
		MockRestClient client = new MockRestClient() {
			@Override
			protected RestResponse createRestResponse(RestRequest request) {
				RestResponse rr = new RestResponse();
				rr.addHeader("h1", "v1");
				rr.addHeader("h2", "v2");
				rr.addHeader("Content-Type", "application/json");
				rr.setBody("{\"test\":\"me\"}");
				rr.setStatusCode(200);
				rr.setStatusText("a text");
				rr.setResource(request.getResource());
				return rr;
			}

		};
		fixture.setRestClient(client);
		Parse t = helper.createFitTestInstance(helper.createFitTestRow("GET",
				"/uri",
				"200",
				"", "{\"test\":\"me\"}"));
		fixture.doCells(t);
		assertAllCells(
				t,
				"GET",
				buildResUriLink("/uri"),
				"200",
				"<span class=\"fit_grey\">h1&nbsp;:&nbsp;v1<br/>h2&nbsp;:&nbsp;v2<br/>Content-Type&nbsp;:&nbsp;application/json</span>",
				"{\"test\":\"me\"}");
		// actual value is displayed unless displayActualOnRight is false
		fixture.setDisplayActualOnRight(false);
		t = helper.createFitTestInstance(helper.createFitTestRow("GET", "/uri",
				"200",
				"",
				"{\"test\":\"me\"}"));
		fixture.doCells(t);
		assertAllCells(
				t,
				"GET",
				buildResUriLink("/uri"),
				"200",
				"<span class=\"fit_grey\">h1&nbsp;:&nbsp;v1<br/>h2&nbsp;:&nbsp;v2<br/>Content-Type&nbsp;:&nbsp;application/json</span>",
				"{\"test\":\"me\"}");
	}

	@Test
	public void shoudlExecuteVerbOnAUriWithExcpectationsSetOnHeaders() {
		Parse t = helper.createFitTestInstance(helper.createFitTestRow("GET",
				"/uri",
				"", "",
				""));
		fixture.doCells(t);
		assertEquals("GET", extractCell1(t));
		t = helper.createFitTestInstance(helper.createFitTestRow("POST",
				"/uri", "",
				"", ""));
		fixture.body("<post/>");
		fixture.doCells(t);
		assertEquals("POST", extractCell1(t));
		t = helper.createFitTestInstance(helper.createFitTestRow("DELETE",
				"/uri", "",
				"", ""));
		fixture.doCells(t);
		assertEquals("DELETE", extractCell1(t));
		t = helper.createFitTestInstance(helper.createFitTestRow("PUT", "/uri",
				"",
				"", ""));
		fixture.doCells(t);
		fixture.body("<put/>");
		assertEquals("PUT", extractCell1(t));
	}

	@Test
	public void mustCaptureErrorsOnExpectationsAndDisplayThemInTheSameCell() {
		Parse t = helper.createFitTestInstance(helper.createFitTestRow("GET",
				"/uri",
				"404",
				"", ""));
		fixture.doCells(t);
		assertAllCells(
				t,
				"GET",
				buildResUriLink("/uri"),
				"404 <span class=\"fit_label\">expected</span><hr>200 <span class=\"fit_label\">actual</span><hr>not&nbsp;match:&nbsp;404<br/> <span class=\"fit_label\">errors</span>",
				"<span class=\"fit_grey\">h1&nbsp;:&nbsp;v1<br/>h2&nbsp;:&nbsp;v2<br/>Content-Type&nbsp;:&nbsp;application/xml</span>",
				"<span class=\"fit_grey\">&lt;body&gt;text&lt;/body&gt;</span>");
	}

	/**
	 * <code>| let | content |  body | /body/text() | |</code>
	 */
	@Test
	public void mustAllowStorageInVariablesOfValuesExtractedViaXPathFromBody() {
		Parse t = helper.createFitTestInstance(helper.createFitTestRow("GET",
				"/uri",
				"", "",
				""));
		fixture.doCells(t);
		t = helper.createFitTestInstance(helper.createFitTestRow("let",
				"content",
				"body",
				"/body/text()", "text"));
		fixture.doCells(t);
		assertAllCells(t, "let", "content", "body", "/body/text()", "text");
		assertEquals("text", new Variables().get("content"));
	}

	/**
	 * <code>| let | val | header | h1 : (\w\d) | |</code>
	 */
	@Test
	public void mustAllowStorageInVariablesOfValuesExtractedViaRegexFromHeader() {
		Parse t = helper.createFitTestInstance(helper.createFitTestRow("GET",
				"/uri",
				"", "",
				""));
		fixture.doCells(t);
		t = helper.createFitTestInstance(helper.createFitTestRow("let", "val",
				"header",
				"h1:(\\w\\d)", "v1"));
		fixture.doCells(t);
		assertAllCells(t, "let", "val", "header", "h1:(\\w\\d)", "v1");
		assertEquals("v1", new Variables().get("val"));
	}

	private void assertAllCells(Parse t, String c1, String c2, String c3,
			String c4, String c5) {
		assertEquals(c1, extractCell1(t));
		assertEquals(c2, extractCell2(t).trim());
		assertEquals(c3, extractCell3(t).trim());
		assertEquals(c4, extractCell4(t).trim());
		assertEquals(c5, extractcell5(t).trim());
	}

	private String buildResUriLink(String resUri) {
		return "<a href='" + BASE_URL + resUri + "'>" + resUri + "</a>";
	}

	private String extractCell1(Parse p) {
		return p.body;
	}

	private String extractCell2(Parse p) {
		return p.more.body;
	}

	private String extractCell3(Parse p) {
		return p.more.more.body;
	}

	private String extractCell4(Parse p) {
		return p.more.more.more.body;
	}

	private String extractcell5(Parse p) {
		System.out.println(p.more.more.more.more.body);
		return p.more.more.more.more.body;
	}


}
