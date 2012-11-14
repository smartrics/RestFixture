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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import smartrics.rest.client.RestClient;
import smartrics.rest.client.RestRequest;
import smartrics.rest.client.RestResponse;
import smartrics.rest.fitnesse.fixture.support.BodyTypeAdapter;
import smartrics.rest.fitnesse.fixture.support.CellFormatter;
import smartrics.rest.fitnesse.fixture.support.CellWrapper;
import smartrics.rest.fitnesse.fixture.support.Config;
import smartrics.rest.fitnesse.fixture.support.ContentType;
import smartrics.rest.fitnesse.fixture.support.RowWrapper;
import fit.Parse;
import fit.exception.FitParseException;

public class RestFixtureTestHelper {

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public RowWrapper<?> createTestRow(String... cells) {
        RowWrapper<?> row = mock(RowWrapper.class);
        for (int i = 0; i < cells.length; i++) {
            CellWrapper cell = mock(CellWrapper.class);
            when(cell.getWrapped()).thenReturn(cells[i], cells[i]);
            when(cell.text()).thenReturn(cells[i], cells[i]);
            when(cell.body()).thenReturn(cells[i], cells[i]);
            when(row.getCell(i)).thenReturn(cell, cell);
        }
		return row;
	}

    public Parse createSingleRowFitTable(String... cells) {
        return createFitTable(cells);
    }

    public Parse createFitTable(String[] ... cellsArray) {
        Parse t = null;
        StringBuffer rBuff = new StringBuffer();
        rBuff.append("<table>");
        for(String[] cells : cellsArray) {
        	rBuff.append(createFitRow(cells));
        }
        rBuff.append("</table>");
        try {
            t = new Parse(rBuff.toString());
        } catch (FitParseException e) {
            throw new RuntimeException(e);
        }
        return t;
    }

    private String createFitRow(String... cells) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<tr>");
        for (String c : cells) {
            buffer.append("<td>").append(c).append("</td>");
        }
        buffer.append("</tr>");
        return buffer.toString();
    }

    public Parse buildEmptyParse() {
        return createSingleRowFitTable("&nbsp;");
    }

    public List<List<String>> createSingleRowSlimTable(String... cells) {
        List<List<String>> table = new ArrayList<List<String>>();
        table.add(Arrays.asList(cells));
        return table;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void wireMocks(Config conf, PartsFactory pf, RestClient rc, RestRequest req, RestResponse resp, CellFormatter cf, BodyTypeAdapter bta) {
        when(pf.buildRestClient(conf)).thenReturn(rc);
        when(pf.buildRestRequest()).thenReturn(req);
        when(rc.execute(req)).thenReturn(resp);
        when(pf.buildCellFormatter(any(RestFixture.Runner.class))).thenReturn(cf);
        when(pf.buildBodyTypeAdapter(isA(ContentType.class), isA(String.class))).thenReturn(bta);
    }

}
    
