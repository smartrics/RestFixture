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
import java.util.List;

public class Resources {
	private List<String> resources = new ArrayList<String>();
	private static Resources instance = new Resources();
	public static Resources getInstance(){
		return instance;
	}

	public void clear(){
		resources.clear();
	}

	public void add(String r){
		resources.add(r);
	}

	public String get(int i){
		return resources.get(i);
	}

	public int size(){
		return resources.size();
	}

	public void add(int index, String element) {
		resources.add(index, element);
	}

	public String remove(int index) {
		return resources.remove(index);
	}

	public boolean remove(Object o) {
		return resources.remove(o);
	}

	public void reset() {
		resources.clear();
		resources.add("<resource>" + System.getProperty("line.separator") +
				"   <name>a funky name</name>" + System.getProperty("line.separator") +
				"   <data>an important message</data>" + System.getProperty("line.separator") +
				"</resource>");
	}


}

