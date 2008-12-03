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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Resources {
	private final Map<Integer, Resource> resources = Collections
			.synchronizedMap(new HashMap<Integer, Resource>());
	private static Resources instance = new Resources();
	private static int counter = 0;

	public static Resources getInstance() {
		return instance;
	}

	public void clear() {
		resources.clear();
	}

	public void add(Resource r) {
		if (r.getId() == -1) {
			r.setId(newCounter());
		}
		resources.put(r.getId(), r);
	}

	public Resource get(int i) {
		return resources.get(i);
	}

	public int size() {
		return resources.size();
	}

	public Resource remove(int index) {
		return resources.remove(index);
	}

	public void remove(Resource o) {
		resources.remove(o.getId());
	}

	public void reset() {
		resources.clear();
		add(new Resource(0, "a funky name", "an important message"));
	}

	private static synchronized int newCounter() {
		return counter++;
	}

}
