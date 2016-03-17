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
package smartrics.rest.fitnesse.fixture.support;

/**
 * Wrapper of a Slim/Fit cell.
 * 
 * @author smartrics
 * 
 * @param <E> the type of the cell content
 */
public interface CellWrapper<E> {

	/**
	 * 
	 * @return the underlying cell object.
	 */
	E getWrapped();

	/**
	 * @return the text in the cell.
	 */
	String text();

	/**
	 * @param string
	 *            the body of the cell to set.
	 */
	void body(String string);

	/**
	 * @return the current body of the cell.
	 */
	String body();

	/**
	 * appends to the current cell body.
	 * 
	 * @param string
	 *            the string to append.
	 */
	void addToBody(String string);
}
