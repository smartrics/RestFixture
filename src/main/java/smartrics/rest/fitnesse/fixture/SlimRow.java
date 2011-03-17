package smartrics.rest.fitnesse.fixture;

import java.util.ArrayList;
import java.util.List;

public class SlimRow implements RowWrapper<String> {

	private final List<CellWrapper<String>> row;

    public SlimRow(List<String> rawRow) {
        this.row = new ArrayList<CellWrapper<String>>();
        for (String r : rawRow) {
            this.row.add(new SlimCell(r));
		}
	}

	public CellWrapper<String> getCell(int c) {
        if (c < this.row.size()) {
            return this.row.get(c);
		}
		return null;
	}

}
