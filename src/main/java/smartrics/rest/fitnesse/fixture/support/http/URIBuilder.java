package smartrics.rest.fitnesse.fixture.support.http;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpHost;
import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.params.HttpMethodParams;

class URIBuilder {
    public URI getURI(String scheme, String host, int port, String path, String queryString, HttpMethodParams params) throws URIException {
        HttpHost httphost = new HttpHost(host, port);
        StringBuffer buffer = new StringBuffer();
        if (httphost != null) {
            buffer.append(httphost.getProtocol().getScheme());
            buffer.append("://");
            buffer.append(httphost.getHostName());
            int p = httphost.getPort();
            if (p != -1 && p != httphost.getProtocol().getDefaultPort()) {
                buffer.append(":");
                buffer.append(p);
            }
        }
        buffer.append(path);
        if (queryString != null) {
            buffer.append('?');
            buffer.append(queryString);
        }
        String charset = params.getUriCharset();
        return new HttpURL(buffer.toString(), charset);
    }

    @SuppressWarnings("deprecation")
    public void setURI(org.apache.commons.httpclient.HttpMethodBase m, URI uri) throws URIException {
        HostConfiguration conf = m.getHostConfiguration();
        if (uri.isAbsoluteURI()) {
            conf.setHost(new HttpHost(uri));
            m.setHostConfiguration(conf);
        }
        m.setPath(uri.getPath() != null ? uri.getEscapedPath() : "/");
        m.setQueryString(uri.getQuery());
    }
}
