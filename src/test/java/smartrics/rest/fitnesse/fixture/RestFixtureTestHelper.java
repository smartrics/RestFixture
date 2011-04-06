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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import smartrics.rest.fitnesse.fixture.support.CellWrapper;
import smartrics.rest.fitnesse.fixture.support.RowWrapper;

public class RestFixtureTestHelper {

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public RowWrapper<?> createFitTestRow(String... rows) {
        RowWrapper<?> row = mock(RowWrapper.class);
        for (int i = 0; i < rows.length; i++) {
            CellWrapper cell = mock(CellWrapper.class);
            when(cell.getWrapped()).thenReturn(rows[i]);
            when(cell.text()).thenReturn(rows[i]);
            when(row.getCell(i)).thenReturn(cell, cell);
        }
		return row;
	}

    public List<List<String>> createSingleRowSlimTable(String... cells) {
        List<List<String>> table = new ArrayList<List<String>>();
        table.add(Arrays.asList(cells));
        return table;
    }
}
