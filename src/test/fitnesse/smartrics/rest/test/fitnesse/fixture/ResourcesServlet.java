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
	private Resources resources = Resources.getInstance();

	public ResourcesServlet(){
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String uri = req.getRequestURI();
		echoHeader(req, resp);
		if (uri.endsWith("/"))
			uri = uri.substring(0, uri.length() - 1);
		try {
			int pos = uri.lastIndexOf("/");
			String sId = uri.substring(pos + 1);
			int id = Integer.parseInt(sId);
			if (id >= resources.size()) {
				notFound(resp);
			} else {
				String resource = resources.get(id);
				if("-deleted-".equals(resource)){
					notFound(resp);
				} else {
					resp.getOutputStream().write(resource.getBytes());
					resp.setStatus(HttpServletResponse.SC_OK);
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
		if(s!=null)
			resp.setHeader("Echo-Header", s);
	}


	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		echoHeader(req, resp);
		int id = getId(req);
		resources.remove(id);
		resources.add(id, "-deleted-");
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
		resources.add(id, content);
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
		if(content.trim().startsWith("<"))
		{
			resources.add(content);
			// todo: should put the ID in
			resp.setStatus(HttpServletResponse.SC_CREATED);
			resp.addHeader("Location", "/resources/" + (resources.size()-1));
		} else {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}
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
