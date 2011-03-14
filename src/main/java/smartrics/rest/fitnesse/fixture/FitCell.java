package smartrics.rest.fitnesse.fixture;

import fit.Parse;

public class FitCell implements CellWrapper<Parse> {

	private final Parse cell;

	public FitCell(Parse c) {
		this.cell = c;
	}

	@Override
	public String text() {
		return cell.text();
	}

	@Override
	public void body(String string) {
		cell.body = string;
	}

	@Override
	public String body() {
		return cell.body;
	}

	@Override
	public void addToBody(String string) {
		cell.addToBody(string);
	}

	@Override
	public Parse getWrapped() {
		// TODO Auto-generated method stub
		return cell;
	}
}
