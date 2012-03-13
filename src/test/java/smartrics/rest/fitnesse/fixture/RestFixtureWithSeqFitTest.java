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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Before;
import org.junit.Test;

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
        RowWrapper<?> row = helper.createTestRow("embed", "");
        fixture.processRow(row);
        verify(row.getCell(1)).body();
        verify(row.getCell(1)).body(
                "<img src=\"data:image/svg;base64,PHN2ZyAgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB2ZXJzaW9uPSIxLjE"
                        + "iPg0KICA8ZGVmcz4NCiAgICA8c3R5bGUgdHlwZT0idGV4dC9jc3MiPg0KICAgICAgPCFbQ0RBVEFbDQogICAgICAgIHJlY3QuY2xhc"
                        + "3Nib3h7DQogICAgICAgICAgc3Ryb2tlICAgICAgIDogYmxhY2s7DQogICAgICAgICAgc3Ryb2tlLXdpZHRoIDogMTsNCiAgICAgICA"
                        + "gICBmaWxsCQkgICA6IHdoaXRlOw0KICAgICAgICB9DQogICAgICAgIGVsbGlwc2UucGF0dGVybnsNCiAgICAgICAgICBzdHJva2UgI"
                        + "CAgICAgOiBibGFjazsNCiAgICAgICAgICBzdHJva2Utd2lkdGggOiAxOw0KICAgICAgICAgIGZpbGwJCSAgIDogd2hpdGU7DQogICA"
                        + "gICAgICAgc3Ryb2tlLWRhc2hhcnJheTogOSw1Ow0KICAgICAgICB9DQogICAgICAgIGxpbmUuY2xhc3Nib3h7DQogICAgICAgICAgc"
                        + "3Ryb2tlICAgICAgIDogYmxhY2s7DQogICAgICAgICAgc3Ryb2tlLXdpZHRoIDogMTsNCiAgICAgICAgfQ0KICAgICAgICBwb2x5Z29"
                        + "uLmNsYXNzYm94ew0KICAgICAgICAgIGZpbGwJCSAgIDogd2hpdGU7DQogICAgICAgICAgc3Ryb2tlICAgICAgIDogd2hpdGU7DQogI"
                        + "CAgICAgICAgc3Ryb2tlLXdpZHRoIDogMDsNCiAgICAgICAgfQ0KICAgICAgICBwb2x5Z29uLm5vdGV7DQogICAgICAgICAgZmlsbAk"
                        + "JICAgOiB3aGl0ZTsNCiAgICAgICAgICBzdHJva2UgICAgICAgOiBibGFjazsNCiAgICAgICAgICBzdHJva2Utd2lkdGggOiAxOw0KI"
                        + "CAgICAgICB9DQogICAgICAgIGxpbmUubGlmZWxpbmV7DQogICAgICAgICAgc3Ryb2tlICAgICAgIDogYmxhY2s7DQogICAgICAgICA"
                        + "gc3Ryb2tlLXdpZHRoIDogMTsNCiAgICAgICAgICBzdHJva2UtZGFzaGFycmF5OiA5LDU7DQogICAgICAgIH0NCiAgICAgICAgcGF0a"
                        + "C5hcnJvd3sNCiAgICAgICAgICBmaWxsCQkgICA6IG5vbmU7DQogICAgICAgICAgc3Ryb2tlICAgICAgIDogYmxhY2s7DQogICAgICA"
                        + "gICAgc3Ryb2tlLXdpZHRoIDogMTsNCiAgICAgICAgfQ0KICAgICAgICBwYXRoLmRhc2hhcnJvd3sNCiAgICAgICAgICBmaWxsCQkgI"
                        + "CA6IG5vbmU7DQogICAgICAgICAgc3Ryb2tlICAgICAgIDogYmxhY2s7DQogICAgICAgICAgc3Ryb2tlLXdpZHRoIDogMTsNCiAgICA"
                        + "gICAgICBzdHJva2UtZGFzaGFycmF5OiAxMCw3Ow0KICAgICAgICB9DQogICAgICAgIHRleHR7DQogICAgICAgICAgZmlsbCAgICAgI"
                        + "CAgIDogYmxhY2s7DQogICAgICAgICAgc3Ryb2tlLXdpZHRoIDogMDsNCiAgICAgICAgICBmb250LWZhbWlseSAgOiBzYW5zLXNlcml"
                        + "mOw0KICAgICAgICB9DQogICAgICAgIHRleHQubmFtZXsNCiAgICAgICAgICBmb250LXNpemUgICAgOiAxMnB4Ow0KICAgICAgICAgI"
                        + "GZvbnQtd2VpZ2h0ICA6IGJvbGQ7DQogICAgICAgICAgdGV4dC1hbmNob3IgIDogbWlkZGxlOw0KICAgICAgICB9DQogICAgICAgIHR"
                        + "leHQuaXRlbXsNCiAgICAgICAgICBmb250LXNpemUgICAgOiAxMnB4Ow0KICAgICAgICB9DQogICAgICAgIHRleHQudmlzaWJpbGl0e"
                        + "XsNCiAgICAgICAgICBmb250LXNpemUgICAgOiAxMnB4Ow0KICAgICAgICAgIGZvbnQtZmFtaWx5ICA6IG1vbm9zcGFjZTsNCiAgICA"
                        + "gICAgICBmb250LXdlaWdodCAgOiBib2xkOw0KICAgICAgICAgIHRleHQtYW5jaG9yICA6IG1pZGRsZTsNCiAgICAgICAgfQ0KICAgI"
                        + "CAgICB0ZXh0LmFic3RyYWN0ew0KICAgICAgICAgIGZvbnQtc3R5bGUgICA6IGl0YWxpYzsNCiAgICAgICAgfQ0KICAgICAgICB0ZXh"
                        + "0Lm1lc3NhZ2V7DQogICAgICAgICAgZm9udC1zaXplICAgIDogMTJweDsNCiAgICAgICAgICB0ZXh0LWFuY2hvciAgOiBzdGFydDsNC"
                        + "iAgICAgICAgfQ0KICAgICAgICB0ZXh0LmJhY2ttZXNzYWdlew0KICAgICAgICAgIGZvbnQtc2l6ZSAgICA6IDEycHg7DQogICAgICA"
                        + "gICAgdGV4dC1hbmNob3IgIDogZW5kOw0KICAgICAgICB9DQogICAgICBdXT4NCiAgICA8L3N0eWxlPg0KICANCiAgDQogICAgPG1hc"
                        + "mtlciBpZD0ibm9IZWFkIiByZWZYPSIwIiByZWZZPSIwIiA+PC9tYXJrZXI+DQogIA0KICAgIDwhLS0gVU1MIFNlcXVlbmNlIFN5bmM"
                        + "gTWVzc2FnZSBIZWFkIC0tPg0KICAgIDxtYXJrZXIgaWQ9ImNsb3NlZEhlYWQiDQogICAgICB2aWV3Qm94PSIwIDAgMzAgMzAiIHJlZ"
                        + "lg9IjMwIiByZWZZPSIxNSIgDQogICAgICBtYXJrZXJVbml0cz0ic3Ryb2tlV2lkdGgiDQogICAgICBtYXJrZXJXaWR0aD0iMTAiIG1"
                        + "hcmtlckhlaWdodD0iMTAiDQogICAgICBvcmllbnQ9ImF1dG8iPg0KICAgICAgPHBhdGggZD0iTSAwIDAgTCAzMCAxNSBMIDAgMzAge"
                        + "iIgLz4NCiAgICA8L21hcmtlcj4NCiAgICANCiAgICA8IS0tIFVNTCBTZXF1ZW5jZSBBc3luYyBNZXNzYWdlIEhlYWQgLS0+DQogICA"
                        + "gPG1hcmtlciBpZD0iaGFsZkNsb3NlZEhlYWQiDQogICAgICB2aWV3Qm94PSIwIDAgMzAgMzAiIHJlZlg9IjMwIiByZWZZPSIxNSIgD"
                        + "QogICAgICBtYXJrZXJVbml0cz0ic3Ryb2tlV2lkdGgiDQogICAgICBtYXJrZXJXaWR0aD0iMTUiIG1hcmtlckhlaWdodD0iMTUiDQo"
                        + "gICAgICBvcmllbnQ9ImF1dG8iPg0KICAgICAgPHBhdGggZD0iTSAwIDE1IDMwIDE1IEwgMCAzMCB6IiAvPg0KICAgIDwvbWFya2VyP"
                        + "g0KICAgIA0KICAgIDwhLS0gVU1MIEluaGVyaXRhbmNlIEhlYWQgLS0+DQogICAgPG1hcmtlciBpZD0iZW1wdHlIZWFkIg0KICAgICA"
                        + "gdmlld0JveD0iMCAwIDMwIDMwIiByZWZYPSIzMCIgcmVmWT0iMTUiIA0KICAgICAgbWFya2VyVW5pdHM9InN0cm9rZVdpZHRoIg0KI"
                        + "CAgICAgbWFya2VyV2lkdGg9IjE1IiBtYXJrZXJIZWlnaHQ9IjE1Ig0KICAgICAgb3JpZW50PSJhdXRvIj4NCiAgICAgIDxwYXRoIGQ9"
                        + "Ik0gMCAwIEwgMzAgMTUgTCAwIDMwIHoiIGZpbGw9IndoaXRlIiBzdHJva2U9ImJsYWNrIiAvPg0KICAgIDwvbWFya2VyPg0KICAgI"
                        + "A0KICAgIDwhLS0gVU1MIEFzc29jaWF0aW9uIEhlYWQgLS0+DQogICAgPG1hcmtlciBpZD0ib3BlbkhlYWQiDQogICAgICB2aWV3Qm94P"
                        + "SIwIDAgMzAgMzAiIHJlZlg9IjMwIiByZWZZPSIxNSIgDQogICAgICBtYXJrZXJVbml0cz0ic3Ryb2tlV2lkdGgiDQogICAgICBtYX"
                        + "JrZXJXaWR0aD0iMTAiIG1hcmtlckhlaWdodD0iMTAiDQogICAgICBvcmllbnQ9ImF1dG8iPg0KICAgICAgPHBhdGggZD0iTSAwIDA"
                        + "gTCAzMCAxNSBMIDAgMzAiIGZpbGw9Im5vbmUiIHN0cm9rZT0iYmxhY2siIC8+DQogICAgPC9tYXJrZXI+DQogICAgDQogICAgPCEt"
                        + "LSBIZWFkIC0tPg0KICAgIDxtYXJrZXIgaWQ9ImhhbGZPcGVuSGVhZCINCiAgICAgIHZpZXdCb3g9IjAgMCAzMCAzMCIgcmVmWD0iMz"
                        + "AiIHJlZlk9IjE1IiANCiAgICAgIG1hcmtlclVuaXRzPSJzdHJva2VXaWR0aCINCiAgICAgIG1hcmtlcldpZHRoPSIxNSIgbWFya2Vy"
                        + "SGVpZ2h0PSIxNSINCiAgICAgIG9yaWVudD0iYXV0byI+DQogICAgICA8cGF0aCBkPSJNIDAgMTUgTCAzMCAxNSBMIDAgMzAiIGZpbG"
                        + "w9Im5vbmUiIHN0cm9rZT0iYmxhY2siIC8+DQogICAgPC9tYXJrZXI+DQogICAgDQogICAgPCEtLSBVTUwgQWdncmVnYXRpb24gVGFp"
                        + "bCAtLT4NCiAgICA8bWFya2VyIGlkPSJvcGVuRGlhbW9uZCINCiAgICAgIHZpZXdCb3g9IjAgMCAzMCAzMCIgcmVmWD0iMCIgcmVmWT"
                        + "0iMTUiIA0KICAgICAgbWFya2VyVW5pdHM9InN0cm9rZVdpZHRoIg0KICAgICAgbWFya2VyV2lkdGg9IjE1IiBtYXJrZXJIZWlnaHQ9"
                        + "IjE1Ig0KICAgICAgb3JpZW50PSJhdXRvIj4NCiAgICAgIDxwYXRoIGQ9Ik0gMCAxNSBMIDE1IDMgTCAzMCAxNSBMIDE1IDI3IHoiIG"
                        + "ZpbGw9IndoaXRlIiBzdHJva2U9ImJsYWNrIiAvPg0KICAgIDwvbWFya2VyPg0KICAgIA0KICAgIDwhLS0gVU1MIENvbXBvc2l0aW9u"
                        + "IFRhaWwgLS0+DQogICAgPG1hcmtlciBpZD0iY2xvc2VkRGlhbW9uZCINCiAgICAgIHZpZXdCb3g9IjAgMCAzMCAzMCIgcmVmWD0iMC"
                        + "IgcmVmWT0iMTUiIA0KICAgICAgbWFya2VyVW5pdHM9InN0cm9rZVdpZHRoIg0KICAgICAgbWFya2VyV2lkdGg9IjE1IiBtYXJrZXJI"
                        + "ZWlnaHQ9IjE1Ig0KICAgICAgb3JpZW50PSJhdXRvIj4NCiAgICAgIDxwYXRoIGQ9Ik0gMCAxNSBMIDE1IDMgTCAzMCAxNSBMIDE1ID"
                        + "I3IHoiIC8+DQogICAgPC9tYXJrZXI+DQogICAgDQogICAgPCEtLSBVTUwgQ29tcG9uZW50IEludGVyZmFjZSBQcm92aWRlZCBIZWFk"
                        + "IC0tPg0KICAgIDxtYXJrZXIgaWQ9ImVtcHR5Q2lyY2xlIg0KICAgICAgdmlld0JveD0iMCAwIDMwIDMwIiByZWZYPSIxNSIgcmVmWT"
                        + "0iMTUiIA0KICAgICAgbWFya2VyVW5pdHM9InN0cm9rZVdpZHRoIg0KICAgICAgbWFya2VyV2lkdGg9IjE1IiBtYXJrZXJIZWlnaHQ9"
                        + "IjE1IiA+DQogICAgIDxjaXJjbGUgY3g9IjE1IiBjeT0iMTUiIHI9IjEwIiBmaWxsPSJ3aGl0ZSIgc3Ryb2tlPSJibGFjayIgc3Ryb2"
                        + "tlLXdpZHRoPSIxIiAvPg0KICAgIDwvbWFya2VyPg0KICAgIA0KICAgIDwhLS0gVU1MIENvbXBvbmVudCBJbnRlcmZhY2UgUmVxdWly"
                        + "ZWQgVGFpbCAtLT4NCiAgICA8bWFya2VyIGlkPSJoYWxmQXJjIg0KICAgICAgdmlld0JveD0iMCAwIDMwIDMwIiByZWZYPSIxNSIgcm"
                        + "VmWT0iMTUiIA0KICAgICAgbWFya2VyVW5pdHM9InN0cm9rZVdpZHRoIg0KICAgICAgbWFya2VyV2lkdGg9IjE1IiBtYXJrZXJIZWln"
                        + "aHQ9IjE1Ig0KICAgICAgb3JpZW50PSJhdXRvIj4NCiAgICAgPHBhdGggZD0iTSAxNSwwIGExMiwxMiAwIDAsMCAxNSwzMCIgZmlsbD"
                        + "0ibm9uZSIgc3Ryb2tlPSJibGFjayIgc3Ryb2tlLXdpZHRoPSIxIiAvPg0KICAgIDwvbWFya2VyPg0KICAgIA0KICAgIDxkZXNjPkNv"
                        + "bXBvbmVudCBTeW1ib2w8L2Rlc2M+DQogICAgPHN5bWJvbCBpZD0iQ29tcG9uZW50IiB2aWV3Qm94PSIwIDAgMTYgMjUiID4NCiAgIC"
                        + "AgIDxyZWN0IHg9IjQiIHk9IjAiIHdpZHRoPSIxMiIgaGVpZ2h0PSIyMCIgZmlsbD0id2hpdGUiIHN0cm9rZT0iYmxhY2siIC8+DQog"
                        + "ICAgICA8cmVjdCB4PSIwIiB5PSI0IiB3aWR0aD0iOCIgaGVpZ2h0PSI1IiBmaWxsPSJ3aGl0ZSIgc3Ryb2tlPSJibGFjayIgLz4NCi"
                        + "AgICAgIDxyZWN0IHg9IjAiIHk9IjEyIiB3aWR0aD0iOCIgaGVpZ2h0PSI1IiBmaWxsPSJ3aGl0ZSIgc3Ryb2tlPSJibGFjayIvPg0K"
                        + "ICAgIDwvc3ltYm9sPg0KICAgIA0KICAgIDxkZXNjPkhpZXJhcmNoeSBTeW1ib2w8L2Rlc2M+DQogICAgPHN5bWJvbCBpZD0iSGllcm"
                        + "FyY2h5IiB2aWV3Qm94PSIwIDAgMTYgMjUiID4NCiAgICAgIDxyZWN0IHg9IjUiIHk9IjAiIHdpZHRoPSI2IiBoZWlnaHQ9IjYiIGZp"
                        + "bGw9IndoaXRlIiBzdHJva2U9ImJsYWNrIiAvPg0KICAgICAgPHJlY3QgeD0iMCIgeT0iMTIiIHdpZHRoPSI2IiBoZWlnaHQ9IjYiIG"
                        + "ZpbGw9IndoaXRlIiBzdHJva2U9ImJsYWNrIi8+DQogICAgICA8cmVjdCB4PSIxMCIgeT0iMTIiIHdpZHRoPSI2IiBoZWlnaHQ9IjYi"
                        + "IGZpbGw9IndoaXRlIiBzdHJva2U9ImJsYWNrIi8+DQogICAgICA8cGF0aCBkPSJNIDMsMTEgTCAzLDkgTCAxMyw5IEwgMTMsMTEiIG"
                        + "ZpbGw9Im5vbmUiIHN0cm9rZT0iYmxhY2siIHN0cm9rZS13aWR0aD0iMSIgLz4NCiAgICAgIDxsaW5lIHgxPSI4IiB4Mj0iOCIgeTE9"
                        + "IjkiIHkyPSI3IiBmaWxsPSJub25lIiBzdHJva2U9ImJsYWNrIiBzdHJva2Utd2lkdGg9IjEiIC8+DQogICAgPC9zeW1ib2w+DQogIC"
                        + "AgDQogICAgPGRlc2M+RGVsZWdhdGlvbiBTeW1ib2w8L2Rlc2M+DQogICAgPHN5bWJvbCBpZD0iRGVsZWdhdGlvbiIgdmlld0JveD0i"
                        + "MCAwIDE2IDI1IiA+DQogICAgICA8cmVjdCB4PSIwIiB5PSI1IiB3aWR0aD0iNiIgaGVpZ2h0PSI2IiBmaWxsPSJ3aGl0ZSIgc3Ryb2"
                        + "tlPSJibGFjayIvPg0KICAgICAgPHJlY3QgeD0iMTAiIHk9IjUiIHdpZHRoPSI2IiBoZWlnaHQ9IjYiIGZpbGw9IndoaXRlIiBzdHJv"
                        + "a2U9ImJsYWNrIi8+DQogICAgICA8bGluZSB4MT0iNiIgeDI9IjEwIiB5MT0iOCIgeTI9IjgiIGZpbGw9Im5vbmUiIHN0cm9rZT0iYm"
                        + "xhY2siIHN0cm9rZS13aWR0aD0iMSIgLz4NCiAgICA8L3N5bWJvbD4NCiAgICANCiAgICA8ZGVzYz5SYWRpYWwgU3ltYm9sPC9kZXNj"
                        + "Pg0KICAgIDxzeW1ib2wgaWQ9IlJhZGlhbCIgdmlld0JveD0iMCAwIDE2IDI1IiA+DQogICAgICA8cmVjdCB4PSI2IiB5PSIwIiB3aW"
                        + "R0aD0iNSIgaGVpZ2h0PSI1IiBmaWxsPSJ3aGl0ZSIgc3Ryb2tlPSJibGFjayIvPg0KICAgICAgPHJlY3QgeD0iMCIgeT0iNiIgd2lk"
                        + "dGg9IjUiIGhlaWdodD0iNSIgZmlsbD0id2hpdGUiIHN0cm9rZT0iYmxhY2siLz4NCiAgICAgIDxyZWN0IHg9IjEyIiB5PSI2IiB3aW"
                        + "R0aD0iNSIgaGVpZ2h0PSI1IiBmaWxsPSJ3aGl0ZSIgc3Ryb2tlPSJibGFjayIvPg0KICAgICAgPHJlY3QgeD0iNiIgeT0iMTQiIHdp"
                        + "ZHRoPSI1IiBoZWlnaHQ9IjUiIGZpbGw9IndoaXRlIiBzdHJva2U9ImJsYWNrIi8+DQogICAgICA8bGluZSB4MT0iOCIgeTE9IjE0Ii"
                        + "B4Mj0iOCIgeTI9IjUiIGZpbGw9Im5vbmUiIHN0cm9rZT0iYmxhY2siIHN0cm9rZS13aWR0aD0iMSIgLz4NCiAgICAgIDxsaW5lIHgx"
                        + "PSI4IiB5MT0iMTQiIHgyPSIzIiB5Mj0iMTEiIGZpbGw9Im5vbmUiIHN0cm9rZT0iYmxhY2siIHN0cm9rZS13aWR0aD0iMSIgLz4NCi"
                        + "AgICAgIDxsaW5lIHgxPSI4IiB5MT0iMTQiIHgyPSIxMiIgeTI9IjExIiBmaWxsPSJub25lIiBzdHJva2U9ImJsYWNrIiBzdHJva2Ut"
                        + "d2lkdGg9IjEiIC8+DQogICAgPC9zeW1ib2w+DQogICAgDQogICAgPGRlc2M+RmxvdyBTeW1ib2w8L2Rlc2M+DQogICAgPHN5bWJvbC"
                        + "BpZD0iRmxvdyIgdmlld0JveD0iMCAwIDE2IDI1IiA+DQogICAgICA8cmVjdCB4PSIwIiB5PSIwIiB3aWR0aD0iNSIgaGVpZ2h0PSI1"
                        + "IiBmaWxsPSJ3aGl0ZSIgc3Ryb2tlPSJibGFjayIvPg0KICAgICAgPHJlY3QgeD0iOSIgeT0iMCIgd2lkdGg9IjUiIGhlaWdodD0iNS"
                        + "IgZmlsbD0id2hpdGUiIHN0cm9rZT0iYmxhY2siLz4NCiAgICAgIDxyZWN0IHg9IjAiIHk9IjgiIHdpZHRoPSI1IiBoZWlnaHQ9IjUi"
                        + "IGZpbGw9IndoaXRlIiBzdHJva2U9ImJsYWNrIi8+DQogICAgPC9zeW1ib2w+DQogICAgDQogIDwvZGVmcz4NCiAgDQoKPGRlc2M+QW"
                        + "dlbnRzIGFuZCBsaWZlbGluZXMgb2YgdGhlIHNlcXVlbmNlIGRpYWdyYW08L2Rlc2M+Cgo8ZGVzYz5NZXNzYWdlczwvZGVzYz4NCiAgDQo8L3N2Zz4NCg==\" />");
        verifyNoMoreInteractions(row.getCell(1));
        verifyNoMoreInteractions(mockModel);
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
