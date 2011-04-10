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
package smartrics.rest.fitnesse.fixture.support;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import fit.Parse;

/**
 * Type adapter for body cell containing plain text.
 * 
 * @author fabrizio
 * 
 */
public class TextBodyTypeAdapter extends BodyTypeAdapter {

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
        try {
            if (!Pattern.matches(expected, actual)) {
                addError("not match: " + expected);
            }
        } catch (PatternSyntaxException e) {
            // lets try to string match just to be kind
            if (!expected.equals(actual)) {
                addError("not found: " + expected);
            }
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
    public String toXmlString(String content) {
        return "<text>" + content + "</text>";
    }

}
