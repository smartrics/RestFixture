/*  Copyright 2012 PROS Pricing (www.prospricing.com)
 *
 *  This file is donated to RestFixture.
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
 *  If you want to contact the original author of RestFixture please leave a comment here
 *  http://smartrics.blogspot.com/2008/08/get-fitnesse-with-some-rest.html
 */

package smartrics.rest.fitnesse.fixture.loopsupport;

import smartrics.rest.fitnesse.fixture.support.CellWrapper;
import smartrics.rest.fitnesse.fixture.support.RowWrapper;

/**
 * This class is used to hide the first X cells in a row, where X is an integer that is passed
 * into the constructor.  This allows the first cells to be used to configure a test that is then
 * executed from the remainder of the row. see LoopUntilRestFixture for an example
 *
 * @author Adam Roberts (aroberts@alum.rit.edu)
 */
public class RowShiftWrapper<E> implements RowWrapper<E>
{

    /** The row that is being shifted to hide the beginning cells */
    private final RowWrapper<E> row;

    /** The number of cells that should be shifted */
    private final int leftShiftAmount;

    /**
     * @param row the row of cells that needs to have the start fields hidden
     * @param leftShiftAmount the number of fields at the beginning of the row to hide
     */
    public RowShiftWrapper(RowWrapper<E> row, int leftShiftAmount)
    {
        this.row = row;
        this.leftShiftAmount = leftShiftAmount;
    }

    /**
     * @return Returns the row.
     */
    public RowWrapper<E> getRow()
    {
        return row;
    }

    /**
     * @see smartrics.rest.fitnesse.fixture.support.RowWrapper#getCell(int)
     */
    @Override
    public CellWrapper<E> getCell(int cellPosition)
    {
        return row.getCell(cellPosition + leftShiftAmount);
    }

    /**
     * @see smartrics.rest.fitnesse.fixture.support.RowWrapper#size()
     */
    @Override
    public int size()
    {
        return row.size() - leftShiftAmount;
    }

    /**
     * @see smartrics.rest.fitnesse.fixture.support.RowWrapper#removeCell(int)
     */
    @Override
    public CellWrapper<E> removeCell(int cellPosition)
    {
        return row.removeCell(cellPosition + leftShiftAmount);
    }

}
