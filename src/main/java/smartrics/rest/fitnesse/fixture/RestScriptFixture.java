package smartrics.rest.fitnesse.fixture;

import java.util.List;

import smartrics.rest.client.RestData.Header;
import smartrics.rest.fitnesse.fixture.support.BodyTypeAdapter;
import smartrics.rest.fitnesse.fixture.support.ContentType;
import smartrics.rest.fitnesse.fixture.support.HeadersTypeAdapter;
import smartrics.rest.fitnesse.fixture.support.LetHandler;
import smartrics.rest.fitnesse.fixture.support.LetHandlerFactory;
import smartrics.rest.fitnesse.fixture.support.RestDataTypeAdapter;

/**
 * Based on work on https://github.com/ggramlich/RestFixture
 * @author Geoffreyd
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

    private void initialize() {
        initialize(Runner.SLIM);
    }

    public void get(String resourceUrl) {
        doMethod("Get", resourceUrl, null);
    }

    public void post(String resourceUrl) {
        doMethod("Post", resourceUrl, emptifyBody(getRequestBody()));
    }

    public void put(String resourceUrl) {
        doMethod("Put", resourceUrl, emptifyBody(getRequestBody()));
    }

    public void delete(String resourceUrl) {
        doMethod("Delete", resourceUrl, null);
    }

    public String header(String expr) {
        return applyExpressionToLastResponse("header", expr);
    }

    public String body(String expr) {
        return applyExpressionToLastResponse("body", expr);
    }

    public String js(String expr) {
        return applyExpressionToLastResponse("js", expr);
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setMultipartFileName(String multipartFileName) {
        this.multipartFileName = multipartFileName;
    }

    public void setMultipartFileParameterName(String multipartFileParameterName) {
        this.multipartFileParameterName = multipartFileParameterName;
    }

    public Integer statusCode() {
        if (getLastResponse() == null) {
            return -1;
        }
        return getLastResponse().getStatusCode();
    }

    public List<Header> headers() {
        if (getLastResponse() == null) {
            return null;
        }
        return getLastResponse().getHeaders();
    }

    public String responseBody() {
        if (getLastResponse() == null) {
            return null;
        }
        return getLastResponse().getBody();
    }

    public boolean hasBody(String expected) throws Exception {
        BodyTypeAdapter bodyTypeAdapter = createBodyTypeAdapter();
        String actual = responseBody();
        return equalsWithAdapter(expected, actual, bodyTypeAdapter);
    }

    public boolean hasBodyUsingType(String expected, String type) throws Exception {
        ContentType ct = ContentType.typeFor(type);
        BodyTypeAdapter bodyTypeAdapter = createBodyTypeAdapter(ct);
        String actual = responseBody();
        return equalsWithAdapter(expected, actual, bodyTypeAdapter);
    }
    
    public boolean hasHeaders(String expected) throws Exception {
        return hasHeader(expected);
    }

    public boolean hasHeader(String expected) throws Exception {
        return equalsWithAdapter(expected, headers(), new HeadersTypeAdapter());
    }
    
    public void setBody(String text) {
        setRequestBody(GLOBALS.substitute(text));
    }

    protected boolean equalsWithAdapter(String expected, Object actual, RestDataTypeAdapter typeAdapter) throws Exception {
        typeAdapter.set(actual);
        Object parse = typeAdapter.parse(expected);
        return typeAdapter.equals(parse, actual);
    }

    private String applyExpressionToLastResponse(String type, String expr) {
        LetHandler letHandler = LetHandlerFactory.getHandlerFor(type);
        return GLOBALS.replaceNull(letHandler.handle(getLastResponse(), getNamespaceContext(), expr));
    }
}