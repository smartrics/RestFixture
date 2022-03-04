/*  Copyright 2012 Fabrizio Cannizzo
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
package smartrics.rest.fitnesse.fixture.support.http;

/**
 * Builds URIs with query strings.
 * 
 * @author 702161900
 * 
 */
class URIBuilder {
/*

	public URI getURI(String scheme, String host, int port, String path,
					  String queryString, HttpMethodParams params) throws URIException {
		HttpHost httphost = new HttpHost(host, port);
		StringBuffer buffer = new StringBuffer();
		if (httphost != null) {
			buffer.append(httphost.getProtocol().getScheme());
			buffer.append("://");
			buffer.append(httphost.getHostName());
			int p = httphost.getPort();
			if (p != -1 && p != httphost.getProtocol().getDefaultPort()) {
				buffer.append(":");
				buffer.append(p);
			}
		}
		buffer.append(path);
		if (queryString != null) {
			buffer.append('?');
			buffer.append(queryString);
		}
		String charset = params.getUriCharset();
		return new HttpURL(buffer.toString(), charset);
	}
*/

/*
	@SuppressWarnings("deprecation")
	public void setURI(org.apache.commons.httpclient.HttpMethodBase m, URI uri)
			throws URIException {
		HostConfiguration conf = m.getHostConfiguration();
		if (uri.isAbsoluteURI()) {
			conf.setHost(new HttpHost(uri));
			m.setHostConfiguration(conf);
		}
		m.setPath(uri.getPath() != null ? uri.getEscapedPath() : "/");
		m.setQueryString(uri.getQuery());
	}

	public static URI newURI(HttpRequestBase m, HostConfiguration conf) throws URIException {
		String scheme = conf.getProtocol().getScheme();
		String host = conf.getHost();
		int port = conf.getPort();
		return new URIBuilder().getURI(scheme, host, port, m.getPath(),
				m.getQueryString(), m.getParams());
	}
*/
}
