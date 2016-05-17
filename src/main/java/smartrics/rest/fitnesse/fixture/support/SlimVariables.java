/*  Copyright 2015 Fabrizio Cannizzo
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

import fitnesse.slim.StatementExecutorInterface;

/**
 * Facade to FitNesse global symbols map for SliM.
 *
 * @author smartrics
 */
public class SlimVariables extends Variables {

    private final StatementExecutorInterface executor;

    /**
     * initialises the variables. reade
     * {@code restfixture.null.value.representation} to know how to render
     * {@code null}s.
     *
     * @param c        the config object
     * @param executor the executor
     */
    public SlimVariables(Config c, StatementExecutorInterface executor) {
        super(c);
        this.executor = executor;
    }

    /**
     * puts a value.
     *
     * @param label the symbol
     * @param val   the value to store
     */
    public void put(String label, String val) {
        if(val == null || val.equals(super.nullValue)) {
            executor.assign(label, null);
        } else {
            executor.assign(label, val);
        }
    }

    /**
     * gets a value.
     *
     * @param label the symbol
     * @return the value.
     */
    public String get(String label) {
        final Object symbol = executor.getSymbol(label);
        if (symbol == null) {
            return super.nullValue;
        }
        return symbol.toString();
    }

}

