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
package smartrics.rest.test.fitnesse.fixture;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;

public class Resources {
	private final Map<String, Map<String, Resource>> resourceDb = Collections
			.synchronizedMap(new HashMap<String, Map<String, Resource>>());
	private static Resources instance = new Resources();
	private int counter = 0;

	public static Resources getInstance() {
		return instance;
	}

	public void clear() {
		for (String c : resourceDb.keySet()) {
			resourceDb.get(c).clear();
		}
		resourceDb.clear();
	}

	public Collection<Resource> asCollection(String context) {
		Collection<Resource> c = new Vector<Resource>();
		Map<String, Resource> m = resourceDb.get(context);
		if (m != null) {
			for (Entry<String, Resource> e : m.entrySet()) {
				String s = e.getKey();
				c.add(m.get(s));
			}
		}
		return c;
	}

	public List<String> contexts() {
		List<String> ctxKeys = new ArrayList<String>();
		ctxKeys.addAll(resourceDb.keySet());
		return ctxKeys;
	}

	public void add(String context, Resource r) {
		if (r.getId() == null) {
			r.setId(Integer.toString(newCounter()));
		}
		Map<String, Resource> m = getMapForContext(context);
		m.put(r.getId(), r);
	}

	private Map<String, Resource> getMapForContext(String context) {
		Map<String, Resource> m = resourceDb.get(context);
		if (m == null) {
			m = new HashMap<String, Resource>();
			resourceDb.put(context, m);
		}
		return m;
	}

	public Resource get(String context, String i) {
		return getMapForContext(context).get(i);
	}

	public int size(String context) {
		return getMapForContext(context).size();
	}

	public Resource remove(String context, String index) {
		return getMapForContext(context).remove(index);
	}

	public void remove(String context, Resource o) {
		remove(context, o.getId());
	}

	public void reset() {
		clear();
		counter = 0;
		add("/resources", new Resource(
				"<resource><name>a funky name</name>"
						+ "<data>an important message</data></resource>"));
		add("/resources", new Resource(
				"{ \"resource\" : { \"name\" : \"a funky name\", "
						+ "\"data\" : \"an important message\" } }"));
	}

	private synchronized int newCounter() {
		return counter++;
	}

	@Override
	public String toString() {
		StringBuffer b = new StringBuffer();
		String nl = System.getProperty("line.separator");
		b.append("Resources:[").append(nl);
		for (String c : resourceDb.keySet()) {
			b.append(" Context(").append(c).append("):[").append(nl);
			for (Resource r : asCollection(c)) {
				b.append(r).append(nl);
			}
			b.append(" ]").append(nl);
		}
		b.append("]").append(nl);
		return b.toString();
	}

}
