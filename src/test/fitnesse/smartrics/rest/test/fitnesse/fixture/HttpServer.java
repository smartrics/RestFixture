/*  Copyright 2015 Fabrizio Cannizzo
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

import javax.servlet.http.HttpServlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.BasicConfigurator;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

/**
 * Wrapper to jetty providing the sample application used by RestFixture CATs.
 * 
 * @author fabrizio
 * 
 */
public class HttpServer {

    private static final Log LOG = LogFactory.getLog(HttpServer.class);
    private Server server = null;
    private int port;

    public HttpServer(int port) {
        BasicConfigurator.configure();
        server = new Server();
        server.setStopAtShutdown(true);
        setPort(port);
    }

    protected Server getServer() {
        return server;
    }

    public String stop() {
        String ret = null;
        LOG.debug("Stopping jetty in port " + getPort());
        try {
            getServer().stop();
            // Wait for 3 seconds to stop
            int watchdog = 30;
            while (!getServer().isStopped() && watchdog > 0) {
                Thread.sleep(100);
                watchdog--;
            }
            ret = "OK";
        } catch (Exception e) {
            ret = "Failed stopping http server: " + e.getMessage();
        }
        if (!getServer().isStopped()) {
            ret = "Failed stopping http server after 30 seconds wait";
        }
        return ret;
    }

    private void setPort(int port) {
        LOG.debug("Adding socket connector to Jetty on port " + port);
        Connector connector = createHttpConnector(port);
        addConnector(port, connector);
        this.port = port;
    }

    private void addConnector(int port, Connector connector) {
        boolean found = false;
        Connector[] cArray = getServer().getConnectors();
        if (cArray != null) {
            for (Connector c : cArray) {
                if (c.getPort() == port) {
                    found = true;
                    break;
                }
            }
        }
        if (!found) {
            // Configure port.
            getServer().addConnector(connector);
        }
    }

    private Connector createHttpConnector(int port) {
        Connector connector = new SocketConnector();
        connector.setPort(port);
        return connector;
    }

    public int getPort() {
        return port;
    }

    public String start() {
        String ret = null;
        LOG.debug("Starting jetty in port " + getPort());
        try {
            getServer().start();
            // Wait for 3 seconds to start
            int watchdog = 30;
            while (!getServer().isRunning() && !getServer().isStarted() && watchdog > 0) {
                Thread.sleep(100);
                watchdog--;
            }
            ret = "OK";
        } catch (Exception e) {
            ret = "Failed starting http server: " + e.getMessage();
            LOG.error(ret, e);
        }
        if (!getServer().isRunning()) {
            ret = "Failed to start http server after waiting 30 seconds";
        }
        return ret;
    }

    public boolean isStarted() {
        return server.isStarted();
    }

    public boolean isStopped() {
        return server.isStopped();
    }

    public void join() {
        try {
            getServer().join();
        } catch (InterruptedException e) {
            throw new IllegalStateException("Interruption occurred!", e);
        }
    }

    public void addServlet(HttpServlet servlet, String ctxRoot) {
        Context ctx = new Context(server, ctxRoot, Context.SESSIONS);
        ctx.addServlet(new ServletHolder(servlet), "/*");
    }

}
