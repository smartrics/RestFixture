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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fit.ActionFixture;

/**
 * Fixture to manage the HTTP server required to support RestFixture CATs.
 * 
 * @author fabrizio
 * 
 */
public class HttpServerFixture extends ActionFixture {

    private static final Log LOG = LogFactory.getLog(HttpServerFixture.class);
    private int port;
    private static HttpServer server;

    public HttpServerFixture() throws Exception {
        super();
    }

    public void startServer(String port) {
        startServer(Integer.parseInt(port));
    }

    private void startServer(int port) {
        if (server == null) {
            this.port = port;
            LOG.info("Starting server on port " + port);
			server = new HttpServer(port);
			server.addServlet(new ResourcesServlet(), "/");
			server.start();
        } else {
            LOG.info("Server already started on port " + port);
		}
	}

    public boolean isStarted() {
        return server != null && server.isStarted();
	}

    public boolean isStopped() {
        return server != null && server.isStopped();
	}

    public void stopServer() {
        if (server != null) {
            LOG.info("Stopping server on port " + port);
			server.stop();
        } else {
            if (port == 0) {
                LOG.info("Server never started");
            } else {
                LOG.info("Server already stopped on port " + port);
            }
		}
	}

    public void join() {
        server.join();
	}

    public boolean resetResourcesDatabase() {
        Resources.getInstance().reset();
        return true;
	}

    public static void main(String[] args) throws Exception {
        HttpServerFixture f = new HttpServerFixture();
        f.startServer(8765);
		f.join();
	}
}
