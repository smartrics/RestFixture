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
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;

import smartrics.rest.client.RestClient;
import smartrics.rest.client.RestRequest;
import smartrics.rest.client.RestResponse;
import smartrics.rest.config.Config;
import smartrics.rest.fitnesse.fixture.RestFixture.Runner;
import smartrics.rest.fitnesse.fixture.RestFixtureWithSeq.Model;
import smartrics.rest.fitnesse.fixture.support.BodyTypeAdapter;
import smartrics.rest.fitnesse.fixture.support.CellFormatter;
import smartrics.rest.fitnesse.fixture.support.ContentType;
import smartrics.rest.fitnesse.fixture.support.RowWrapper;
import smartrics.rest.fitnesse.fixture.support.Variables;
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
    private RestRequest lastRequest;
    private BodyTypeAdapter mockBodyTypeAdapter;
    @SuppressWarnings("rawtypes")
    private CellFormatter mockCellFormatter;
    private Config config;
    private RestResponse lastResponse;
    private Model mockModel;

    class IsEmbeddedImageEncodedInBase64 extends ArgumentMatcher<String> {
        public boolean matches(Object arg) {
            return arg.toString().startsWith("<img src=\"data:image/svg;base64,");
        }
     }    
    
    @Before
    public void setUp() {
        helper = new RestFixtureTestHelper();
        mockModel = mock(Model.class);
        mockCellFormatter = mock(CellFormatter.class);
        mockRestClient = mock(RestClient.class);
        lastRequest = new RestRequest();
        lastRequest.setBody("body");
        lastRequest.setResource("/uri");
        mockPartsFactory = mock(PartsFactory.class);
        mockBodyTypeAdapter = mock(BodyTypeAdapter.class);

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

        helper.wireMocks(config, mockPartsFactory, mockRestClient, lastRequest, lastResponse, mockCellFormatter, mockBodyTypeAdapter);
        fixture = new RestFixtureWithSeq(mockPartsFactory, "http://localhost:8080", null, "sequence.svg");
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
                RestRequest request = new RestRequest();
                request.setResource("http://host:8080/resources");
                request.setBody("<bob />");
                return request;
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
    public void shouldEmbedAPictureWithAnImgTagAndAnEncodedInlineImg() {
        when(mockCellFormatter.gray(argThat(isEmbeddedImageEncodedInBase64()))).thenReturn("grayed out cell with embedded image");
        RowWrapper<?> row = helper.createTestRow("embed", "");
        fixture.processRow(row);
        verify(row.getCell(1)).body();
        verify(row.getCell(1)).body("grayed out cell with embedded image");
        verifyNoMoreInteractions(row.getCell(1));
        verifyNoMoreInteractions(mockModel);
    }

    private IsEmbeddedImageEncodedInBase64 isEmbeddedImageEncodedInBase64() {
        return new IsEmbeddedImageEncodedInBase64();
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
            assertThat(e.getMessage(), is(equalTo("Both baseUrl and picture data (containing the picture name) need to be passed to the fixture")));
        }
    }

    @Test
    public void mustDelegateToModelAPost() {
        lastResponse.addHeader("Location", "/resources/999");
        RowWrapper<?> row = helper.createTestRow("POST", "/uri", "", "", "");
        fixture.setModel(mockModel);
        fixture.processRow(row);
        verify(mockModel).post("/uri", null, "id=999, status=200");
        verifyNoMoreInteractions(mockModel);
    }

    @Test
    public void mustDelegateToModelAComment() {
        RowWrapper<?> row = helper.createTestRow("comment", "some text");
        fixture.setModel(mockModel);
        fixture.processRow(row);
        verify(mockModel).comment("some text");
        verifyNoMoreInteractions(mockModel);
    }

    @Test
    public void mustDelegateToModelAGet() {
        RowWrapper<?> row = helper.createTestRow("GET", "/uri/123", "", "", "");
        fixture.setModel(mockModel);
        fixture.processRow(row);
        verify(mockModel).get("/uri/123", null, "status=200");
        verifyNoMoreInteractions(mockModel);
    }

    @Test
    public void mustDelegateToModelAPut() {
        RowWrapper<?> row = helper.createTestRow("PUT", "/uri/123", "", "", "");
        fixture.setModel(mockModel);
        fixture.processRow(row);
        verify(mockModel).put("/uri/123", null, "status=200");
        verifyNoMoreInteractions(mockModel);
    }

    @Test
    public void mustDelegateToModelADelete() {
        RowWrapper<?> row = helper.createTestRow("DELETE", "/uri/123", "", "", "");
        fixture.setModel(mockModel);
        fixture.processRow(row);
        verify(mockModel).delete("/uri/123", null, "status=200");
        verifyNoMoreInteractions(mockModel);
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
