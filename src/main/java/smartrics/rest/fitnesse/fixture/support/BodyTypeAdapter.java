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

public abstract class BodyTypeAdapter extends RestDataTypeAdapter {

	public BodyTypeAdapter() {
		super();
	}

	@SuppressWarnings("unchecked")
	protected boolean checkNoBody(Object value) {
		boolean res = value == null;
		if (!res && (value instanceof String)) {
			res = checkNoBodyForString(value.toString());
		}
		if (!res && (value instanceof Collection)) {
			res = ((Collection) value).size() == 0;
		}
		if (!res && (value instanceof Parse)) {
			res = checkNoBodyForString(((Parse) value).text().trim());
		}
		return res;
	}

	private boolean checkNoBodyForString(String value) {
		return "".equals(value.trim()) || "no-body".equals(value.trim());
	}

	public abstract String toXmlString(String content);

	/**
	 * This renders the actual body - expected as a String containing XML - as
	 * HTML to be displayed in the test page.
	 * 
	 * @param the
	 *            {@code List<String>} actual body, or an empty/null body
	 *            rendered as HTML
	 * @return the string representation
	 */
	@Override
	public String toString(Object obj) {
		if (obj == null || obj.toString().trim().equals(""))
			return "no-body";
		// the actual value is passed as an xml string
		// todo: pretty print?
		return Tools.toHtml(obj.toString());
	}

}