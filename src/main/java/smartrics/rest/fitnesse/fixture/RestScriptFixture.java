package smartrics.rest.fitnesse.fixture;

import java.util.List;

import smartrics.rest.client.RestData.Header;
import smartrics.rest.config.Config;
import smartrics.rest.fitnesse.fixture.support.BodyTypeAdapter;
import smartrics.rest.fitnesse.fixture.support.ContentType;
import smartrics.rest.fitnesse.fixture.support.HeadersTypeAdapter;
import smartrics.rest.fitnesse.fixture.support.LetHandler;
import smartrics.rest.fitnesse.fixture.support.LetHandlerFactory;
import smartrics.rest.fitnesse.fixture.support.RestDataTypeAdapter;

/**
 * Based on work on https://github.com/ggramlich/RestFixture
 * @author Geoffrey Dunn <geoff@warmage.com>
 *
 */
public class RestScriptFixture extends RestFixture {

    public RestScriptFixture(String hostName) {
        super(hostName);
        initialize();
    }

    public RestScriptFixture(String hostName, String configName) {
        super(hostName, configName);
        initialize();
    }
    
    /**
     * Constructor for test interfaces
     */
    RestScriptFixture(PartsFactory partsFactory, String hostName) {
        this(partsFactory, hostName, Config.DEFAULT_CONFIG_NAME);
    }

    /**
     * Constructor for test interfaces
     */
    RestScriptFixture(PartsFactory partsFactory, String hostName, String configName) {
    	super(partsFactory, hostName, configName);
    }
    

    private void initialize() {
        initialize(Runner.SLIM);
    }

    /**
     * <code> | get | URL |</code>
     * <p/>
     * executes a GET on the URL
     */
    public void get(String resourceUrl) {
        doMethod("Get", resourceUrl, null);
    }

    /**
     * <code> | post | URL |</code>
     * <p/>
     * executes a POST on the URL
     */
    public void post(String resourceUrl) {
        doMethod("Post", resourceUrl, emptifyBody(requestBody));
    }

    /**
     * <code> | put | URL |</code>
     * <p/>
     * executes a PUT on the URL
     */
    public void put(String resourceUrl) {
        doMethod("Put", resourceUrl, emptifyBody(requestBody));
    }

    /**
     * <code> | delete | URL |</code>
     * <p/>
     * executes a DELETE on the URL
     */
    public void delete(String resourceUrl) {
        doMethod("Delete", resourceUrl, null);
    }

    /**
     * <code> | $var= | header | HEADER |</code>
     * <code> | show  | header | HEADER |</code>
     * <code> | check | header | HEADER | ?value |</code>
     * <p/>
     * Can extract value of a header element using a regular expression
     */
    public String header(String expr) {
        return applyExpressionToLastResponse("header", expr);
    }

    /**
     * <code> | $var= | body | BODY |</code>
     * <code> | show  | body | BODY |</code>
     * <code> | check | body | BODY | ?value |</code>
     * <p/>
     * Can extract part of the body using a xpath or regular expression
     */
    public String body(String expr) {
        return applyExpressionToLastResponse("body", expr);
    }

    /**
     * <code> | $var= | js | BODY |</code>
     * <code> | show  | js | BODY |</code>
     * <code> | check | js | BODY | ?value |</code>
     * <p/>
     * Can extract part of the body using a javascript expression
     */
    public String js(String expr) {
        return applyExpressionToLastResponse("js", expr);
    }

    /**
     * <code> |set file name |FILE| </code>
     * <p/>
     * Set a file to use for a simple file post operation
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    /**
     * <code> |set multipart file name |FILE| </code>
     * <p/>
     * Set a file to use for a post operation
     */
    public void setMultipartFileName(String multipartFileName) {
        this.multipartFileName = multipartFileName;
    }

    /**
     * <code> |set multipart file parameter name |FILENAME| </code>
     * <p/>
     * Set a filename to use with a mutlipart file for a post operation
     */
    public void setMultipartFileParameterName(String multipartFileParameterName) {
        this.multipartFileParameterName = multipartFileParameterName;
    }

    /**
     * <code> | check | status code | ?code |</code>
     * <code> | show  | status code |</code>
     * <code> | $var= | status code |</code>
     * <p/>
     * Checks if the last url action returned a status code matching CODE. Show can
     * be used to output the status code or it can be assigned to a variable
     */
    public Integer statusCode() {
        if (getLastResponse() == null) {
            return -1;
        }
        return getLastResponse().getStatusCode();
    }

    /**
     * <code> | show  | response body |</code>
     * <code> | check | response body | ?body |</code>
     * <code> | $var= | response body |</code>
     * <p/>
     * Show the body from the last url action. Can be also used to check the contents
     * but is only recommended for simple text body values
     */
    public String responseBody() {
        if (getLastResponse() == null) {
            return null;
        }
        return getLastResponse().getBody();
    }

    /**
     * <code> | ensure  | has body | ?body | </code>
     * <code> | reject  | has body | ?body | </code>
     * <p/>
     * Compare the body from the last url action using a body value or
     * special comparator type
     */
    public boolean hasBody(String expected) throws Exception {
        BodyTypeAdapter bodyTypeAdapter = createBodyTypeAdapter();
        String actual = responseBody();
        return equalsWithAdapter(expected, actual, bodyTypeAdapter);
    }

    /**
     * <code> | ensure  | has body | ?body | using type | TYPE | </code>
     * <code> | reject  | has body | ?body | using type | TYPE | </code>
     * <p/>
     * Compare the body from the last url action using special comparator type
     * where the type application/json, text/plain or application/x-javascript
     * can be forced 
     */
    public boolean hasBodyUsingType(String expected, String type) throws Exception {
        ContentType ct = ContentType.typeFor(type);
        BodyTypeAdapter bodyTypeAdapter = createBodyTypeAdapter(ct);
        String actual = responseBody();
        return equalsWithAdapter(expected, actual, bodyTypeAdapter);
    }
    
    /**
     * Wrapper for hasHeaders
     */
    public boolean hasHeaders(String expected) throws Exception {
        return hasHeader(expected);
    }

    /**
     * <code> | ensure  | has headers | ?header | </code>
     * <code> | reject  | has headers | ?header | </code>
     * <p/>
     * Compare the headers from the last url action using a header value or
     * special comparator type
     */
    public boolean hasHeader(String expected) throws Exception {
        return equalsWithAdapter(expected, getResponseHeaders(), new HeadersTypeAdapter());
    }
    
    /**
     * <code> | set body  | BODY | </code>
     * <p/>
     * Set the body used for a PUT or POST url action
     */
    public void setBody(String text) {
    	requestBody = GLOBALS.substitute(text);
    }

    /**
     * <code> | set header  | HEADER | </code>
     * <p/>
     * Set the headers used for a url action
     */
    public void setHeader(String text) {
    	setHeaders(text);
    }

    private boolean equalsWithAdapter(String expected, Object actual, RestDataTypeAdapter typeAdapter) throws Exception {
        typeAdapter.set(actual);
        Object parse = typeAdapter.parse(expected);
        return typeAdapter.equals(parse, actual);
    }

    private String applyExpressionToLastResponse(String type, String expr) {
        LetHandler letHandler = LetHandlerFactory.getHandlerFor(type);
        return GLOBALS.replaceNull(letHandler.handle(getLastResponse(), getNamespaceContext(), expr));
    }

    private List<Header> getResponseHeaders() {
        if (getLastResponse() == null) {
            return null;
        }
        return getLastResponse().getHeaders();
    }

}