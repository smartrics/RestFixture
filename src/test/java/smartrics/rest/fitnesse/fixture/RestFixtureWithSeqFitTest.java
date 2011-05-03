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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import smartrics.rest.client.RestClient;
import smartrics.rest.client.RestRequest;
import smartrics.rest.client.RestResponse;
import smartrics.rest.config.Config;
import smartrics.rest.fitnesse.fixture.RestFixture.Runner;
import smartrics.rest.fitnesse.fixture.support.CellFormatter;
import smartrics.rest.fitnesse.fixture.support.ContentType;
import smartrics.rest.fitnesse.fixture.support.RowWrapper;
import smartrics.rest.fitnesse.fixture.support.Variables;
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
    private final Variables variables = new Variables();
    private RestFixtureTestHelper helper;
    private PartsFactory mockPartsFactory;
    private RestClient mockRestClient;
    private RestRequest mockLastRequest;
    @SuppressWarnings("rawtypes")
    private CellFormatter mockCellFormatter;
    private Config config;
    private RestResponse lastResponse;

    @Before
    public void setUp() {
        helper = new RestFixtureTestHelper();

        mockCellFormatter = mock(CellFormatter.class);
        mockRestClient = mock(RestClient.class);
        mockLastRequest = mock(RestRequest.class);
        mockPartsFactory = mock(PartsFactory.class);

        variables.clearAll();

        lastResponse = new RestResponse();
        lastResponse.setStatusCode(200);
        lastResponse.setBody("");
        lastResponse.setResource("/uri");
        lastResponse.setStatusText("OK");
        lastResponse.setTransactionId(0L);

        config = Config.getConfig();
        config.add("restfixture.graphs.dir", "./build");

        ContentType.resetDefaultMapping();

        helper.wireMocks(config, mockPartsFactory, mockRestClient, mockLastRequest, lastResponse, mockCellFormatter);
        fixture = new RestFixtureWithSeq(mockPartsFactory, "http://localhost:8080", null, "sequence.pic");
        fixture.initialize(Runner.OTHER);
    }

    @Test
    public void shouldInitializeFixtureForFitRunner() {
        RestFixtureWithSeq seqFixture = new RestFixtureWithSeq() {
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
        seqFixture.doCells(helper.createSingleRowFitTable("GET", "/uri", "", "", ""));
        assertThat(seqFixture.getConfig().getName(), is(equalTo(Config.DEFAULT_CONFIG_NAME)));
        assertThat(seqFixture.getBaseUrl(), is(equalTo("http://localhost:8080")));
        assertThat(seqFixture.getPictureName(), is(equalTo("sequence.gif")));
    }

    @Test
    public void shouldHaveConfigNameAsOptionalSecondParameterToBeSetToSpecifiedValue() throws FitParseException {
        RestFixtureWithSeq seqFixture = new RestFixtureWithSeq() {
            {
                super.args = new String[] { "http://localhost:8080", "configName", "sequence.gif" };
            }
        };
        seqFixture.doCells(new Parse("<table><tr><td></td></tr></table>"));
        assertThat(seqFixture.getConfig().getName(), is(equalTo("configName")));
        assertThat(seqFixture.getBaseUrl(), is(equalTo("http://localhost:8080")));
        assertThat(seqFixture.getPictureName(), is(equalTo("sequence.gif")));
    }

    @Test
    public void mustNotifyCallerThatPictureNameIsMandatory() throws FitParseException {
        RestFixtureWithSeq seqFixture = new RestFixtureWithSeq() {
            {
                super.args = new String[] { "http://localhost:8080" };
            }
        };
        try {
            seqFixture.doCells(new Parse("<table><tr><td></td></tr></table>"));
            fail("Should have spotted that either/both baseUrl and/or pic name are missing");
        } catch (FitFailureException e) {
            assertThat(e.getMessage(), is(equalTo("Both baseUrl and picture name need to be passed to the fixture")));
        }
    }

    @Test
    public void mustGenerateTwoEventsForPut() {
        RowWrapper<?> row = helper.createTestRow("PUT", "/uri", "", "", "");
        fixture.processRow(row);
        assertOnMethods("PUT");
    }

    @Test
    public void mustGenerateTwoEventsForGet() {
        RowWrapper<?> row = helper.createTestRow("GET", "/uri", "", "", "");
        fixture.processRow(row);
        assertOnMethods("GET");
    }

    @Test
    public void mustGenerateTwoEventsForDelete() {
        RowWrapper<?> row = helper.createTestRow("DELETE", "/uri", "", "", "");
        fixture.processRow(row);
        assertOnMethods("DELETE");
    }

    @Test
    public void mustGenerateThreeEventsForPost() {
        lastResponse.addHeader("Location", "/resources/999");
        RowWrapper<?> row = helper.createTestRow("POST", "/uri", "", "", "");
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
