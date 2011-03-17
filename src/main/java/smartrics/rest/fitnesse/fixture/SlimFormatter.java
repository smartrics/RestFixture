package smartrics.rest.fitnesse.fixture;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import smartrics.rest.fitnesse.fixture.support.StringTypeAdapter;

public class SlimFormatter implements CellFormatter<String> {

    public SlimFormatter() {
	}

	@Override
    public void exception(CellWrapper<String> cell, Throwable exception) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(out);
        exception.printStackTrace(ps);
        cell.body("error:" + cell.getWrapped() + "<br /><hr/><br/>" + out.toString());
	}

	@Override
    public void check(CellWrapper<String> valueCell, StringTypeAdapter adapter) {
	}

	@Override
	public String label(String string) {
        return "<i>" + string + "</i>";
	}

	@Override
    public void wrong(CellWrapper<String> expected) {
        expected.body("fail:" + expected.getWrapped());
	}

	@Override
    public void wrong(CellWrapper<String> expected, String actual) {
        expected.body("fail:" + expected.getWrapped() + "<br/><hr/><br/>actual:<br/>" + actual);
	}

	@Override
    public void right(CellWrapper<String> expected) {
        expected.body("pass:" + expected.getWrapped());
	}

	@Override
	public String gray(String string) {
        return "ignore:" + string;
	}
}
