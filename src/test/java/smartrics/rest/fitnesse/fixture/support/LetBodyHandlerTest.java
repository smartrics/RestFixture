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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import smartrics.rest.client.RestResponse;

/**
 * Test class for the js body handler.
 * 
 * @author fabrizio
 * 
 */
public class LetBodyHandlerTest {

    private Variables variables;

    @Before
    public void setUp() {
        variables = new Variables();
        variables.clearAll();
    }

    @Test
    public void shouldHandleExpressionsReturningNull() {
        LetBodyHandler h = new LetBodyHandler();
        String r = h.handle(new RestResponse(), null, "null");
        assertNull(r);
    }

    @Test
    public void shouldHandleJsBodyWithXPaths() {
        LetBodyHandler h = new LetBodyHandler();
        RestResponse response = new RestResponse();
        response.addHeader("Content-Type", "application/json");
        response.setBody("{\"root\" : {\"accountRef\":\"http://something:8111\",\"label\":\"default\",\"websiteRef\":\"ws1\",\"dispersionRef\":\"http://localhost:8111\"} }");
        String ret = h.handle(response, null, "/root/dispersionRef/text()");
        assertThat(ret, is(equalTo("http://localhost:8111")));
    }
}
