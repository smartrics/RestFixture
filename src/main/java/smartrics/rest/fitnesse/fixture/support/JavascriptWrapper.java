/*  Copyright 2012 Fabrizio Cannizzo
 *
 *  This file is part of RestFixture.
 *
 *  RestFixture (http://code.google.com/p/rest-fixture/) is free software:
 *  you can redistribute it and/or modify it under the terms of the
 *  GNU Lesser General Public License as published by the Free Software Foundation,
 *  either version 3 of the License, or (at your option) any later version.
 *
 *  RestFixture is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with RestFixture.  If not, see <http://www.gnu.org/licenses/>.
 *
 *  If you want to contact the author please leave a comment here
 *  http://smartrics.blogspot.com/2008/08/get-fitnesse-with-some-rest.html
 */
package smartrics.rest.fitnesse.fixture.support;

import org.mozilla.javascript.*;
import smartrics.rest.client.RestData.Header;
import smartrics.rest.client.RestResponse;
import smartrics.rest.fitnesse.fixture.RunnerVariablesProvider;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Wrapper class to all that related to JavaScript.
 *
 * @author smartrics
 */
public class JavascriptWrapper {

    /**
     * the name of the JS object containig the http response: {@code response}.
     */
    public static final String RESPONSE_OBJ_NAME = "response";
    /**
     * the name of the JS object containing the symbol table: {@code symbols}.
     */
    public static final String SYMBOLS_OBJ_NAME = "symbols";
    /**
     * the name of the JS object containing the json body: {@code jsonbody}.
     */
    public static final String JSON_OBJ_NAME = "jsonbody";
    private static final long _64K = 65534;
    private RunnerVariablesProvider variablesProvider;

    public JavascriptWrapper(RunnerVariablesProvider variablesProvider) {
        this.variablesProvider = variablesProvider;
    }

    /**
     * evaluates a Javascript expression in the given {@link RestResponse}.
     *
     * @param response   the {@link RestResponse}
     * @param expression the javascript expression
     * @return the result of the expression evaluation.
     */
    public Object evaluateExpression(RestResponse response, String expression) {
        return evaluateExpression(response, expression, new HashMap<String, String>());
    }

    public Object evaluateExpression(RestResponse response, String expression, Map<String, String> imports) {
        if (expression == null) {
            return null;
        }
        Context context = Context.enter();
        removeOptimisationForLargeExpressions(expression, context);
        ScriptableObject scope = context.initStandardObjects();
        injectImports(context, scope, imports);
        injectFitNesseSymbolMap(scope);
        injectResponse(context, scope, response);
        Object result = evaluateExpression(context, scope, expression);
        return result;
    }

    /**
     * evaluates an expression on a given json object represented as string.
     *
     * @param json       the json object.
     * @param expression the expression.
     * @return the result of the evaluation
     */
    public Object evaluateExpression(String json, String expression) {
        return evaluateExpression(json, expression, new HashMap<String, String>());
    }

    public Object evaluateExpression(String json, String expression, Map<String, String> imports) {
        if (json == null || expression == null) {
            return null;
        }
        Context context = Context.enter();
        ScriptableObject scope = context.initStandardObjects();
        injectImports(context, scope, imports);
        injectFitNesseSymbolMap(scope);
        injectJson(context, scope, json);
        Object result = evaluateExpression(context, scope, expression);
        return result;
    }

    /**
     * @param json the potential json string. loosely checks if the input string contains {@link JavascriptWrapper#JSON_OBJ_NAME}.
     * @return whether it's actually a json object.
     */
    public boolean looksLikeAJsExpression(String json) {
        return json != null && json.contains(JSON_OBJ_NAME + ".");
    }

    private void injectFitNesseSymbolMap(ScriptableObject scope) {
        Variables v = variablesProvider.createRunnerVariables();
        Object wrappedVariables = Context.javaToJS(v, scope);
        ScriptableObject.putProperty(scope, SYMBOLS_OBJ_NAME, wrappedVariables);
    }

    private void injectJson(Context cx, ScriptableObject scope, String json) {
        evaluateExpression(cx, scope, "var " + JSON_OBJ_NAME + "=" + json);
    }

    private Object evaluateExpression(Context context, ScriptableObject scope,
                                      String expression) {
        try {
            Object result = context.evaluateString(scope, expression, null, 1,
                    null);
            return result;
        } catch (EvaluatorException e) {
            throw new JavascriptException(e.getMessage());
        } catch (EcmaError e) {
            throw new JavascriptException(e.getMessage());
        }
    }

