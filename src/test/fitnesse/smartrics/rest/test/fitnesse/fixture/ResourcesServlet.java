/*  Copyright 2008 Fabrizio Cannizzo
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
package smartrics.rest.test.fitnesse.fixture;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.oreilly.servlet.multipart.FilePart;
import com.oreilly.servlet.multipart.MultipartParser;
import com.oreilly.servlet.multipart.ParamPart;
import com.oreilly.servlet.multipart.Part;

/**
 * The controller.
 * 
 * @author fabrizio
 * 
 */
public class ResourcesServlet extends HttpServlet {
    private static final Log LOG = LogFactory.getLog(ResourcesServlet.class);
    public static final String CONTEXT_ROOT = "/resources";
    private static final long serialVersionUID = -7012866414216034826L;
    private final Resources resources = Resources.getInstance();

    public ResourcesServlet() {
        LOG.info("Resources: " + resources.toString());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOG.debug("Resource GET REQUEST ========= " + req.toString());
        String uri = sanitise(req.getRequestURI());
        String id = getId(uri);
        String type = getType(uri);
        String extension = getExtension(uri);
        echoHeader(req, resp);
        echoQString(req, resp);
        try {
            if (id == null) {
                list(resp, type, extension);
                headers(resp, extension, ";charset=ISO-8859-1");
            } else if (resources.get(type, id) == null) {
                notFound(resp);
            } else {
                if (resources.get(type, id).isDeleted()) {
                    notFound(resp);
                } else {
                    found(resp, type, id);
                    headers(resp, extension, ";charset=ISO-8859-1");
                }
            }
        } catch (RuntimeException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } finally {
            LOG.debug("Resource GET RESPONSE ========= " + resp.toString());
        }
    }

    private void echoQString(HttpServletRequest req, HttpServletResponse resp) {
        String qstring = req.getQueryString();
        if (qstring != null) {
            resp.setHeader("Query-String", qstring);
        }
    }

    private String sanitise(String rUri) {
        String uri = rUri;
        if (uri.endsWith("/")) {
            uri = uri.substring(0, uri.length() - 1);
        }
        return uri;
    }

    private void headers(HttpServletResponse resp, String extension, String optCharset) {
        resp.setStatus(HttpServletResponse.SC_OK);
        String s = "";
        if (optCharset != null) {
            s = optCharset;
        }
        resp.addHeader("Content-Type", "application/" + extension + s);
    }

    private void list(HttpServletResponse resp, String type, String extension) throws IOException {
        if (type.contains("root-context")) {
            list(resp, extension);
        } else {
            StringBuffer buffer = new StringBuffer();
            String slashremoved = type.substring(1);
            if ("json".equals(extension)) {
                buffer.append("{ \"" + slashremoved + "\" : ");
            } else {
                buffer.append("<" + slashremoved + ">");
            }
            for (Resource r : resources.asCollection(type)) {
                buffer.append(r.getPayload());
            }
            if ("json".equals(extension)) {
                buffer.append("}");
            } else {
                buffer.append("</" + slashremoved + ">");
            }
            resp.getOutputStream().write(buffer.toString().getBytes());
        }
    }

    private void list(HttpServletResponse resp, String extension) throws IOException {
        StringBuffer buffer = new StringBuffer();
        if ("json".equals(extension)) {
            buffer.append("{ \"root-context\" : ");
        } else {
            buffer.append("<root-context>");
        }
        resp.getOutputStream().write(buffer.toString().getBytes());
        for (String s : resources.contexts()) {
            list(resp, s, extension);
        }
        buffer = new StringBuffer();
        if ("json".equals(extension)) {
            buffer.append("}");
        } else {
            buffer.append("</root-context>");
        }
        resp.getOutputStream().write(buffer.toString().getBytes());
    }

    private String getExtension(String uri) {
        int extensionPoint = uri.lastIndexOf(".");
        if (extensionPoint != -1) {
            return uri.substring(extensionPoint + 1);
        } else {
            return "xml";
        }
    }

    private void found(HttpServletResponse resp, String type, String id) throws IOException {
        StringBuffer buffer = new StringBuffer();
        Resource r = resources.get(type, id);
        buffer.append(r);
        resp.getOutputStream().write(buffer.toString().getBytes());
        // resp.setHeader("Content-Lenght",
        // Integer.toString(buffer.toString().getBytes().length));
    }

    private String getType(String uri) {
        if (uri.length() <= 1) {
            return "/root-context";
        }
        int pos = uri.substring(1).indexOf('/');
        String ret = uri;
        if (pos >= 0) {
            ret = uri.substring(0, pos + 1);
        }
        return ret;
    }

    private void notFound(HttpServletResponse resp) throws IOException {
        resp.getOutputStream().write("".getBytes());
        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        // resp.setHeader("Content-Lenght", "0");
    }

