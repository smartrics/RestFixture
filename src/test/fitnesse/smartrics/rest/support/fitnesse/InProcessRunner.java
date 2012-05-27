/*  Copyright 2012 Fabrizio Cannizzo
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
