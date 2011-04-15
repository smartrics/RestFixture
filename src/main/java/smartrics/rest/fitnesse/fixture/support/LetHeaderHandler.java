package smartrics.rest.fitnesse.fixture.support;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import smartrics.rest.client.RestData.Header;
import smartrics.rest.client.RestResponse;

/**
 * Handles header (a list of Header objects) LET manipulations.
 * 
 * @author fabrizio
 * 
 */
public class LetHeaderHandler implements LetHandler {

    public LetHeaderHandler() {
    }

    public String handle(RestResponse response, Object expressionContext, String expression) {
        List<String> content = new ArrayList<String>();
        if (response != null) {
            for (Header e : response.getHeaders()) {
                String string = Tools.convertEntryToString(e.getName(), e.getValue(), ":");
                content.add(string);
            }
        }

        String value = null;
        if (content.size() > 0) {
            Pattern p = Pattern.compile(expression);
            for (String c : content) {
                System.err.println("matching " + c + " with " + expression);
                Matcher m = p.matcher(c);
                if (m.find()) {
                    int cc = m.groupCount();
                    value = m.group(cc);
                    break;
                }
            }
        }
        return value;
    }

}
