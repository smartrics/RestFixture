/*  Copyright 2011 Fabrizio Cannizzo
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
package smartrics.rest.fitnesse.fixture;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import smartrics.rest.client.RestRequest;
import smartrics.rest.client.RestResponse;
import smartrics.rest.config.Config;
import smartrics.sequencediagram.Create;
import smartrics.sequencediagram.Event;
import smartrics.sequencediagram.Message;
import smartrics.sequencediagram.Return;
import fit.Counts;
import fit.FixtureListener;
import fit.Parse;
import fit.exception.FitFailureException;
import fit.exception.FitParseException;

public class RestFixtureWithSeqFitTest {

    private RestFixtureWithSeq fixture;
    private RestFixtureTestHelper helper;

    @Before
    public void setUp() {
        helper = new RestFixtureTestHelper();
        fixture = new RestFixtureWithSeq() {
            {
                super.args = new String[] { "http://localhost:8080", "sequence.gif" };
            }

            public RestResponse getLastResponse() {
                RestResponse r = new RestResponse();
                r.setStatusCode(200);
                r.addHeader("Location", "http://host:8080/resources/1");
                return r;
            }

            public RestRequest getLastRequest() {
                return new RestRequest();
            }

            @Override
            protected void doMethod(String body, String method) {
            }
        };
    }

    @Test
    public void shouldHaveConfigNameAsOptionalSecondParameterToDefaultWhenNotSpecified() throws FitParseException {
        fixture.doCells(new Parse("<table><tr><td></td></tr></table>"));
        assertEquals(Config.DEFAULT_CONFIG_NAME, fixture.getConfig().getName());
    }

    @Test
    public void shouldHaveConfigNameAsOptionalSecondParameterToBeSetToSpecifiedValue() throws FitParseException {
        fixture = new RestFixtureWithSeq() {
            {
                super.args = new String[] { "http://localhost:8080", "configName", "sequence.gif" };
            }
        };
        fixture.doCells(new Parse("<table><tr><td></td></tr></table>"));
        assertEquals("configName", fixture.getConfig().getName());
        assertEquals("sequence.gif", fixture.getPictureName());
    }

    @Test(expected = FitFailureException.class)
    public void mustNotifyCallerThatPictureNameIsMandatory() throws FitParseException {
        fixture = new RestFixtureWithSeq() {
            {
                super.args = new String[] { "http://localhost:8080" };
            }
        };
        fixture.doCells(new Parse("<table><tr><td></td></tr></table>"));
    }

    @Test
    public void mustGenerateTwoEventsForPut() {
        RowWrapper<?> row = helper.createFitTestRow("PUT", "/uri", "", "", "");
        fixture.processRow(row);
        assertOnMethods("PUT");
    }

    @Test
    public void mustGenerateTwoEventsForGet() {
        RowWrapper<?> row = helper.createFitTestRow("GET", "/uri", "", "", "");
        fixture.processRow(row);
        assertOnMethods("GET");
    }

    @Test
    public void mustGenerateTwoEventsForDelete() {
        RowWrapper<?> row = helper.createFitTestRow("DELETE", "/uri", "", "", "");
        fixture.processRow(row);
        assertOnMethods("DELETE");
    }

    @Test
    public void mustGenerateThreeEventsForPost() {
        RowWrapper<?> row = helper.createFitTestRow("POST", "/uri", "", "", "");
        fixture.processRow(row);
        assertEquals(3, fixture.getModel().getEvents().size());
        Event event = fixture.getModel().getEvents().get(0);
        assertTrue(event instanceof Message);
        assertEquals("POST", event.getName());
        assertTrue(fixture.getModel().getEvents().get(1) instanceof Create);
        assertTrue(fixture.getModel().getEvents().get(2) instanceof Return);
    }

    @Test
    public void mustInvokeListenerWhenTableProcessCompletes() {
        MockFixtureListener l = new MockFixtureListener();
        RestFixtureWithSeq fixture = new RestFixtureWithSeq() {
            @Override
            public void doCells(Parse p) {
                // do nothing
            }
        };
        fixture.setFixtureListener(l);
        // TODO: need to look at this - artificial Parse to make doTable pass...
        Parse p0 = new Parse("table", "", null, null);
        Parse p1 = new Parse("table", "", p0, p0);
        Parse p2 = new Parse("table", "", p1, p1);
        Parse p3 = new Parse("table", "", p2, p2);
        fixture.doTable(p3);
        assertTrue("Listener tableFinished not called", l.called);
    }

    private void assertOnMethods(String name) {
        assertEquals(2, fixture.getModel().getEvents().size());
        Event event = fixture.getModel().getEvents().get(0);
        assertTrue(event instanceof Message);
        assertTrue(fixture.getModel().getEvents().get(1) instanceof Return);
        assertEquals(name, event.getName());
    }

    private static class MockFixtureListener implements FixtureListener {
        boolean called = false;

        public void tableFinished(Parse table) {
            called = true;
        }

        public void tablesFinished(Counts count) {
            // TODO Auto-generated method stub

        }
    }

}
