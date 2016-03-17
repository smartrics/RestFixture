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

import fit.Fixture;

/**
 * Facade to FitNesse global symbols map for FIT.
 * 
 * @author smartrics
 */
public class FitVariables extends Variables {

	/**
	 * initialises variables with default config. See @link
	 * {@link #FitVariables(Config)}
	 */
	public FitVariables() {
		super();
	}

	/**
	 * initialises the variables. reade
	 * {@code restfixture.null.value.representation} to know how to render
	 * {@code null}s.
	 * 
	 * @param c the config
	 */
	public FitVariables(Config c) {
		super(c);
	}

	/**
	 * puts a value.
	 * 
	 * @param label the symbol
	 * @param val the value
	 */
	@Override
	public void put(String label, String val) {
		Fixture.setSymbol(label, val);
	}

	/**
	 * gets a value.
	 *
	 * @param label the symbol
	 * @return the value.
	 */
	@Override
	public String get(String label) {
		if (Fixture.hasSymbol(label)) {
			return Fixture.getSymbol(label).toString();
		}
		return null;
	}

	/**
	 * crears all variables
	 * (used for tests only, given the fact that the Fit variables are in fact static)
	 */
	public void clearAll() {
		Fixture.ClearSymbols();
	}

}
