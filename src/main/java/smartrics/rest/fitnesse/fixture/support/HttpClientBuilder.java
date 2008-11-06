package smartrics.rest.fitnesse.fixture.support;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.params.HostParams;
import org.apache.commons.httpclient.params.HttpClientParams;

import smartrics.rest.config.Config;

/**
 * Helper builder class for an apache {@link HttpClient} that uses data in the
 * {@link Config} to configure the object.
 * 
 * @author fabrizio
 * 
 */
public class HttpClientBuilder {
	public static final Integer DEFAULT_SO_TO = 3000;
	public static final Integer DEFAULT_PROXY_PORT = 80;

	public HttpClient createHttpClient(final Config config) {
		HttpClientParams params = new HttpClientParams();
		params.setSoTimeout(config == null ? DEFAULT_SO_TO : config
				.getAsInteger("http.client.connection.timeout", DEFAULT_SO_TO));
		HttpClient client = new HttpClient(params);
		HostConfiguration hostConfiguration = client.getHostConfiguration();
		String proxyHost = (config == null ? null : config
				.get("http.proxy.host"));
		if (proxyHost != null) {
			int proxyPort = config.getAsInteger("http.proxy.port",
					DEFAULT_PROXY_PORT);
			hostConfiguration.setProxy(proxyHost, proxyPort);
		}
		HostParams hostParams = new HostParams();
		hostConfiguration.setParams(hostParams);
		String username = config == null ? null : config
				.get("http.basicauth.username");
		String password = config == null ? null : config
				.get("http.basicauth.password");
		if (username != null && password != null) {
			Credentials defaultcreds = new UsernamePasswordCredentials(
					username, password);
			client.getState().setCredentials(AuthScope.ANY, defaultcreds);
		}
		return client;
	}
}
