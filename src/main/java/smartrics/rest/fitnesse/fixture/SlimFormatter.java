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
    public void check(CellWrapper<String> expected, StringTypeAdapter actual) {
        if (null == expected.body() || "".equals(expected.body())) {
            if (actual.get() == null) {
                return;
            } else {
                expected.body(gray(actual.get().toString()));
                return;
            }
        }

        if (actual.get() != null && actual.equals(expected.body(), actual.get().toString())) {
            right(expected, actual);
        } else {
            wrong(expected, actual);
        }
    }

    @Override
    public String label(String string) {
        return Tools.toHtmlLabel(string);
    }

    @Override
    public void wrong(CellWrapper<String> expected, RestDataTypeAdapter ta) {
        wrong(expected, ta.get().toString(), ta);
    }

    @Override
    public void wrong(CellWrapper<String> expected, String actual, RestDataTypeAdapter ta) {
        expected.body(Tools.toHtml(expected.body()));
        StringBuffer sb = new StringBuffer();
        sb.append(Tools.toHtml("\n"));
        sb.append(label("expected"));
        if (displayActual) {
            sb.append(Tools.toHtml("-----"));
            sb.append(Tools.toHtml("\n"));
            sb.append(Tools.toHtml(ta.toString()));
            sb.append(Tools.toHtml("\n"));
            sb.append(label("actual"));
        }
        if (ta.getErrors().size() > 0) {
            sb.append(Tools.toHtml("-----"));
            sb.append(Tools.toHtml("\n"));
            for (String e : ta.getErrors()) {
                sb.append(Tools.toHtml(e + "\n"));
            }
            sb.append(Tools.toHtml("\n"));
            sb.append(label("errors"));
        }

        expected.addToBody(sb.toString());

        expected.body("fail:" + expected.body());
    }

    @Override
    public void right(CellWrapper<String> expected, RestDataTypeAdapter ta) {
        if (displayActual && !expected.text().equals(ta.toString())) {
            expected.body(Tools.toHtml(expected.body()));
            StringBuffer sb = new StringBuffer();
            sb.append(Tools.toHtml("\n"));
            sb.append(label("expected"));
            sb.append(Tools.toHtml("-----"));
            sb.append(Tools.toHtml("\n"));
            sb.append(Tools.toHtml(ta.toString()));
            sb.append(Tools.toHtml("\n"));
            sb.append(label("actual"));
            expected.addToBody(sb.toString());
        }
        expected.body("pass:" + expected.body());
    }

    @Override
    public String gray(String string) {
        return "report:" + Tools.toHtml(string);
    }

    @Override
    public void asLink(CellWrapper<String> cell, String link, String text) {
        cell.body("report:" + Tools.toHtmlLink(link, text));
    }

    @Override
    public String fromRaw(String text) {
        return Tools.fromHtml(text);
    }
}
