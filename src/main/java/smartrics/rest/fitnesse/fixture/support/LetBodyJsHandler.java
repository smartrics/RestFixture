package smartrics.rest.fitnesse.fixture.support;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import smartrics.rest.client.RestData.Header;
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
        Context context = Context.enter();
        ScriptableObject scope = context.initStandardObjects();
        Variables v = new Variables();
        Object wrappedVariables = Context.javaToJS(v, scope);
        ScriptableObject.putProperty(scope, "symbols", wrappedVariables);
        injectResponse("response", context, scope, response);
        Object result = context.evaluateString(scope, expression, null, 1, null);
        return result.toString();
    }

    private void injectResponse(String jsName, Context cx, ScriptableObject scope, RestResponse r) {
        try {
            ScriptableObject.defineClass(scope, JsResponse.class);
            Scriptable response = null;
            if (r != null) {
                Object[] arg = new Object[1];
                arg[0] = r;
                response = cx.newObject(scope, "JsResponse", arg);
                putPropertyOnJsObject(response, "body", r.getBody());
                putPropertyOnJsObject(response, "resource", r.getResource());
                putPropertyOnJsObject(response, "statusText", r.getStatusText());
                putPropertyOnJsObject(response, "statusCode", r.getStatusCode());
                putPropertyOnJsObject(response, "transactionId", r.getTransactionId());
                for (Header h : r.getHeaders()) {
                    callMethodOnJsObject(response, "addHeader", h.getName(), h.getValue());
                }
            }
            scope.put(jsName, scope, response);

        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void callMethodOnJsObject(Scriptable o, String mName, Object... arg) {
        ScriptableObject.callMethod(o, mName, arg);
    }

    private void putPropertyOnJsObject(Scriptable o, String mName, Object value) {
        ScriptableObject.putProperty(o, mName, value);
    }

    /**
     * Wrapper class for Response to be embedded in the Rhino Context.
     * 
     * @author fabrizio
     * 
     */
    public static class JsResponse extends ScriptableObject {
        private Map<String, List<String>> headers;

        public JsResponse() {

        }

        public void jsConstructor() {
            headers = new HashMap<String, List<String>>();
        }

        @Override
        public String getClassName() {
            return "JsResponse";
        }

        public void jsFunction_addHeader(String name, String value) {
            List<String> vals = headers.get(name);
            if (vals == null) {
                vals = new ArrayList<String>();
                headers.put(name, vals);
            }
            vals.add(value);
        }

        public void jsFunction_putHeader(String name, String value) {
            List<String> vals = new ArrayList<String>();
            vals.add(value);
            headers.put(name, vals);
        }

        public int jsFunction_headerListSize(String name) {
            List<String> vals = headers.get(name);
            if (vals == null || vals.size() == 0) {
                return 0;
            }
            return vals.size();
        }

        public int jsFunction_headersSize() {
            int sz = 0;
            for (List<String> hList : headers.values()) {
                sz += hList.size();
            }
            return sz;
        }

        public String jsFunction_header0(String name) {
            return jsFunction_header(name, 0);
        }

        public List<String> jsFunction_headers(String name) {
            int sz = jsFunction_headerListSize(name);
            if (sz > 0) {
                return headers.get(name);
            } else {
                return new ArrayList<String>();
            }
        }

        public String jsFunction_header(String name, int pos) {
            if (jsFunction_headerListSize(name) > 0) {
                return headers.get(name).get(pos);
            } else {
                return null;
            }
        }

    }

}
