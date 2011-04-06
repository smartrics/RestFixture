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
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import smartrics.rest.client.RestClient;
import smartrics.rest.client.RestRequest;
import smartrics.rest.client.RestRequest.Method;
import smartrics.rest.client.RestResponse;
import smartrics.rest.config.Config;
import smartrics.rest.fitnesse.fixture.RestFixture.Runner;
import smartrics.rest.fitnesse.fixture.support.BodyTypeAdapter;
import smartrics.rest.fitnesse.fixture.support.CellFormatter;
import smartrics.rest.fitnesse.fixture.support.CellWrapper;
import smartrics.rest.fitnesse.fixture.support.HeadersTypeAdapter;
import smartrics.rest.fitnesse.fixture.support.JSONBodyTypeAdapter;
import smartrics.rest.fitnesse.fixture.support.RowWrapper;
import smartrics.rest.fitnesse.fixture.support.StatusCodeTypeAdapter;
import smartrics.rest.fitnesse.fixture.support.TextBodyTypeAdapter;
import smartrics.rest.fitnesse.fixture.support.Variables;
import smartrics.rest.fitnesse.fixture.support.XPathBodyTypeAdapter;
import fit.Fixture;

public class RestFixtureTest {

    private static final String BASE_URL = "http://localhost:9090";
    private RestFixture fixture;
    private final Variables variables = new Variables();
    private RestFixtureTestHelper helper;
    private PartsFactory mockPartsFactory;
    private RestClient mockRestClient;
    private RestRequest mockLastRequest;
    @SuppressWarnings("rawtypes")
    private CellFormatter mockCellFormatter;
    private Config config;
    private RestResponse lastResponse;

    @Before
    public void setUp() {
        helper = new RestFixtureTestHelper();

        mockCellFormatter = mock(CellFormatter.class);
        mockRestClient = mock(RestClient.class);
        mockLastRequest = mock(RestRequest.class);
        mockPartsFactory = mock(PartsFactory.class);

        variables.clearAll();

        lastResponse = new RestResponse();
        lastResponse.setStatusCode(200);
        lastResponse.setBody("");
        lastResponse.setResource("/uri");
        lastResponse.setStatusText("OK");
        lastResponse.setTransactionId(0L);

        config = new Config();
    }

    @After
    public void tearDown() {
        config.clear();
    }

    @Test
    public void mustSetConfigNameToDefaultWhenNotSpecifiedAsSecondOptionalParameter_SLIM() {
        fixture = new RestFixture(BASE_URL, "configName");
        assertEquals("configName", fixture.getConfig().getName());
    }
    public void mustSetConfigNameToSpecifiedValueIfOptionalSecondParameterIsSpecified_SLIM() {
        fixture = new RestFixture(BASE_URL, "configName");
        assertEquals("configName", fixture.getConfig().getName());
    }

    @Test
    public void mustUseDefaultHeadersIfDefinedOnNamedConfig() {
        config.add("restfixture.default.headers", "added1 : 1" + System.getProperty("line.separator") + "added2 : 2");
        wireMocks();
        RowWrapper<?> row = helper.createFitTestRow("GET", "/uri", "", "", "");
        fixture = new RestFixture(Runner.OTHER, mockPartsFactory, config, BASE_URL);
        fixture.processRow(row);
        verify(mockLastRequest).addHeaders(fixture.getDefaultHeaders());
    }

    @Test
    public void mustAllowMultilineHeadersWhenSettingHeaders() {
        fixture = new RestFixture(BASE_URL);
        String multilineHeaders = "!-header1:one" + System.getProperty("line.separator") + "header2:two" + System.getProperty("line.separator") + "-!";
        RowWrapper<?> row = helper.createFitTestRow("setHeaders", multilineHeaders);
        fixture.processRow(row);
        assertEquals("one", fixture.getHeaders().get("header1"));
        assertEquals("two", fixture.getHeaders().get("header2"));
    }

