package smartrics.rest.fitnesse.fixture.support.http;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;

public class PutMethod extends org.apache.commons.httpclient.methods.GetMethod {
    @SuppressWarnings("deprecation")
    public URI getURI() throws URIException {
        HostConfiguration conf = super.getHostConfiguration();
        String scheme = conf.getProtocol().getScheme();
        String host = conf.getHost();
        int port = conf.getPort();
        return new URIBuilder().getURI(scheme, host, port, getPath(), getQueryString(), getParams());
    }

    public void setURI(URI uri) throws URIException {
        new URIBuilder().setURI(this, uri);
    }

}
