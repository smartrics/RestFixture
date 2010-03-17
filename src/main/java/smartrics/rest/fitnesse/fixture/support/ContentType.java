/*  Copyright 2008 Andrew Ochsner
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

import smartrics.rest.client.RestData.Header;

/**
 * Supported content types.
 *
 * @author fabrizio
 */
public enum ContentType {
    XML("application/xml"), JSON("application/json"), TEXT("text/plain"), UNKNOWN(null), JSONX("application/x-javascript");
    private String contentTypeString;

    ContentType(String contentTypeString) {
        this.contentTypeString = contentTypeString;
    }

    public static ContentType parse(List<Header> contentTypeHeaders) {
        if (contentTypeHeaders.size() != 1
                || !"Content-Type".equals(contentTypeHeaders.get(0).getName()))
            return UNKNOWN;
        String typeString = contentTypeHeaders.get(0).getValue();
        if (typeString == null)
            return UNKNOWN;
        if (typeString.contains(XML.contentTypeString))
            return XML;
        else if (typeString.contains(JSON.contentTypeString))
            return JSON;
        else if (typeString.contains(JSONX.contentTypeString))
            return JSON;
        else if (typeString.contains(TEXT.contentTypeString))
            return TEXT;
        else
            return UNKNOWN;
    }
}
