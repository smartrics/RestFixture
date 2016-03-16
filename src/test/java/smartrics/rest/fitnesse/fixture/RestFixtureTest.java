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
package smartrics.rest.fitnesse.fixture;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import smartrics.rest.client.RestClient;
import smartrics.rest.client.RestRequest;
import smartrics.rest.client.RestRequest.Method;
import smartrics.rest.client.RestResponse;
import smartrics.rest.fitnesse.fixture.RestFixture.Runner;
import smartrics.rest.fitnesse.fixture.support.BodyTypeAdapter;
import smartrics.rest.fitnesse.fixture.support.CellFormatter;
import smartrics.rest.fitnesse.fixture.support.CellWrapper;
import smartrics.rest.fitnesse.fixture.support.Config;
import smartrics.rest.fitnesse.fixture.support.ContentType;
import smartrics.rest.fitnesse.fixture.support.FitVariables;
import smartrics.rest.fitnesse.fixture.support.HeadersTypeAdapter;
import smartrics.rest.fitnesse.fixture.support.JavascriptException;
import smartrics.rest.fitnesse.fixture.support.RowWrapper;
import smartrics.rest.fitnesse.fixture.support.StatusCodeTypeAdapter;
import smartrics.rest.fitnesse.fixture.support.StringTypeAdapter;
import fit.Fixture;

/**
 * Tests for the RestFixture class.
 * 
 * @author smartrics
 * 
 */
public class RestFixtureTest {

    private static final String BASE_URL = "http://localhost:9090";
    private RestFixture fixture;
    private final FitVariables variables = new FitVariables();
    private RestFixtureTestHelper helper;
    private PartsFactory mockPartsFactory;
    private RestClient mockRestClient;
    private RestRequest mockLastRequest;
    @SuppressWarnings("rawtypes")
    private CellFormatter mockCellFormatter;
    private Config config;
    private RestResponse lastResponse;
    private BodyTypeAdapter mockBodyTypeAdapter;

    @Before
    public void setUp() {
        helper = new RestFixtureTestHelper();

        mockBodyTypeAdapter = mock(BodyTypeAdapter.class);
        mockCellFormatter = mock(CellFormatter.class);
        mockRestClient = mock(RestClient.class);
        mockLastRequest = mock(RestRequest.class);
        mockPartsFactory = mock(PartsFactory.class);

        variables.clearAll();

        lastResponse = new RestResponse();
        lastResponse.setStatusCode(200);
        lastResponse.setRawBody("".getBytes());
        lastResponse.setResource("/uri");
        lastResponse.setStatusText("OK");
        lastResponse.setTransactionId(0L);

        config = Config.getConfig();

        ContentType.resetDefaultMapping();

        helper.wireMocks(config, mockPartsFactory, mockRestClient, mockLastRequest, lastResponse, mockCellFormatter, mockBodyTypeAdapter);
        fixture = new RestFixture(mockPartsFactory, BASE_URL, Config.DEFAULT_CONFIG_NAME);
        fixture.initialize(Runner.OTHER);
    }

    @After
    public void tearDown() {
        config.clear();
    }

