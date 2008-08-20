/**
 * (c) British Telecommunications plc, 2008, All Rights Reserved
 */
package smartrics.rest.fitnesse.fixture;

import org.apache.commons.httpclient.HttpClient;

import smartrics.rest.client.RestClient;
import smartrics.rest.client.RestRequest;
import smartrics.rest.client.RestResponse;

public class MockRestClient implements RestClient {

	private String baseUrl;

	public RestResponse execute(RestRequest request) {
		return createRestResponse(request);
	}

	public RestResponse execute(String hostAddr, RestRequest request) {
		return createRestResponse(request);
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public HttpClient getClient() {
		return null;
	}

	public void setBaseUrl(String url) {
		this.baseUrl = url;
	}

	private RestResponse createRestResponse(RestRequest request) {
		RestResponse rr = new RestResponse();
		rr.addHeader("h1", "v1");
		rr.addHeader("h2", "v2");
		if(!request.getMethod().name().toUpperCase().equals("DELETE")){
			rr.setBody("<body>text</body>");
		}
		rr.setStatusCode(200);
		rr.setStatusText("a text");
		rr.setResource(request.getResource());
		return rr;
	}

}
