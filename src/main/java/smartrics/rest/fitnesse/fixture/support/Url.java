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

/**
 * Facade to {@link java.net.URL}. Just to offer a REST oriented interface.
 * 
 * @author smartrics
 * 
 */
public class Url {

	private URL baseUrl;

	/**
	 * @param url
	 *            the string representation of url.
	 */
	public Url(String url) {
		try {
			if (url == null || "".equals(url.trim())) {
				throw new IllegalArgumentException("Null or empty input: "
						+ url);
			}
			String u = url;
			if (url.endsWith("/")) {
				u = url.substring(0, u.length() - 1);
			}
			baseUrl = new URL(u);
			if ("".equals(baseUrl.getHost())) {
				throw new IllegalArgumentException(
						"No host specified in base URL: " + url);
			}
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Malformed base URL: " + url, e);
		}
	}

	/**
	 * @return the base url
	 */
	public URL getUrl() {
		return baseUrl;
	}

	@Override
	public String toString() {
		return getUrl().toExternalForm();
	}

	/**
	 * @return the resource
	 */
	public String getResource() {
		String res = getUrl().getPath().trim();
		if (res.isEmpty()) {
			return "/";
		}
		return res;
	}

	/**
	 * 
	 * @return the base url.
	 */
	public String getBaseUrl() {
		String path = getResource().trim();
		if (path.length() == 0 || path.equals("/")) {
			return toString();
		}
		int index = toString().indexOf(getResource());
		if (index >= 0) {
			return toString().substring(0, index);
		} else {
			throw new IllegalStateException("Invalid URL");
		}
	}

	/**
	 * builds a url
	 * 
	 * @param file
	 *            the file
	 * @return the full url.
	 */
	public URL buildURL(String file) {
		try {
			return new URL(baseUrl, file);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Invalid URL part: " + file);
		}
	}

}
