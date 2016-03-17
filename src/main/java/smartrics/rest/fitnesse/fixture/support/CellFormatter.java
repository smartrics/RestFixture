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
 * @author smartrics
 * 
 * @param <E> the type of the cell
 */
public interface CellFormatter<E> {

	/**
	 * formats a cell containing an exception.
	 * 
	 * @param cellWrapper
	 *            the cell wrapper
	 * @param exception
	 *            the excteption to render.
	 */
	void exception(CellWrapper<E> cellWrapper, Throwable exception);

	/**
	 * formats a cell containing an exception.
	 * 
	 * @param cellWrapper
	 *            the cell wrapper
	 * @param exceptionMessage
	 *            the exception message to render.
	 */
	void exception(CellWrapper<E> cellWrapper, String exceptionMessage);

	/**
	 * formats a check cell.
	 * 
	 * @param valueCell
	 *            the cell value.
	 * @param adapter
	 *            the adapter interpreting the value.
	 */
	void check(CellWrapper<E> valueCell, RestDataTypeAdapter adapter);

	/**
	 * formats a cell label
	 * 
	 * @param string
	 *            the label
	 * @return the cell content as a label.
	 */
	String label(String string);

	/**
	 * formats a cell representing a wrong expectation.
	 * 
	 * @param expected
	 *            the expected value
	 * @param typeAdapter
	 *            the adapter with the actual value.
	 */
	void wrong(CellWrapper<E> expected, RestDataTypeAdapter typeAdapter);

	/**
	 * formats a cell representing a right expectation.
	 * 
	 * @param expected
	 *            the expected value
	 * @param typeAdapter
	 *            the adapter with the actual value.
	 */
	void right(CellWrapper<E> expected, RestDataTypeAdapter typeAdapter);

	/**
	 * formats a cell with a gray background. used to ignore the content or for
	 * comments.
	 * 
	 * @param string
	 *            the content
	 * @return the content grayed out.
	 */
	String gray(String string);

	/**
	 * formats the content as a hyperlink.
	 * 
	 * @param cell
	 *            the cell.
	 * @param resolvedUrl
	 * 	          the cell content after symbols' substitution.
	 * @param link
	 *            the uri in the href.
	 * @param text
	 *            the text.
	 */
	void asLink(CellWrapper<E> cell, String resolvedUrl, String link, String text);

	/**
	 * sets whether the cell should display the actual value after evaluation.
	 * 
	 * @param displayActual
	 *            true if actual value has to be rendered.
	 */
	void setDisplayActual(boolean displayActual);

	/**
	 * sets whether absolute urls are displayed in full
	 *
	 * @param displayAbsoluteURLInFull the value to set
	 */
	void setDisplayAbsoluteURLInFull(boolean displayAbsoluteURLInFull);

	/**
	 * renders the cell as a toggle area if the content of the cell is over the
	 * min value set here.
	 * 
	 * @param minLen
	 *            the min value of the content of a cell.
	 */
	void setMinLengthForToggleCollapse(int minLen);

	/**
	 * @return true if actual values are rendered.
	 */
	boolean isDisplayActual();

	/**
	 * in SLIM cell content is HTML escaped - we abstract this method to
	 * delegate to formatter the cleaning of the content.
	 * 
	 * @param text the text
	 * @return the cleaned text
	 */
	String fromRaw(String text);

}
