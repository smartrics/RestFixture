package smartrics.rest.fitnesse.fixture;

import smartrics.rest.fitnesse.fixture.support.RestDataTypeAdapter;
import smartrics.rest.fitnesse.fixture.support.StringTypeAdapter;
import smartrics.rest.fitnesse.fixture.support.Tools;
import fit.ActionFixture;
import fit.Parse;

public class FitFormatter implements CellFormatter<Parse> {

    private ActionFixture fixture;
    private boolean displayActual;

    public void setActionFixtureDelegate(ActionFixture f) {
		this.fixture = f;
	}

    public void setDisplayActual(boolean d) {
        this.displayActual = d;
    }

	@Override
	public void exception(CellWrapper<Parse> cell, Throwable exception) {
		Parse wrapped = cell.getWrapped();
        fixture.exception(wrapped, exception);
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
    public void wrong(CellWrapper<Parse> expected, RestDataTypeAdapter typeAdapter) {
        wrong(expected, typeAdapter.get().toString(), typeAdapter);
	}

	@Override
    public void wrong(CellWrapper<Parse> expected, String actual, RestDataTypeAdapter ta) {
        fixture.wrong(expected.getWrapped());
        StringBuffer sb = new StringBuffer();
        for (String e : ta.getErrors()) {
            sb.append(e).append(System.getProperty("line.separator"));
        }
        expected.addToBody(label("expected") + "<hr>" + ta.toString() + label("actual") + "<hr>" + Tools.toHtml(sb.toString()) + label("errors"));
	}

	@Override
    public void right(CellWrapper<Parse> expected, RestDataTypeAdapter ta) {
        fixture.right(expected.getWrapped());
        if (displayActual && !expected.text().equals(ta.toString())) {
            expected.addToBody(label("expected") + "<hr>" + ta.toString() + label("actual"));
        }
	}

	@Override
	public String gray(String string) {
		return ActionFixture.gray(string);
	}

    @Override
    public void asLink(CellWrapper<Parse> cell, String link, String text) {
        cell.body("<a href='" + link + "'>" + text + "</a>");
    }

}
