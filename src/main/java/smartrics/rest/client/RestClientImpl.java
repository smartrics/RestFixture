package smartrics.rest.client;

import java.io.IOException;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A generic REST client based on HttpClient
 */
public class RestClientImpl implements RestClient {

	private static Log LOG = LogFactory.getLog(RestClientImpl.class);

	private HttpClient client;

	private String baseUrl;

	public RestClientImpl(HttpClient client) {
		if (client == null)
			throw new IllegalArgumentException("Null HttpClient instance");
		this.client = client;
	}

	/* (non-Javadoc)
	 * @see smartrics.rest.client.RestClient#setBaseUrl(java.lang.String)
	 */
	public void setBaseUrl(String bUrl){
		this.baseUrl = bUrl;
	}

	/* (non-Javadoc)
	 * @see smartrics.rest.client.RestClient#getBaseUrl()
	 */
	public String getBaseUrl() {
		return baseUrl;
	}

	/* (non-Javadoc)
	 * @see smartrics.rest.client.RestClient#getClient()
	 */
	public HttpClient getClient(){
		return client;
	}

	/* (non-Javadoc)
	 * @see smartrics.rest.client.RestClient#execute(smartrics.rest.client.RestRequest)
	 */
	public RestResponse execute(RestRequest request) {
		return execute(getBaseUrl(), request);
	}

	/* (non-Javadoc)
	 * @see smartrics.rest.client.RestClient#execute(java.lang.String, smartrics.rest.client.RestRequest)
	 */
	public RestResponse execute(String hostAddr, RestRequest request) {
		if (request == null || !request.isValid())
			throw new IllegalArgumentException("Invalid request " + request);
		if(request.getTransactionId() == null)
			request.setTransactionId(Long.valueOf(System.currentTimeMillis()));
		LOG.debug(request);
		HttpMethod m = createHttpClientMethod(request);
		addHeaders(m, request);
		setUri(m, hostAddr, request);
		m.setQueryString(request.getQuery());
		if(m instanceof EntityEnclosingMethod){
			((EntityEnclosingMethod)m).setRequestBody(request.getBody());
		}
		RestResponse resp = new RestResponse();
		resp.setTransactionId(request.getTransactionId());
		resp.setResource(request.getResource());
		try {
			client.executeMethod(m);
			for (Header h : m.getResponseHeaders()) {
				resp.addHeader(h.getName(), h.getValue());
			}
			resp.setStatusCode(m.getStatusCode());
			resp.setStatusText(m.getStatusText());
			resp.setBody(m.getResponseBodyAsString());
		} catch (HttpException e) {
			String message = "Http call failed for protocol failure";
			LOG.warn(message);
			throw new IllegalStateException(message, e);
		} catch (IOException e) {
			String message = "Http call failed for IO failure";
			LOG.warn(message);
			throw new IllegalStateException(message, e);
		} finally {
			m.releaseConnection();
		}
		LOG.debug(resp);
		return resp;
	}

	private void setUri(HttpMethod m, String hostAddr, RestRequest request) {
		String host = hostAddr == null ? client.getHostConfiguration().getHost() : hostAddr;
		if(host==null)
			throw new IllegalStateException("hostAddress is null: please config httpClient host configuration or pass a valid host address or config a baseUrl on this client");
		String uriString = host + request.getResource();
		try {
			m.setURI(new URI(uriString, false));
		} catch (URIException e) {
			throw new IllegalStateException("Problem when building URI: " + uriString, e);
		} catch (NullPointerException e) {
			throw new IllegalStateException("Building URI with null string", e);
		}
	}

	private void addHeaders(HttpMethod m, RestRequest request) {
		for (RestData.Header h : request.getHeaders()) {
			m.addRequestHeader(h.getName(), h.getValue());
		}
	}

	@SuppressWarnings("unchecked")
	protected HttpMethod createHttpClientMethod(RestRequest request) {
		String mName = request.getMethod().toString();
		String className = String.format("org.apache.commons.httpclient.methods.%sMethod", mName);
		try {
			Class<HttpMethod> clazz = (Class<HttpMethod>) Class.forName(className);
			HttpMethod m = clazz.newInstance();
			return m;
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException(className + " not found: you may be using a too old or very new version of HttpClient", e);
		} catch (InstantiationException e) {
			throw new IllegalStateException("An object of type " + className + " cannot be instantiated", e);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException("The default ctor for type " + className + " cannot be invoked", e);
		}

	}
}
