package smartrics.rest.client;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.junit.Before;
import org.junit.Test;

public class RestClientTest {

	private MockHttpMethod mockHttpMethod;

	private RestClientImpl mockRestClientAlwaysOK = new RestClientImpl(new MockHttpClient(200)){

		@Override
		protected HttpMethod createHttpClientMethod(RestRequest request) {
			MockHttpMethod m = new MockHttpMethod(request.getMethod().name());
			m.setStatusCode(200);
			mockHttpMethod = m;
			return m;
		}
	};

	private RestClientImpl mockRestClientAlwaysThrowsIOException = new RestClientImpl(new MockHttpClient(new IOException())){

		@Override
		protected HttpMethod createHttpClientMethod(RestRequest request) {
			mockHttpMethod = new MockHttpMethod(request.getMethod().name());
			return mockHttpMethod;
		}
	};

	private RestClientImpl mockRestClientAlwaysThrowsProtocolException = new RestClientImpl(new MockHttpClient(new HttpException())){

		@Override
		protected HttpMethod createHttpClientMethod(RestRequest request) {
			mockHttpMethod = new MockHttpMethod(request.getMethod().name());
			return mockHttpMethod;
		}
	};

	private RestRequest validRestRequest = (RestRequest)new RestRequest().setMethod(RestRequest.Method.Get).setResource("/a/resource");

	public RestClientTest() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Before
	public void setUp() {
		mockRestClientAlwaysOK.setBaseUrl("http://alwaysok:8080");
		mockRestClientAlwaysThrowsIOException.setBaseUrl("http://ioexception:8080");
		mockRestClientAlwaysThrowsProtocolException.setBaseUrl("http://httpexception:8080");
		validRestRequest.setQuery("aQuery");
		validRestRequest.addHeader("a", "v");
	}

	@Test
	public void mustBeConstructedWithAValidHttpClient(){
		HttpClient httpClient = new HttpClient();
		RestClientImpl restClient = new RestClientImpl(httpClient);
		assertSame(httpClient, restClient.getClient());
	}

	@Test
	public void mustExposeTheBaseUri(){
		assertEquals("http://alwaysok:8080", mockRestClientAlwaysOK.getBaseUrl());
	}

	@Test(expected=IllegalArgumentException.class)
	public void shoudlFailConstructionWithAnInvalidHttpClient(){
		new RestClientImpl(null);
	}

	@Test(expected=IllegalArgumentException.class)
	public void mustNotExecuteAnInvalidRequest(){
		mockRestClientAlwaysOK.execute(new RestRequest());
	}

	@Test(expected=IllegalArgumentException.class)
	public void mustNotExecuteANullRestRequest(){
		mockRestClientAlwaysOK.execute(null);
	}

	@Test(expected=IllegalStateException.class)
	public void mustNotifyCallerIfHttpCallFailsDueToAnIoFailure(){
		try{
			mockRestClientAlwaysThrowsIOException.execute(validRestRequest);
		} catch(IllegalStateException e){
			throw e;
		} finally {
			mockHttpMethod.verifyConnectionReleased();
		}
	}

	@Test(expected=IllegalStateException.class)
	public void mustNotifyCallerIfHttpCallFailsDueToAProtocolFailure(){
		try{
			mockRestClientAlwaysThrowsProtocolException.execute(validRestRequest);
		} catch(IllegalStateException e){
			throw e;
		} finally {
			mockHttpMethod.verifyConnectionReleased();
		}
	}

	@Test
	public void responseShouldContainTheResultCodeOfASuccessfullHttpCall(){
		RestResponse restResponse = mockRestClientAlwaysOK.execute(validRestRequest);
		mockHttpMethod.verifyConnectionReleased();
		assertEquals(Integer.valueOf(200), restResponse.getStatusCode());
	}

	@Test(expected=IllegalStateException.class)
	public void shouldNotifyCallerThatNullHostAddressesAreNotHandled(){
		mockRestClientAlwaysOK.execute(null, validRestRequest);
	}

	@Test(expected=IllegalStateException.class)
	public void shouldNotifyCallerThatInvalidResourceUriAreNotHandled(){
		validRestRequest.setResource("http://resource/shoud/not/include/the/abs/path");
		mockRestClientAlwaysOK.execute("http://basehostaddress:8080", validRestRequest);
	}

	@Test
	public void shouldCreateHttpMethodsToMatchTheMethodInTheRestRequest(){
		MockRestClient mockRestClientWithVerificationOfHttpMethodCreation = new MockRestClient(new MockHttpClient(200));
		mockRestClientWithVerificationOfHttpMethodCreation.verifyCorrectHttpMethodCreation();
	}
}
