/*  Copyright 2011 Fabrizio Cannizzo
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import smartrics.rest.config.Config;
import fit.Fixture;
import fit.Parse;
import fit.exception.FitParseException;

public class RestFixtureConfigTest {
	private static final String CONFIG_NAME = "configName";
	private Config namedConfig;
	private Config defaultNamedConfig;

	@Before
	public void setUp(){
		defaultNamedConfig = new Config();
		namedConfig = new Config(CONFIG_NAME);
	}

	@After
	public void tearDown(){
		namedConfig.clear();
		defaultNamedConfig.clear();
	}

	@Test
	public void mustStoreDataInNamedConfigWhoseNameIsPassedAsFirstArgToTheFixture() {
		RestFixtureConfig fixture = new RestFixtureConfig() {
			{
				super.args = new String[] { CONFIG_NAME };
			}
		};
		testStoreDataInNamedConfig(fixture, namedConfig);
	}

	@Test
	public void mustStoreDataInNamedConfigWhoseNameIsNotPassedHenceUsingDefault() {
		RestFixtureConfig fixtureNoArg = new RestFixtureConfig();
		testStoreDataInNamedConfig(fixtureNoArg, defaultNamedConfig);
	}

	private void testStoreDataInNamedConfig(Fixture fixture, final Config config) {
		String row1 = createFitTestRow("key1", "value1");
		String row2 = createFitTestRow("key2", "value2");
		Parse table = createFitTestInstance(row1, row2);
		fixture.doRows(table);
		assertEquals("value1", config.get("key1"));
		assertEquals("value2", config.get("key2"));
	}

	private Parse createFitTestInstance(String... rows) {
		Parse t = null;
		StringBuffer rBuff = new StringBuffer();
		rBuff.append("<table>");
		for (String r : rows) {
			rBuff.append(r);
		}
		rBuff.append("</table>");
		try {
			t = new Parse(rBuff.toString(), new String[] { "table", "row",
					"col" }, 1, 0);
		} catch (FitParseException e) {
			fail("Unable to build Parse object");
		}
		return t;
	}

	private String createFitTestRow(String cell1, String cell2) {
		String row = String.format("<row><col>%s</col><col>%s</col></row>",
				cell1, cell2);
		return row;
	}
}
