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

package smartrics.rest.fitnesse.fixture;

import java.util.ArrayList;
import java.util.List;

import smartrics.rest.client.RestData.Header;
import smartrics.rest.fitnesse.fixture.loopsupport.LoopResultsCellFormatter;
import smartrics.rest.fitnesse.fixture.loopsupport.RowShiftWrapper;
import smartrics.rest.fitnesse.fixture.support.BodyTypeAdapter;
import smartrics.rest.fitnesse.fixture.support.CellFormatter;
import smartrics.rest.fitnesse.fixture.support.CellWrapper;
import smartrics.rest.fitnesse.fixture.support.HeadersTypeAdapter;
import smartrics.rest.fitnesse.fixture.support.RestDataTypeAdapter;
import smartrics.rest.fitnesse.fixture.support.RowWrapper;
import smartrics.rest.fitnesse.fixture.support.StatusCodeTypeAdapter;
import smartrics.rest.fitnesse.fixture.support.Tools;

/**
 * This expansion of the RestFixture allows a REST call to be repeated until either a certain value
 * is returned in the response, or there is a max number of loops exceeded.  The REST call can be
 * set to ignore failure and continue, or to fail the entire row on the first failed REST call.
 *
 * @author Adam Roberts (aroberts@alum.rit.edu)
 */
public class LoopUntilRestFixture extends RestFixture
{

    /**
     * Max number of times to loop, defaults to 10
     */
    int maxNumberOfLoops = 10;

    /**
     * Number of milli seconds to delay between loop executions, -1 means no delay
     */
    long milliSecondsDelayPerLoop = -1;

    /**
     * If the REST call response fails should the loop stop or continue
     */
    boolean continueIfLoopFails = false;

    /**
     * internal flag that specifies if a loop action is currently being performed
     */
    boolean currentlyPerformingLoopAction = false;

    /**
     * A wrapper around the default cell formatter that allows loop results to be aggregated
     */
    @SuppressWarnings("rawtypes")
    private LoopResultsCellFormatter loopFormatter;

    /**
     * <code>| setMilliSecondsDelayPerLoop | long value for how long to sleep after each loop action, -1 disables sleeping, defaults to -1 |</code>
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void setMilliSecondsDelayPerLoop()
    {
        CellWrapper cell = row.getCell(1);
        if (cell == null)
        {
            getFormatter().exception(row.getCell(0), "You must pass a number to set");
        }
        else
        {
            try
            {
                this.milliSecondsDelayPerLoop = Long.parseLong(cell.text());
            }
            catch (NumberFormatException e)
            {
                getFormatter().exception(row.getCell(1), e);
            }
        }
    }

    /**
     * <code>| setMaxNumberOfLoops | integer value for when to kill the test if the until hasn't returned true, default value of 10 |</code>
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void setMaxNumberOfLoops()
    {
        CellWrapper cell = row.getCell(1);
        if (cell == null)
        {
            getFormatter().exception(row.getCell(0), "You must pass a number to set");
        }
        else
        {
            try
            {
                this.maxNumberOfLoops = Integer.parseInt(cell.text());
            }
            catch (NumberFormatException e)
            {
                getFormatter().exception(row.getCell(1), e);
            }
        }
    }

    /**
     * <code>| setContinueIfLoopFails | boolean value, should the test continue if a loop action fails, defaults to false |</code>
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void setContinueIfLoopFails()
    {
        CellWrapper cell = row.getCell(1);
        if (cell == null)
        {
            getFormatter().exception(row.getCell(0), "You must pass a value to set");
        }
        else
        {
            this.continueIfLoopFails = Boolean.parseBoolean(cell.text());
        }
    }

    /**
     * <code>| LOOP | Until Status Code | Until Headers | Until Condition? | HTTP METHOD {GET, PUT, POST, ...} | uri | ?ret | ?headers | ?body |</code>
     * Until Condition: the fixture will run the "HTTP METHOD" until this returns true or a failure
     * condition occurs. Failure conditions can be: hit the max number of loops, failure of the
     * "HTTP METHOD", or an uncaught exception
     *
     * If a loop until condtion is blank then it will be ignored, if all loop conditions are blank
     * then it will loop until max number of loops is reached (or failure or loop action).
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void LOOP()
    {
        debugMethodCall("LOOP Start =>");
        if (row.size() < 5)
        {
            getFormatter().exception(
                row.getCell(0),
                "LOOPs must have an until condition and a command to loop (5 or more cells)");
        }
        else
        {
            // Clear any results from a previous row in the fixture
            getLoopFormatter().resetResults();
            // Save the original row for accessing the "Loop Until Conditions"
            RowWrapper completeRow = row;
            // Remove the "LOOP" and the "Loop Until Conditions" to get the action to run repeatedly
            RowShiftWrapper loopActionRow = new RowShiftWrapper(row, 4);

            performLoop(completeRow, loopActionRow);

            // Add the result to the current action row
            getLoopFormatter().addResultsToTheCurrentRow(continueIfLoopFails);
        }
        debugMethodCall("<= LOOP End");
    }

    /**
     * @param completeRow the entire row with the loop conditions still showing
     * @param loopActionRow the row shifted so that the loop conditions are hidden
     */
    @SuppressWarnings({ "rawtypes" })
    void performLoop(RowWrapper completeRow, RowShiftWrapper loopActionRow)
    {
        for (int i = 0; shouldTheLoopContinue(i, completeRow); i++)
        {
            pauseBeforeNextLoopAction();
            performLoopAction(loopActionRow);
        }
    }

