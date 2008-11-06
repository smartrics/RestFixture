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

public class ResourcesServlet extends HttpServlet {
	public static String CONTEXT_ROOT = "/resources";
	private static final long serialVersionUID = -7012866414216034826L;
	private final Resources resources = Resources.getInstance();

	public ResourcesServlet() {
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String uri = req.getRequestURI();
		echoHeader(req, resp);
		if (uri.endsWith("/"))
			uri = uri.substring(0, uri.length() - 1);
		try {
			int pos = uri.lastIndexOf("/");
			String sId = uri.substring(pos + 1, pos + 2);
			int id = Integer.parseInt(sId);
			if (id >= resources.size()) {
				notFound(resp);
			} else {
				Resource resource = resources.get(id);
				if (resource.isDeleted()) {
					notFound(resp);
				} else {
					int extensionPoint = uri.lastIndexOf(".");
					if (extensionPoint != -1) {
						String extension = uri.substring(extensionPoint + 1);
						if ("json".equals(extension)) {
							resp.getOutputStream().write(
									resource.toJson().getBytes());
							resp.setStatus(HttpServletResponse.SC_OK);
							resp.addHeader("Content-Type", "application/json");							
							return;
						}
					}
					resp.getOutputStream().write(resource.toXml().getBytes());
					resp.setStatus(HttpServletResponse.SC_OK);
					resp.addHeader("Content-Type", "application/xml");

				}
			}
		} catch (RuntimeException e) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	private void notFound(HttpServletResponse resp) throws IOException {
		resp.getOutputStream().write("".getBytes());
		resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
	}

	private void echoHeader(HttpServletRequest req, HttpServletResponse resp) {
		String s = req.getHeader("Echo-Header");
		if (s != null)
			resp.setHeader("Echo-Header", s);
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		echoHeader(req, resp);
		int id = getId(req);
		Resource resource = resources.remove(id);
		resource.setDeleted(true);
		resources.add(id, resource);
		resp.getOutputStream().write("".getBytes());
		resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		echoHeader(req, resp);
		int id = getId(req);
		String content = getContent(req);
		resources.remove(id);
		resources.add(id, new Resource(content));
		resp.getOutputStream().write("".getBytes());
		resp.setStatus(HttpServletResponse.SC_OK);
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
		echoHeader(req, resp);
		String content = getContent(req);
		if (content.trim().startsWith("<")) {
			resources.add(new Resource(content));
			// TODO: should put the ID in
			resp.setStatus(HttpServletResponse.SC_CREATED);
			final String contextRoot = getContextRoot(req);
			resp.addHeader("Location", contextRoot + "/"
					+ (resources.size() - 1));
		} else {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}
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

}