    @Test(expected = RuntimeException.class)
    public void mustNotifyClientIfHTTPVerbInFirstCellIsNotSupported() {
        wireMocks();
        RowWrapper<?> row = helper.createFitTestRow("IDONTEXIST", "/uri", "", "", "");
        fixture = new RestFixture(Runner.OTHER, mockPartsFactory, config, "http://service-host:1357");
        fixture.processRow(row);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void mustExecuteVerbOnAUriWithNoExcpectationsOnRestResponseParts() {
        wireMocks();
        when(mockLastRequest.getQuery()).thenReturn("a=b");
        when(mockRestClient.getBaseUrl()).thenReturn(BASE_URL);
        lastResponse.setResource("/uri");
        lastResponse.addHeader("Header", "some/thing");
        lastResponse.setBody("<body />");
        RowWrapper<?> row = helper.createFitTestRow("GET", "/uri?a=b", "", "", "");
        fixture = new RestFixture(Runner.OTHER, mockPartsFactory, config, BASE_URL);

        fixture.processRow(row);

        // correctly builds request
        verify(mockLastRequest).addHeaders(fixture.getHeaders());
        verify(mockLastRequest).setMethod(Method.Get);
        verify(mockLastRequest).setResource("/uri");
        verify(mockLastRequest).setQuery("a=b");
        verify(mockLastRequest).setMultipartFileParameterName("file");
        verify(mockLastRequest).getQuery();
        // correctly executes request
        verify(mockRestClient).setBaseUrl(fixture.getBaseUrl());
        verify(mockRestClient).getBaseUrl();
        verify(mockRestClient).execute(mockLastRequest);
        // correctly formats the response
        verify(mockCellFormatter).asLink(any(CellWrapper.class), eq(BASE_URL + "/uri?a=b"), eq("/uri?a=b"));
        verify(mockCellFormatter).gray("200");
        verify(mockCellFormatter).gray("Header : some/thing");
        verify(mockCellFormatter).gray("<body />");
        
        verifyNoMoreInteractions(mockRestClient);
        verifyNoMoreInteractions(mockCellFormatter);
        verifyNoMoreInteractions(mockLastRequest);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void mustExecutePOSTWithFileUploadWhenFileParamNameIsDefault() {
        wireMocks();
        when(mockLastRequest.getQuery()).thenReturn("");
        when(mockRestClient.getBaseUrl()).thenReturn(BASE_URL);
        lastResponse.setStatusCode(202);
        lastResponse.addHeader("Content-Type", "text/plain; charset=iso-8859-1");
        lastResponse.addHeader("Transfer-Encoding", "chunked");
        String body = "file: { \"resource\" : { \"name\" : \"test post\", \"data\" : \"some data\" } }";
        lastResponse.setBody(body);

        RowWrapper<?> row = helper.createFitTestRow("POST", "/uri", "", "", "file: { \"resource\" : { \"name\" : \"test post\", \"data\" : \"some data\" } }");
        fixture = new RestFixture(Runner.OTHER, mockPartsFactory, config, BASE_URL);

        fixture.processRow(row);

        verify(mockLastRequest).addHeaders(fixture.getHeaders());
        verify(mockLastRequest).getQuery();
        verify(mockLastRequest).setMethod(Method.Post);
        verify(mockLastRequest).setResource("/uri");
        verify(mockLastRequest).setBody("");
        verify(mockLastRequest).setMultipartFileParameterName("file");

        verify(mockRestClient).setBaseUrl(fixture.getBaseUrl());
        verify(mockRestClient).getBaseUrl();
        verify(mockRestClient).execute(mockLastRequest);

        verify(mockCellFormatter).asLink(any(CellWrapper.class), eq(BASE_URL + "/uri"), eq("/uri"));
        verify(mockCellFormatter).gray("202");
        verify(mockCellFormatter).right(isA(CellWrapper.class), isA(TextBodyTypeAdapter.class));
        verify(mockCellFormatter).gray("Content-Type : text/plain; charset=iso-8859-1\nTransfer-Encoding : chunked");

        verifyNoMoreInteractions(mockLastRequest);
        verifyNoMoreInteractions(mockRestClient);
        verifyNoMoreInteractions(mockCellFormatter);

    }

    @Test
    @SuppressWarnings("unchecked")
    public void mustExecuteVerbOnAUriWithExcpectationsSetOnEachResponsePart_ExpectationsMatched() {
        wireMocks();
        when(mockLastRequest.getQuery()).thenReturn("a=b");
        when(mockRestClient.getBaseUrl()).thenReturn(BASE_URL);
        lastResponse.setResource("/uri");
        lastResponse.addHeader("Header", "some/thing");
        lastResponse.setBody("<body />");
        RowWrapper<?> row = helper.createFitTestRow("GET", "/uri?a=b", "200", "Header : some/thing", "//body");
        fixture = new RestFixture(Runner.OTHER, mockPartsFactory, config, BASE_URL);

        fixture.processRow(row);

        // correctly builds request
        verify(mockLastRequest).addHeaders(fixture.getHeaders());
        verify(mockLastRequest).setMethod(Method.Get);
        verify(mockLastRequest).setResource("/uri");
        verify(mockLastRequest).setQuery("a=b");
        verify(mockLastRequest).setMultipartFileParameterName("file");
        verify(mockLastRequest).getQuery();
        // correctly executes request
        verify(mockRestClient).setBaseUrl(fixture.getBaseUrl());
        verify(mockRestClient).getBaseUrl();
        verify(mockRestClient).execute(mockLastRequest);
        // correctly formats the response
        verify(mockCellFormatter).asLink(isA(CellWrapper.class), eq(BASE_URL + "/uri?a=b"), eq("/uri?a=b"));
        // status code cell
        verify(mockCellFormatter).right(isA(CellWrapper.class), isA(StatusCodeTypeAdapter.class));
        verify(mockCellFormatter).right(isA(CellWrapper.class), isA(HeadersTypeAdapter.class));
        verify(mockCellFormatter).right(isA(CellWrapper.class), isA(XPathBodyTypeAdapter.class));
        
        verifyNoMoreInteractions(mockRestClient);
        verifyNoMoreInteractions(mockCellFormatter);
        verifyNoMoreInteractions(mockLastRequest);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mustExecuteVerbOnAUriWithExcpectationsSetOnEachResponsePart_ExpectationsNotMatched() {
        wireMocks();
        when(mockLastRequest.getQuery()).thenReturn("a=b");
        when(mockRestClient.getBaseUrl()).thenReturn(BASE_URL);
        lastResponse.setResource("/uri");
        lastResponse.addHeader("Header", "some/thing");
        lastResponse.setBody("<body />");
        RowWrapper<?> row = helper.createFitTestRow("GET", "/uri?a=b", "201", "Header : someother/thing", "//count");
        fixture = new RestFixture(Runner.OTHER, mockPartsFactory, config, BASE_URL);

        fixture.processRow(row);

        // correctly builds request
        verify(mockLastRequest).addHeaders(fixture.getHeaders());
        verify(mockLastRequest).setMethod(Method.Get);
        verify(mockLastRequest).setResource("/uri");
        verify(mockLastRequest).setQuery("a=b");
        verify(mockLastRequest).setMultipartFileParameterName("file");
        verify(mockLastRequest).getQuery();
        // correctly executes request
        verify(mockRestClient).setBaseUrl(fixture.getBaseUrl());
        verify(mockRestClient).getBaseUrl();
        verify(mockRestClient).execute(mockLastRequest);
        // correctly formats the response
        verify(mockCellFormatter).asLink(isA(CellWrapper.class), eq(BASE_URL + "/uri?a=b"), eq("/uri?a=b"));
        // status code cell
        verify(mockCellFormatter).wrong(isA(CellWrapper.class), isA(StatusCodeTypeAdapter.class));
        verify(mockCellFormatter).wrong(isA(CellWrapper.class), isA(HeadersTypeAdapter.class));
        verify(mockCellFormatter).wrong(isA(CellWrapper.class), isA(XPathBodyTypeAdapter.class));

        verifyNoMoreInteractions(mockRestClient);
        verifyNoMoreInteractions(mockCellFormatter);
        verifyNoMoreInteractions(mockLastRequest);
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void mustMatchRequestsWithNoBodyExpressedAsNoBodyString() {
        wireMocks();
        when(mockRestClient.getBaseUrl()).thenReturn(BASE_URL);
        lastResponse.setResource("/uri");
        lastResponse.setBody("");
        RowWrapper<?> row = helper.createFitTestRow("DELETE", "/uri", "", "", "no-body");
        fixture = new RestFixture(Runner.OTHER, mockPartsFactory, config, BASE_URL);

        fixture.processRow(row);

        // correctly builds request
        verify(mockLastRequest).setMethod(Method.Delete);
        verify(mockLastRequest).addHeaders(fixture.getHeaders());
        verify(mockLastRequest).setResource("/uri");
        verify(mockLastRequest).setMultipartFileParameterName("file");
        verify(mockLastRequest).getQuery();
        // correctly executes request
        verify(mockRestClient).setBaseUrl(fixture.getBaseUrl());
        verify(mockRestClient).getBaseUrl();
        verify(mockRestClient).execute(mockLastRequest);
        // correctly formats the response
        verify(mockCellFormatter).asLink(any(CellWrapper.class), eq(BASE_URL + "/uri"), eq("/uri"));
        verify(mockCellFormatter).gray("200");
        // matches no-body and format it with right - first arg should be
        // row.getCell(4) but mockito doesn't like it
        verify(mockCellFormatter).right(isA(CellWrapper.class), isA(BodyTypeAdapter.class));

        verifyNoMoreInteractions(mockRestClient);
        verifyNoMoreInteractions(mockCellFormatter);
        verifyNoMoreInteractions(mockLastRequest);
    }

    /**
     * expectations on headers are verified by checking that the expected list
     * of headers is a subset of the actual list of headers
     */
    @SuppressWarnings("unchecked")
    @Test
    public void mustExecuteVerbOnAUriWithExcpectationsSetOnHeaders() {
        wireMocks();
        when(mockLastRequest.getQuery()).thenReturn("");
        when(mockRestClient.getBaseUrl()).thenReturn(BASE_URL);
        lastResponse.setResource("/uri");
        lastResponse.addHeader("Header1", "some/thing/1");
        lastResponse.addHeader("Header2", "some/thing/2");
        lastResponse.addHeader("Header3", "some/thing/3");
        lastResponse.setBody("<body />");
        RowWrapper<?> row = helper.createFitTestRow("GET", "/uri", "", "Header2 : some/thing/2", "");
        fixture = new RestFixture(Runner.OTHER, mockPartsFactory, config, BASE_URL);

        fixture.processRow(row);

        // correctly builds request
        verify(mockCellFormatter).right(isA(CellWrapper.class), isA(HeadersTypeAdapter.class));
    }

    @Test
    public void mustExpectOnlySupportedVerbOnFirstCell() {
        wireMocks();
        fixture = new RestFixture(Runner.OTHER, mockPartsFactory, config, BASE_URL);

        RowWrapper<?> row = helper.createFitTestRow("GET", "/uri", "", "", "");
        fixture.processRow(row);
        verify(mockLastRequest).setMethod(Method.Get);

        row = helper.createFitTestRow("DELETE", "/uri", "", "", "");
        fixture.processRow(row);
        verify(mockLastRequest).setMethod(Method.Delete);

        row = helper.createFitTestRow("setBody", "<body />");
        fixture.processRow(row);

        row = helper.createFitTestRow("POST", "/uri", "", "", "");
        fixture.processRow(row);
        verify(mockLastRequest).setMethod(Method.Delete);

        row = helper.createFitTestRow("PUT", "/uri", "", "", "");
        fixture.processRow(row);
        verify(mockLastRequest).setMethod(Method.Delete);

    }

    /**
     * expectations on body are verified by delegating to the correct body type
     * adapter.
     * 
     * the body type adapter is inferred looking at the content type of the
     * response.
     * 
     * If content type is some form of XML/JSON then expectations are verified
     * using XPath. If it's text, then using regexes
     * 
     * check passes if the type adapter returns a non empty match (a non empty
     * node list or a match, in case of regexes)
     */
    @Test
    @SuppressWarnings("unchecked")
    public void mustExecuteVerbOnAUriWithExcpectationsSetOnBody_XML() {
        wireMocks();
        when(mockLastRequest.getQuery()).thenReturn("");
        when(mockRestClient.getBaseUrl()).thenReturn(BASE_URL);
        lastResponse.setResource("/uri");
        lastResponse.addHeader("Content-Type", "application/xml");
        lastResponse.setBody("<body />");

        RowWrapper<?> row = helper.createFitTestRow("GET", "/uri", "", "", "//body");
        fixture = new RestFixture(Runner.OTHER, mockPartsFactory, config, BASE_URL);

        fixture.processRow(row);

        // correctly builds request
        verify(mockCellFormatter).right(isA(CellWrapper.class), isA(XPathBodyTypeAdapter.class));
    }

    /**
     * expectations on body that parse into text will be processed w/ the Text
     * body adapter. content type expected is text/plain
     */
    @Test
    @SuppressWarnings("unchecked")
    public void mustExecuteVerbOnAUriWithExcpectationsSetOnBody_TEXT() {
        wireMocks();
        when(mockLastRequest.getQuery()).thenReturn("");
        when(mockRestClient.getBaseUrl()).thenReturn(BASE_URL);
        lastResponse.setResource("/uri");
        lastResponse.addHeader("Content-Type", "text/plain");
        lastResponse.setBody("in AD 1492 Columbus discovered America");

        RowWrapper<?> row = helper.createFitTestRow("GET", "/uri", "", "", ".+AD \\d\\d\\d\\d.+");
        fixture = new RestFixture(Runner.OTHER, mockPartsFactory, config, BASE_URL);

        fixture.processRow(row);

        // correctly builds request
        verify(mockCellFormatter).right(isA(CellWrapper.class), isA(TextBodyTypeAdapter.class));
    }

    /**
     * expectations on body that parse into JSON will be processed w/ the JSON
     * body adapter. content type expected is application/json
     */
    @Test
    @SuppressWarnings("unchecked")
    public void mustExecuteVerbOnAUriWithExcpectationsSetOnBody_JSON() {
        wireMocks();
        when(mockLastRequest.getQuery()).thenReturn("");
        when(mockRestClient.getBaseUrl()).thenReturn(BASE_URL);
        lastResponse.setResource("/uri");
        lastResponse.addHeader("Content-Type", "application/json");
        lastResponse.setBody("{\"test\":\"me\"}");

        RowWrapper<?> row = helper.createFitTestRow("GET", "/uri", "", "", "/test[text()='me']");
        fixture = new RestFixture(Runner.OTHER, mockPartsFactory, config, BASE_URL);

        fixture.processRow(row);

        // correctly builds request
        verify(mockCellFormatter).right(isA(CellWrapper.class), isA(JSONBodyTypeAdapter.class));
    }

    @Test
    public void mustUseValueOnSymbolMapIfNoVariableIsFoundInTheLocalMap() {
        Fixture.setSymbol("fred", "bloggs");
        wireMocks();
        when(mockLastRequest.getQuery()).thenReturn("");
        when(mockRestClient.getBaseUrl()).thenReturn(BASE_URL);
        RowWrapper<?> row = helper.createFitTestRow("GET", "/uri/%fred%", "", "", "");
        fixture = new RestFixture(Runner.OTHER, mockPartsFactory, config, BASE_URL);

        fixture.processRow(row);

        // correctly builds request
        verify(mockLastRequest).setResource("/uri/bloggs");
    }

    @Test
    public void mustSetValueOnSymbolMapIfVariableNameStartsWith$() {
        wireMocks();
        when(mockLastRequest.getQuery()).thenReturn("");
        when(mockRestClient.getBaseUrl()).thenReturn(BASE_URL);
        lastResponse.setBody("<body>text</body>");

        fixture = new RestFixture(Runner.OTHER, mockPartsFactory, config, BASE_URL);

        RowWrapper<?> row = helper.createFitTestRow("GET", "/uri", "", "", "");
        fixture.processRow(row);
        row = helper.createFitTestRow("let", "$content", "body", "/body/text()", "text");
        fixture.processRow(row);

        // correctly builds request
        assertNull(new Variables().get("content"));
        assertNull(new Variables().get("$content"));
        assertEquals("text", Fixture.getSymbol("content"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mustCaptureErrorsOnExpectationsAndDisplayThemInTheSameCell() {
        wireMocks();
        when(mockLastRequest.getQuery()).thenReturn("");
        when(mockRestClient.getBaseUrl()).thenReturn(BASE_URL);
        lastResponse.setStatusCode(201);
        lastResponse.setStatusText("OK");

        RowWrapper<?> row = helper.createFitTestRow("GET", "/uri", "404", "", "");
        fixture = new RestFixture(Runner.OTHER, mockPartsFactory, config, BASE_URL);

        fixture.processRow(row);

        // correctly builds request
        verify(mockCellFormatter).wrong(isA(CellWrapper.class), isA(StatusCodeTypeAdapter.class));

    }

    /**
     * <code>| let | content |  body | /body/text() | |</code>
     */
    @Test
    public void mustAllowGlobalStorageOfValuesExtractedViaXPathFromBody() {
        wireMocks();
        when(mockRestClient.getBaseUrl()).thenReturn(BASE_URL);
        lastResponse.setBody("<body>text</body>");

        fixture = new RestFixture(Runner.OTHER, mockPartsFactory, config, BASE_URL);

        RowWrapper<?> row = helper.createFitTestRow("GET", "/uri", "", "", "");
        fixture.processRow(row);
        row = helper.createFitTestRow("let", "$content", "body", "/body/text()", "text");
        fixture.processRow(row);
        assertEquals("text", Fixture.getSymbol("content"));
        row = helper.createFitTestRow("let", "content", "body", "/body/text()", "text");
        fixture.processRow(row);
        assertEquals("text", new Variables().get("content"));

    }

    /**
     * <code>| let | content |  body | count(body) | |</code>
     */
    @Test
    public void mustAllowStorageOfValuesExtractedViaXPathsReturningStringValues() {
        wireMocks();
        when(mockRestClient.getBaseUrl()).thenReturn(BASE_URL);
        lastResponse.setBody("<body>text</body>");

        fixture = new RestFixture(Runner.OTHER, mockPartsFactory, config, BASE_URL);

        RowWrapper<?> row = helper.createFitTestRow("GET", "/uri", "", "", "");
        fixture.processRow(row);
        row = helper.createFitTestRow("let", "$count", "body", "count(body)", "1");
        fixture.processRow(row);
        assertEquals("1", Fixture.getSymbol("count"));
        row = helper.createFitTestRow("let", "count", "body", "count(body)", "1");
        fixture.processRow(row);
        assertEquals("1", new Variables().get("count"));
    }

    /**
     * <code>| let | val | header | h1 : (\w\d) | |</code>
     */
    @Test
    public void mustAllowGlobalStorageOfValuesExtractedViaRegexFromHeader() {
        wireMocks();
        when(mockRestClient.getBaseUrl()).thenReturn(BASE_URL);
        lastResponse.setBody("<body>text</body>");
        lastResponse.addHeader("header", "value1");

        fixture = new RestFixture(Runner.OTHER, mockPartsFactory, config, BASE_URL);

        RowWrapper<?> row = helper.createFitTestRow("GET", "/uri", "", "", "");
        fixture.processRow(row);
        row = helper.createFitTestRow("let", "keytovalue", "header", "header:(.+\\d)", "value1");
        fixture.processRow(row);
        assertEquals("value1", new Variables().get("keytovalue"));
    }

    /**
     * tests for slim support
     */

    @Test
    public void testConstructingWithOneArgShoudlBeTheSUTUri() {
        String uri = "http://localhost:9090";
        RestFixture f = new RestFixture(uri);
        assertEquals(uri, f.getBaseUrl());
    }

    @Test
    public void testConstructingWithOneArgShoudlStripAnyTagAndSetTheSUTUri() {
        String uri = "http://localhost:9090";
        String taggedUri = "<sometag att='1'>" + uri + "</sometag>";
        RestFixture f = new RestFixture(taggedUri);
        assertEquals(uri, f.getBaseUrl());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructingWithOneFailsIfArgIsNotAnUri() {
        String uri = "rubbish";
        new RestFixture(uri);
    }

    /**
     * helper methods
     */

    @SuppressWarnings("unchecked")
    private void wireMocks() {
        when(mockPartsFactory.buildRestClient(config)).thenReturn(mockRestClient);
        when(mockPartsFactory.buildRestRequest()).thenReturn(mockLastRequest);
        when(mockRestClient.execute(mockLastRequest)).thenReturn(lastResponse);
        when(mockPartsFactory.buildCellFormatter(any(RestFixture.Runner.class))).thenReturn(mockCellFormatter);
    }

}
