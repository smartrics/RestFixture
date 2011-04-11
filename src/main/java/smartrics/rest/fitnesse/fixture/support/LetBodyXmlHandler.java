package smartrics.rest.fitnesse.fixture.support;

import java.util.Map;

import org.w3c.dom.NodeList;

import smartrics.rest.client.RestResponse;

/**
 * Handles let expressions on XML content, returning XML string rather than the
 * string with the content within the tags.
 * 
 * @author fabrizio
 * 
 */
public class LetBodyXmlHandler implements LetHandler {

    @Override
    public String handle(RestResponse response, Object expressionContext, String expression) {
        @SuppressWarnings("unchecked")
        Map<String, String> namespaceContext = (Map<String, String>) expressionContext;
        NodeList list = Tools.extractXPath(namespaceContext, expression, response.getBody());
        String val = Tools.xPathResultToXmlString(list);
        int pos = val.indexOf("?>");
        if (pos >= 0) {
            val = val.substring(pos + 2);
        }
        return val;
    }

}
