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
import java.util.Collection;
import java.util.List;

import org.w3c.dom.NodeList;

import fit.Parse;

public class BodyTypeAdapter extends RestDataTypeAdapter {

	public BodyTypeAdapter() {
//		super(actual);
	}

	@SuppressWarnings("unchecked")
	private boolean checkNoBody(Object value){
		boolean res = value==null;
		if(!res && (value instanceof String)){
			res = checkNoBodyForString(value.toString());
		}
		if(!res && (value instanceof Collection)){
			res = ((Collection)value).size()==0;
		}
		if(!res && (value instanceof Parse)){
			res = checkNoBodyForString(((Parse)value).text().trim());
		}
		return res;
	}

	private boolean checkNoBodyForString(String value){
		return "".equals(value.trim()) || "no-body".equals(value.trim());
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object expected, Object actual) {
		if (checkNoBody(expected)){
			return checkNoBody(actual);
		}
		if (checkNoBody(actual)){
			return checkNoBody(expected);
		}
		// r2 is the actual. it needs  to be parsed as XML and the XPaths in r1
		// must be verified
		List<String> expressions = (List<String>) expected;
		for (String expr : expressions) {
			try {
				NodeList ret = Tools.extractXPath(expr, actual.toString());
				if (ret == null || ret.getLength() == 0){
					addError("not found: '" + expr + "'");
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new IllegalArgumentException("Cannot extract xpath '"+ expr + "' from document " + actual.toString());
			}
		}
		return getErrors().size()==0;
	}

	@Override
	public Object parse(String expectedListOfXpathsAsString) throws Exception {
		// expected values are parsed as a list of XPath expressions
		List<String> expectedXPathAsList = new ArrayList<String>();
		if(expectedListOfXpathsAsString == null)
			return expectedXPathAsList;
		String expStr = expectedListOfXpathsAsString.trim();
		if("no-body".equals(expStr.trim()))
			return expectedXPathAsList;
		if ("".equals(expectedListOfXpathsAsString.trim()))
			return expectedXPathAsList;
		expStr = Tools.fromHtml(expStr);
		String[] nvpArray = expStr.split(System.getProperty("line.separator"));
		for (String nvp : nvpArray) {
			if(!"".equals(nvp.trim()))
				expectedXPathAsList.add(nvp.trim());
		}
		return expectedXPathAsList;
	}

	@Override
	public String toString(Object obj) {
		if(obj==null || obj.toString().trim().equals(""))
			return "no-body";
		// the actual value is passed as an xml string
		// todo: pretty print?
		return Tools.toHtml(obj.toString());
	}
}
