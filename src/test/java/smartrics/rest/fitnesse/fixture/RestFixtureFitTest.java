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

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import smartrics.rest.fitnesse.fixture.support.Config;
import smartrics.rest.fitnesse.fixture.support.Variables;
import fit.Parse;

public class RestFixtureFitTest {

    private static final String BASE_URL = "http://localhost:9090";
    private FitRestFixture fixture;
    private final Variables variables = new Variables();
    private Config config;
    private Parse emptyParse;

    @Before
    public void setUp() {
        config = Config.getConfig();
        // this is created for tests that don't need to verify expectations
        fixture = new FitRestFixture() {
            {
                super.args = new String[] {BASE_URL};
            }
        };
                
        variables.clearAll();
        RestFixtureTestHelper helper = new RestFixtureTestHelper();
        emptyParse = helper.buildEmptyParse();
    }

    @After
    public void tearDown() {
        config.clear();
    }

    @Test(expected = RuntimeException.class)
    public void mustNotifyCallerThatBaseUrlAsFixtureArgIsMandatory() {
        fixture = new FitRestFixture() {
            {
                super.args = new String[] {};
            }
        };
        fixture.doCells(emptyParse);
    }

    @Test
    public void mustSetConfigNameToDefaultWhenNotSpecifiedAsSecondOptionalParameter_FIT() {
        fixture = new FitRestFixture() {
            {
                super.args = new String[] { BASE_URL };
            }
        };
        fixture.doCells(emptyParse);
        assertEquals(Config.DEFAULT_CONFIG_NAME, fixture.getConfig().getName());
    }

    @Test
    public void mustSetConfigNameToSpecifiedValueIfOptionalSecondParameterIsSpecified_FIT() {
        fixture = new FitRestFixture() {
            {
                super.args = new String[] { BASE_URL, "configName" };
            }
        };
        fixture.doCells(emptyParse);
        assertEquals("configName", fixture.getConfig().getName());
    }
}
