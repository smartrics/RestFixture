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

import java.util.ArrayList;
import java.util.List;

import smartrics.rest.fitnesse.fixture.support.CellWrapper;
import smartrics.rest.fitnesse.fixture.support.RowWrapper;
import fit.Parse;

/**
 * Wrapper class for table row for Fit Runner.
 * 
 * @author smartrics
 * 
 */
public class FitRow implements RowWrapper<Parse> {

	private final Parse cells;

	private final List<CellWrapper<Parse>> row;

	public FitRow(Parse parse) {
		this.cells = parse;
		Parse next = cells;
		row = new ArrayList<CellWrapper<Parse>>();
		while (next != null) {
			row.add(new FitCell(next));
			next = next.more;
		}
	}

    public int size() {
        if (row != null) {
            return row.size();
        }
        return 0;
    }

    public CellWrapper<Parse> getCell(int c) {
        if (c < row.size()) {
            return row.get(c);
        }
        return null;
    }

    public CellWrapper<Parse> removeCell(int c) {
        if (c < row.size()) {
            return row.remove(c);
        }
        return null;
    }
}
