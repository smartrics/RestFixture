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
package smartrics.rest.fitnesse.fixture;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import smartrics.rest.client.RestClient;
import smartrics.rest.client.RestRequest;
import smartrics.rest.fitnesse.fixture.support.Config;
import smartrics.rest.fitnesse.fixture.support.Variables;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class PartsFactoryTest {

    private PartsFactory f;

    @Before
    public void setUp() {
        Config c = Config.getConfig();
        c.add("http.client.use.new.http.uri.factory", "false");
        f = new PartsFactory(new RunnerVariablesProvider() {
			@Override
			public Variables createRunnerVariables() {
				return null;
			}
		}, c);
    }

    @Test
    public void cannotBuildACellFormatterForANullRunner() {
        try {
            f.buildCellFormatter(null);
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is(equalTo("Runner is null")));
        }
    }

    @Test
    public void buildsRestRequest() {
        assertThat(f.buildRestRequest(), is(instanceOf(RestRequest.class)));
    }

    @Test
    public void buildsRestClientWithStandardUri() throws Exception {
        Config c = Config.getConfig();
        RestClient restClient = f.buildRestClient(c, true);
        assertThat(restClient, is(instanceOf(RestClient.class)));
        Method m = getCreateUriMethod(restClient);
        m.setAccessible(true);
        Object r = m.invoke(restClient, "http://localhost:9900?something", false);
        assertThat(r, is(instanceOf(URI.class)));
        assertThat(r.toString(), is(equalTo("http://localhost:9900?something")));
    }

    @Test
    public void buildsRestClientWithoutSquareBracketsInUri() throws Exception {
        // URI validation will throw an exception as per httpclient 3.1
        Config c = Config.getConfig();
        RestClient restClient = f.buildRestClient(c, true);
        assertThat(restClient, is(instanceOf(RestClient.class)));
        Method m = getCreateUriMethod(restClient);
        m.setAccessible(true);
        try {
            m.invoke(restClient, "http://localhost:9900?something[data]=1", true);
        } catch(InvocationTargetException e) {
            assertThat(e.getCause(), is(instanceOf(URISyntaxException.class)));
        }
    }

    @Test
    @Ignore("to check if this is still needed.")
    public void buildsRestClientWithEscapedSquareBracketsInUri() throws Exception {
        // URI will be escaped as per httpclient 3.1
        Config c = Config.getConfig();
        RestClient restClient = f.buildRestClient(c, true);
        assertThat(restClient, is(instanceOf(RestClient.class)));
        Method m = getCreateUriMethod(restClient);
        m.setAccessible(true);
        Object r = m.invoke(restClient, "http://localhost:9900?something[data]=1", false);
        assertThat(r, is(instanceOf(URI.class)));
        assertThat(r.toString(), is(equalTo("http://localhost:9900?something%5Bdata%5D=1")));
    }

    @Test
    public void buildsRestClientWithDefaultURIFactory() throws Exception {
        Config c = Config.getConfig();
        RestClient restClient = f.buildRestClient(c, true);
        assertThat(restClient, is(instanceOf(RestClient.class)));
        Method m = getGetMethodClassnameFromMethodNameMethod(restClient);
        m.setAccessible(true);
        Object r = m.invoke(restClient, "Some");
        assertThat(r.toString(), is(equalTo("org.apache.http.client.methods.HttpSome")));
    }

    @Test
    public void cantBuildACellFormatterForNonFitOrSlimRunner() {
        try {
            f.buildCellFormatter(RestFixture.Runner.OTHER);
        } catch (IllegalStateException e) {
            assertThat(e.getMessage(), is(equalTo("Runner OTHER not supported")));
        }
    }

    @Test
    public void buildsASlimFormatterForSLIMRunner() {
        assertThat(f.buildCellFormatter(RestFixture.Runner.SLIM), is(instanceOf(SlimFormatter.class)));
    }

    @Test
    public void buildsASlimFormatterForFITRunner() {
        assertThat(f.buildCellFormatter(RestFixture.Runner.FIT), is(instanceOf(FitFormatter.class)));
    }

    private Method getCreateUriMethod(RestClient client) {
        try {
            return client.getClass().getDeclaredMethod("createUri", String.class, boolean.class);
        } catch (SecurityException e) {
            throw new IllegalArgumentException("Can't access method");
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Method doesn't exist");
        }
    }

    private Method getGetMethodClassnameFromMethodNameMethod(RestClient client) {
        try {
            return client.getClass().getMethod("getMethodClassnameFromMethodName", String.class);
        } catch (SecurityException e) {
            throw new IllegalArgumentException("Can't access method");
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Method doesn't exist");
        }
    }
}
