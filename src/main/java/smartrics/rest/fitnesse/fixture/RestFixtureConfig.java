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
package smartrics.rest.fitnesse.fixture;

import smartrics.rest.config.Config;
import fit.Fixture;
import fit.Parse;

/**
 * A simple fixture to store configuration data for the rest fixture.
 * 
 * A configuration is a named map that stores key/value pairs. The name of the
 * map is passed as an optional parameter to the fixture. If not passed it's
 * assumed that a default name is used. The default value of the map name is
 * {@link Config.DEFAULT_CONFIG_NAME}.
 * 
 * The structure of the table of this fixture simply a table that reports
 * key/values. The name of the config is optionally passed to the fixture.
 * 
 * Example:
 * 
 * Uses the default config name:
 * <table border="1">
 * <tr>
 * <td colspan="2">smartrics.rest.fitnesse.fixture.RestFixtureConfig</td>
 * </tr>
 * <tr>
 * <td>key1</td>
 * <td>value1</td>
 * </tr>
 * <tr>
 * <td>key2</td>
 * <td>value2</td>
 * </tr>
 * <tr>
 * <td>...</td>
 * <td>...</td>
 * </tr>
 * </table>
 * <p/>
 * Uses the config name <i>confname</i>:
 * <table border="1">
 * <tr>
 * <td>smartrics.rest.fitnesse.fixture.RestFixtureConfig</td>
 * <td>confname</td>
 * </tr>
 * <tr>
 * <td>key1</td>
 * <td>value1</td>
 * </tr>
 * <tr>
 * <td>key2</td>
 * <td>value2</td>
 * </tr>
 * <tr>
 * <td>...</td>
 * <td>...</td>
 * </tr>
 * </table>
 * <p/>
 * {@link RestFixture} accesses the config passed by name as second parameter to
 * the fixture or the default if no name is passed:
 * <table border="1">
 * <tr>
 * <td>smartrics.rest.fitnesse.fixture.RestFixture</td>
 * <td>http://localhost:7070</td>
 * </tr>
 * <tr>
 * <td colspan="2">...</td>
 * </tr>
 * </table>
 * 
 * or
 * 
 * <table border="1">
 * <tr>
 * <td >smartrics.rest.fitnesse.fixture.RestFixture</td>
 * <td>http://localhost:7070</td>
 * <td>confname</td>
 * </tr>
 * <tr>
 * <td colspan="3">...</td>
 * </tr>
 * </table>
 */
public class RestFixtureConfig extends Fixture {

	private Config config;

	/**
	 * processes each row in the config fixture table and loads the key/value
	 * pairs. The fixture optional first argument is the config name. If not
	 * supplied the value is defaulted. See {@link Config.DEFAULT_CONFIG_NAME}.
	 */
	@Override
	public void doRow(Parse p) {
		Config c = getConfig();
		Parse cells = p.parts;
		try {
			String key = cells.text();
			String value = cells.more.text();
			c.add(key, value);
			String fValue = value.replaceAll(System
					.getProperty("line.separator"), "<br>");
			right(cells);
			Parse valueParse = cells.more;
			valueParse.body = fValue;
			right(valueParse);
		} catch (Exception e) {
			System.err.println("Exception for " + p.text());
			e.printStackTrace();
			exception(p, e);
		}
	}

	private Config getConfig() {
		if (config != null)
			return config;
		if (super.args != null && super.args.length > 0) {
			config = new Config(super.args[0]);
		} else {
			config = new Config();
		}
		return config;
	}
}
