package smartrics.rest.fitnesse.fixture;

import smartrics.rest.fitnesse.fixture.support.StringTypeAdapter;
import fit.ActionFixture;
import fit.Parse;

public class FitFormatter implements CellFormatter<Parse> {

	private final ActionFixture fixture;

	public FitFormatter(ActionFixture f) {
		this.fixture = f;
	}

	@Override
	public void exception(CellWrapper<Parse> cell, Throwable exception) {
		fixture.exception(cell.getWrapped(), exception);
	}

	@Override
	public void check(CellWrapper<Parse> valueCell, StringTypeAdapter adapter) {
		fixture.check(valueCell.getWrapped(), adapter);
	}

	@Override
	public String label(String string) {
		return ActionFixture.label(string);
	}

	@Override
	public void wrong(CellWrapper<Parse> expected) {
		fixture.wrong(expected.getWrapped());
	}

	@Override
	public void wrong(CellWrapper<Parse> expected, String actual) {
		fixture.wrong(expected.getWrapped(), actual);
	}

	@Override
	public void right(CellWrapper<Parse> expected) {
		fixture.right(expected.getWrapped());
	}

	@Override
	public String gray(String string) {
		return ActionFixture.gray(string);
	}

}
