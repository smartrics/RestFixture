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
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import smartrics.rest.config.Config;
import fit.exception.FitFailureException;

public class RestFixtureWithSeqSlimTest {

    private RestFixtureWithSeq fixture;

    @Test
    public void itFailsToConstructWhenCreatedWithURLOnly() {
        try {
            fixture = new RestFixtureWithSeq("http://localhost:9090");
        } catch (FitFailureException e) {

        }
    }

    @Test
    public void usesDefaultConfigWhenCreatedWithURLAndPicFileName() {
        fixture = new RestFixtureWithSeq("http://localhost:9090", "some_picture.png");
        assertThat(fixture.getConfig().getName(), is(equalTo(Config.DEFAULT_CONFIG_NAME)));
    }

    @Test
    public void usesNamedConfigWhenCreatedWithURLConfigNameAndPicFileName() {
        fixture = new RestFixtureWithSeq("http://localhost:9090", "some_config", "some_picture.png");
        assertThat(fixture.getConfig().getName(), is(equalTo("some_config")));
    }
}
