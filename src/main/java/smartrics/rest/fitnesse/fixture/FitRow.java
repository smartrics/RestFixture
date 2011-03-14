package smartrics.rest.fitnesse.fixture;

import java.util.ArrayList;
import java.util.List;

import fit.Parse;

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

	public CellWrapper<Parse> getCell(int c) {
		if (c < row.size()) {
			return row.get(c);
		}
		return null;
	}

}
