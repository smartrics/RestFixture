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