    /**
     * @param iterationCount the number of time the loop has executed
     * @param completeRow the entire row with the loop conditions still showing
     *
     * @return true if the the loop completed correctly or if the loop results are ignored, and
     *      if the the max number of loops has not been exceeded
     */
    @SuppressWarnings({ "rawtypes" })
    boolean shouldTheLoopContinue(int iterationCount, RowWrapper completeRow)
    {
        CellWrapper loopUntilResponseCode = completeRow.getCell(1);
        CellWrapper loopUntilHeaders = completeRow.getCell(2);
        CellWrapper loopUntilCondition = completeRow.getCell(3);

        final boolean maxLoopsExceeded = iterationCount >= maxNumberOfLoops;
        final boolean stopBecauseOfFailedLoopAction = !getLoopFormatter().haveAllTestsPassed();
        final boolean stopBecauseLoopSucceeded = hasTheUntilBeenMet(
            loopUntilResponseCode,
            loopUntilHeaders,
            loopUntilCondition);
        final boolean loopContinue = !maxLoopsExceeded &&
                                     (continueIfLoopFails || !stopBecauseOfFailedLoopAction) &&
                                     !stopBecauseLoopSucceeded;
        if (!loopContinue)
        {
            setLoopUntilStatus(
                iterationCount,
                loopUntilResponseCode,
                loopUntilHeaders,
                loopUntilCondition,
                stopBecauseOfFailedLoopAction,
                stopBecauseLoopSucceeded);
        }
        return loopContinue;
    }

    /**
     * This method should only be called after the loop execution has been completed, it causes
     * every cell of the current row to be formatted with the aggregated result values, calling this
     * twice will cause multiple aggregated results to be appended to the cell.
     */
    @SuppressWarnings("rawtypes")
    void setLoopUntilStatus(
        int iterationCount,
        CellWrapper loopUntilResponseCode,
        CellWrapper loopUntilHeaders,
        CellWrapper loopUntilCondition,
        final boolean stopBecauseOfFailedLoopAction,
        final boolean stopBecauseLoopSucceeded)
    {
        if (stopBecauseOfFailedLoopAction && !continueIfLoopFails)
        {
            String displayErrorMsg = "Loop Action failed!";
            setLoopToFailed(
                displayErrorMsg,
                loopUntilResponseCode,
                loopUntilHeaders,
                loopUntilCondition);
        }
        else if (stopBecauseLoopSucceeded)
        {
            String successMsg = String.format("Succeeded after %1$d iterations", iterationCount);
            setCellToPassed(loopUntilResponseCode, getLastResponseStatusCode(), successMsg);
            setCellToPassed(loopUntilHeaders, getLastResponseHeadersString(), successMsg);
            setCellToPassed(loopUntilCondition, getLastResponseBody(), successMsg);
        }
        else
        {
            String displayErrorMsg = "Max number of loops exceeded!";
            setLoopToFailed(
                displayErrorMsg,
                loopUntilResponseCode,
                loopUntilHeaders,
                loopUntilCondition);
        }
    }

    /**
     * Formats the cells to a failure state
     */
    @SuppressWarnings("rawtypes")
    void setLoopToFailed(
        String displayErrorMsg,
        CellWrapper loopUntilResponseCode,
        CellWrapper loopUntilHeaders,
        CellWrapper loopUntilCondition)
    {
        setCellToFailed(loopUntilResponseCode, getLastResponseStatusCode(), displayErrorMsg);
        setCellToFailed(loopUntilHeaders, getLastResponseHeadersString(), displayErrorMsg);
        setCellToFailed(loopUntilCondition, getLastResponseBody(), displayErrorMsg);
    }

    /**
     * @return the last response's body
     */
    String getLastResponseBody()
    {
        return getLastResponse().getBody();
    }

    /**
     * @return the last response's status code
     */
    String getLastResponseStatusCode()
    {
        return getLastResponse().getStatusCode().toString();
    }

    /**
     * @return the last response's header values
     */
    String getLastResponseHeadersString()
    {
        StringBuilder headerString = new StringBuilder();
        List<Header> lastHeaders = getLastResponse().getHeaders();
        for (Header header : lastHeaders)
        {
            headerString.append(header.toString());
            headerString.append("/r/n");
        }
        return headerString.toString();
    }

