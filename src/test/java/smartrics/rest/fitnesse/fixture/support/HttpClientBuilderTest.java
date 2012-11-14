/*  Copyright 2011 Fabrizio Cannizzo
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class HttpClientBuilderTest {

	private Config config;
	private Config incompleteConfig;

	@Before
	public void createConfig() {
        config = Config.getConfig("complete");
		config.add("http.client.connection.timeout", "111");
		config.add("http.proxy.host", "HOST");
		config.add("http.proxy.port", "1200");
		config.add("http.basicauth.username", "UNAMEIT");
		config.add("http.basicauth.password", "secr3t");
		
        incompleteConfig = Config.getConfig("incomplete");
		incompleteConfig.add("http.proxy.host", "HOST");
		incompleteConfig.add("http.basicauth.username", "UNAMEIT");

	}
	
	@After
	public void removeConfig() {
		config.clear();
		incompleteConfig.clear();
	}

	@Test
	public void mustSetDefaultsForNotSuppliedConfigValues() {
		HttpClientBuilder b = new HttpClientBuilder();
        HttpClient cli = b.createHttpClient(Config.getConfig());
		assertEquals(HttpClientBuilder.DEFAULT_SO_TO.intValue(), cli
				.getParams().getSoTimeout());
		assertNull(cli.getHostConfiguration().getProxyHost());
		assertNull(cli.getState().getProxyCredentials(AuthScope.ANY));
	}

	@Test
	public void mustSetValuesAsOfThoseSuppliedInConfig() {
		HttpClientBuilder b = new HttpClientBuilder();
		HttpClient cli = b.createHttpClient(config);
		assertEquals(111, cli.getParams().getSoTimeout());
		assertEquals("HOST", cli.getHostConfiguration().getProxyHost());
		assertEquals(1200, cli.getHostConfiguration().getProxyPort());
		UsernamePasswordCredentials credentials = (UsernamePasswordCredentials) cli
				.getState().getCredentials(AuthScope.ANY);
		assertEquals("UNAMEIT", credentials.getUserName());
		assertEquals("secr3t", credentials.getPassword());
	}
	
	@Test
	public void mustNotSetCredentialsIfBothConfigValueAreNotAvailable() {
		HttpClientBuilder b = new HttpClientBuilder();
		HttpClient cli = b.createHttpClient(incompleteConfig);
		assertNull(cli.getState().getProxyCredentials(AuthScope.ANY));
	}
	
	@Test
	public void mustSetDefaultProxyPortIfNotSuppliedWithProxyHost() {
		HttpClientBuilder b = new HttpClientBuilder();
		HttpClient cli = b.createHttpClient(incompleteConfig);
		assertEquals(80, cli.getHostConfiguration().getProxyPort());
	}
	
}
