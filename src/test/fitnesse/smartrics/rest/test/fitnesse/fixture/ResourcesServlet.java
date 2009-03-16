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

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import smartrics.rest.client.RestClient;
import smartrics.rest.client.RestClientImpl;
import smartrics.rest.client.RestRequest;
import smartrics.rest.client.RestResponse;
import smartrics.rest.client.RestRequest.Method;

public class ResourcesServlet extends HttpServlet {
	public static String _CONTEXT_ROOT = "/resources";
	private static final long serialVersionUID = -7012866414216034826L;
	private final Resources resources = Resources.getInstance();
	private static Log log = LogFactory.getLog(ResourcesServlet.class);

	public ResourcesServlet() {
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		log.debug("REQUEST ========= " + req.toString());
		String uri = sanitise(req.getRequestURI());
		String id = getId(uri);
		String type = getType(uri);
		String extension = getExtension(uri);
		echoHeader(req, resp);
		echoQString(req, resp);
		try {
			if (id == null) {
				list(resp, type, extension);
				headers(resp, extension);
			} else if (resources.get(type, id) == null) {
				notFound(resp);
			} else {
				if (resources.get(type, id).isDeleted()) {
					notFound(resp);
				} else {
					found(resp, type, id);
					headers(resp, extension);
				}
			}
		} catch (RuntimeException e) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
		} finally {
			log.debug("RESPONSE ========= " + resp.toString());
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
		if (uri.endsWith("/"))
			uri = uri.substring(0, uri.length() - 1);
		return uri;
	}

	private void headers(HttpServletResponse resp, String extension) {
		resp.setStatus(HttpServletResponse.SC_OK);
		resp.addHeader("Content-Type", "application/" + extension);
	}

	private void list(HttpServletResponse resp, String type, String extension)
			throws IOException {
		if (type.contains("root-context")) {
			list(resp, extension);
		} else {
			StringBuffer buffer = new StringBuffer();
			String slashremoved = type.substring(1);
			if ("json".equals(extension))
				buffer.append("{ \"" + slashremoved + "\" : ");
			else
				buffer.append("<" + slashremoved + ">");
			for (Resource r : resources.asCollection(type)) {
				buffer.append(r.getPayload());
			}
			if ("json".equals(extension))
				buffer.append("}");
			else
				buffer.append("</" + slashremoved + ">");
			resp.getOutputStream().write(buffer.toString().getBytes());
		}
	}

	private void list(HttpServletResponse resp, String extension)
			throws IOException {
		StringBuffer buffer = new StringBuffer();
		if ("json".equals(extension))
			buffer.append("{ \"root-context\" : ");
		else
			buffer.append("<root-context>");
		resp.getOutputStream().write(buffer.toString().getBytes());
		for (String s : resources.contexts()) {
			list(resp, s, extension);
		}
		buffer = new StringBuffer();
		if ("json".equals(extension))
			buffer.append("}");
		else
			buffer.append("</root-context>");
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

	private void found(HttpServletResponse resp, String type, String id)
			throws IOException {
		StringBuffer buffer = new StringBuffer();
		buffer.append(resources.get(type, id));
		resp.getOutputStream().write(buffer.toString().getBytes());
		// resp.setHeader("Content-Lenght",
		// Integer.toString(buffer.toString().getBytes().length));
	}

	private String getType(String uri) {
		if (uri.length() <= 1)
			return "/root-context";
		int pos = uri.substring(1).indexOf('/');
		String ret = uri;
		if (pos >= 0)
			ret = uri.substring(0, pos + 1);
		return ret;
	}

	private void notFound(HttpServletResponse resp) throws IOException {
		resp.getOutputStream().write("".getBytes());
		resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
		// resp.setHeader("Content-Lenght", "0");
	}

	private void echoHeader(HttpServletRequest req, HttpServletResponse resp) {
		String s = req.getHeader("Echo-Header");
		if (s != null)
			resp.setHeader("Echo-Header", s);
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		log.debug(req.toString());
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
		log.debug(resp.toString());
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		log.debug(req.toString());
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
		log.debug(resp.toString());
	}

	private String getId(String uri) {
		if (uri.length() <= 1)
			return null;
		int pos = uri.substring(1).lastIndexOf("/");
		String sId = null;
		if (pos > 0)
			sId = uri.substring(pos + 2);
		if (sId != null) {
			int pos2 = sId.lastIndexOf('.');
			if (pos2 >= 0)
				sId = sId.substring(0, pos2);
		}
		return sId;
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		log.debug(req.toString());
		echoHeader(req, resp);
		String uri = sanitise(req.getRequestURI());
		String type = getType(uri);
		try {
			String content = getContent(req.getInputStream());
			if (content.trim().startsWith("<") || content.trim().endsWith("}")) {
				Resource newResource = new Resource(content);
				resources.add(type, newResource);
				// TODO: should put the ID in
				resp.setStatus(HttpServletResponse.SC_CREATED);
				resp.addHeader("Location", type + "/" + newResource.getId());
			} else if (req.getContentType().startsWith("multipart")) {
				resp.setStatus(HttpServletResponse.SC_OK);
			} else {
				resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			}
		} catch (RuntimeException e) {
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		log.debug(resp.toString());
	}

	private String getContent(InputStream is) throws IOException {
		StringBuffer sBuff = new StringBuffer();
		int c;
		while ((c = is.read()) != -1)
			sBuff.append((char) c);
		String content = sBuff.toString();
		return content;
	}

	public static void main(String[] args) {
		RestClient c = new RestClientImpl(new HttpClient());
		RestRequest req = new RestRequest();

		req.setBody("<resource><name>n</name><data>d1</data></resource>");
		req.setResource("/resources/");
		req.setMethod(Method.Post);
		RestResponse res = c.execute("http://localhost:8765", req);
		System.out.println("=======>\n" + res + "\n<=======");

		String loc = res.getHeader("Location").get(0).getValue();
		req.setResource(loc + ".json");
		req.setMethod(Method.Get);
		res = c.execute("http://localhost:8765", req);
		System.out.println("=======>\n" + res + "\n<=======");

		req.setMethod(Method.Put);
		req
				.setBody("<resource><name>another name</name><data>another data</data></resource>");
		res = c.execute("http://localhost:8765", req);
		System.out.println("=======>\n" + res + "\n<=======");

		req.setResource("/resources/");
		req.setMethod(Method.Get);
		res = c.execute("http://localhost:8765", req);
		System.out.println("=======>\n" + res + "\n<=======");

		req.setMethod(Method.Delete);
		req.setResource(loc);
		res = c.execute("http://localhost:8765", req);
		System.out.println("=======>\n" + res + "\n<=======");
	}

}
