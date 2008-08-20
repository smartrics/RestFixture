package smartrics.rest.client;

import org.apache.commons.httpclient.HttpClient;

public interface RestClient {

	void setBaseUrl(String bUrl);

	String getBaseUrl();

	HttpClient getClient();

	RestResponse execute(RestRequest request);

	RestResponse execute(String hostAddr, RestRequest request);

}