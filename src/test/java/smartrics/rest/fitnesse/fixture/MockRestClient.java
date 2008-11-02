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

	protected RestResponse createRestResponse(RestRequest request) {
		RestResponse rr = new RestResponse();
		rr.addHeader("h1", "v1");
		rr.addHeader("h2", "v2");
		rr.addHeader("Content-Type", "application/xml");
		if(!request.getMethod().name().toUpperCase().equals("DELETE")){
			rr.setBody("<body>text</body>");
		}
		if (request.getMethod().name().toUpperCase().equals("POST")) {
			rr.addHeader("Location", "/resource/1");
		}
		rr.setStatusCode(200);
		rr.setStatusText("a text");
		rr.setResource(request.getResource());
		return rr;
	}

}
