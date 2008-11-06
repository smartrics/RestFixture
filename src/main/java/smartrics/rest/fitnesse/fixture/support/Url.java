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
package smartrics.rest.fitnesse.fixture.support;

import java.net.MalformedURLException;
import java.net.URL;

public class Url {

	private URL baseUrl;

	public Url(String url) {
		try {
			if (url == null || "".equals(url.trim())) {
				throw new IllegalArgumentException("Null or empty input: " + url);
			}
			String u = url;
			if(url.endsWith("/")){
				u = url.substring(0, u.length() - 1);
			}
			baseUrl = new URL(u);
			if ("".equals(baseUrl.getHost())) {
				throw new IllegalArgumentException("No host specified in base URL: " + url);
			}
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Malformed base URL: " + url, e);
		}
	}

	public URL getBaseUrl() {
		return baseUrl;
	}

	// public String getBaseUrl() {
	// return baseUrl.toExternalForm();
	// }

	@Override
	public String toString() {
		return getBaseUrl().toExternalForm();
	}

	public URL buildURL(String file) {
		try {
			return new URL(baseUrl, file);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Invalid URL part: " + file);
		}
	}

}