    /**
     * Formats the given cell as a passed cell
     *
     * @param loopUntilCondition the cell to format
     * @param actual the actual value from the response
     * @param successMsg the message to format
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    void setCellToPassed(
        CellWrapper loopUntilCondition,
        final String actual,
        final String successMsg)
    {
        if (!loopUntilCondition.body().trim().isEmpty())
        {
            CellFormatter formatter = getFormatter();
            String message = Tools.makeContentForRightCell(
                loopUntilCondition.body(),
                successMsg + "\r\n" + actual,
                formatter,
                formatter.getMinLenghtForToggleCollapse());
            formatter.right(loopUntilCondition, message);
        }
    }

    /**
     * Formats the given cell as a failed cell
     *
     * @param loopUntilCondition the cell to format
     * @param actual the actual value from the response
     * @param displayErrorMsg the message to format
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    void setCellToFailed(
        CellWrapper loopUntilCondition,
        final String actual,
        final String displayErrorMsg)
    {
        if (!loopUntilCondition.body().trim().isEmpty())
        {
            CellFormatter formatter = getFormatter();
            String errorMsg = Tools.makeContentForWrongCell(
                loopUntilCondition.body(),
                displayErrorMsg + "\r\n" + actual,
                new ArrayList<String>(),
                formatter,
                formatter.getMinLenghtForToggleCollapse());
            formatter.wrong(loopUntilCondition, errorMsg);
        }
    }

    /**
     * @return true if each of the three loop conditions was successful on the last execution of
     *      the loop, otherwise false
     */
    @SuppressWarnings("rawtypes")
    boolean hasTheUntilBeenMet(
        CellWrapper loopUntilResponseCode,
        CellWrapper loopUntilHeaders,
        CellWrapper loopUntilCondition)
    {
        return hasTheUntilResponseBeenMet(loopUntilResponseCode) &&
               hasTheUntilHeadersBeenMet(loopUntilHeaders) &&
               hasTheUntilBodyBeenMet(loopUntilCondition);
    }

    /**
     * @return true if the passed in cell was successful on the last execution of the loop,
     *      otherwise false
     */
    @SuppressWarnings({ "rawtypes" })
    boolean hasTheUntilResponseBeenMet(CellWrapper loopUntilResponseCode)
    {
        String lastStatusCode = getLastResponseStatusCode();
        StatusCodeTypeAdapter statusCodeAdapter = new StatusCodeTypeAdapter();
        return doesResponsePass(loopUntilResponseCode, lastStatusCode, statusCodeAdapter);
    }

    /**
     * @return true if the passed in cell was successful on the last execution of the loop,
     *      otherwise false
     */
    @SuppressWarnings({ "rawtypes" })
    boolean hasTheUntilHeadersBeenMet(CellWrapper loopUntilHeaders)
    {
        List<Header> lastHeaders = getLastResponse().getHeaders();
        HeadersTypeAdapter headersAdapter = new HeadersTypeAdapter();
        return doesResponsePass(loopUntilHeaders, lastHeaders, headersAdapter);
    }

    /**
     * @return true if the passed in cell was successful on the last execution of the loop,
     *      otherwise false
     */
    @SuppressWarnings({ "rawtypes" })
    boolean hasTheUntilBodyBeenMet(CellWrapper loopUntilBody)
    {
        BodyTypeAdapter bodyTypeAdapter = createBodyTypeAdapter();
        Object actualResponse;
        if (bodyTypeAdapter.isTextResponse())
        {
            actualResponse = getLastResponseBody();
        }
        else
        {
            actualResponse = getLastResponse().getRawBody();
        }
        return doesResponsePass(loopUntilBody, actualResponse, bodyTypeAdapter);
    }

    /**
     * @return true if the actualResponse evaluates the expectedResponse correctly using the
     *      passed in responseEvaluator, otherwise false
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    boolean doesResponsePass(
        CellWrapper expectedResponse,
        Object actualResponse,
        RestDataTypeAdapter responseEvaluator)
    {
        boolean successful = false;
        if (!expectedResponse.body().trim().isEmpty())
        {
            try
            {
                successful = evaluateExpected(expectedResponse, actualResponse, responseEvaluator);
            }
            catch (Exception e)
            {
                getFormatter().exception(expectedResponse, e);
                return false;
            }
        }
        return successful;
    }

    /**
     * @param loopActionRow the loop row shifted to hide the loop until cells at the beginning
     */
    @SuppressWarnings({ "rawtypes" })
    void performLoopAction(RowShiftWrapper loopActionRow)
    {
        try
        {
            currentlyPerformingLoopAction = true;
            this.processRow(loopActionRow);
        }
        finally
        {
            currentlyPerformingLoopAction = false;
        }
    }

    /**
     * Pauses the fixture for the requested length of time
     */
    void pauseBeforeNextLoopAction()
    {
        if (milliSecondsDelayPerLoop != -1)
        {
            try
            {
                Thread.sleep(milliSecondsDelayPerLoop);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * @see smartrics.rest.fitnesse.fixture.RestFixture#getFormatter()
     */
    @Override
    public CellFormatter<?> getFormatter()
    {
        if (!currentlyPerformingLoopAction)
        {
            return super.getFormatter();
        }
        else
        {
            return getLoopFormatter();
        }
    }

    /**
     * @return the instance of the formatter that defers setting the results until the loop has
     *         completed, never null
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    LoopResultsCellFormatter getLoopFormatter()
    {
        if (loopFormatter == null)
        {
            loopFormatter = new LoopResultsCellFormatter(super.getFormatter());
        }
        return loopFormatter;
    }

}
