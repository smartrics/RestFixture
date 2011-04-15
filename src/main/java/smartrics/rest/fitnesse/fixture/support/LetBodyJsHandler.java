package smartrics.rest.fitnesse.fixture.support;

import smartrics.rest.client.RestResponse;

/**
 * Handles let expressions on XML content, returning XML string rather than the
 * string with the content within the tags.
 * 
 * @author fabrizio
 * 
 */
public class LetBodyJsHandler implements LetHandler {

    @Override
    public String handle(RestResponse response, Object expressionContext, String expression) {
        JavascriptWrapper js = new JavascriptWrapper();
        Object result = js.evaluateExpression(response, expression);
        if (result == null) {
            return null;
        }
        return result.toString();
    }


}
