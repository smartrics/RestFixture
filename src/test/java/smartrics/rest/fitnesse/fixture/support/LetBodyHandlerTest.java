package smartrics.rest.fitnesse.fixture.support;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
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
public class LetBodyHandlerTest {

    private Variables variables;

    @Before
    public void setUp() {
        variables = new Variables();
        variables.clearAll();
    }

    @Test
    public void shouldHandleExpressionsReturningNull() {
        LetBodyHandler h = new LetBodyHandler();
        String r = h.handle(new RestResponse(), null, "null");
        assertNull(r);
    }

    @Test
    public void shouldHandleJsBodyWithXPaths() {
        LetBodyHandler h = new LetBodyHandler();
        RestResponse response = new RestResponse();
        response.addHeader("Content-Type", "application/json");
        response.setBody("{\"root\" : {\"accountRef\":\"http://something:8111\",\"label\":\"default\",\"websiteRef\":\"ws1\",\"dispersionRef\":\"http://localhost:8111\"} }");
        String ret = h.handle(response, null, "/root/dispersionRef/text()");
        assertThat(ret, is(equalTo("http://localhost:8111")));
    }
}
