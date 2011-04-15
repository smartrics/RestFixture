package smartrics.rest.fitnesse.fixture.support;

import static org.junit.Assert.assertNull;

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
    public void shouldHandleExpressionsReturningNull() {
        LetBodyJsHandler h = new LetBodyJsHandler();
        String r = h.handle(new RestResponse(), null, "null");
        assertNull(r);
    }
}
