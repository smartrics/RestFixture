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
 * Formatter of the content of a cell.
 * 
 * @author fabrizio
 * 
 * @param <E>
 */
public interface CellFormatter<E> {

	void exception(CellWrapper<E> cellWrapper, Throwable exception);

	void check(CellWrapper<E> valueCell, StringTypeAdapter adapter);

	String label(String string);

    void wrong(CellWrapper<E> expected, RestDataTypeAdapter typeAdapter);

	void right(CellWrapper<E> expected, RestDataTypeAdapter typeAdapter);

	String gray(String string);

    void asLink(CellWrapper<E> cell, String link, String text);

    void setDisplayActual(boolean displayActual);

    boolean isDisplayActual();

    // in SLIM cell content is HTML escaped - we abstract this method to
    // delegate to formatter the
    // cleaning of the content.
    String fromRaw(String text);
}
