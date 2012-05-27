/*  Copyright 2012 Fabrizio Cannizzo
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
package smartrics.rest.fitnesse.fixture.support;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import smartrics.rest.fitnesse.fixture.FitCell;
import smartrics.rest.fitnesse.fixture.RestFixtureTestHelper;
import fit.Parse;

public class FitCellTest {
    private FitCell c;

    @Before
    public void setUp() throws Exception {
        RestFixtureTestHelper helper = new RestFixtureTestHelper();
        Parse p = helper.createSingleRowFitTable("justone");
        c = new FitCell(p.parts.parts);
    }

    @Test
    public void testConstruction() {
        assertThat(c.body(), is(equalTo("justone")));
    }

    @Test
    public void testAddBodyAppendsContentToExisting() {
        c.addToBody("_more");
        assertThat(c.body(), is(equalTo("justone_more")));
    }

    @Test
    public void textAndBodyAreEquivalent() {
        assertThat(c.body(), is(equalTo(c.text())));
    }

    @Test
    public void shouldBeAbleToOverrideTheContent() {
        c.body("another");
        assertThat("another", is(equalTo(c.body())));
    }

    @Test
    public void wrappedObjectIsTheStringUsedForConstruction() {
        assertThat("justone", is(equalTo(c.getWrapped().body)));
    }
}
