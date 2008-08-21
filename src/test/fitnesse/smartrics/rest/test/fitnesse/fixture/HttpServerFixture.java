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
