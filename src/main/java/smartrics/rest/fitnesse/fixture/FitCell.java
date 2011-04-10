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

import smartrics.rest.fitnesse.fixture.support.CellWrapper;
import fit.Parse;

/**
 * Wrapper class to a table cell for the Fit runner.
 * 
 * @author fabrizio
 * 
 */
public class FitCell implements CellWrapper<Parse> {

    private final Parse cell;

    public FitCell(Parse c) {
        this.cell = c;
    }

    @Override
    public String text() {
        try {
            return cell.text();
        } catch (RuntimeException e) {
            return "";
        }
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
