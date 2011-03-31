package smartrics.rest.support.fitnesse;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.Socket;

import fitnesse.ComponentFactory;
import fitnesse.FitNesseContext;
import fitnesse.Responder;
import fitnesse.VelocityFactory;
import fitnesse.WikiPageFactory;
import fitnesse.http.Request;
import fitnesse.http.Response;
import fitnesse.http.ResponseSender;
import fitnesse.responders.ResponderFactory;
import fitnesse.wiki.WikiPage;

public class InProcessRunner {

    private static String SRC = "./src";
    private static String SUITE_ROOT = "RestFixtureTests.SlimTest.SequenceDiagramGenerationTests";
    private static String FITNESSE_ROOT_PAGE = "FitNesseRoot";

    public static void main(String... args) throws Exception {
        ResponderFactory rFac = new ResponderFactory(SRC);
        ComponentFactory componentFactory = new ComponentFactory();
        WikiPageFactory pFac = new WikiPageFactory();
        WikiPage root = pFac.makeRootPage(SRC, FITNESSE_ROOT_PAGE, componentFactory);
        Request request = mock(Request.class);
        when(request.getResource()).thenReturn(SUITE_ROOT);
        when(request.getQueryString()).thenReturn("test");
        Responder responder = rFac.makeResponder(request, root);
        FitNesseContext context = new FitNesseContext(root);
        context.rootDirectoryName = FITNESSE_ROOT_PAGE;
        context.rootPath = SRC;
        context.doNotChunk = true;
        context.setRootPagePath();
        VelocityFactory.makeVelocityFactory(context);
        Response response = responder.makeResponse(context, request);
        final StringBuffer sb = new StringBuffer();
        ResponseSender sender = new ResponseSender() {

            @Override
            public void send(byte[] bytes) throws Exception {
                sb.append(new String(bytes));
            }

            @Override
            public void close() throws Exception {
            }

            @Override
            public Socket getSocket() throws Exception {
                return null;
            }

        };
        response.readyToSend(sender);
        for (int i = 0; i < 100; i++) {
            if (sb.toString().indexOf("Exit-Code") > 0)
                break;
        }

        System.out.println(sb.toString());
    }

}
