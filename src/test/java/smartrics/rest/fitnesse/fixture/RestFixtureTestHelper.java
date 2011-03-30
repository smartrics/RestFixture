package smartrics.rest.fitnesse.fixture;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
