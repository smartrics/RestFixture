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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import smartrics.rest.fitnesse.fixture.support.CellWrapper;
import smartrics.rest.fitnesse.fixture.support.RowWrapper;

/**
 * @author Adam Roberts (aroberts@alum.rit.edu)
 */
public class RowShiftWrapperTest
{

    @Mock RowWrapper<Object> mockOriginalRow;
    @Mock CellWrapper<Object> mockCell1;
    @Mock CellWrapper<Object> mockCell2;
    @Mock CellWrapper<Object> mockCell3;
    @Mock CellWrapper<Object> mockCell4;
    @Mock CellWrapper<Object> mockCell5;

    @Before
    public void setUp() throws Exception
    {
        MockitoAnnotations.initMocks(this);

        when(mockOriginalRow.getCell(1)).thenReturn(mockCell1);
        when(mockOriginalRow.getCell(2)).thenReturn(mockCell2);
        when(mockOriginalRow.getCell(3)).thenReturn(mockCell3);
        when(mockOriginalRow.getCell(4)).thenReturn(mockCell4);
        when(mockOriginalRow.getCell(5)).thenReturn(mockCell5);
        when(mockOriginalRow.removeCell(1)).thenReturn(mockCell1);
        when(mockOriginalRow.removeCell(2)).thenReturn(mockCell2);
        when(mockOriginalRow.removeCell(3)).thenReturn(mockCell3);
        when(mockOriginalRow.removeCell(4)).thenReturn(mockCell4);
        when(mockOriginalRow.removeCell(5)).thenReturn(mockCell5);
        when(mockOriginalRow.size()).thenReturn(5);
    }

    @Test
    public final void testGetRow()
    {
        // Setup
        RowShiftWrapper<Object> rowShift = new RowShiftWrapper<Object>(mockOriginalRow, 2);

        // Test & Validate
        assertEquals("Failed to return the wrapped row", mockOriginalRow, rowShift.getRow());
    }

    @Test
    public final void testGetCell()
    {
        // Setup
        RowShiftWrapper<Object> rowShift = new RowShiftWrapper<Object>(mockOriginalRow, 2);

        // Test & Validate
        assertEquals("Failed to shift correctly", mockCell3, rowShift.getCell(1));
        assertEquals("Failed to shift correctly", mockCell4, rowShift.getCell(2));
        assertEquals("Failed to shift correctly", mockCell5, rowShift.getCell(3));
        assertNull("Only 3 cells should be reachable", rowShift.getCell(4));
        assertNull("Only 3 cells should be reachable", rowShift.getCell(5));
    }

    @Test
    public final void testSize()
    {
        // Setup
        RowShiftWrapper<Object> rowShift = new RowShiftWrapper<Object>(mockOriginalRow, 2);

        // Test & Validate
        assertEquals("The size should have been shrunk by 2", 3, rowShift.size());
    }

    @Test
    public final void testRemoveCell()
    {
        // Setup
        RowShiftWrapper<Object> rowShift = new RowShiftWrapper<Object>(mockOriginalRow, 2);

        // Test & Validate
        assertEquals("Failed to shift correctly", mockCell3, rowShift.removeCell(1));
        assertEquals("Failed to shift correctly", mockCell4, rowShift.removeCell(2));
        assertEquals("Failed to shift correctly", mockCell5, rowShift.removeCell(3));
        assertNull("Only 3 cells should be reachable", rowShift.removeCell(4));
        assertNull("Only 3 cells should be reachable", rowShift.removeCell(5));
    }

}
