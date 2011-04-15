package smartrics.rest.fitnesse.fixture.support;

/**
 * Signals an error in the evaluation of the JavaScript in LetBodyJsHandler.
 * 
 * @author fabrizio
 * 
 */
public class JavascriptException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public JavascriptException(String message) {
        super(message);
    }
}
