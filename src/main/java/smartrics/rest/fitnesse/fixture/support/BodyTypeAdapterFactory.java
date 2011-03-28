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

/**
 * Depending on Content-Type passed in it'll build the appropriate type adapter
 * for parsing/rendering the cell content
 * 
 * TODO: allow configuration of DEFAULT Adapter (no recognised content type) and
 * what content types are associated with each adapter.
 * 
 * @author fabrizio
 * 
 */
public class BodyTypeAdapterFactory {

	public static BodyTypeAdapter getBodyTypeAdapter(ContentType content) {
		if (content == ContentType.JSON)
			return new JSONBodyTypeAdapter();
		else if (content == ContentType.XML)
			return new XPathBodyTypeAdapter();
		else if (content == ContentType.TEXT)
			return new TextBodyTypeAdapter();
		else if (content == ContentType.UNKNOWN)
			return new XPathBodyTypeAdapter();
		else
			throw new IllegalArgumentException(
					"Content-Type is UNKNOWN.  Unable to find a BodyTypeAdapter to instantiate.");
	}
}
