/*  Copyright 2011 Fabrizio Cannizzo
 *
 *  This file is part of RestFixture.
 *
 *  RestFixture (http://code.google.com/p/rest-fixture/) is free software:
 *  you can redistribute it and/or modify it under the terms of the
 *  GNU Lesser General Public License as published by the Free Software Foundation,
 *  either version 3 of the License, or (at your option) any later version.
 *
 *  RestFixture is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with RestFixture.  If not, see <http://www.gnu.org/licenses/>.
 *
 *  If you want to contact the author please leave a comment here
 *  http://smartrics.blogspot.com/2008/08/get-fitnesse-with-some-rest.html
 */
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
        String m = Tools.toHtml(cell.getWrapped() + "\n-----\n") + Tools.toCode(Tools.toHtml(out.toString()));
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
