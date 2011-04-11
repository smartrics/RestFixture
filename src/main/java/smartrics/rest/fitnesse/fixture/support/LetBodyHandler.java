package smartrics.rest.fitnesse.fixture.support;

import java.util.Map;

import javax.xml.xpath.XPathConstants;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import smartrics.rest.client.RestResponse;

/**
 * Handles body of the last response on behalf of LET in RestFixture.
 * 
 * @author fabrizio
 * 
 */
public class LetBodyHandler implements LetHandler {

    @Override
    public String handle(RestResponse response, Object expressionContext, String expression) {
        @SuppressWarnings("unchecked")
        Map<String, String> namespaceContext = (Map<String, String>) expressionContext;
        BodyTypeAdapter bodyTypeAdapter = BodyTypeAdapterFactory.getBodyTypeAdapter(ContentType.parse(response.getHeader("Content-Type")));

        String body = bodyTypeAdapter.toXmlString(response.getBody());

        if (body == null) {
            return null;
        }
        String val = null;
        try {
            NodeList list = Tools.extractXPath(namespaceContext, expression, body);
            Node item = list.item(0);
            if (item != null) {
                val = item.getTextContent();
            }
        } catch (IllegalArgumentException e) {
            // ignore - may be that it's evaluating to a string
            val = (String) Tools.extractXPath(namespaceContext, expression, body, XPathConstants.STRING);
        }
        if (val != null) {
            val = val.trim();
        }
        return val;
    }
}