    @SuppressWarnings("unchecked")
	@Test
    public void setBodyShouldRenderResolvedSymbols() {
        Fixture.setSymbol("name", "one");
        RowWrapper<?> row = helper.createTestRow("setBody", "name is %name%");
        when(mockCellFormatter.fromRaw("name is %name%")).thenReturn("name is %name%");
        fixture.processRow(row);
        verify(row.getCell(1)).body("name is one");
        verify(mockCellFormatter).right(isA(CellWrapper.class), isA(StringTypeAdapter.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void setBodyShouldNotRenderEncodedSymbols() {
        Fixture.setSymbol("name", "one");
        RowWrapper<?> row = helper.createTestRow("setBody", "this %name% is encoded %7B%22myvar%22%7D");
        when(mockCellFormatter.fromRaw("this %name% is encoded %7B%22myvar%22%7D")).thenReturn("this %name% is encoded " +
          "%7B%22myvar%22%7D");
        fixture.processRow(row);
        verify(row.getCell(1)).body("this one is encoded %7B%22myvar%22%7D");
        verify(mockCellFormatter).right(isA(CellWrapper.class), isA(StringTypeAdapter.class));
    }

    @Test
    public void mustSetConfigNameToDefaultWhenNotSpecifiedAsSecondOptionalParameter_SLIM() {
        fixture = new RestFixture(BASE_URL, "configName");
        assertEquals("configName", fixture.getConfig().getName());
    }

    @Test
    public void mustSetConfigNameToSpecifiedValueIfOptionalSecondParameterIsSpecified_SLIM() {
        fixture = new RestFixture(BASE_URL, "configName");
        assertEquals("configName", fixture.getConfig().getName());
    }

    @Test
    public void mustUseDefaultHeadersIfDefinedOnNamedConfig() {
        config.add("restfixture.default.headers", "added1 : 1" + System.getProperty("line.separator") + "added2 : 2");
        RowWrapper<?> row = helper.createTestRow("GET", "/uri", "", "", "");
        fixture.processRow(row);
        verify(mockLastRequest).addHeaders(fixture.getDefaultHeaders());
    }

    @Test
    public void mustAllowMultilineHeadersWhenSettingHeaders() {
        String multilineHeaders = "!-header1:one \n header2:two \nheader3 : with:colon \nheader4 : \n header5 -!";
        RowWrapper<?> row = helper.createTestRow("setHeaders", multilineHeaders);
        fixture.processRow(row);
        assertEquals("one", fixture.getHeaders().get("header1"));
        assertEquals("two", fixture.getHeaders().get("header2"));
        assertEquals("with:colon", fixture.getHeaders().get("header3"));
        assertEquals("", fixture.getHeaders().get("header4"));
        assertEquals("", fixture.getHeaders().get("header5"));
    }

    @Test
    public void mustExpandSymbolsWhenSettingMultilineHeaders() {
        Fixture.setSymbol("hval1", "one");
        Fixture.setSymbol("hval2", "two");
        String multilineHeaders = "!-header1:%hval1% \n header2:%hval2% \nheader3 : with:colon \nheader4 : \n header5 -!";
        RowWrapper<?> row = helper.createTestRow("setHeaders", multilineHeaders);
        fixture.processRow(row);
        assertEquals("one", fixture.getHeaders().get("header1"));
        assertEquals("two", fixture.getHeaders().get("header2"));
        assertEquals("with:colon", fixture.getHeaders().get("header3"));
        assertEquals("", fixture.getHeaders().get("header4"));
        assertEquals("", fixture.getHeaders().get("header5"));
    }

    @Test
    public void mustAllowSettingHeaders() {
        String header = "header1:one";
        RowWrapper<?> row = helper.createTestRow("setHeader", header);
        fixture.processRow(row);
        assertEquals("one", fixture.getHeaders().get("header1"));
    }

    @Test
    public void mustAllowAddingMultipleHeaders() {
        RowWrapper<?> row = helper.createTestRow("addHeader", "header1:one");
        fixture.processRow(row);
        row = helper.createTestRow("addHeader", "header2:two");
        fixture.processRow(row);

        assertEquals("one", fixture.getHeaders().get("header1"));
        assertEquals("two", fixture.getHeaders().get("header2"));
    }

    @Test
    public void mustAllowAddingHeaders() {
        String header = "header1:one";
        RowWrapper<?> row = helper.createTestRow("addHeader", header);
        fixture.processRow(row);
        assertEquals("one", fixture.getHeaders().get("header1"));
    }

    @Test
    public void mustExpandSymbolSetWithLetWhenSettingHeaders() {
        when(mockLastRequest.getQuery()).thenReturn("");
        when(mockRestClient.getBaseUrl()).thenReturn(BASE_URL);
        lastResponse.setRawBody("<body>1234</body>".getBytes());

        RowWrapper<?> row = helper.createTestRow("GET", "/uri", "", "", "");
        fixture.processRow(row);
        row = helper.createTestRow("let", "headerValue", "body", "/body/text()", "1234");
        fixture.processRow(row);

        row = helper.createTestRow("setHeader", "header1:%headerValue%");
        fixture.processRow(row);
        assertEquals("1234", fixture.getHeaders().get("header1"));
    }

    @Test
    public void mustExpandSymbolWhenSettingHeaders() {
        Fixture.setSymbol("hval", "one");
        String header = "headerWithSymbol:%hval%";
        RowWrapper<?> row = helper.createTestRow("setHeader", header);
        fixture.processRow(row);
        assertEquals("one", fixture.getHeaders().get("headerWithSymbol"));
    }

    @Test
    public void mustRenderSymbolValueWhenSettingHeaders() {
    	when(mockCellFormatter.gray("headerWithSymbol:one")).thenReturn("gray(headerWithSymbol:one)");
        Fixture.setSymbol("hval", "one");
        String header = "headerWithSymbol:%hval%";
        RowWrapper<?> row = helper.createTestRow("setHeader", header);
        fixture.processRow(row);
        verify(row.getCell(1)).text();
        verify(row.getCell(1)).body("gray(headerWithSymbol:one)");
        
        verifyNoMoreInteractions(row.getCell(1));
    }

    @Test(expected = RuntimeException.class)
    public void mustNotifyClientIfHTTPVerbInFirstCellIsNotSupported() {
        RowWrapper<?> row = helper.createTestRow("IDONTEXIST", "/uri", "", "", "");
        fixture.processRow(row);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mustNotifyClientIfExpectedBodyCellHasNotBeenSpecified() {
        when(mockRestClient.getBaseUrl()).thenReturn(BASE_URL);
        RowWrapper<?> row = helper.createTestRow("GET", "/uri", "", "");
        fixture.processRow(row);
        verify(mockCellFormatter).exception(any(CellWrapper.class), eq("Execution of Get caused exception 'You must specify a body cell'"));
        verify(mockCellFormatter).asLink(any(CellWrapper.class), eq("/uri"), eq(BASE_URL + "/uri"), eq("/uri"));
        verify(mockCellFormatter).gray("200");
        verifyNoMoreInteractions(mockCellFormatter);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mustNotifyClientIfExpectedHeadersCellHasNotBeenSpecified() {
        when(mockRestClient.getBaseUrl()).thenReturn(BASE_URL);
        RowWrapper<?> row = helper.createTestRow("GET", "/uri", "");
        fixture.processRow(row);
        verify(mockCellFormatter).exception(any(CellWrapper.class), eq("Execution of Get caused exception 'You must specify a headers cell'"));
        verify(mockCellFormatter).asLink(any(CellWrapper.class), eq("/uri"), eq(BASE_URL + "/uri"), eq("/uri"));
        verify(mockCellFormatter).gray("200");
        verifyNoMoreInteractions(mockCellFormatter);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mustNotifyClientIfExpectedStatusCodeCellHasNotBeenSpecified() {
        when(mockRestClient.getBaseUrl()).thenReturn(BASE_URL);
        RowWrapper<?> row = helper.createTestRow("GET", "/uri");
        fixture.processRow(row);
        verify(mockCellFormatter).exception(any(CellWrapper.class), eq("Execution of Get caused exception 'You must specify a status code cell'"));
        verify(mockCellFormatter).asLink(any(CellWrapper.class), eq("/uri"), eq(BASE_URL + "/uri"), eq("/uri"));
        verifyNoMoreInteractions(mockCellFormatter);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mustExecuteVerbByOverridingBaseUriIfRowResourceIsAbsoluteUri() {
        when(mockBodyTypeAdapter.toString()).thenReturn("returned <body />");
        when(mockLastRequest.getQuery()).thenReturn("a=b");
        when(mockRestClient.getBaseUrl()).thenReturn("http://some.url");
        lastResponse.setResource("/path/to/resource");
        lastResponse.addHeader("Header", "some/thing");
        lastResponse.setBody("<body />");
        final String theUrl = "http://some.url/path/to/resource?a=b";
        RowWrapper<?> row = helper.createTestRow("GET", theUrl, "", "", "");

        fixture.processRow(row);

        // correctly builds request
        verify(mockLastRequest).addHeaders(fixture.getHeaders());
        verify(mockLastRequest).setMethod(Method.Get);
        verify(mockLastRequest).setResource("/path/to/resource");
        verify(mockLastRequest).setQuery("a=b");
        verify(mockLastRequest).setMultipartFileParameterName("file");
        verify(mockLastRequest).getQuery();
        // correctly executes request
        verify(mockRestClient).setBaseUrl("http://some.url");
        verify(mockRestClient).getBaseUrl();
        verify(mockRestClient).execute(mockLastRequest);

        verify(mockCellFormatter).asLink(any(CellWrapper.class), eq(theUrl), eq(theUrl), eq("/path/to/resource?a=b"));
        verify(mockCellFormatter).gray("200");
        verify(mockCellFormatter).gray("Header : some/thing");
        verify(mockCellFormatter).gray("returned <body />");

        verify(mockBodyTypeAdapter).setContext(isA(Map.class));
        verify(mockBodyTypeAdapter).set("<body />");

        verifyNoMoreInteractions(mockRestClient);
        verifyNoMoreInteractions(mockCellFormatter);
        verifyNoMoreInteractions(mockBodyTypeAdapter);

    }

    @Test
    @SuppressWarnings("unchecked")
    public void mustExecuteVerbOnAUriWithNoExcpectationsOnRestResponseParts() {
        when(mockBodyTypeAdapter.toString()).thenReturn("returned <body />");
        when(mockLastRequest.getQuery()).thenReturn("a=b");
        when(mockRestClient.getBaseUrl()).thenReturn(BASE_URL);
        lastResponse.setResource("/uri");
        lastResponse.addHeader("Header", "some/thing");
        lastResponse.setBody("<body />");
        RowWrapper<?> row = helper.createTestRow("GET", "/uri?a=b", "", "", "");

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
        verify(mockCellFormatter).asLink(any(CellWrapper.class), eq("/uri?a=b"), eq(BASE_URL + "/uri?a=b"), eq("/uri?a=b"));
        verify(mockCellFormatter).gray("200");
        verify(mockCellFormatter).gray("Header : some/thing");
        verify(mockCellFormatter).gray("returned <body />");

        verify(mockBodyTypeAdapter).setContext(isA(Map.class));
        verify(mockBodyTypeAdapter).set("<body />");

        verifyNoMoreInteractions(mockRestClient);
        verifyNoMoreInteractions(mockCellFormatter);
        verifyNoMoreInteractions(mockBodyTypeAdapter);

    }

    @Test
    @SuppressWarnings("unchecked")
    public void mustExecutePOSTWithFileUploadWhenFileParamNameIsDefault() throws Exception {
        String body = "file: { \"resource\" : { \"name\" : \"test post\", \"data\" : \"some data\" } }";
        when(mockBodyTypeAdapter.toString()).thenReturn(body);
        when(mockBodyTypeAdapter.parse(body)).thenReturn(body);
        when(mockBodyTypeAdapter.equals(body, body)).thenReturn(true);
        when(mockLastRequest.getQuery()).thenReturn("");
        when(mockRestClient.getBaseUrl()).thenReturn(BASE_URL);
        lastResponse.setStatusCode(202);
        lastResponse.addHeader("Content-Type", "text/plain; charset=iso-8859-1");
        lastResponse.addHeader("Transfer-Encoding", "chunked");
        lastResponse.setBody(body);

        RowWrapper<?> row = helper.createTestRow("POST", "/uri", "", "", body);

        fixture.processRow(row);

        verify(mockLastRequest).addHeaders(fixture.getHeaders());
        verify(mockLastRequest).getQuery();
        verify(mockLastRequest).setMethod(Method.Post);
        verify(mockLastRequest).setResource("/uri");
        verify(mockLastRequest).setBody("");
        verify(mockLastRequest).setResourceUriEscaped(false);
        verify(mockLastRequest).setFollowRedirect(true);
        verify(mockLastRequest).setMultipartFileParameterName("file");

        verify(mockRestClient).setBaseUrl(fixture.getBaseUrl());
        verify(mockRestClient).getBaseUrl();
        verify(mockRestClient).execute(mockLastRequest);

        verify(mockCellFormatter).asLink(any(CellWrapper.class), eq("/uri"), eq(BASE_URL + "/uri"), eq("/uri"));
        verify(mockCellFormatter).gray("202");
        verify(mockCellFormatter).right(isA(CellWrapper.class), eq(mockBodyTypeAdapter));
        verify(mockCellFormatter).gray("Content-Type : text/plain; charset=iso-8859-1\nTransfer-Encoding : chunked");

        verify(mockBodyTypeAdapter).setContext(isA(Map.class));
        verify(mockBodyTypeAdapter).set(body);
        verify(mockBodyTypeAdapter).parse(body);
        verify(mockBodyTypeAdapter).equals(body, body);

        verifyNoMoreInteractions(mockBodyTypeAdapter);
        verifyNoMoreInteractions(mockLastRequest);
        verifyNoMoreInteractions(mockRestClient);
        verifyNoMoreInteractions(mockCellFormatter);

    }

    @Test
    @SuppressWarnings("unchecked")
    public void mustExecuteVerbOnAUriWithExcpectationsSetOnEachResponsePart_ExpectationsMatched() throws Exception {
        when(mockBodyTypeAdapter.toString()).thenReturn("returned <body />");
        when(mockLastRequest.getQuery()).thenReturn("a=b");
        when(mockRestClient.getBaseUrl()).thenReturn(BASE_URL);
        when(mockBodyTypeAdapter.parse("//body")).thenReturn("//body");
        when(mockBodyTypeAdapter.equals("//body", "<body />")).thenReturn(true);
        lastResponse.setResource("/uri");
        lastResponse.addHeader("Header", "some/thing");
        lastResponse.setBody("<body />");
        RowWrapper<?> row = helper.createTestRow("GET", "/uri?a=b", "200", "Header : some/thing", "//body");

        fixture.processRow(row);

        // correctly builds request
        verify(mockLastRequest).addHeaders(fixture.getHeaders());
        verify(mockLastRequest).setMethod(Method.Get);
        verify(mockLastRequest).setResource("/uri");
        verify(mockLastRequest).setQuery("a=b");
        verify(mockLastRequest).setMultipartFileParameterName("file");
        verify(mockLastRequest).setResourceUriEscaped(false);
        verify(mockLastRequest).setFollowRedirect(true);
        verify(mockLastRequest).getQuery();
        // correctly executes request
        verify(mockRestClient).setBaseUrl(fixture.getBaseUrl());
        verify(mockRestClient).getBaseUrl();
        verify(mockRestClient).execute(mockLastRequest);
        // correctly formats the response
        verify(mockCellFormatter).asLink(isA(CellWrapper.class), eq("/uri?a=b"), eq(BASE_URL + "/uri?a=b"), eq("/uri?a=b"));
        // status code cell
        verify(mockCellFormatter).right(isA(CellWrapper.class), isA(StatusCodeTypeAdapter.class));
        verify(mockCellFormatter).right(isA(CellWrapper.class), isA(HeadersTypeAdapter.class));
        verify(mockCellFormatter).right(isA(CellWrapper.class), eq(mockBodyTypeAdapter));
        verify(mockBodyTypeAdapter).setContext(isA(Map.class));
        verify(mockBodyTypeAdapter).set("<body />");
        verify(mockBodyTypeAdapter).parse("//body");
        verify(mockBodyTypeAdapter).equals("//body", "<body />");

        verifyNoMoreInteractions(mockBodyTypeAdapter);
        verifyNoMoreInteractions(mockRestClient);
        verifyNoMoreInteractions(mockCellFormatter);
        verifyNoMoreInteractions(mockLastRequest);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mustExecuteVerbOnAUriWithExcpectationsSetOnEachResponsePart_ExpectationsNotMatched() throws Exception {
        when(mockLastRequest.getQuery()).thenReturn("a=b");
        when(mockRestClient.getBaseUrl()).thenReturn(BASE_URL);

        when(mockBodyTypeAdapter.parse("//count")).thenReturn("//count");
        when(mockBodyTypeAdapter.equals("//count", "<body />")).thenReturn(false);

        lastResponse.setResource("/uri");
        lastResponse.addHeader("Header", "some/thing");
        lastResponse.setBody("<body />");
        RowWrapper<?> row = helper.createTestRow("GET", "/uri?a=b", "201", "Header : someother/thing", "//count");

        fixture.processRow(row);

        // correctly builds request
        verify(mockLastRequest).addHeaders(fixture.getHeaders());
        verify(mockLastRequest).setMethod(Method.Get);
        verify(mockLastRequest).setResource("/uri");
        verify(mockLastRequest).setQuery("a=b");
        verify(mockLastRequest).setMultipartFileParameterName("file");
        verify(mockLastRequest).getQuery();
        verify(mockLastRequest).setFollowRedirect(true);
        verify(mockLastRequest).setResourceUriEscaped(false);
        // correctly executes request
        verify(mockRestClient).setBaseUrl(fixture.getBaseUrl());
        verify(mockRestClient).getBaseUrl();
        verify(mockRestClient).execute(mockLastRequest);
        // correctly formats the response
        verify(mockCellFormatter).asLink(isA(CellWrapper.class), eq("/uri?a=b"), eq(BASE_URL + "/uri?a=b"), eq("/uri?a=b"));
        // status code cell
        verify(mockCellFormatter).wrong(isA(CellWrapper.class), isA(StatusCodeTypeAdapter.class));
        verify(mockCellFormatter).wrong(isA(CellWrapper.class), isA(HeadersTypeAdapter.class));
        verify(mockCellFormatter).wrong(isA(CellWrapper.class), eq(mockBodyTypeAdapter));

        verify(mockBodyTypeAdapter).setContext(isA(Map.class));
        verify(mockBodyTypeAdapter).set("<body />");
        verify(mockBodyTypeAdapter).parse("//count");
        verify(mockBodyTypeAdapter).equals("//count", "<body />");

        verifyNoMoreInteractions(mockBodyTypeAdapter);
        verifyNoMoreInteractions(mockRestClient);
        verifyNoMoreInteractions(mockCellFormatter);
        verifyNoMoreInteractions(mockLastRequest);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void mustMatchRequestsWithNoBodyExpressedAsNoBodyString() throws Exception {
        when(mockBodyTypeAdapter.parse("no-body")).thenReturn("no-body");
        when(mockBodyTypeAdapter.equals("no-body", "")).thenReturn(true);
        when(mockRestClient.getBaseUrl()).thenReturn(BASE_URL);
        lastResponse.setResource("/uri");
        lastResponse.setBody("");
        RowWrapper<?> row = helper.createTestRow("DELETE", "/uri", "", "", "no-body");

        fixture.processRow(row);

        // correctly builds request
        verify(mockLastRequest).setMethod(Method.Delete);
        verify(mockLastRequest).addHeaders(fixture.getHeaders());
        verify(mockLastRequest).setResource("/uri");
        verify(mockLastRequest).setResourceUriEscaped(false);
        verify(mockLastRequest).setFollowRedirect(true);
        verify(mockLastRequest).setMultipartFileParameterName("file");
        verify(mockLastRequest).getQuery();
        // correctly executes request
        verify(mockRestClient).setBaseUrl(fixture.getBaseUrl());
        verify(mockRestClient).getBaseUrl();
        verify(mockRestClient).execute(mockLastRequest);
        // correctly formats the response
        verify(mockCellFormatter).asLink(any(CellWrapper.class), eq("/uri"), eq(BASE_URL + "/uri"), eq("/uri"));
        verify(mockCellFormatter).gray("200");
        // matches no-body and format it with right - first arg should be
        // row.getCell(4) but mockito doesn't like it
        verify(mockCellFormatter).right(isA(CellWrapper.class), eq(mockBodyTypeAdapter));

        verify(mockBodyTypeAdapter).setContext(isA(Map.class));
        verify(mockBodyTypeAdapter).set("");
        verify(mockBodyTypeAdapter).parse("no-body");
        verify(mockBodyTypeAdapter).equals("no-body", "");

        verifyNoMoreInteractions(mockBodyTypeAdapter);
        verifyNoMoreInteractions(mockCellFormatter);
        verifyNoMoreInteractions(mockRestClient);
        verifyNoMoreInteractions(mockLastRequest);
    }

    /**
     * expectations on headers are verified by checking that the expected list
     * of headers is a subset of the actual list of headers
     */
    @SuppressWarnings("unchecked")
    @Test
    public void mustExecuteVerbOnAUriWithExcpectationsSetOnHeaders() {
        when(mockLastRequest.getQuery()).thenReturn("");
        when(mockRestClient.getBaseUrl()).thenReturn(BASE_URL);
        lastResponse.setResource("/uri");
        lastResponse.addHeader("Header1", "some/thing/1");
        lastResponse.addHeader("Header2", "some/thing/2");
        lastResponse.addHeader("Header3", "some/thing/3");
        lastResponse.setBody("<body />");
        RowWrapper<?> row = helper.createTestRow("GET", "/uri", "", "Header2 : some/thing/2", "");

        fixture.processRow(row);

        // correctly builds request
        verify(mockCellFormatter).right(isA(CellWrapper.class), isA(HeadersTypeAdapter.class));
    }

    @Test
    public void mustExpectOnlySupportedVerbOnFirstCell() {
        RowWrapper<?> row = helper.createTestRow("GET", "/uri", "", "", "");
        fixture.processRow(row);
        verify(mockLastRequest).setMethod(Method.Get);

        row = helper.createTestRow("DELETE", "/uri", "", "", "");
        fixture.processRow(row);
        verify(mockLastRequest).setMethod(Method.Delete);

        row = helper.createTestRow("setBody", "<body />");
        fixture.processRow(row);

        row = helper.createTestRow("POST", "/uri", "", "", "");
        fixture.processRow(row);
        verify(mockLastRequest).setMethod(Method.Post);

        row = helper.createTestRow("PUT", "/uri", "", "", "");
        fixture.processRow(row);
        verify(mockLastRequest).setMethod(Method.Put);

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
    public void mustExecuteVerbOnAUriWithExcpectationsSetOnBody_XML() throws Exception {
        when(mockBodyTypeAdapter.parse("//body")).thenReturn("//body");
        when(mockBodyTypeAdapter.equals("//body", "<body />")).thenReturn(true);
        when(mockLastRequest.getQuery()).thenReturn("");
        when(mockRestClient.getBaseUrl()).thenReturn(BASE_URL);
        lastResponse.setResource("/uri");
        lastResponse.addHeader("Content-Type", "application/xml");
        lastResponse.setBody("<body />");

        RowWrapper<?> row = helper.createTestRow("GET", "/uri", "", "", "//body");

        fixture.processRow(row);

        // correctly builds request
        verify(mockCellFormatter).right(isA(CellWrapper.class), eq(mockBodyTypeAdapter));
        verify(mockCellFormatter).asLink(isA(CellWrapper.class), eq("/uri"), eq("http://localhost:9090/uri"), eq("/uri"));
        verify(mockCellFormatter).gray("200");
        verify(mockCellFormatter).gray("Content-Type : application/xml");

        verify(mockBodyTypeAdapter).setContext(isA(Map.class));
        verify(mockBodyTypeAdapter).set("<body />");
        verify(mockBodyTypeAdapter).parse("//body");
        verify(mockBodyTypeAdapter).equals("//body", "<body />");

        verifyNoMoreInteractions(mockBodyTypeAdapter);

        verifyNoMoreInteractions(mockCellFormatter);
    }

    /**
     * expectations on body that parse into text will be processed w/ the Text
     * body adapter. content type expected is text/plain
     */
    @Test
    @SuppressWarnings("unchecked")
    public void mustExecuteVerbOnAUriWithExcpectationsSetOnBody_TEXT() throws Exception {
        String regex = ".+AD \\d\\d\\d\\d.+";
        String content = "in AD 1492 Columbus discovered America";
        when(mockBodyTypeAdapter.parse(regex)).thenReturn(regex);
        when(mockBodyTypeAdapter.equals(regex, content)).thenReturn(true);

        when(mockLastRequest.getQuery()).thenReturn("");
        when(mockRestClient.getBaseUrl()).thenReturn(BASE_URL);
        lastResponse.setResource("/uri");
        lastResponse.addHeader("Content-Type", "text/plain");
        lastResponse.setBody(content);

        RowWrapper<?> row = helper.createTestRow("GET", "/uri", "", "", regex);

        fixture.processRow(row);

        // correctly builds request
        verify(mockCellFormatter).right(isA(CellWrapper.class), eq(mockBodyTypeAdapter));

        verify(mockBodyTypeAdapter).setContext(isA(Map.class));
        verify(mockBodyTypeAdapter).set(content);
        verify(mockBodyTypeAdapter).parse(regex);
        verify(mockBodyTypeAdapter).equals(regex, content);
        verify(mockCellFormatter).asLink(isA(CellWrapper.class), eq("/uri"), eq(BASE_URL + "/uri"), eq("/uri"));
        verify(mockCellFormatter).gray("200");
        verify(mockCellFormatter).gray("Content-Type : text/plain");

        verifyNoMoreInteractions(mockBodyTypeAdapter);
        verifyNoMoreInteractions(mockCellFormatter);
    }

    /**
     * expectations on body that parse into text will be processed w/ the Text
     * body adapter. content type expected is text/plain.
     * Make sure that if the expectation is some form of HTML or XML then it's 
     * correctly handled
     */
    @Test
    @SuppressWarnings("unchecked")
    public void mustExecuteVerbOnAUriWithExcpectationsSetOnBody_TEXTWithHtml() throws Exception {
        String regex = "<name>Columbus</name>";
        String content = "in AD 1492 <name>Columbus</name> discovered America";
        when(mockBodyTypeAdapter.parse(regex)).thenReturn(regex);
        when(mockBodyTypeAdapter.equals(regex, content)).thenReturn(true);

        when(mockLastRequest.getQuery()).thenReturn("");
        when(mockRestClient.getBaseUrl()).thenReturn(BASE_URL);
        lastResponse.setResource("/uri");
        lastResponse.addHeader("Content-Type", "text/plain");
        lastResponse.setBody(content);

        RowWrapper<?> row = helper.createTestRow("GET", "/uri", "", "", regex);

        fixture.processRow(row);

        // correctly builds request
        verify(mockCellFormatter).right(isA(CellWrapper.class), eq(mockBodyTypeAdapter));

        verify(mockBodyTypeAdapter).setContext(isA(Map.class));
        verify(mockBodyTypeAdapter).set(content);
        verify(mockBodyTypeAdapter).parse(regex);
        verify(mockBodyTypeAdapter).equals(regex, content);
        verify(mockCellFormatter).asLink(isA(CellWrapper.class), eq("/uri"), eq(BASE_URL + "/uri"), eq("/uri"));
        verify(mockCellFormatter).gray("200");
        verify(mockCellFormatter).gray("Content-Type : text/plain");

        verifyNoMoreInteractions(mockBodyTypeAdapter);
        verifyNoMoreInteractions(mockCellFormatter);
    }

    /**
     * expectations on body that parse into JSON will be processed w/ the JSON
     * body adapter. content type expected is application/json
     */
    @Test
    @SuppressWarnings("unchecked")
    public void mustExecuteVerbOnAUriWithExcpectationsSetOnBody_JSON() throws Exception {
        String json = "{\"test\":\"me\"}";
        String xpath = "/test[text()='me']";
        when(mockBodyTypeAdapter.parse(xpath)).thenReturn(xpath);
        when(mockBodyTypeAdapter.equals(xpath, json)).thenReturn(true);
        when(mockLastRequest.getQuery()).thenReturn("");
        when(mockRestClient.getBaseUrl()).thenReturn(BASE_URL);
        lastResponse.setResource("/uri");
        lastResponse.addHeader("Content-Type", "application/json");
        lastResponse.setBody(json);

        RowWrapper<?> row = helper.createTestRow("GET", "/uri", "", "", xpath);

        fixture.processRow(row);

        // correctly builds request
        verify(mockCellFormatter).right(isA(CellWrapper.class), eq(mockBodyTypeAdapter));
    }

    @Test
    public void mustExecuteAllHttpVerbSupported() {
        RowWrapper<?> get = helper.createTestRow("GET", "/uri", "", "", "");
        fixture.processRow(get);
        RowWrapper<?> post = helper.createTestRow("POST", "/uri", "", "", "");
        fixture.processRow(post);
        RowWrapper<?> put = helper.createTestRow("PUT", "/uri", "", "", "");
        fixture.processRow(put);
        RowWrapper<?> del = helper.createTestRow("DELETE", "/uri", "", "", "");
        fixture.processRow(del);
        RowWrapper<?> head = helper.createTestRow("HEAD", "/uri", "", "", "");
        fixture.processRow(head);
        RowWrapper<?> opt = helper.createTestRow("OPTIONS", "/uri", "", "", "");
        fixture.processRow(opt);
        RowWrapper<?> trace = helper.createTestRow("TRACE", "/uri", "", "", "");
        fixture.processRow(trace);
    	
        verify(mockLastRequest).setMethod(Method.Get);
        verify(mockLastRequest).setMethod(Method.Post);
        verify(mockLastRequest).setMethod(Method.Put);
        verify(mockLastRequest).setMethod(Method.Delete);
        verify(mockLastRequest).setMethod(Method.Head);
        verify(mockLastRequest).setMethod(Method.Options);
        verify(mockLastRequest).setMethod(Method.Trace);
    }
    
    @Test
    public void mustUseValueOnSymbolMapEvenIfNotSetViaVariables() {
        when(mockLastRequest.getQuery()).thenReturn("");
        when(mockRestClient.getBaseUrl()).thenReturn(BASE_URL);
        RowWrapper<?> row = helper.createTestRow("GET", "/uri/%fred%", "", "", "");
        Fixture.setSymbol("fred", "bloggs");

        fixture.processRow(row);

        // correctly builds request
        verify(mockLastRequest).setResource("/uri/bloggs");
    }

    @Test
    public void mustNotSetValueOnSymbolMapIfVariableNameStartsWith$() {
        when(mockLastRequest.getQuery()).thenReturn("");
        when(mockRestClient.getBaseUrl()).thenReturn(BASE_URL);
        lastResponse.setBody("<body>text</body>");

        RowWrapper<?> row = helper.createTestRow("GET", "/uri", "", "", "");
        fixture.processRow(row);
        row = helper.createTestRow("let", "$content", "body", "/body/text()", "text");
        fixture.processRow(row);

        // correctly builds request
        assertEquals(null, variables.get("content"));
        assertEquals("text", variables.get("$content"));
        assertEquals(null, Fixture.getSymbol("content"));
    }

    @Test
    public void mustReplaceVariablesInExpectedContentOfLetCell() {
        when(mockLastRequest.getQuery()).thenReturn("");
        when(mockRestClient.getBaseUrl()).thenReturn(BASE_URL);
        lastResponse.setBody("<body>text</body>");

        variables.put("the_content", "text");

        RowWrapper<?> row = helper.createTestRow("GET", "/uri", "", "", "");
        fixture.processRow(row);
        row = helper.createTestRow("let", "$content", "body", "/body/text()", "%the_content%");
        fixture.processRow(row);

        verify(row.getCell(4)).body();
        verify(row.getCell(4)).body("text");
        verifyNoMoreInteractions(row.getCell(4));
    }

    @Test
    public void mustSplitQueryOnFirstQuestionMark() {
        String q = "a=http://another.com?zzz=1?zzz=2";
        when(mockLastRequest.getQuery()).thenReturn(q);
        when(mockRestClient.getBaseUrl()).thenReturn(BASE_URL);
        lastResponse.setRawBody("<body>1234</body>".getBytes());

        RowWrapper<?> row = helper.createTestRow("GET", "/uri?" + q, "", "", "");
        fixture.processRow(row);

        verify(mockLastRequest).setQuery(q);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mustSetValueOnSymbolMapAsXmlStringIfSourceIsBodyAsXml() throws Exception {
        String xmlString = "<root><header>some</header><body>text</body></root>";

        when(mockBodyTypeAdapter.toString()).thenReturn(xmlString);
        when(mockBodyTypeAdapter.parse(xmlString)).thenReturn(xmlString);

        when(mockLastRequest.getQuery()).thenReturn("");
        when(mockRestClient.getBaseUrl()).thenReturn(BASE_URL);
        lastResponse.setBody(xmlString);

        RowWrapper<?> row = helper.createTestRow("GET", "/uri", "", "", "");
        fixture.processRow(row);
        row = helper.createTestRow("let", "content", "body:xml", "/", "");
        fixture.processRow(row);

        verify(mockCellFormatter).asLink(isA(CellWrapper.class), eq("/uri"), eq("http://localhost:9090/uri"), eq("/uri"));
        verify(mockCellFormatter).gray(eq("200"));
        verify(mockCellFormatter).gray(eq("/"));
        verify(mockCellFormatter).gray(eq(xmlString));
        verify(mockCellFormatter).check(isA(CellWrapper.class), isA(StringTypeAdapter.class));

        verify(mockBodyTypeAdapter).set(xmlString);
        verify(mockBodyTypeAdapter).setContext(isA(Map.class));

        assertEquals(xmlString, clean(variables.get("content")));
        assertEquals(null, clean(variables.get("$content")));
        assertEquals(xmlString, clean(Fixture.getSymbol("content").toString()));

        verifyNoMoreInteractions(mockBodyTypeAdapter);
        verifyNoMoreInteractions(mockCellFormatter);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mustSetValiueOnSymbolMapAsJsonStringIfSourceIsJs() throws Exception {
        String jsonString = "{ \"person\" : { \"name\" : \"fred\", \"age\" : \"20\"} }";
        when(mockBodyTypeAdapter.toString()).thenReturn(jsonString);
        when(mockBodyTypeAdapter.parse(jsonString)).thenReturn(jsonString);

        when(mockLastRequest.getQuery()).thenReturn("");
        when(mockRestClient.getBaseUrl()).thenReturn(BASE_URL);
        lastResponse.setBody(jsonString);
        lastResponse.addHeader("Content-Type", ContentType.JSON.toMime().get(0));

        RowWrapper<?> row = helper.createTestRow("GET", "/uri", "", "", "");
        fixture.processRow(row);
        row = helper.createTestRow("let", "name", "js", "response.jsonbody.person.name", "");
        fixture.processRow(row);
        row = helper.createTestRow("let", "age", "js", "response.jsonbody.person.age", "");
        fixture.processRow(row);

        verify(mockCellFormatter).asLink(isA(CellWrapper.class), eq("/uri"), eq("http://localhost:9090/uri"), eq("/uri"));
        verify(mockCellFormatter).gray(eq("200"));
        verify(mockCellFormatter).gray(eq("Content-Type : application/json"));
        verify(mockCellFormatter).gray(eq(jsonString));
        verify(mockCellFormatter).gray(eq("response.jsonbody.person.name"));
        verify(mockCellFormatter).gray(eq("response.jsonbody.person.age"));
        verify(mockCellFormatter, times(2)).check(isA(CellWrapper.class), isA(StringTypeAdapter.class));

        // correctly builds request
        assertEquals("fred", variables.get("name"));
        assertEquals("20", variables.get("age"));

        verifyNoMoreInteractions(mockCellFormatter);
    }

    public void mustRenderCommentMessagesWithSubstitutedLabels() {

        RowWrapper<?> row = helper.createTestRow("let", "seven", "js", "3 + 4");
        fixture.processRow(row);

        row = helper.createTestRow("comment", "three plus four is: %seven%");
        fixture.processRow(row);

        verify(mockCellFormatter).gray("three plus four is: 7");
        verifyNoMoreInteractions(mockCellFormatter);
    }

    @Test
    public void mustAllowRegexesWhenLetIsInvokedOnHeaders() {
        when(mockLastRequest.getQuery()).thenReturn("");
        when(mockLastRequest.getBody()).thenReturn("<bovver />");
        when(mockRestClient.getBaseUrl()).thenReturn(BASE_URL);
        lastResponse.setBody("");
        lastResponse.addHeader("Location", "/res/12321");

        RowWrapper<?> row = helper.createTestRow("POST", "/res", "", "", "");
        fixture.processRow(row);
        row = helper.createTestRow("let", "id", "header", "Location:/res/(\\d+)", "");
        fixture.processRow(row);

        assertEquals("12321", variables.get("id"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mustReportToTheUserIfLetCellsAreMissing() {
        RowWrapper<?> row = helper.createTestRow("let", "id", "header", "Location:res(\\d+)");
        fixture.processRow(row);
        verify(mockCellFormatter).exception(isA(CellWrapper.class), eq("Not all cells found: | let | label | type | expr | result |"));
        verifyNoMoreInteractions(mockCellFormatter);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mustReportToTheUserIfLetCantFindTheHandlerToHandleTheDesiredExpression() {
        RowWrapper<?> row = helper.createTestRow("let", "$content", "something_non_handled", "-", "");
        fixture.processRow(row);
        verify(mockCellFormatter).exception(isA(CellWrapper.class), eq("I don't know how to process the expression for 'something_non_handled'"));
        verifyNoMoreInteractions(mockCellFormatter);
    }

    private String clean(String s) {
    	if(s == null) {
    		return null;
    	}
        return s.trim().replaceAll("\n", "").replaceAll("\r", "");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mustCaptureErrorsOnExpectationsAndDisplayThemInTheSameCell() {
        when(mockLastRequest.getQuery()).thenReturn("");
        when(mockRestClient.getBaseUrl()).thenReturn(BASE_URL);
        lastResponse.setStatusCode(201);
        lastResponse.setStatusText("OK");

        RowWrapper<?> row = helper.createTestRow("GET", "/uri", "404", "", "");

        fixture.processRow(row);

        // correctly builds request
        verify(mockCellFormatter).wrong(isA(CellWrapper.class), isA(StatusCodeTypeAdapter.class));
    }

    /**
     * <code>| let | content |  body | /body/text() | |</code>.
     */
    @Test
    public void mustAllowGlobalStorageOfValuesExtractedViaXPathFromBody() {
        when(mockRestClient.getBaseUrl()).thenReturn(BASE_URL);
        lastResponse.setBody("<body>text</body>");

        RowWrapper<?> row = helper.createTestRow("GET", "/uri", "", "", "");
        fixture.processRow(row);
        row = helper.createTestRow("let", "content", "body", "/body/text()", "text");
        fixture.processRow(row);
        assertEquals("text", Fixture.getSymbol("content"));

        row = helper.createTestRow("let", "content", "body", "/body/text()", "text");
        fixture.processRow(row);
        assertEquals("text", variables.get("content"));

    }

    /**
     * <code>| let | content |  body | count(body) | |</code>.
     */
    @Test
    public void mustAllowStorageOfValuesExtractedViaXPathsReturningStringValues() {
        when(mockRestClient.getBaseUrl()).thenReturn(BASE_URL);
        lastResponse.setBody("<body>text</body>");

        RowWrapper<?> row = helper.createTestRow("GET", "/uri", "", "", "");
        fixture.processRow(row);
        row = helper.createTestRow("let", "count", "body", "count(body)", "1");
        fixture.processRow(row);
        assertEquals("1", Fixture.getSymbol("count"));
        row = helper.createTestRow("let", "count", "body", "count(body)", "1");
        fixture.processRow(row);
        assertEquals("1", variables.get("count"));
    }

    /**
     * <code>| let | val | header | h1 : (\w\d) | |</code>.
     */
    @Test
    public void mustAllowGlobalStorageOfValuesExtractedViaRegexFromHeader() {
        when(mockRestClient.getBaseUrl()).thenReturn(BASE_URL);
        lastResponse.setBody("<body>text</body>");
        lastResponse.addHeader("header", "value1");

        RowWrapper<?> row = helper.createTestRow("GET", "/uri", "", "", "");
        fixture.processRow(row);
        row = helper.createTestRow("let", "keytovalue", "header", "header:(.+\\d)", "value1");
        fixture.processRow(row);
        assertEquals("value1", variables.get("keytovalue"));
    }

    @Test
    public void mustAllowSetOfFileNameForFileUpload() {
        RowWrapper<?> row = helper.createTestRow("setFileName", "/tmp/filename");
        fixture.processRow(row);
        assertEquals("/tmp/filename", fixture.getFileName());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mustReportAsExceptionWhenSettingMissingFileName() {
        RowWrapper<?> row = helper.createTestRow("setFileName");
        fixture.processRow(row);
        verify(mockCellFormatter).exception(isA(CellWrapper.class), isA(String.class));
        verifyNoMoreInteractions(mockCellFormatter);
    }

    @Test
    public void mustAllowSetOfMultipartFileNameForFileUpload() {
        RowWrapper<?> row = helper.createTestRow("setMultipartFileName", "/tmp/filename");
        fixture.processRow(row);
        assertEquals("/tmp/filename", fixture.getMultipartFileName());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mustReportAsExceptionWhenAttemptingToSetMissingMultipartFileName() {
        RowWrapper<?> row = helper.createTestRow("setMultipartFileName");
        when(row.getCell(1)).thenReturn(null);
        fixture.processRow(row);
        verify(mockCellFormatter).exception(isA(CellWrapper.class), isA(String.class));
        verifyNoMoreInteractions(mockCellFormatter);
    }

    @Test
    public void mustAllowSetOfMultipartFileParameterNameForFileUpload() {
        RowWrapper<?> row = helper.createTestRow("setMultipartFileParameterName", "thefile");
        fixture.processRow(row);
        assertEquals("thefile", fixture.getMultipartFileParameterName());
    }

    @Test
    public void mustExecuteVariableSubstitutionOnBodyForNextRequest() {
        variables.put("content", "<xml />");
        when(mockCellFormatter.fromRaw("%content%")).thenReturn("%content%");
        RowWrapper<?> row = helper.createTestRow("setBody", "%content%");
        fixture.processRow(row);
        row = helper.createTestRow("POST", "/uri", "", "", "");
        fixture.processRow(row);
        verify(mockLastRequest).setMethod(Method.Post);
        verify(mockLastRequest).setBody("<xml />");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mustEvalJavascriptStringsWithEval() {
        RowWrapper<?> row = helper.createTestRow("evalJs", "a=1; b=2; a + b;");
        fixture.processRow(row);
        verify(mockCellFormatter).right(isA(CellWrapper.class), isA(StringTypeAdapter.class));
        assertThat(fixture.getLastEvaluation(), is(equalTo("3.0")));
        verifyNoMoreInteractions(mockCellFormatter);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mustNotifyExceptionsWhenEvalJavascriptStrings() {
        RowWrapper<?> row = helper.createTestRow("evalJs", "a=");
        fixture.processRow(row);
        verify(mockCellFormatter).exception(isA(CellWrapper.class), isA(JavascriptException.class));
        assertThat(fixture.getLastEvaluation(), is(nullValue()));
        verifyNoMoreInteractions(mockCellFormatter);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mustReportAsRTEWhenSettingMissingMultipartFileParameterName() {
        RowWrapper<?> row = helper.createTestRow("setMultipartFileParameterName");
        when(row.getCell(1)).thenReturn(null);
        fixture.processRow(row);
        verify(mockCellFormatter).exception(isA(CellWrapper.class), eq("You must pass a parameter name to set"));
        verifyNoMoreInteractions(mockCellFormatter);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mustReportAsRTEWhenSettingAMissingBody() {
        RowWrapper<?> row = helper.createTestRow("setBody");
        when(row.getCell(1)).thenReturn(null);
        fixture.processRow(row);
        verify(mockCellFormatter).exception(isA(CellWrapper.class), eq("You must pass a body to set"));
        verifyNoMoreInteractions(mockCellFormatter);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mustReportAsRTEWhenSettingAMissingHeader() {
        RowWrapper<?> row = helper.createTestRow("setHeader");
        when(row.getCell(1)).thenReturn(null);
        fixture.processRow(row);
        verify(mockCellFormatter).exception(isA(CellWrapper.class), eq("You must pass a header map to set"));
        verifyNoMoreInteractions(mockCellFormatter);
    }

    /**
     * tests for slim support
     */

    @Test
    public void constructingWithOneArgShoudlBeTheSUTUri() {
        String uri = "http://localhost:9090";
        RestFixture f = new RestFixture(uri);
        assertEquals(uri, f.getBaseUrl());
    }

    @Test
    public void constructingWithOneArgShoudlStripAnyTagAndSetTheSUTUri() {
        String uri = "http://localhost:9090";
        String taggedUri = "<sometag att='1'>" + uri + "</sometag>";
        RestFixture f = new RestFixture(taggedUri);
        assertEquals(uri, f.getBaseUrl());
    }

    @Test
    public void constructingWithOneFailsIfArgIsNotAnUri() {
        String uri = "rubbish";
        try {
            new RestFixture(uri);
        } catch (IllegalArgumentException e) {

        }
    }

    @Test
    public void setsRequestBodyExplicitly() {
        String uri = "http://localhost:9090";
        RestFixture f = new RestFixture(uri);
        f.setBody("<body />");
        assertThat(f.getRequestBody(), is("<body />"));
    }

    @Test
    public void setsRequestBodyExplicitlyWithSubstitutions() {
        RowWrapper<?> row = helper.createTestRow("let", "foo", "const", "bar", "");
        fixture.processRow(row);
        fixture.setBody("<%foo% />");
        assertThat(fixture.getRequestBody(), is("<bar />"));
    }
}
