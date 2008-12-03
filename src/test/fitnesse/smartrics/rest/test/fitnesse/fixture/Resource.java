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
package smartrics.rest.test.fitnesse.fixture;

public class Resource {
	String name;
	String data;
	boolean deleted;
	int id;

	public Resource(int id, String name, String data) {
		super();
		this.name = name;
		this.data = data;
		this.id = id;
	}

	public Resource(String xmlContent) {
		this.id = -1;
		this.name = xmlContent.split("<name>")[1].split("</name>")[0];
		this.data = xmlContent.split("<data>")[1].split("</data>")[0];
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public String toXml() {
		return "<resource>" + System.getProperty("line.separator")
				+ "   <id>"
				+ getId() + "</id>" + System.getProperty("line.separator")
				+ "   <name>" + getName() + "</name>"
				+ System.getProperty("line.separator")
				+ "   <data>"
				+ getData() + "</data>"
				+ System.getProperty("line.separator") + "</resource>";
	}

	public String toJson() {
		return "{ \"resource\" : { \"id\" : \"" + getId() + "\", \"name\" : \""
				+ getName() + "\", \"data\" : \"" + getData() + "\" } }";
	}


}
