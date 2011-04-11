package smartrics.rest.fitnesse.fixture.support;

import smartrics.rest.client.RestResponse;

/**
 * Strategy to handle LET expressions.
 * 
 * @author fabrizio
 * 
 */
public interface LetHandler {

    String handle(RestResponse response, Object expressionContext, String expression);

}
