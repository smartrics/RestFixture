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

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPathConstants;

import org.w3c.dom.NodeList;

/**
 * Type adapter for body cells with XML content.
 * 
 * @author smartrics
 * 
 */
public class XPathBodyTypeAdapter extends BodyTypeAdapter {

    public XPathBodyTypeAdapter() {
    }

    /**
     * Equality check for bodies.
     * 
     * Expected body is a {@code List<String>} of XPaths - as parsed by {@see
     * smartrics.rest.fitnesse.fixture.support.XPathBodyTypeAdapter#parse(String
     * )} - to be executed in the actual body. The check is true if all XPaths
     * executed in the actual body return a node list not null or empty.
     * 
     * A special case is dedicated to {@code no-body}. If the expected body is
     * {@code no-body}, the equality check is true if the actual body returned
     * by the REST response is empty or null.
     * 
     * @param expected
     *            the expected body, it's a string with XPaths separated by
     *            {@code System.getProperty("line.separator")}
     * @param actual
     *            the body of the REST response returned by the call in the
     *            current test row
     * @see fit.TypeAdapter
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(final Object expected, final Object actual) {
        if (checkNoBody(expected)) {
            return checkNoBody(actual);
        }
        if (checkNoBody(actual)) {
            return checkNoBody(expected);
        }
        // r2 is the actual. it needs to be parsed as XML and the XPaths in r1
        // must be verified
        List<String> expressions = (List<String>) expected;
        for (String expr : expressions) {
            try {
                boolean b = eval(expr, actual.toString());
                if (!b) {
                    addError("not found: '" + expr + "'");
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("Cannot evaluate '" + expr + "' in " + actual.toString(), e);
            }
        }
        return getErrors().size() == 0;
    }

    protected boolean eval(String expr, String content) {
        Boolean b;
        try {
            NodeList ret = Tools.extractXPath(getContext(), expr, content, getCharset());
            return !(ret == null || ret.getLength() == 0);
        } catch (IllegalArgumentException e) {
            // may be evaluated as BOOLEAN
            b = (Boolean) Tools.extractXPath(getContext(), expr, content, XPathConstants.BOOLEAN, getCharset());
        }
        return b;
    }

    /**
     * Parses the expected body in the current test.
     * 
     * A body is a String containing XPaths one for each new line. Empty body
     * would result in an empty {@code List<String>}. A body containing the
     * value {@code no-body} is especially treated separately.
     * 
     * @param expectedListOfXpathsAsString
     */
    @Override
    public Object parse(String expectedListOfXpathsAsString) throws Exception {
        // expected values are parsed as a list of XPath expressions
        List<String> expectedXPathAsList = new ArrayList<String>();
        if (expectedListOfXpathsAsString == null) {
            return expectedXPathAsList;
        }
        String expStr = expectedListOfXpathsAsString.trim();
        if ("no-body".equals(expStr)) {
            return expectedXPathAsList;
        }
        if ("".equals(expStr)) {
            return expectedXPathAsList;
        }
        expStr = Tools.fromHtml(expStr);
        String[] nvpArray = expStr.split("\n");
        for (String nvp : nvpArray) {
            if (!"".equals(nvp.trim())) {
                expectedXPathAsList.add(nvp.trim());
            }
        }
        return expectedXPathAsList;
    }

    @Override
    public String toXmlString(String content) {
        return content;
    }
}
