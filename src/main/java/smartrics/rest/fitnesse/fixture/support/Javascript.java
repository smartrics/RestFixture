package smartrics.rest.fitnesse.fixture.support;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;

/**
 * Facade class to a javascript interpreter that exposes the necessary API to
 * perform basic expression manipulation and string evaluation.
 * 
 * Current implementation is based on Mozilla Rhino.
 * 
 * @author fabrizio
 * 
 */
public class Javascript {

    private Context context;
    private ScriptableObject scope;

    public Javascript() {
        context = Context.enter();
        scope = context.initStandardObjects();
    }
}
