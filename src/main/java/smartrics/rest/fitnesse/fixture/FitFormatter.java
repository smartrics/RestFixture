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

import smartrics.rest.fitnesse.fixture.support.CellFormatter;
import smartrics.rest.fitnesse.fixture.support.CellWrapper;
import smartrics.rest.fitnesse.fixture.support.RestDataTypeAdapter;
import smartrics.rest.fitnesse.fixture.support.StringTypeAdapter;
import smartrics.rest.fitnesse.fixture.support.Tools;
import fit.ActionFixture;
import fit.Parse;
import fit.exception.FitFailureException;

/**
 * Cell formatter for the Fit runner.
 * 
 * @author fabrizio
 * 
 */
public class FitFormatter implements CellFormatter<Parse> {

    private ActionFixture fixture;
    private boolean displayActual;

    public void setActionFixtureDelegate(ActionFixture f) {
        this.fixture = f;
    }

    @Override
    public boolean isDisplayActual() {
        return displayActual;
    }

    @Override
    public void setDisplayActual(boolean d) {
        this.displayActual = d;
    }

    @Override
    public void exception(CellWrapper<Parse> cell, String exceptionMessage) {
        Parse wrapped = cell.getWrapped();
        fixture.exception(wrapped, new FitFailureException(exceptionMessage));
    }

	@Override
	public void exception(CellWrapper<Parse> cell, Throwable exception) {
		Parse wrapped = cell.getWrapped();
        fixture.exception(wrapped, exception);
	}

	@Override
	public void check(CellWrapper<Parse> valueCell, StringTypeAdapter adapter) {
        valueCell.body(Tools.toHtml(valueCell.body()));
		fixture.check(valueCell.getWrapped(), adapter);
	}

	@Override
	public String label(String string) {
        return ActionFixture.label(string);
	}

	@Override
    public void wrong(CellWrapper<Parse> expected, RestDataTypeAdapter typeAdapter) {
        String expectedContent = expected.body();
        String body = Tools.makeContentForWrongCell(expectedContent, typeAdapter, this);
        expected.body(body);
        fixture.wrong(expected.getWrapped());
	}

	@Override
    public void right(CellWrapper<Parse> expected, RestDataTypeAdapter typeAdapter) {
        String expectedContent = expected.body();
        expected.body(Tools.makeContentForRightCell(expectedContent, typeAdapter, this));
        fixture.right(expected.getWrapped());
	}

	@Override
	public String gray(String string) {
        return ActionFixture.gray(Tools.toHtml(string));
	}

    @Override
    public void asLink(CellWrapper<Parse> cell, String link, String text) {
        cell.body(Tools.toHtmlLink(link, text));
    }

    @Override
    public String fromRaw(String text) {
        return text;
    }
}
