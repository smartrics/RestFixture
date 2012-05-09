package smartrics.rest.fitnesse.fixture;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import smartrics.rest.client.RestClient;
import smartrics.rest.client.RestRequest;
import smartrics.rest.client.RestResponse;
import smartrics.rest.config.Config;
import smartrics.rest.fitnesse.fixture.RestFixture.Runner;
import smartrics.rest.fitnesse.fixture.support.BodyTypeAdapter;
import smartrics.rest.fitnesse.fixture.support.CellFormatter;
import smartrics.rest.fitnesse.fixture.support.ContentType;
import smartrics.rest.fitnesse.fixture.support.RowWrapper;
import smartrics.rest.fitnesse.fixture.support.Variables;

public class RestScriptFixtureTest {

    private static final String BASE_URL = "http://localhost:9090";
    private RestScriptFixture fixture;
    private final Variables variables = new Variables();
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
	public void setUp() throws Exception {
    	helper = new RestFixtureTestHelper();

        mockBodyTypeAdapter = mock(BodyTypeAdapter.class);
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

        config = Config.getConfig();

        ContentType.resetDefaultMapping();

        helper.wireMocks(config, mockPartsFactory, mockRestClient, mockLastRequest, lastResponse, mockCellFormatter, mockBodyTypeAdapter);
        fixture = new RestScriptFixture(mockPartsFactory, BASE_URL);
        fixture.initialize(Runner.OTHER);
	}

	@After
	public void tearDown() throws Exception {
		config.clear();
	}
	
	@Test
	public void testRestScriptFixtureString() {
        fixture = new RestScriptFixture("http://example.com");
        assertEquals("http://example.com", fixture.getBaseUrl());
	}

	@Test
	public void testRestScriptFixtureStringString() {
        fixture = new RestScriptFixture("http://example.com:8080", "configName");
        assertEquals("http://example.com:8080", fixture.getBaseUrl());
        assertEquals("configName", fixture.getConfig().getName());
	}

	@Test
	public void testGet() {
        when(mockRestClient.getBaseUrl()).thenReturn(BASE_URL);
        fixture.get("/uri");
        verify(mockLastRequest).setMethod(RestRequest.Method.Get);
        verify(mockRestClient).execute(any(RestRequest.class));
	}

	@Test
	public void testPost() {
        when(mockRestClient.getBaseUrl()).thenReturn(BASE_URL);
        fixture.post("/uri");
        verify(mockLastRequest).setMethod(RestRequest.Method.Post);
        verify(mockRestClient).execute(any(RestRequest.class));
	}

	@Test
	public void testPut() {
        when(mockRestClient.getBaseUrl()).thenReturn(BASE_URL);
        fixture.put("/uri");
        verify(mockLastRequest).setMethod(RestRequest.Method.Put);
        verify(mockRestClient).execute(any(RestRequest.class));
	}

	@Test
	public void testDelete() {
        when(mockRestClient.getBaseUrl()).thenReturn(BASE_URL);
        fixture.delete("/uri");
        verify(mockLastRequest).setMethod(RestRequest.Method.Delete);
        verify(mockRestClient).execute(any(RestRequest.class));
	}

	@Test
	public void testHeader() {
        setupLastResponse();

        lastResponse.addHeader("Furry-cat", "9000");

        assertEquals("Furry-cat:9000", fixture.header("Furry-cat:\\d+"));
        assertEquals("null", fixture.header("Content-size:\\d+"));
        
	}

	@Test
	public void testBody() {
		setupLastResponse();

		lastResponse.addHeader("Content-type", "application/xml");
		lastResponse.setBody("<node><greeting>Hi there</greeting><name>Person</name></node>");
		
		assertEquals("Hi there", fixture.body("//greeting"));
		assertEquals("null", fixture.body("//welcome"));
	}

	@Test
	public void testJs() {
		setupLastResponse();

		lastResponse.addHeader("Content-type", "application/xml");
		lastResponse.setBody("{node: {greeting: \"Hi there\", name: \"Person\"}}");
		
		assertEquals("200", fixture.js("response.statusCode"));
		assertEquals("Hi there", fixture.js("response.jsonbody.node.greeting"));
	}

	@Test
	public void testSetFileNameString() {
        fixture.setFileName("/tmp/filename");
        assertEquals("/tmp/filename", fixture.getFileName());
	}

	@Test
	public void testSetMultipartFileNameString() {
        fixture.setMultipartFileName("/tmp/filename");
        assertEquals("/tmp/filename", fixture.getMultipartFileName());
	}

	@Test
	public void testSetMultipartFileParameterNameString() {
        fixture.setMultipartFileParameterName("/tmp/filename");
        assertEquals("/tmp/filename", fixture.getMultipartFileParameterName());
	}

	@Test
	public void testStatusCode() {
		setupLastResponse();
		
		assertEquals(new Integer(200), fixture.statusCode());
	}

	@Test
	public void testStatusCodeWithDifferentValue() {
		lastResponse.setStatusCode(404);
		setupLastResponse();
		
		assertEquals(new Integer(404), fixture.statusCode());
	}

	@Test
	public void testResponseBody() {
		setupLastResponse();

		lastResponse.setBody("Simple text");
		
		assertEquals("Simple text", fixture.responseBody());
	}

	/*@Test
	public void testHasBody() throws Exception {
		setupLastResponse();

		lastResponse.addHeader("Content-type", "application/xml");
		lastResponse.setBody("<node><greeting>Hi there</greeting><name>Person</name></node>");
		
		assertEquals(true, fixture.hasBody("//greeting[text()='Hi there']"));
	}

	@Test
	public void testHasBodyUsingType() {
		fail("Not yet implemented");
	}

	@Test
	public void testHasHeaders() {
		fail("Not yet implemented");
	}

	@Test
	public void testHasHeader() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetBodyString() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetHeaderString() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetResponseHeaders() {
		fail("Not yet implemented");
	}*/

	private void setupLastResponse() {
		when(mockRestClient.getBaseUrl()).thenReturn(BASE_URL);
        fixture.get("/uri");
	}

}
