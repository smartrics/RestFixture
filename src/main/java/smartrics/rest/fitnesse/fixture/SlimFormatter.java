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

import smartrics.rest.fitnesse.fixture.support.CellFormatter;
import smartrics.rest.fitnesse.fixture.support.CellWrapper;
import smartrics.rest.fitnesse.fixture.support.RestDataTypeAdapter;
import smartrics.rest.fitnesse.fixture.support.Tools;

/**
 * Formatter of cells handled by Slim.
 * 
 * @author smartrics
 * 
 */
public class SlimFormatter implements CellFormatter<String> {

    private int minLenForToggle = -1;
    private boolean displayActual;
	private boolean printAsHtml;

    public SlimFormatter(boolean printAsHtml) {
    	this.printAsHtml = printAsHtml;
    }
    
    @Override
    public void setDisplayActual(boolean d) {
        this.displayActual = d;
    }

    @Override
    public void setMinLenghtForToggleCollapse(int minLen) {
        this.minLenForToggle = minLen;
    }

    public boolean isDisplayActual() {
        return displayActual;
    }

    @Override
    public void exception(CellWrapper<String> cell, String exceptionMessage) {
        cell.body("error:" + Tools.wrapInDiv(exceptionMessage));
    }

    @Override
    public void exception(CellWrapper<String> cell, Throwable exception) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(out);
        exception.printStackTrace(ps);
        //String m = Tools.toHtml(cell.getWrapped() + "\n-----\n") + Tools.toCode(Tools.toHtml(out.toString()));
        String m = Tools.toHtml(printAsHtml, cell.getWrapped() + "\n-----\n") + Tools.toCode(Tools.toHtml(printAsHtml, out.toString()));
        cell.body("error:" + Tools.wrapInDiv(m));
        //cell.body("error:" + m);
    }

    @Override
    public void check(CellWrapper<String> expected, RestDataTypeAdapter actual) {
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
        return Tools.toHtmlLabel(printAsHtml, string);
    }

    @Override
    public void wrong(CellWrapper<String> expected, RestDataTypeAdapter ta) {
        String expectedContent = expected.body();
        expected.body(Tools.makeContentForWrongCell(printAsHtml, expectedContent, ta, this, minLenForToggle));
        expected.body("fail:" + Tools.wrapInDiv(expected.body()));
    }

    @Override
    public void right(CellWrapper<String> expected, RestDataTypeAdapter typeAdapter) {
        expected.body("pass:" + Tools.wrapInDiv(Tools.makeContentForRightCell(printAsHtml, expected.body(), typeAdapter, this, minLenForToggle)));
    }

    @Override
    public String gray(String string) {
        return "report:" + Tools.wrapInDiv(Tools.toHtml(printAsHtml, string));
    }

    @Override
    public void asLink(CellWrapper<String> cell, String link, String text) {
        cell.body("report:" + Tools.wrapInDiv(Tools.toHtmlLink(printAsHtml, link, text)));
    }

    @Override
    public String fromRaw(String text) {
        return Tools.fromHtml(text);
    }

}
