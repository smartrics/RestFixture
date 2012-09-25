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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import smartrics.rest.fitnesse.fixture.support.Config;

public class RestFixtureSlimFitTest {

    private static final String BASE_URL = "http://localhost:9090";
    private RestFixture fixture;
    private Config config;
    private RestFixtureTestHelper helper = new RestFixtureTestHelper();

    @Before
    public void setUp() {
        config = Config.getConfig();
        fixture = new RestFixture(BASE_URL);
    }

    @After
    public void tearDown() {
        config.clear();
    }

    @Test
    public void mustSetConfigNameToDefaultWhenNotSpecifiedAsSecondOptionalParameter_SLIM() {
        fixture = new RestFixture(BASE_URL, "configName");
        assertEquals("configName", fixture.getConfig().getName());
    }

    @Test
    public void mustSetConfigNameToSpecifiedValueIfOptionalSecondParameterIsSpecified_SLIM() {
        fixture = new RestFixture(BASE_URL, "configName");
        assertThat(fixture.getConfig().getName(), is(equalTo("configName")));
    }

    @Test
    public void mustLeaveCellsForSetXMethodsIgnored() {
        List<List<String>> table = helper.createSingleRowSlimTable("setBody", "<some>content</some>");
        List<List<String>> result = fixture.doTable(table);
        assertThat(result.get(0).get(0), is(equalTo("")));
        // content is unchanged so we pass back an empty string for slim to
        // re-display the old content.
        assertThat(result.get(0).get(1), is(equalTo("")));
    }

}
