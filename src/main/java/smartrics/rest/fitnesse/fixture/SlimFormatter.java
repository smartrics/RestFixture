package smartrics.rest.fitnesse.fixture;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import smartrics.rest.fitnesse.fixture.support.RestDataTypeAdapter;
import smartrics.rest.fitnesse.fixture.support.StringTypeAdapter;
import smartrics.rest.fitnesse.fixture.support.Tools;

public class SlimFormatter implements CellFormatter<String> {

    private boolean displayActual;

    public SlimFormatter() {
	}

    public void setDisplayActual(boolean d) {
        this.displayActual = d;
    }

    @Override
    public void exception(CellWrapper<String> cell, Throwable exception) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(out);
        exception.printStackTrace(ps);
        String m = Tools.toHtml(cell.getWrapped() + "\n-----\n") + Tools.toCode(out.toString());
        cell.body("error:" + m);
	}

	@Override
    public void check(CellWrapper<String> valueCell, StringTypeAdapter adapter) {
        // TODO: to implement check for SlimFormatter
	}

	@Override
	public String label(String string) {
        return Tools.toHtmlLabel(string);
	}

	@Override
    public void wrong(CellWrapper<String> expected, RestDataTypeAdapter ta) {
        expected.body("fail:" + expected.getWrapped());
	}

	@Override
    public void wrong(CellWrapper<String> expected, String actual, RestDataTypeAdapter ta) {
        String m = Tools.toHtml(expected.getWrapped());
        if (displayActual) {
            m = m + "\n-----\n" + Tools.toHtmlLabel("actual") + Tools.toHtml(actual);
        }
        expected.body("fail:" + m);
	}

	@Override
    public void right(CellWrapper<String> expected, RestDataTypeAdapter ta) {
        expected.body("pass:" + expected.getWrapped());
	}

	@Override
	public String gray(String string) {
        return "ignore:" + string;
	}

    @Override
    public void asLink(CellWrapper<String> cell, String link, String text) {
        cell.body("ignore:<a href='" + link + "'>" + text + "</a>");
    }
}
