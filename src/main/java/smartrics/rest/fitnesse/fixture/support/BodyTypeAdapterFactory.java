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

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import smartrics.rest.fitnesse.fixture.RunnerVariablesProvider;

/**
 * Depending on Content-Type passed in, it'll build the appropriate type adapter
 * for parsing/rendering the cell content.
 * 
 * @author smartrics
 * 
 */
public class BodyTypeAdapterFactory {

	private final RunnerVariablesProvider variablesProvider;
    private final Config config;

    private Map<ContentType, BodyTypeAdapterCreator> contentTypeToBodyTypeAdapter = new HashMap<ContentType, BodyTypeAdapterCreator>();
    {
    	BodyTypeAdapterCreator jsonBodyTypeAdapterCreator = new BodyTypeAdapterCreator() {
			@Override
			public BodyTypeAdapter createBodyTypeAdapter() {
				return new JSONBodyTypeAdapter(variablesProvider, config);
			}
		};

        contentTypeToBodyTypeAdapter.put(ContentType.JS, jsonBodyTypeAdapterCreator);
        contentTypeToBodyTypeAdapter.put(ContentType.JSON, jsonBodyTypeAdapterCreator);
        contentTypeToBodyTypeAdapter.put(ContentType.XML, new BodyTypeAdapterCreator() {
			@Override
			public BodyTypeAdapter createBodyTypeAdapter() {
				return new XPathBodyTypeAdapter();
			}
		});
        contentTypeToBodyTypeAdapter.put(ContentType.TEXT, new BodyTypeAdapterCreator() {
			@Override
			public BodyTypeAdapter createBodyTypeAdapter() {
				return new TextBodyTypeAdapter();
			}
        });;
    }

	public BodyTypeAdapterFactory(final RunnerVariablesProvider variablesProvider, Config config) {
		this.variablesProvider = variablesProvider;
        this.config = config;
	}
	
    /**
     * Returns a @link {@link BodyTypeAdapter} for the given charset and @link {@link ContentType}.
     * 
     * @param content the contentType
     * @param charset the charset.
     * @return an instance of {@link BodyTypeAdapter}
     */
    public BodyTypeAdapter getBodyTypeAdapter(ContentType content, String charset) {
        final BodyTypeAdapterCreator creator = contentTypeToBodyTypeAdapter.get(content);
        if (creator == null) {
            throw new IllegalArgumentException("Content-Type is UNKNOWN.  Unable to find a BodyTypeAdapter to instantiate.");
        }
        final BodyTypeAdapter instance = creator.createBodyTypeAdapter();
        if (charset != null) {
        	instance.setCharset(charset);
        } else {
        	instance.setCharset(Charset.defaultCharset().name());
        }
        return instance;
    }
    
    interface BodyTypeAdapterCreator {
    	BodyTypeAdapter createBodyTypeAdapter();
    }
    
}
