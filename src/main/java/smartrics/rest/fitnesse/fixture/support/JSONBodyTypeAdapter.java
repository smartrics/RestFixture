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

import net.sf.json.JSONSerializer;

public class JSONBodyTypeAdapter extends BodyTypeAdapter {
	/**
	 * Equality check for bodies.
	 * 
	 * Expected body is a JSON object string - as parsed by {@see
	 * smartrics.rest.fitnesse.fixture.support.JSONBodyTypeAdapter#parse(String)
	 * * } - to be executed in the actual body. The check is true if the JSON
	 * objects are equivalent
	 * 
	 * A special case is dedicated to {@code no-body}. If the expected body is
	 * {@code no-body}, the equality check is true if the actual body returned
	 * by the REST response is empty or null.
	 * 
	 * @param expected
	 *            the expected body, it's a string with JSON
	 * @param actual
	 *            the body of the REST response returned by the call in the
	 *            current test row
	 * @see fit.TypeAdapter
	 */
	@Override
	public boolean equals(Object expected, Object actual) {
		if (checkNoBody(expected)) {
			return checkNoBody(actual);
		}
		if (checkNoBody(actual)) {
			return checkNoBody(expected);
		}
		try {
			Object expectedObject = JSONSerializer.toJSON(expected);
			Object actualObject = JSONSerializer.toJSON(actual);
			if (!expectedObject.equals(actualObject)) {
				addError("not found: '" + expected + "'");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Cannot compare json '"
					+ expected + "' from actual " + actual.toString());
		}

		return getErrors().size() == 0;
	}

	/**
	 * Parses the expected body in the current test.
	 * 
	 * A body is a String containing JSON. A body containing the value {@code
	 * no-body} is especially treated separately.
	 * 
	 * @param expectedJSON
	 */
	@Override
	public Object parse(String expectedJSON) throws Exception {
		if (expectedJSON == null)
			return "";
		String expStr = expectedJSON.trim();
		if ("no-body".equals(expStr.trim()))
			return "";
		if ("".equals(expectedJSON.trim()))
			return "";
		expStr = Tools.fromHtml(expStr);
		return expStr.trim();
	}

}
