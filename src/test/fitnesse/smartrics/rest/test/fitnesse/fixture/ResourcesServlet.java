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
	public static String CONTEXT_ROOT = "/resources";
	private static final long serialVersionUID = -7012866414216034826L;
	private final Resources resources = Resources.getInstance();
	private static Log log = LogFactory.getLog(ResourcesServlet.class);

	public ResourcesServlet() {
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String uri = req.getRequestURI();
		log.debug("REQUEST ========= " + req.toString());
		echoHeader(req, resp);
		if (uri.endsWith("/"))
			uri = uri.substring(0, uri.length() - 1);
		try {
			int pos = uri.lastIndexOf("/");
			String sId = uri.substring(pos + 1, pos + 2);
			int id = -1;
			try {
				id = Integer.parseInt(sId);
			} catch (NumberFormatException e) {
				// get on resource type
			}
			if (id == -1) {
				list(resp, uri);
				headers(resp, uri);
			} else if (resources.get(id) == null) {
				notFound(resp);
			} else {
				if (resources.get(id).isDeleted()) {
					notFound(resp);
				} else {
					found(resp, uri, id);
					headers(resp, uri);
				}
			}
		} catch (RuntimeException e) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
		} finally {
			log.debug("RESPONSE ========= " + resp.toString());
		}
	}

	private void headers(HttpServletResponse resp, String uri) {
		resp.setStatus(HttpServletResponse.SC_OK);
		resp.addHeader("Content-Type", "application/" + extension(uri));
	}

	private void list(HttpServletResponse resp, String uri) throws IOException {
		StringBuffer buffer = new StringBuffer();
		if ("json".equals(extension(uri))) {
			buffer.append("resources : {");
			for (int i = 0; i < resources.size(); i++) {
				buffer.append(resources.get(i).toJson());
				if (i != resources.size() - 1) {
					buffer.append(", ");
				}
			}
			buffer.append(" }");
		} else {
			buffer.append("<resources>");
			for (int i = 0; i < resources.size(); i++) {
				buffer.append(resources.get(i).toXml());
			}
			buffer.append("</resources>");
		}
		resp.getOutputStream().write(buffer.toString().getBytes());
		resp.setHeader("Content-Lenght", Integer.toString(buffer.toString()
				.getBytes().length));
	}

	private String extension(String uri) {
		int extensionPoint = uri.lastIndexOf(".");
		if (extensionPoint != -1) {
			return uri.substring(extensionPoint + 1);
		} else {
			return "xml";
		}
	}

	private void found(HttpServletResponse resp, String uri, int id)
			throws IOException {
		StringBuffer buffer = new StringBuffer();
		if ("json".equals(extension(uri))) {
			buffer.append(resources.get(id).toJson());
		} else {
			buffer.append(resources.get(id).toXml());
		}
		resp.getOutputStream().write(buffer.toString().getBytes());
		resp.setHeader("Content-Lenght", Integer.toString(buffer.toString()
				.getBytes().length));
	}

	private void notFound(HttpServletResponse resp) throws IOException {
		resp.getOutputStream().write("".getBytes());
		resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
		resp.setHeader("Content-Lenght", "0");
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
		echoHeader(req, resp);
		int id = getId(req);
		Resource resource = resources.remove(id);
		// resource.setDeleted(true);
		// resources.add(id, resource);
		resp.getOutputStream().write("".getBytes());
		resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
		log.debug(resp.toString());
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		log.debug(req.toString());
		echoHeader(req, resp);
		int id = getId(req);
		String content = getContent(req);
		resources.remove(id);
		Resource resource = new Resource(content);
		resource.setId(id);
		resources.add(resource);
		resp.getOutputStream().write("".getBytes());
		resp.setStatus(HttpServletResponse.SC_OK);
		log.debug(resp.toString());
	}

	private int getId(HttpServletRequest req) {
		String uri = req.getRequestURI();
		int pos = uri.lastIndexOf("/");
		String sId = uri.substring(pos + 1);
		int id = Integer.parseInt(sId);
		return id;
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		log.debug(req.toString());
		echoHeader(req, resp);
		String content = getContent(req);
		if (content.trim().startsWith("<")) {
			Resource newResource = new Resource(content);
			resources.add(newResource);
			// TODO: should put the ID in
			resp.setStatus(HttpServletResponse.SC_CREATED);
			final String contextRoot = getContextRoot(req);
			resp.addHeader("Location", contextRoot + "/"
					+ newResource.getId());
		} else {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}
		log.debug(resp.toString());
	}

	private String getContextRoot(HttpServletRequest req) {
		if (req.getContextPath().indexOf(CONTEXT_ROOT) >= 0)
			return CONTEXT_ROOT;
		return "";
	}

	private String getContent(HttpServletRequest req) throws IOException {
		InputStream is = req.getInputStream();
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
		System.out.println("=======\n" + res);

		req.setResource("/resources/0");
		req.setMethod(Method.Delete);
		res = c.execute("http://localhost:8765", req);
		System.out.println("=======\n" + res);

		req.setMethod(Method.Post);
		req.setBody("<resource><name>n</name><data>d2</data></resource>");
		res = c.execute("http://localhost:8765", req);
		System.out.println("=======\n" + res);

		req.setResource("/resources/0");
		req.setMethod(Method.Get);
		res = c.execute("http://localhost:8765", req);
		System.out.println("=======\n" + res);


	}

}
