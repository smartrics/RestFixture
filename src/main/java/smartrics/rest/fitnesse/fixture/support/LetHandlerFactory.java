package smartrics.rest.fitnesse.fixture.support;

import java.util.HashMap;
import java.util.Map;

/**
 * Builds strategies to handle LET body.
 * 
 * @author fabrizio
 * 
 */
public class LetHandlerFactory {
    private static Map<String, LetHandler> strategies = new HashMap<String, LetHandler>();

    static {
        strategies.put("header", new LetHeaderHandler());
        strategies.put("body", new LetBodyHandler());
        strategies.put("body:xml", new LetBodyXmlHandler());
        // strategies.put("js", new LetJsHandler());
    }

    private LetHandlerFactory() {

    }

    public static LetHandler getHandlerFor(String part) {
        return strategies.get(part);
    }
}
