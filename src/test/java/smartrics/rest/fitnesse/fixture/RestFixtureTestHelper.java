package smartrics.rest.fitnesse.fixture;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

    // public String createFitTestInstance(String... rows) {
    // StringBuffer buffer = new StringBuffer();
    // buffer.append("<row>");
    // for (String c : cells) {
    // buffer.append("<col>").append(c).append("</col>");
    // }
    // buffer.append("</row>");
    // return buffer.toString();
    // }

}
