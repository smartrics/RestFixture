/*  Copyright 2015 Andrew Ochsner
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

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import smartrics.rest.fitnesse.fixture.RunnerVariablesProvider;

public class BodyTypeAdapterFactoryTest {
    public String charset = "UTF-8";
    
    private final BodyTypeAdapterFactory factory = new BodyTypeAdapterFactory(new RunnerVariablesProvider() {
		@Override
		public Variables createRunnerVariables() {
			return null;
		}
	},  Config.getConfig());
    
    @Test
    public void jsonContentTypeReturnsJSONBodyTypeAdapter() {
        // act
        BodyTypeAdapter bodyTypeAdapter = factory.getBodyTypeAdapter(ContentType.JSON, charset);
        // assert
        assertTrue(bodyTypeAdapter instanceof JSONBodyTypeAdapter);
    }

    @Test
    public void xmlContentTypeReturnsXPathBodyTypeAdapter() {
        // act
        BodyTypeAdapter bodyTypeAdapter = factory.getBodyTypeAdapter(ContentType.XML, charset);
        // assert
        assertTrue(bodyTypeAdapter instanceof XPathBodyTypeAdapter);
    }

    @Test
    public void textContentTypeReturnsXPathBodyTypeAdapter() {
        // act
        BodyTypeAdapter bodyTypeAdapter = factory.getBodyTypeAdapter(ContentType.TEXT, charset);
        // assert
        assertTrue(bodyTypeAdapter instanceof TextBodyTypeAdapter);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullContentTypeThrowsException() {
        // act
    	factory.getBodyTypeAdapter(null, charset);
    }

    @Test
    public void unknownContentTypeReturnsXPathBodyTypeAdapter() {
        // act
        BodyTypeAdapter bodyTypeAdapter = factory.getBodyTypeAdapter(ContentType.typeFor("unknown"), charset);
        // assert
        assertTrue(bodyTypeAdapter instanceof XPathBodyTypeAdapter);
    }

}
