/*  Copyright 2012 Fabrizio Cannizzo
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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import smartrics.rest.client.RestData.Header;
import smartrics.rest.client.RestResponse;

/**
 * Handles header (a list of Header objects) LET manipulations.
 * 
 * @author smartrics
 * 
 */
public class LetHeaderHandler implements LetHandler {

    public String handle(RestResponse response, Object expressionContext, String expression) {
        List<String> content = new ArrayList<String>();
        if (response != null) {
            for (Header e : response.getHeaders()) {
                String string = Tools.convertEntryToString(e.getName(), e.getValue(), ":");
                content.add(string);
            }
        }

        String value = null;
        if (content.size() > 0) {
            Pattern p = Pattern.compile(expression);
            for (String c : content) {
                Matcher m = p.matcher(c);
                if (m.find()) {
                    int cc = m.groupCount();
                    value = m.group(cc);
                    break;
                }
            }
        }
        return value;
    }

}
