package smartrics.rest.test.fitnesse.drivers;

import smartrics.rest.test.fitnesse.fixture.HttpServer;
import smartrics.rest.test.fitnesse.fixture.ResourcesServlet;

public class ServerDriver {
    public static void main(String[] args) {
        // as in EventListener
        HttpServer server = new HttpServer(8765);
        server.addServlet(new ResourcesServlet(), "/");
        server.start();
        server.join();
    }

}
