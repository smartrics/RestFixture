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

import java.util.Collection;

import fit.Parse;

/**
 * Base class for body type adaptors.
 * 
 * @author smartrics
 * 
 */
public abstract class BodyTypeAdapter extends RestDataTypeAdapter {

    private String charset;

    /**
     * Default constructor.
     */
    public BodyTypeAdapter() {
        super();
    }

    protected void setCharset(String charset) {
        this.charset = charset;
    }

    public String getCharset() {
        return charset;
    }

    /**
     * Checks if body of a cell is "no-body" meaning empty in the context of a
     * REST call.
     * 
     * @param value
     *            the cell
     * @return true if no-body
     */
    protected boolean checkNoBody(final Object value) {
        if (value == null) {
            return true;
        }
        if (value instanceof Collection) {
            return ((Collection<?>) value).size() == 0;
        }
        String s = value.toString();
        if (value instanceof Parse) {
            s = ((Parse) value).text().trim();
        }
        return checkNoBodyForString(s);
    }

    private boolean checkNoBodyForString(final String value) {
        return "".equals(value.trim()) || "no-body".equals(value.trim());
    }

    public abstract String toXmlString(String content);

    /**
     * This renders the actual body - expected as a String containing XML - as
     * HTML to be displayed in the test page.
     * 
     * @param obj
     *            the {@code List<String>} actual body, or an empty/null body
     *            rendered as HTML
     * @return the string representation
     */
    @Override
    public String toString(final Object obj) {
        if (obj == null || obj.toString().trim().equals("")) {
            return "no-body";
        }
        // the actual value is passed as an xml string
        // TODO: pretty print toString on BodyTypeAdapter
        return obj.toString();
    }

}
