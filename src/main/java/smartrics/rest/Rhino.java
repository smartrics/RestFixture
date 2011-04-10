package smartrics.rest;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

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
            Object result = cx.evaluateString(scope, "json={\"address\" : \"rue de la mignot\" }\njson.address.xxx", null, 1, null);
            System.out.println(Context.toString(result));
        } finally {
            Context.exit();
        }
    }
}
