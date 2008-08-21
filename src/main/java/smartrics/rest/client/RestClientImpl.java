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
