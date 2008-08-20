package smartrics.rest.client;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethodBase;

import static org.junit.Assert.assertTrue;

public class MockHttpMethod extends HttpMethodBase{

	private String name;
	private int statusCode;
	private boolean connectionReleased = false;

	public MockHttpMethod(String name){
		this.name=name;
	}

	public int getStatusCode(){
		return statusCode;
	}

	public String getStatusText(){
		return "status text";
	}

	public String getResponseBodyAsString(){
		return "response";
	}

	public void setStatusCode(int rc){
		this.statusCode = rc;
	}

	public Header[] getResponseHeaders(){
		Header h1 = new Header("name1", "value1");
		Header h2 = new Header("name1", "value1");
		return new Header[]{h1, h2};
	}
	public String getName(){
		return name;
	}

	@Override
	public void releaseConnection() {
		connectionReleased = true;
	}

	public void verifyConnectionReleased(){
		assertTrue("connection not released on mock http method", connectionReleased);
	}

}
