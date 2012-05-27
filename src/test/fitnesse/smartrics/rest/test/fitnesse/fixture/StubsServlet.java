package smartrics.rest.test.fitnesse.fixture;

import static smartrics.rest.test.fitnesse.fixture.ServletUtils.sanitiseUri;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import smartrics.rest.client.RestResponse;

public class StubsServlet extends HttpServlet {
    private static final long serialVersionUID = 5557300437355123426L;
    private static final Log LOG = LogFactory.getLog(StubsServlet.class);
    public static final String CONTEXT_ROOT = "/stubs";
    private static RestResponse nextResponse;

    public StubsServlet() {
    }

    /**
     * starts with /responses, takes the rest and uses as uri
     */
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getMethod();
        String uri = sanitiseUri(req.getRequestURI());
        if (method.equals("POST") && uri.endsWith("/responses")) {
            nextResponse = new RestResponse();
            InputStream is = req.getInputStream();
            String line = HttpParser.readLine(is, Charset.defaultCharset().name());
            String[] incipit = line.split(" ");
            nextResponse.setStatusCode(Integer.valueOf(incipit[0]));
            Header[] headers = HttpParser.parseHeaders(is, Charset.defaultCharset().name());
            for (Header h : headers) {
                nextResponse.addHeader(h.getName(), h.getValue());
            }
            line = HttpParser.readLine(is, Charset.defaultCharset().name());
            while (line.trim().length() < 1) {
                line = HttpParser.readLine(is, Charset.defaultCharset().name());
            }
            // check content length and decide how much body you need to parse
            List<smartrics.rest.client.RestData.Header> cl = nextResponse.getHeader("Content-Length");
            int len = 0;
            if (cl.size() > 0) {
                len = Integer.valueOf(cl.get(0).getValue());
            }
            if (len > 0) {
                String content = getContent(req.getInputStream());
                nextResponse.setBody(line + "\n" + content);
            }
        } else {
            resp.setStatus(nextResponse.getStatusCode());
            List<smartrics.rest.client.RestData.Header> headers = nextResponse.getHeaders();
            for (smartrics.rest.client.RestData.Header h : headers) {
                resp.addHeader(h.getName(), h.getValue());
            }
            resp.getOutputStream().write(nextResponse.getBody().getBytes());
        }
    }

    private String sanitise(String rUri) {
        String uri = rUri;
        if (uri.endsWith("/")) {
            uri = uri.substring(0, uri.length() - 1);
        }
        return uri;
    }

    private String getContent(InputStream is) throws IOException {
        StringBuffer sBuff = new StringBuffer();
        int c;
        while ((c = is.read()) != -1) {
            sBuff.append((char) c);
        }
        String content = sBuff.toString();
        return content;
    }

}
