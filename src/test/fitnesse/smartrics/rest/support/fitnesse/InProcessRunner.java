package smartrics.rest.support.fitnesse;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;
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

    private static String SRC = "build/fitnesse";
    private static String SUITE_ROOT = "RestFixtureTests";
    private static String FITNESSE_ROOT_PAGE = "FitNesseRoot";

    public static void main(String... args) throws Exception {
        ResponderFactory rFac = new ResponderFactory(SRC);
        ComponentFactory componentFactory = new ComponentFactory();
        WikiPageFactory pFac = new WikiPageFactory();
        WikiPage root = pFac.makeRootPage(SRC, FITNESSE_ROOT_PAGE, componentFactory);
        Request request = mock(Request.class);
        when(request.getResource()).thenReturn(SUITE_ROOT);
        when(request.getQueryString()).thenReturn("suite&format=xml");
        verifyNoMoreInteractions(request);
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
            public void send(byte[] bytes) {
                sb.append(new String(bytes));
            }

            @Override
            public void close() {
            }

            @Override
            public Socket getSocket() {
                return null;
            }

        };
        response.readyToSend(sender);
        System.out.println();
        for (int i = 0; i < 20; i++) {
            // System.out.print(".");
            System.out.print(sb.toString());
            Thread.sleep(1000);
            if (sb.toString().indexOf("Exit-Code") > 0) {
                System.out.println();
                break;
            }
        }

        System.out.println(sb.toString());
    }

}
