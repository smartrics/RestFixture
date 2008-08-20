package smartrics.rest.client;

import static org.junit.Assert.assertTrue;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;

public class MockRestClient extends RestClientImpl{

	public MockRestClient(HttpClient client) {
		super(client);
	}

	public void verifyCorrectHttpMethodCreation(){
		RestRequest req = new RestRequest();
		req.setMethod(RestRequest.Method.Get);
		HttpMethod m = this.createHttpClientMethod(req);
		assertTrue("method is not a GetMethod", m instanceof org.apache.commons.httpclient.methods.GetMethod);
		req.setMethod(RestRequest.Method.Post);
		m = this.createHttpClientMethod(req);
		assertTrue("method is not a PostMethod", m instanceof org.apache.commons.httpclient.methods.PostMethod);
	}

}
