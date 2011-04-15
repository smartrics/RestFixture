package smartrics.rest.fitnesse.fixture.support;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import smartrics.rest.client.RestResponse;

/**
 * Test class for the js body handler.
 * 
 * @author fabrizio
 * 
 */
public class LetBodyJsHandlerTest {

    private Variables variables;

    @Before
    public void setUp() {
        variables = new Variables();
        variables.clearAll();
    }

    @Test
    public void shouldProvideSymbolMapInJsContext() {
        variables.put("my_sym", "98");
        RestResponse response = new RestResponse();
        LetBodyJsHandler h = new LetBodyJsHandler();
        String res = h.handle(response, null, "'my sym is: ' + symbols.get('my_sym')");
        assertThat(res, is(equalTo("my sym is: 98")));
    }

    @Test
    public void shouldProvideLastResponseBodyInJsContext() {
        RestResponse response = createResponse();
        LetBodyJsHandler h = new LetBodyJsHandler();
        String res = h.handle(response, null, "'my last response body is: ' + response.body");
        assertThat(res, is(equalTo("my last response body is: <xml />")));
    }

    @Test
    public void shouldProvideLastResponseBodyAsJsonForJsonContentTypeInJsContext() {
        String json = "{ \"person\" : { \"name\" : \"Rokko\", \"age\" : \"30\" } }";
        RestResponse response = createResponse(ContentType.JSON, json);
        LetBodyJsHandler h = new LetBodyJsHandler();
        String res = h.handle(response, null, "'My friend ' + response.jsonbody.person.name + ' is ' + response.jsonbody.person.age + ' years old.'");
        assertThat(res, is(equalTo("My friend Rokko is 30 years old.")));
    }

    @Test
    public void shouldNotProvideLastResponseBodyInJsContextIfResponseIsNull() {
        LetBodyJsHandler h = new LetBodyJsHandler();
        String res = h.handle(null, null, "'response is null: ' + (response == null)");
        assertThat(res, is(equalTo("response is null: true")));
    }

    @Test
    public void shouldProvideLastResponseResourceInJsContext() {
        RestResponse response = createResponse();
        LetBodyJsHandler h = new LetBodyJsHandler();
        String res = h.handle(response, null, "'my last response resource is: ' + response.resource");
        assertThat(res, is(equalTo("my last response resource is: /resources")));
    }

    @Test
    public void shouldProvideLastResponseStatusTextInJsContext() {
        RestResponse response = createResponse();
        LetBodyJsHandler h = new LetBodyJsHandler();
        String res = h.handle(response, null, "'my last response statusText is: ' + response.statusText");
        assertThat(res, is(equalTo("my last response statusText is: OK")));
    }

    @Test
    public void shouldProvideLastResponseTxIdInJsContext() {
        RestResponse response = createResponse();
        LetBodyJsHandler h = new LetBodyJsHandler();
        String res = h.handle(response, null, "'my last response transactionId is: ' + response.transactionId");
        assertThat(res, is(equalTo("my last response transactionId is: 123456789")));
    }

    @Test
    public void shouldProvideLastResponseStatusCodeInJsContext() {
        RestResponse response = createResponse();
        LetBodyJsHandler h = new LetBodyJsHandler();
        String res = h.handle(response, null, "'my last response statusCode is: ' + response.statusCode");
        assertThat(res, is(equalTo("my last response statusCode is: 200")));
    }

    @Test
    public void shouldProvideLastResponseHeadersInJsContext() {
        RestResponse response = createResponse();
        LetBodyJsHandler h = new LetBodyJsHandler();

        String res = h.handle(response, null, "'my last response Content-Type is: ' + response.header('Content-Type')");
        assertThat(res, is(equalTo("my last response Content-Type is: application/xml")));

        res = h.handle(response, null, "'my last response Content-Length is: ' + response.header0('Content-Length')");
        assertThat(res, is(equalTo("my last response Content-Length is: 7")));

        res = h.handle(response, null, "'my last response Bespoke-Header[0] is: ' + response.header('Bespoke-Header', 0)");
        assertThat(res, is(equalTo("my last response Bespoke-Header[0] is: jolly")));

        res = h.handle(response, null, "'my last response Bespoke-Header[1] is: ' + response.header('Bespoke-Header', 1)");
        assertThat(res, is(equalTo("my last response Bespoke-Header[1] is: good")));

        res = h.handle(response, null, "'my last response Bespoke-Header zise is: ' + response.headerListSize('Bespoke-Header')");
        assertThat(res, is(equalTo("my last response Bespoke-Header zise is: 2")));

        res = h.handle(response, null, "'my last response Bespoke-Header: ' + response.headers('Bespoke-Header')");
        assertThat(res, is(equalTo("my last response Bespoke-Header: [jolly, good]")));

        res = h.handle(response, null, "'my last response does not have Ciccio header: ' + response.header0('Ciccio')");
        assertThat(res, is(equalTo("my last response does not have Ciccio header: null")));
    }

    @Test
    public void shouldTrapJavascriptErrorAndWrapThemInErrors() {
        RestResponse response = createResponse();
        LetBodyJsHandler h = new LetBodyJsHandler();
        try {
            h.handle(response, null, "some erroneous javascript");
            fail("Must throw a Javascript Exception");
        } catch (JavascriptException e) {
            assertThat(e.getMessage(), is(equalTo("missing ; before statement (unnamed script#1)")));
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
        response.addHeader("Content-Type", contentType.toMime());
        response.addHeader("Bespoke-Header", "jolly");
        response.addHeader("Bespoke-Header", "good");
        response.addHeader("Content-Length", "7");
        response.setTransactionId(123456789L);
        return response;
    }
}