    private void echoHeader(HttpServletRequest req, HttpServletResponse resp) {
        String s = req.getHeader("Echo-Header");
        if (s != null) {
            resp.setHeader("Echo-Header", s);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOG.debug("Resource DELETE REQUEST ========= " + req.toString());
        String uri = sanitise(req.getRequestURI());
        String type = getType(uri);
        echoHeader(req, resp);
        String id = getId(uri);
        Resource resource = resources.get(type, id);
        if (resource != null) {
            // resource.setDeleted(true);
            resources.remove(type, id);
            resp.getOutputStream().write("".getBytes());
            resp.setStatus(HttpServletResponse.SC_OK);
        } else {
            notFound(resp);
        }
        resp.getOutputStream().write("".getBytes());
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        LOG.debug("Resource DELETE RESPONSE ========= " + req.toString());
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOG.debug("Resource PUT REQUEST ========= " + req.toString());
        echoHeader(req, resp);
        String uri = sanitise(req.getRequestURI());
        String id = getId(uri);
        String type = getType(uri);
        String content = getContent(req.getInputStream());
        Resource resource = resources.get(type, id);
        if (resource != null) {
            resource.setPayload(content);
            resp.getOutputStream().write("".getBytes());
            resp.setStatus(HttpServletResponse.SC_OK);
        } else {
            notFound(resp);
        }
        LOG.debug("Resource PUT RESPONSE ========= " + req.toString());
    }

    private String getId(String uri) {
        if (uri.length() <= 1) {
            return null;
        }
        int pos = uri.substring(1).lastIndexOf("/");
        String sId = null;
        if (pos > 0) {
            sId = uri.substring(pos + 2);
        }
        if (sId != null) {
            int pos2 = sId.lastIndexOf('.');
            if (pos2 >= 0) {
                sId = sId.substring(0, pos2);
            }
        }
        return sId;
    }

    private void processMultiPart(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter out = resp.getWriter();
        resp.setContentType("text/plain");
        MultipartParser mp = new MultipartParser(req, 2048);
        Part part = null;
        while ((part = mp.readNextPart()) != null) {
            String name = part.getName();
            if (part.isParam()) {
                // it's a parameter part
                ParamPart paramPart = (ParamPart) part;
                String value = paramPart.getStringValue();
                LOG.info("param; name=" + name + ", value=" + value);
                out.print("param; name=" + name + ", value=" + value);
            } else if (part.isFile()) {
                // it's a file part
                FilePart filePart = (FilePart) part;
                String fileName = filePart.getFileName();
                if (fileName != null) {
                    // the part actually contained a file
                    // StringWriter sw = new StringWriter();
                    // long size = filePart.writeTo(new File(System
                    // .getProperty("java.io.tmpdir")));
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    long size = filePart.writeTo(baos);
                    LOG.info("file; name=" + name + "; filename=" + fileName + ", filePath=" + filePart.getFilePath() + ", content type=" + filePart.getContentType() + ", size="
                            + size);
                    out.print(String.format("%s: %s", name, new String(baos.toByteArray())));
                } else {
                    // the field did not contain a file
                    LOG.info("file; name=" + name + "; EMPTY");
                }
                out.flush();
            }
        }
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOG.debug("Resource POST REQUEST ========= " + req.toString());
        echoHeader(req, resp);
        String uri = sanitise(req.getRequestURI());
        String type = getType(uri);
        try {
            String contentType = req.getContentType();
            if (contentType.equals("application/octet-stream")) {
                LOG.debug("Resource POST REQUEST is a file upload");
                processFileUpload(req, resp);
            } else if (contentType.startsWith("multipart")) {
                LOG.debug("Resource POST REQUEST is a multipart file upload");
                processMultiPart(req, resp);
            } else {
                String content = getContent(req.getInputStream());
                if (contentType.contains("application/x-www-form-urlencoded")) {
                    try {
                        generateResponse(resp, type, noddyKvpToXml(content, "UTF-8"));
                    } catch (Exception e) {
                        LOG.warn("the content passed in isn't encoded as application/x-www-form-urlencoded: " + content);
                        resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    }
                } else if (content.trim().startsWith("<") || content.trim().endsWith("}")) {
                    generateResponse(resp, type, content);
                } else {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                }
            }
        } catch (RuntimeException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        LOG.debug("Resource POST RESPONSE ========= " + req.toString());
    }

    private String noddyKvpToXml(String content, String encoding) throws UnsupportedEncodingException {
        StringBuffer sb = new StringBuffer();
        sb.append("<resource>").append("\n");
        String[] kvpArray = content.split("&");
        for(String e : kvpArray) {
            String[] kvp = e.split("=");
            sb.append("<").append(kvp[0]).append(">");
            if(kvp.length>1) {
                sb.append(URLDecoder.decode(kvp[1], encoding));
            }
            sb.append("</").append(kvp[0]).append(">").append("\n");
        }
        sb.append("</resource>");
        return sb.toString();
    }

    private void generateResponse(HttpServletResponse resp, String type, String content) {
        Resource newResource = new Resource(content);
        resources.add(type, newResource);
        // TODO: should put the ID in
        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.addHeader("Location", type + "/" + newResource.getId());
    }

    private void processFileUpload(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        InputStream is = req.getInputStream();
        PrintWriter out = resp.getWriter();
        resp.setContentType("text/plain");
        String fileContents = getContent(is);
        out.print(fileContents);
        out.flush();
        resp.setStatus(HttpServletResponse.SC_OK);
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
