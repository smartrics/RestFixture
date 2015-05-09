/*  Copyright 2015 Fabrizio Cannizzo
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
import java.util.Collection;
import java.util.List;

import smartrics.rest.client.RestData.Header;

/**
 * Type adapter for HTTP header cell in a RestFixture table.
 * 
 * @author smartrics
 * 
 */
public class HeadersTypeAdapter extends RestDataTypeAdapter {

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object expectedObj, Object actualObj) {
        if (expectedObj == null || actualObj == null) {
            return false;
        }
        // r1 and r2 are Map<String, String> containing either the header
        // from the HTTP response or the data value in the expected cell
        // equals checks for r1 being a subset of r2
        Collection<Header> expected = (Collection<Header>) expectedObj;
        Collection<Header> actual = (Collection<Header>) actualObj;
        for (Header k : expected) {
            Header aHdr = find(actual, k);
            if (aHdr == null) {
                addError("not found: [" + k.getName() + " : " + k.getValue() + "]");
            }
        }
        return getErrors().size() == 0;
    }

    private Header find(Collection<Header> actual, Header k) {
        for (Header h : actual) {
            boolean nameMatches = h.getName().equals(k.getName());
            boolean valueMatches = Tools.regex(h.getValue(), k.getValue());
            if (nameMatches && valueMatches) {
                return h;
            }
        }
        return null;
    }

    @Override
    public Object parse(String s) throws Exception {
        // parses a cell content as a map of headers.
        // syntax is name:value\n*
        List<Header> expected = new ArrayList<Header>();
        if (!"".equals(s.trim())) {
            String expStr = Tools.fromHtml(s.trim());
            String[] nvpArray = expStr.split("\n");
            for (String nvp : nvpArray) {
                try {
                    String[] nvpEl = nvp.split(":", 2);
                    expected.add(new Header(nvpEl[0].trim(), nvpEl[1].trim()));
                } catch (RuntimeException e) {
                    throw new IllegalArgumentException("Each entry in the must be separated by \\n and each entry must be expressed as a name:value");
                }
            }
        }
        return expected;
    }

    @Override
    @SuppressWarnings("unchecked")
    public String toString(Object obj) {
        StringBuffer b = new StringBuffer();
        List<Header> list = (List<Header>) obj;
        for (Header h : list) {
            b.append(h.getName()).append(" : ").append(h.getValue()).append("\n");
        }
        return b.toString().trim();
    }

}
