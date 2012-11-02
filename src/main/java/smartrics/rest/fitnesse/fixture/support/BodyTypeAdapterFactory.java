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

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * Depending on Content-Type passed in, it'll build the appropriate type adapter
 * for parsing/rendering the cell content.
 * 
 * @author fabrizio
 * 
 */
public class BodyTypeAdapterFactory {

    @SuppressWarnings("rawtypes")
    private static Map<ContentType, Class> contentTypeToBodyTypeAdapter = new HashMap<ContentType, Class>();
    static {
        contentTypeToBodyTypeAdapter.put(ContentType.JS, JSONBodyTypeAdapter.class);
        contentTypeToBodyTypeAdapter.put(ContentType.JSON, JSONBodyTypeAdapter.class);
        contentTypeToBodyTypeAdapter.put(ContentType.XML, XPathBodyTypeAdapter.class);
        contentTypeToBodyTypeAdapter.put(ContentType.TEXT, TextBodyTypeAdapter.class);
        contentTypeToBodyTypeAdapter.put(ContentType.FILE, FileBodyTypeAdapter.class);
    }

    private BodyTypeAdapterFactory() {
    }

    // public static BodyTypeAdapter getBodyTypeAdapter(ContentType content) {
    // return getBodyTypeAdapter(content, Charset.defaultCharset().name());
    // }

    public static BodyTypeAdapter getBodyTypeAdapter(ContentType content, String charset) {
        @SuppressWarnings("rawtypes")
        Class aClass = contentTypeToBodyTypeAdapter.get(content);
        if (aClass == null) {
            throw new IllegalArgumentException("Content-Type is UNKNOWN.  Unable to find a BodyTypeAdapter to instantiate.");
        }
        BodyTypeAdapter instance = null;
        try {
            instance = (BodyTypeAdapter) aClass.newInstance();
            if (charset != null) {
                instance.setCharset(charset);
            } else {
                instance.setCharset(Charset.defaultCharset().name());
            }
        } catch (InstantiationException e) {
            throw new IllegalStateException("Unable to instantiate a the BodyTypeAdapter for " + content + "(" + aClass.getName() + ")");
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Unable access ctor to instantiate a the BodyTypeAdapter for " + content + "(" + aClass.getName() + ")");
        }
        return instance;
    }
}
