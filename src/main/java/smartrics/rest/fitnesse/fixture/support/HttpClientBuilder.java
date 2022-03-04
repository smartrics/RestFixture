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

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultRedirectStrategy;

import java.util.concurrent.TimeUnit;


/**
 * Helper builder class for an apache {@link HttpClient} that uses data in the
 * {@link Config} to configure the object.
 *
 * @author smartrics
 *
 */
public class HttpClientBuilder {
	/**
	 * default value of the socket timeout: 3000ms.
	 */
    public static final Integer DEFAULT_SO_TO = 3000;
    /**
     * default value of the proxy port: 80.
     */
    public static final Integer DEFAULT_PROXY_PORT = 80;

    /**
     * @param config the {@link Config} containing the client configuration paramteres.
     * @param followRedirects
     * @return an instance of an {@link HttpClient}.
     */
    public HttpClient createHttpClient(final Config config, boolean followRedirects) {
        int timeout = config == null ? DEFAULT_SO_TO  : config.getAsInteger("http.client.connection.timeout", DEFAULT_SO_TO);
        org.apache.http.impl.client.HttpClientBuilder builder = org.apache.http.impl.client.HttpClientBuilder.create()
                .setConnectionTimeToLive(timeout, TimeUnit.MILLISECONDS);
        if (followRedirects) {
            builder.setRedirectStrategy(new DefaultRedirectStrategy());
        } else {
            builder.disableRedirectHandling();
        }
        String proxyHost = config.get("http.proxy.host");
        if (proxyHost != null) {
            int proxyPort = config.getAsInteger("http.proxy.port", DEFAULT_PROXY_PORT);
            HttpHost proxy = new HttpHost(proxyHost, proxyPort);
            builder.setProxy(proxy);
            configureProxyCredentials(config, builder, proxyHost, proxyPort);
        }
        return builder.build();
    }

    private void configureProxyCredentials(Config config, org.apache.http.impl.client.HttpClientBuilder builder, String proxyHost, int proxyPort) {
        String username = config.get("http.basicauth.username");
        String password = config.get("http.basicauth.password");
        if (username != null && password != null) {
            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(new AuthScope(proxyHost, proxyPort), new UsernamePasswordCredentials(username, password));
            builder.setDefaultCredentialsProvider(credentialsProvider);
        }
    }

}
