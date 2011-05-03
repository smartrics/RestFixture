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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import smartrics.rest.client.RestClient;
import smartrics.rest.client.RestRequest;
import smartrics.rest.config.Config;

public class PartsFactoryTest {

    private PartsFactory f;

    @Before
    public void setUp() {
        f = new PartsFactory();
    }

    @Test
    public void cannotBuildACellFormatterForANullRunner() {
        try {
            f.buildCellFormatter(null);
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is(equalTo("Runner is null")));
        }
    }

    @Test
    public void buildsRestRequest() {
        assertThat(f.buildRestRequest(), is(instanceOf(RestRequest.class)));
    }

    @Test
    public void buildsRestClient() {
        Config c = Config.getConfig();
        assertThat(f.buildRestClient(c), is(instanceOf(RestClient.class)));
    }

    @Test
    public void cantBuildACellFormatterForNonFitOrSlimRunner() {
        try {
            f.buildCellFormatter(RestFixture.Runner.OTHER);
        } catch (IllegalStateException e) {
            assertThat(e.getMessage(), is(equalTo("Runner OTHER not supported")));
        }
    }

    @Test
    public void buildsASlimFormatterForSLIMRunner() {
        assertThat(f.buildCellFormatter(RestFixture.Runner.SLIM), is(instanceOf(SlimFormatter.class)));
    }

    @Test
    public void buildsASlimFormatterForFITRunner() {
        assertThat(f.buildCellFormatter(RestFixture.Runner.FIT), is(instanceOf(FitFormatter.class)));
    }
}
