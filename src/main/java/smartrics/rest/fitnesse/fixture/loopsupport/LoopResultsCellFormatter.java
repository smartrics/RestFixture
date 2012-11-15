/*  Copyright 2012 PROS Pricing (www.prospricing.com)
 *
 *  This file is donated to RestFixture.
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
 *  If you want to contact the original author of RestFixture please leave a comment here
 *  http://smartrics.blogspot.com/2008/08/get-fitnesse-with-some-rest.html
 */

package smartrics.rest.fitnesse.fixture.loopsupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import smartrics.rest.fitnesse.fixture.support.CellFormatter;
import smartrics.rest.fitnesse.fixture.support.CellWrapper;
import smartrics.rest.fitnesse.fixture.support.RestDataTypeAdapter;
import smartrics.rest.fitnesse.fixture.support.Tools;

/**
 * This Decorator for the CellFormatter allows the cell results to be aggregated for each loop
 * execution and the actual cell format to be defered until the loop execution has completed.
 *
 * @author Adam Roberts (aroberts@alum.rit.edu)
 */
public class LoopResultsCellFormatter<E> implements CellFormatter<E>
{

    /**
     * The actual cell formatter that will be used to format the cells after the loop completes
     */
    private final CellFormatter<E> formatter;

    /**
     * This map collects each individual cell in the loop to the aggregated results of every time
     * it has been executed in the loop, this allows each cell to be formated according to their
     * entire loop performance instead of just the last execution.
     */
    private final Map<CellWrapper<E>, AggregateLoopResults> loopResults;

    /**
     * Constructor takes a copy of the formatter it is wrapping
     *
     * @param formatter The actual cell formatter that will be used to format the cells after the
     *      loop completes
     */
    public LoopResultsCellFormatter(CellFormatter<E> formatter)
    {
        this.formatter = formatter;
        loopResults = new HashMap<CellWrapper<E>, LoopResultsCellFormatter<E>.AggregateLoopResults>();
    }

