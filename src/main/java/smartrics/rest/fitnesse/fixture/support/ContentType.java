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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import smartrics.rest.client.RestData.Header;

/**
 * Supported content types.
 * 
 * @author fabrizio
 */
public enum ContentType {

    XML, JSON, TEXT, JS;

    private static Map<String, ContentType> contentTypeToEnum = new HashMap<String, ContentType>();
    static {
        resetDefaultMapping();
    }

    public List<String> toMime() {
        List<String> types = new ArrayList<String>();
        for (Map.Entry<String, ContentType> e : contentTypeToEnum.entrySet()) {
            if(e.getValue().equals(this)) {
                types.add(e.getKey());
            }
        }        
        return types;
    }

    public static ContentType typeFor(String t) {
        ContentType r = contentTypeToEnum.get(t);
        if (r == null) {
            r = contentTypeToEnum.get("default");
        }
        return r;
    }

    public static void config(String htmlConfig) {
        String config = Tools.fromHtml(htmlConfig);
        Map<String, String> map = Tools.convertStringToMap(config, "=", "\n");
        for (Map.Entry<String, String> e : map.entrySet()) {
            String enumName = e.getValue().toUpperCase();
            ContentType ct = ContentType.valueOf(enumName);
            if (null == ct) {
                throw new IllegalArgumentException("I don't know how to handle " + e.getValue() + ". Use one of " + ContentType.values());
            }
            System.err.println(">> put " + e.getKey() + " = " + ct);
            contentTypeToEnum.put(e.getKey(), ct);
        }
    }

    public static void resetDefaultMapping() {
        contentTypeToEnum.clear();
        contentTypeToEnum.put("default", ContentType.XML);
        contentTypeToEnum.put("application/xml", ContentType.XML);
        contentTypeToEnum.put("application/json", ContentType.JSON);
        contentTypeToEnum.put("text/plain", ContentType.TEXT);
        contentTypeToEnum.put("application/x-javascript", ContentType.JS);
    }

    public static ContentType parse(List<Header> contentTypeHeaders) {
        if (contentTypeHeaders.size() != 1 || !"Content-Type".equalsIgnoreCase(contentTypeHeaders.get(0).getName())) {
            return contentTypeToEnum.get("default");
        }
        String typeString = contentTypeHeaders.get(0).getValue();
        typeString = typeString.split(";")[0].trim();
        ContentType ret = contentTypeToEnum.get(typeString);
        if (ret == null) {
            return contentTypeToEnum.get("default");
        }
        return ret;
    }
}
