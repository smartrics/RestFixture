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

import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import smartrics.rest.client.RestResponse;
import smartrics.rest.fitnesse.fixture.RunnerVariablesProvider;

/**
 * Test class for the js body handler.
 * 
 * @author smartrics
 * 
 */
public class LetBodyJsHandlerTest {

    private FitVariables variables;
    private final RunnerVariablesProvider variablesProvider = new RunnerVariablesProvider() {
		@Override
		public Variables createRunnerVariables() {
			return variables;
		}        	
    };

    @Before
    public void setUp() {
        variables = new FitVariables();
        variables.clearAll();
    }

    @Test
    public void shouldHandleExpressionsReturningNull() {
        LetBodyJsHandler h = new LetBodyJsHandler();
        String r = h.handle(variablesProvider, new RestResponse(), null, "null");
        assertNull(r);
    }
}
