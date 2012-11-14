/*  Copyright 2008 Fabrizio Cannizzo
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
package smartrics.rest.fitnesse.fixture.support;

import fit.Parse;

/**
 * Type adapter for handling http status code cell.
 * 
 * @author smartrics
 * 
 */
public class StatusCodeTypeAdapter extends RestDataTypeAdapter {

    @Override
    public boolean equals(Object r1, Object r2) {
        if (r1 == null || r2 == null) {
            return false;
        }
        String expected = r1.toString();
        if (r1 instanceof Parse) {
            expected = ((Parse) r1).text();
        }
        String actual = (String) r2;
        if (!Tools.regex(actual, expected)) {
            addError("not match: " + expected);
        }
        return getErrors().size() == 0;
    }

    @Override
    public Object parse(String s) {
        if (s == null) {
            return "null";
        }
        return s.trim();
    }

    @Override
    public String toString(Object obj) {
        if (obj == null) {
            return "null";
        }
        if (obj.toString().trim().equals("")) {
            return "blank";
        }
        return obj.toString();
    }

    /**
     * @see smartrics.rest.fitnesse.fixture.support.RestDataTypeAdapter#isBinaryResponse()
     */
    @Override
    public boolean isBinaryResponse()
    {
        return false;
    }
}
