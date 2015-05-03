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
package smartrics.rest.fitnesse.fixture.support;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import smartrics.rest.client.RestResponse;
import smartrics.rest.fitnesse.fixture.RunnerVariablesProvider;

/**
 * Test class for the js body handler.
 * 
 * @author smartrics
 * 
 */
public class JavascriptWrapperTest {

    private FitVariables variables;
    private final RunnerVariablesProvider variablesProvider = new RunnerVariablesProvider() {
		@Override
		public Variables createRunnerVariables() {
			return variables;
		}        	
    };

    @Before
    public void setUp() {
        variables = new FitVariables();
        variables.clearAll();
    }

    @Test
    public void shouldProvideSymbolMapInJsContext() {
        variables.put("my_sym", "98");
        RestResponse response = new RestResponse();
        JavascriptWrapper h = new JavascriptWrapper(variablesProvider);
        Object res = h.evaluateExpression(response, "'my sym is: ' + symbols.get('my_sym')");
        assertThat(res.toString(), is(equalTo("my sym is: 98")));
    }

    @Test
    public void shouldProvideLastResponseBodyInJsContext() {
        RestResponse response = createResponse();
        JavascriptWrapper h = new JavascriptWrapper(variablesProvider);
        Object res = h.evaluateExpression(response, "'my last response body is: ' + response.body");
        assertThat(res.toString(), is(equalTo("my last response body is: <xml />")));
    }

    @Test
    public void shouldProvideLastResponseBodyAsJsonForJsonContentTypeInJsContext() {
        String json = "{ \"person\" : { \"name\" : \"Rokko\", \"age\" : \"30\" } }";
        RestResponse response = createResponse(ContentType.JSON, json);
        JavascriptWrapper h = new JavascriptWrapper(variablesProvider);
        Object res = h.evaluateExpression(response, "'My friend ' + response.jsonbody.person.name + ' is ' + response.jsonbody.person.age + ' years old.'");
        assertThat(res.toString(), is(equalTo("My friend Rokko is 30 years old.")));
    }

    @Test
    public void shouldProvideLastResponseBodyAsJsonForContentThatLooksLikeJsonInJsContext() {
        String json = "{ \"person\" : { \"name\" : \"Rokko\", \"age\" : \"30\" } }";
        RestResponse response = createResponse(ContentType.TEXT, json);
        JavascriptWrapper h = new JavascriptWrapper(variablesProvider);
        Object res = h.evaluateExpression(response, "'My friend ' + response.jsonbody.person.name + ' is ' + response.jsonbody.person.age + ' years old.'");
        assertThat(res.toString(), is(equalTo("My friend Rokko is 30 years old.")));
    }

    @Test
    public void shouldNotProvideLastResponseBodyInJsContextIfResponseIsNull() {
        JavascriptWrapper h = new JavascriptWrapper(variablesProvider);
        Object res = h.evaluateExpression((RestResponse) null, "'response is null: ' + (response == null)");
        assertThat(res.toString(), is(equalTo("response is null: true")));
    }

    @Test
    public void shouldHandleNullReturnedByJsEvaluation() {
        JavascriptWrapper h = new JavascriptWrapper(variablesProvider);
        Object res = h.evaluateExpression((RestResponse) null, "null");
        assertThat(res, is(nullValue()));
    }

    @Test
    public void shouldHandleNullReturnedByStringJsEvaluation() {
        JavascriptWrapper h = new JavascriptWrapper(variablesProvider);
        Object res = h.evaluateExpression((String) null, "null");
        assertThat(res, is(nullValue()));
    }

    @Test
    public void shouldProvideLastResponseResourceInJsContext() {
        RestResponse response = createResponse();
        JavascriptWrapper h = new JavascriptWrapper(variablesProvider);
        Object res = h.evaluateExpression(response, "'my last response resource is: ' + response.resource");
        assertThat(res.toString(), is(equalTo("my last response resource is: /resources")));
    }