    /**
     * @return true if every cell in the loop execution has passed for every execution of the loop
     */
    public boolean haveAllTestsPassed()
    {
        for (Map.Entry<CellWrapper<E>, AggregateLoopResults> entry : getLoopResults().entrySet())
        {
            AggregateLoopResults loopResults = entry.getValue();
            if ((loopResults.wrong.size() > 0) || (loopResults.exceptions.size() > 0))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Resets any aggregated results so this instance can be used for multiple loop executions
     */
    public void resetResults()
    {
        getLoopResults().clear();
    }

    /**
     * After a loop execution has completed set the values from the aggregated results into the
     * individual cells.
     *
     * @param continueIfLoopFails changes how failures are formatted into the cells, if true
     *      loops cells will be gray rather than displaying green or red for status
     */
    public void addResultsToTheCurrentRow(boolean continueIfLoopFails)
    {
        for (Map.Entry<CellWrapper<E>, AggregateLoopResults> entry : getLoopResults().entrySet())
        {
            CellWrapper<E> cellWrapper = entry.getKey();
            AggregateLoopResults loopResults = entry.getValue();
            if (continueIfLoopFails)
            {
                setCellResultAsIgnored(cellWrapper, loopResults);
            }
            else if (loopResults.exceptions.size() > 0)
            {
                setCellResultAsFailedWithException(cellWrapper, loopResults);
            }
            else if (loopResults.wrong.size() > 0)
            {
                setCellResultsAsFailed(cellWrapper, loopResults);
            }
            else
            {
                setCellResultAsPassed(cellWrapper, loopResults);
            }
        }
    }

    /**
     * @param cellWrapper the cell that needs to be formatted
     * @param loopResults the aggregated results to use as the body of the cell
     */
    void setCellResultAsIgnored(CellWrapper<E> cellWrapper, AggregateLoopResults loopResults)
    {
        cellWrapper.addToBody(formatter.gray(loopResults.toString()));
    }

    /**
     * @param cellWrapper the cell that needs to be formatted
     * @param loopResults the aggregated results to use as the body of the cell
     */
    void setCellResultAsFailedWithException(
        CellWrapper<E> cellWrapper,
        AggregateLoopResults loopResults)
    {
        String returnValue = loopResults.toString();
        for (String exceptionMsg : loopResults.exceptions)
        {
            returnValue += "\nException:  " + exceptionMsg;
        }
        for (String failureMsg : loopResults.wrong)
        {
            returnValue += "\nFailure:  " + failureMsg;
        }
        formatter.exception(cellWrapper, returnValue);
    }

    /**
     * @param cellWrapper the cell that needs to be formatted
     * @param loopResults the aggregated results to use as the body of the cell
     */
    void setCellResultsAsFailed(CellWrapper<E> cellWrapper, AggregateLoopResults loopResults)
    {
        String errorMsg = Tools.makeContentForWrongCell(
            cellWrapper.body(),
            loopResults.toString(),
            loopResults.wrong,
            this,
            formatter.getMinLenghtForToggleCollapse());
        formatter.wrong(cellWrapper, errorMsg);
    }

    /**
     * @param cellWrapper the cell that needs to be formatted
     * @param loopResults the aggregated results to use as the body of the cell
     */
    void setCellResultAsPassed(CellWrapper<E> cellWrapper, AggregateLoopResults loopResults)
    {
        String message = Tools.makeContentForRightCell(
            cellWrapper.body(),
            loopResults.toString(),
            this,
            formatter.getMinLenghtForToggleCollapse());
        formatter.right(cellWrapper, message);
    }

    /**
     * @param cellWrapper the cell whose results are requested
     * @return the aggregated results for the requested cell, never null
     */
    AggregateLoopResults getLoopResults(CellWrapper<E> cellWrapper)
    {
        AggregateLoopResults returnValue = getLoopResults().get(cellWrapper);
        if (returnValue == null)
        {
            returnValue = new AggregateLoopResults();
            getLoopResults().put(cellWrapper, returnValue);
        }
        return returnValue;
    }

    /**
     * @return the map containing all of the aggregated results for every cell used in the loop
     *      execution to this point
     */
    Map<CellWrapper<E>, AggregateLoopResults> getLoopResults()
    {
        return loopResults;
    }

    @Override
    public void exception(CellWrapper<E> cellWrapper, Throwable exception)
    {
        this.exception(cellWrapper, exception.getMessage());
        System.err.println("--- Exception that caused failure");
        exception.printStackTrace(System.err);
    }

    @Override
    public void exception(CellWrapper<E> cellWrapper, String exceptionMessage)
    {
        System.err.println();
        System.err.println("--- Failing Loop state");
        System.err.println(cellWrapper.body());
        System.err.println("--- Exception Message that caused failure");
        System.err.println(exceptionMessage);
        this.getLoopResults(cellWrapper).exceptions.add(exceptionMessage);
    }

    @Override
    public void wrong(CellWrapper<E> expected, RestDataTypeAdapter typeAdapter)
    {
        System.err.println();
        System.err.println("--- Failing Loop state - Expected");
        System.err.println(expected.body());
        System.err.println("--- Failing Loop state - Actual");
        System.err.println(typeAdapter.get().toString());
        System.err.println("--- Errors that caused failure");
        for (String error : typeAdapter.getErrors())
        {
            System.err.println(error);
        }
        this.getLoopResults(expected).wrong.addAll(typeAdapter.getErrors());
    }

    @Override
    public void wrong(CellWrapper<E> expected, String failureMessage)
    {
        System.err.println();
        System.err.println("--- Failing Loop state - Expected");
        System.err.println(expected.body());
        System.err.println("--- Failing Loop Message");
        System.err.println(failureMessage);
        this.getLoopResults(expected).wrong.add(failureMessage);
    }

    @Override
    public void right(CellWrapper<E> expected, RestDataTypeAdapter typeAdapter)
    {
        this.getLoopResults(expected).right++;
    }

    @Override
    public void right(CellWrapper<E> expected, String message)
    {
        getLoopResults(expected).right++;
    }

    @Override
    public void check(CellWrapper<E> valueCell, RestDataTypeAdapter adapter)
    {
        formatter.check(valueCell, adapter);
    }

    @Override
    public String label(String string)
    {
        return formatter.label(string);
    }

    @Override
    public String gray(String string)
    {
        return formatter.gray(string);
    }

    @Override
    public void asLink(CellWrapper<E> cell, String link, String text)
    {
        formatter.asLink(cell, link, text);
    }

    @Override
    public void setDisplayActual(boolean displayActual)
    {
        formatter.setDisplayActual(displayActual);
    }

    @Override
    public void setMinLenghtForToggleCollapse(int minLen)
    {
        formatter.setMinLenghtForToggleCollapse(minLen);
    }

    @Override
    public int getMinLenghtForToggleCollapse()
    {
        return formatter.getMinLenghtForToggleCollapse();
    }

    @Override
    public boolean isDisplayActual()
    {
        return formatter.isDisplayActual();
    }

    @Override
    public String fromRaw(String text)
    {
        return formatter.fromRaw(text);
    }

    /**
     * This inner class is used to aggregate the number of times each cell passes or fails during
     * the loop execution
     *
     * @author Adam Roberts (aroberts@alum.rit.edu)
     */
    class AggregateLoopResults
    {
        private final String aggregateMsg = "%1$d right, %2$d wrong, %3$d ignored, %4$d exceptions";

        int right = 0;

        List<String> wrong = new ArrayList<String>();

        int ignored = 0;

        List<String> exceptions = new ArrayList<String>();

        @Override
        public String toString()
        {
            String returnValue = String.format(
                aggregateMsg,
                right,
                wrong.size(),
                ignored,
                exceptions.size());
            return returnValue;
        }
    }

}
