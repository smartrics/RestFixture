package smartrics.rest.test.fitnesse.fixture;

import fit.ActionFixture;

public class HttpServerFixture extends ActionFixture{

	private static HttpServer server;


	public HttpServerFixture() throws Exception {
		super();
	}

	public void start(int port){
		if(server==null){
			server = new HttpServer(port);
			server.addServlet(new ResourcesServlet(), ResourcesServlet.CONTEXT_ROOT);
			server.start();
		}
	}

	public boolean isStarted(){
		return server!=null && server.isStarted();
	}

	public boolean isStopped(){
		return server!=null && server.isStopped();
	}

	public void stop(){
		if(server!=null){
			server.stop();
		}
	}

	public void join(){
		server.join();
	}

	public void resetResourcesDatabase(){
		Resources.getInstance().reset();
	}

	public static void main(String[] args) throws Exception{
		HttpServerFixture f = new HttpServerFixture();
		f.start(8765);
		f.join();
	}
}
