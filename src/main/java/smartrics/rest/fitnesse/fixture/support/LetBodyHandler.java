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

import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPathConstants;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import smartrics.rest.client.RestData.Header;
import smartrics.rest.client.RestResponse;

/**
 * Handles body of the last response on behalf of LET in RestFixture.
 * 
 * @author smartrics
 * 
 */
public class LetBodyHandler implements LetHandler {

    @Override
    public String handle(RestResponse response, Object expressionContext, String expression) {
        @SuppressWarnings("unchecked")
        Map<String, String> namespaceContext = (Map<String, String>) expressionContext;
        List<Header> h = response.getHeader("Content-Type");
        ContentType contentType = ContentType.parse(h);
        String charset = ContentType.parseCharset(h);
        BodyTypeAdapter bodyTypeAdapter = BodyTypeAdapterFactory.getBodyTypeAdapter(contentType, charset);
        String body = bodyTypeAdapter.toXmlString(response.getBody());
        if (body == null) {
            return null;
        }
        String val = null;
        try {
            NodeList list = Tools.extractXPath(namespaceContext, expression, body);
            Node item = list.item(0);
            if (item != null) {
                val = item.getTextContent();
            }
        } catch (IllegalArgumentException e) {
            // ignore - may be that it's evaluating to a string
            val = (String) Tools.extractXPath(namespaceContext, expression, body, XPathConstants.STRING, charset);
        }
        if (val != null) {
            val = val.trim();
        }
        return val;
    }
}
