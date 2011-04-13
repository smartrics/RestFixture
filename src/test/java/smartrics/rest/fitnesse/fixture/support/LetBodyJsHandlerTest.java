package smartrics.rest.fitnesse.fixture.support;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

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
    }

    private RestResponse createResponse() {
        RestResponse response = new RestResponse();
        response.setBody("<xml />");
        response.setResource("/resources");
        response.setStatusCode(200);
        response.setStatusText("OK");
        response.addHeader("Content-Type", "application/xml");
        response.addHeader("Bespoke-Header", "jolly");
        response.addHeader("Bespoke-Header", "good");
        response.addHeader("Content-Length", "7");
        response.setTransactionId(123456789L);
        return response;
    }
}