    private void injectResponse(Context cx, ScriptableObject scope,
                                RestResponse r) {
        try {
            ScriptableObject.defineClass(scope, JsResponse.class);
            Scriptable response = null;
            if (r == null) {
                scope.put(RESPONSE_OBJ_NAME, scope, response);
                return;
            }
            Object[] arg = new Object[1];
            arg[0] = r;
            response = cx.newObject(scope, "JsResponse", arg);
            scope.put(RESPONSE_OBJ_NAME, scope, response);
            putPropertyOnJsObject(response, "body", r.getBody());
            putPropertyOnJsObject(response, JSON_OBJ_NAME, null);
            boolean isJson = isJsonResponse(r);
            if (isJson) {
                evaluateExpression(cx, scope, RESPONSE_OBJ_NAME + "."
                        + JSON_OBJ_NAME + "=" + r.getBody());
            }
            putPropertyOnJsObject(response, "resource", r.getResource());
            putPropertyOnJsObject(response, "statusText", r.getStatusText());
            putPropertyOnJsObject(response, "statusCode", r.getStatusCode());
            putPropertyOnJsObject(response, "transactionId", r.getTransactionId());
            for (Header h : r.getHeaders()) {
                callMethodOnJsObject(response, "addHeader", h.getName(), h.getValue());
            }
        } catch (IllegalAccessException e) {
            throw new JavascriptException(e.getMessage());
        } catch (InstantiationException e) {
            throw new JavascriptException(e.getMessage());
        } catch (InvocationTargetException e) {
            throw new JavascriptException(e.getMessage());
        }
    }

    private void callMethodOnJsObject(Scriptable o, String mName, Object... arg) {
        ScriptableObject.callMethod(o, mName, arg);
    }

    private void putPropertyOnJsObject(Scriptable o, String mName, Object value) {
        ScriptableObject.putProperty(o, mName, value);
    }

    private boolean isJsonResponse(RestResponse r) {
        if (ContentType.JSON.equals(ContentType.parse(r.getContentType()))) {
            return true;
        }
        if (r.getBody() != null && r.getBody().trim().matches("\\{.+\\}")) {
            return Tools.isValidJson(r.getBody());
        }
        return false;
    }

    private void removeOptimisationForLargeExpressions(String expression, Context context) {
        if (expression.getBytes().length > _64K) {
            context.setOptimizationLevel(-1);
        }
    }

    private void injectImports(Context context, ScriptableObject scope, Map<String, String> imports) {
        for (Map.Entry<String, String> e : imports.entrySet()) {
            injectImports(context, scope, e.getKey(), e.getValue());
        }

    }

    private void injectImports(Context context, ScriptableObject scope, String name, String importUrl) {
        InputStream is = null;
        try {
            is = parseImport(importUrl);
            BufferedReader in = new BufferedReader(new InputStreamReader(is));
            context.evaluateReader(scope, in, name, 1, null);
        } catch (Exception e) {
            throw new JavascriptException("Invalid url: " + importUrl + " for '" + name + "'", e);
        } finally {
            if(is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }

    }

    /**
     * Wrapper class for Response to be embedded in the Rhino Context.
     *
     * @author smartrics
     */
    public static class JsResponse extends ScriptableObject {
        private static final long serialVersionUID = 5441026774653915695L;

        private Map<String, List<String>> headers;

        /**
         * def ctor.
         */
        public JsResponse() {

        }

        /**
         * initialises internal headers map.
         */
        public void jsConstructor() {
            headers = new HashMap<String, List<String>>();
        }

        @Override
        public String getClassName() {
            return "JsResponse";
        }

        /**
         * @param name
         * @param value
         */
        public void jsFunction_addHeader(String name, String value) {
            List<String> vals = headers.get(name);
            if (vals == null) {
                vals = new ArrayList<String>();
                headers.put(name, vals);
            }
            vals.add(value);
        }

        /**
         * @param name
         * @param value
         */
        public void jsFunction_putHeader(String name, String value) {
            List<String> vals = new ArrayList<String>();
            vals.add(value);
            headers.put(name, vals);
        }

        /**
         * @param name
         * @return the headers list size
         */
        public int jsFunction_headerListSize(String name) {
            List<String> vals = headers.get(name);
            if (vals == null || vals.size() == 0) {
                return 0;
            }
            return vals.size();
        }

        /**
         * @return the total number of headers in the response.
         */
        public int jsFunction_headersSize() {
            int sz = 0;
            for (List<String> hList : headers.values()) {
                sz += hList.size();
            }
            return sz;
        }

        /**
         * @param name
         * @return the value of the header name in position 0
         */
        public String jsFunction_header0(String name) {
            return jsFunction_header(name, 0);
        }

        /**
         * @param name
         * @return all headers with the given name
         */
        public List<String> jsFunction_headers(String name) {
            int sz = jsFunction_headerListSize(name);
            if (sz > 0) {
                return headers.get(name);
            } else {
                return new ArrayList<String>();
            }
        }

        /**
         * @param name
         * @param pos
         * @return the value of the header with name at pos 0
         */
        public String jsFunction_header(String name, int pos) {
            if (jsFunction_headerListSize(name) > 0) {
                return headers.get(name).get(pos);
            } else {
                return null;
            }
        }

    }

    private InputStream parseImport(String name) {
        try {
            return new URL(name).openStream();
        } catch (Exception e1) {
            File f = new File(name);
            if (f.exists()) {
                if(f.isFile() && f.canRead()) {
                    try {
                        return new FileInputStream(f);
                    } catch (Exception e2) {
                        throw new IllegalArgumentException("Invalid import file: " + name + ", path: " + f.getAbsolutePath());
                    }
                } else {
                    throw new IllegalArgumentException("Import file not accessible: " + name + ", path: " + f.getAbsolutePath());
                }
            } else {
                try {
                    return Thread.currentThread().getContextClassLoader().getResource(name).openStream();
                } catch (Exception e3) {
                    throw new IllegalArgumentException("Import resource not valid: " + name);
                }
            }
        }
    }

}