    @Test
    public void shouldProvideLastResponseStatusTextInJsContext() {
        RestResponse response = createResponse();
        JavascriptWrapper h = new JavascriptWrapper(variablesProvider);
        Object res = h.evaluateExpression(response, "'my last response statusText is: ' + response.statusText");
        assertThat(res.toString(), is(equalTo("my last response statusText is: OK")));
    }

    @Test
    public void shouldProvideLastResponseTxIdInJsContext() {
        RestResponse response = createResponse();
        JavascriptWrapper h = new JavascriptWrapper(variablesProvider);
        Object res = h.evaluateExpression(response, "'my last response transactionId is: ' + response.transactionId");
        assertThat(res.toString(), is(equalTo("my last response transactionId is: 123456789")));
    }

    @Test
    public void shouldProvideLastResponseStatusCodeInJsContext() {
        RestResponse response = createResponse();
        JavascriptWrapper h = new JavascriptWrapper(variablesProvider);
        Object res = h.evaluateExpression(response, "'my last response statusCode is: ' + response.statusCode");
        assertThat(res.toString(), is(equalTo("my last response statusCode is: 200")));
    }

    @Test
    public void shouldProvideLastResponseHeadersInJsContext() {
        RestResponse response = createResponse();
        JavascriptWrapper h = new JavascriptWrapper(variablesProvider);

        Object res = h.evaluateExpression(response, "'my last response Content-Type is: ' + response.header('Content-Type')");
        assertThat(res.toString(), is(equalTo("my last response Content-Type is: application/xml")));

        res = h.evaluateExpression(response, "'my last response Content-Length is: ' + response.header0('Content-Length')");
        assertThat(res.toString(), is(equalTo("my last response Content-Length is: 7")));

        res = h.evaluateExpression(response, "'my last response Bespoke-Header[0] is: ' + response.header('Bespoke-Header', 0)");
        assertThat(res.toString(), is(equalTo("my last response Bespoke-Header[0] is: jolly")));

        res = h.evaluateExpression(response, "'my last response Bespoke-Header[1] is: ' + response.header('Bespoke-Header', 1)");
        assertThat(res.toString(), is(equalTo("my last response Bespoke-Header[1] is: good")));

        res = h.evaluateExpression(response, "'my last response Bespoke-Header zise is: ' + response.headerListSize('Bespoke-Header')");
        assertThat(res.toString(), is(equalTo("my last response Bespoke-Header zise is: 2")));

        res = h.evaluateExpression(response, "'my last response Bespoke-Header: ' + response.headers('Bespoke-Header')");
        assertThat(res.toString(), is(equalTo("my last response Bespoke-Header: [jolly, good]")));

        res = h.evaluateExpression(response, "'my last response does not have Ciccio header: ' + response.header0('Ciccio')");
        assertThat(res.toString(), is(equalTo("my last response does not have Ciccio header: null")));
    }

    @Test
    public void shouldTrapJavascriptErrorAndWrapThemInErrors() throws Exception {
        RestResponse response = createResponse();
        JavascriptWrapper h = new JavascriptWrapper(variablesProvider);
        try {
            h.evaluateExpression(response, "some erroneous javascript");
            fail("Must throw a Javascript Exception");
        } catch (JavascriptException e) {
            // ignore message assertion as it may be localised (see issue #113)
        }
    }

    private RestResponse createResponse() {
        RestResponse r = createResponse(ContentType.XML, "<xml />");
        return r;
    }

    private RestResponse createResponse(ContentType contentType, String body) {
        RestResponse response = new RestResponse();
        response.setResource("/resources");
        response.setStatusCode(200);
        response.setStatusText("OK");
        response.setBody(body);
        response.addHeader("Content-Type", contentType.toMime().get(0));
        response.addHeader("Bespoke-Header", "jolly");
        response.addHeader("Bespoke-Header", "good");
        response.addHeader("Content-Length", "7");
        response.setTransactionId(123456789L);
        return response;
    }
}
