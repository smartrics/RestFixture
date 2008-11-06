package smartrics.rest.fitnesse.fixture.support;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import smartrics.rest.config.Config;

public class HttpClientBuilderTest {

	private Config config;
	private Config incompleteConfig;

	@Before
	public void createConfig() {
		config = new Config("complete");
		config.add("http.client.connection.timeout", "111");
		config.add("http.proxy.host", "HOST");
		config.add("http.proxy.port", "1200");
		config.add("http.basicauth.username", "UNAMEIT");
		config.add("http.basicauth.password", "secr3t");
		
		incompleteConfig = new Config("incomplete");
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
		HttpClient cli = b.createHttpClient(new Config());
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
