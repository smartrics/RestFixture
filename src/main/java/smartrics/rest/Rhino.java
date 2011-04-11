package smartrics.rest;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import smartrics.rest.fitnesse.fixture.support.Variables;

/**
 * a.
 * 
 * @author fabrizio
 * 
 */
public class Rhino {

    private Rhino() {

    }

    public static void main(String[] args) {
        try {
            Context cx = Context.enter();
            Scriptable scope = cx.initStandardObjects();
            Object result = cx.evaluateString(scope, "json={\"address\" : \"regent street\" }\njson.address == 'regent street'", null, 1, null);
            System.out.println("access json object with dot notation:\n" + Context.toString(result));

            Variables v = new Variables();
            v.put("fab1", "1234");
            Object wrappedVariables = Context.javaToJS(v, scope);
            ScriptableObject.putProperty(scope, "symbols", wrappedVariables);
            v.put("fab2", "4321");
            result = cx.evaluateString(scope, "symbols.get('fab1')+' '+symbols.get('fab2')", null, 1, null);
            System.out.println("wrap fit symbols:\n" + Context.toString(result));

        } finally {
            Context.exit();
        }
    }
}
