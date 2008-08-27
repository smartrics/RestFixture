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

/**
 * A Rest Client offers a simplified interface to an underlying implementation of an Http client.
 *
 * A Rest Client is geared to operate of REST resources.
 */
public interface RestClient {

	/**
	 * Sets the base URL.
	 * It is the portion of the full Url not part of the
	 * resource type. For example if a resource type full Url is
	 * {@code http://host:8888/domain/resourcetype} and the resource type is
	 * {@code /resourcetype}, the base Url is {@code http://host:8888/domain}.
	 * It is meant to serve as a default value to be appended to compose the
	 * full Url when
	 * {@link smartrics.rest.client.RestClient.execute(smartrics.rest.client.RestRequest)}
	 * is used.
	 *
	 * @param bUrl
	 *            a string with the base Url.
	 * @see smartrics.rest.client.RestClient#execute(smartrics.rest.client.RestRequest)
	 */
	void setBaseUrl(String bUrl);

	/**
	 * Retrieves the previously set base Url.
	 *
	 * @return the base Url
	 * @see smartrics.rest.client.RestClient#setBaseUrl(java.lang.String)
	 */
	String getBaseUrl();

	/**
	 * Executes a rest request using the underlying Http client implementation.
	 *
	 * @param request
	 *            the request to be executed
	 * @return the response of the rest request
	 */
	RestResponse execute(RestRequest request);

	/**
	 * Executes the rest request.
	 *
	 * This method offers the possibility to override the base Url set on this client.
	 *
	 * @param baseUrl
	 *            the base Url
	 * @param request
	 *            the request to be executed
	 * @return the response of the rest request.
	 * @see smartrics.rest.client.RestClient#setBaseUrl(java.lang.String)
	 */
	RestResponse execute(String baseUrl, RestRequest request);

}