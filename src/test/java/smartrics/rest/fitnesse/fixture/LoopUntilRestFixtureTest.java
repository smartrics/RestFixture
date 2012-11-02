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

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import smartrics.rest.client.RestResponse;
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

/**
 * @author Adam Roberts (aroberts@alum.rit.edu)
 */
public class LoopUntilRestFixtureTest
{

    @SuppressWarnings("rawtypes")
    @Test
    public final void testSetMilliSecondsDelayPerLoop()
    {
        // Setup
        final long milliSecondsDelayPerLoop = 99;

        final CellWrapper cell = mock(CellWrapper.class);
        when(cell.text()).thenReturn(Long.toString(milliSecondsDelayPerLoop));

        final RowWrapper row = mock(RowWrapper.class);
        when(row.getCell(1)).thenReturn(cell);

        final CellFormatter formatter = mock(CellFormatter.class);

        LoopUntilRestFixture fixture = new LoopUntilRestFixture();
        fixture.row = row;
        fixture = spy(fixture);
        doReturn(formatter).when(fixture).getFormatter();

        // Test
        fixture.setMilliSecondsDelayPerLoop();

        // Validate
        assertEquals("Failed to set", milliSecondsDelayPerLoop, fixture.milliSecondsDelayPerLoop);
        verifyZeroInteractions(formatter);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public final void testSetMilliSecondsDelayPerLoopNotANumber()
    {
        // Setup
        final CellWrapper cell = mock(CellWrapper.class);
        when(cell.text()).thenReturn("Not A Number");

        final RowWrapper row = mock(RowWrapper.class);
        when(row.getCell(1)).thenReturn(cell);

        final CellFormatter formatter = mock(CellFormatter.class);

        LoopUntilRestFixture fixture = new LoopUntilRestFixture();
        fixture.row = row;
        fixture = spy(fixture);
        doReturn(formatter).when(fixture).getFormatter();

        // Test
        fixture.setMilliSecondsDelayPerLoop();

        // Validate
        assertEquals("Default should not have changed", -1, fixture.milliSecondsDelayPerLoop);
        verify(formatter).exception(eq(cell), any(NumberFormatException.class));
        verifyNoMoreInteractions(formatter);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public final void testSetMilliSecondsDelayPerLoopNoCell()
    {
        // Setup
        final CellWrapper cell = mock(CellWrapper.class);
        when(cell.text()).thenReturn("Not A Number");

        final RowWrapper row = mock(RowWrapper.class);
        when(row.getCell(0)).thenReturn(cell);
        when(row.getCell(1)).thenReturn(null);

        final CellFormatter formatter = mock(CellFormatter.class);

        LoopUntilRestFixture fixture = new LoopUntilRestFixture();
        fixture.row = row;
        fixture = spy(fixture);
        doReturn(formatter).when(fixture).getFormatter();

        // Test
        fixture.setMilliSecondsDelayPerLoop();

        // Validate
        assertEquals("Default should not have changed", -1, fixture.milliSecondsDelayPerLoop);
        verify(formatter).exception(cell, "You must pass a number to set");
        verifyNoMoreInteractions(formatter);
    }

    @SuppressWarnings("rawtypes")
    @Test
    public final void testSetMaxNumberOfLoopsPerLoop()
    {
        // Setup
        final int maxNumberOfLoops = 99;

        final CellWrapper cell = mock(CellWrapper.class);
        when(cell.text()).thenReturn(Integer.toString(maxNumberOfLoops));

        final RowWrapper row = mock(RowWrapper.class);
        when(row.getCell(1)).thenReturn(cell);

        final CellFormatter formatter = mock(CellFormatter.class);

        LoopUntilRestFixture fixture = new LoopUntilRestFixture();
        fixture.row = row;
        fixture = spy(fixture);
        doReturn(formatter).when(fixture).getFormatter();

        // Test
        fixture.setMaxNumberOfLoops();

        // Validate
        assertEquals("Failed to set", maxNumberOfLoops, fixture.maxNumberOfLoops);
        verifyZeroInteractions(formatter);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public final void testSetMaxNumberOfLoopsNotANumber()
    {
        // Setup
        final CellWrapper cell = mock(CellWrapper.class);
        when(cell.text()).thenReturn("Not A Number");

        final RowWrapper row = mock(RowWrapper.class);
        when(row.getCell(1)).thenReturn(cell);

        final CellFormatter formatter = mock(CellFormatter.class);

        LoopUntilRestFixture fixture = new LoopUntilRestFixture();
        fixture.row = row;
        fixture = spy(fixture);
        doReturn(formatter).when(fixture).getFormatter();

        // Test
        fixture.setMaxNumberOfLoops();

        // Validate
        assertEquals("Default should not have changed", -1, fixture.milliSecondsDelayPerLoop);
        verify(formatter).exception(eq(cell), any(NumberFormatException.class));
        verifyNoMoreInteractions(formatter);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public final void testSetMaxNumberOfLoopsNoCell()
    {
        // Setup
        final CellWrapper cell = mock(CellWrapper.class);
        when(cell.text()).thenReturn("Not A Number");

        final RowWrapper row = mock(RowWrapper.class);
        when(row.getCell(0)).thenReturn(cell);
        when(row.getCell(1)).thenReturn(null);

        final CellFormatter formatter = mock(CellFormatter.class);

        LoopUntilRestFixture fixture = new LoopUntilRestFixture();
        fixture.row = row;
        fixture = spy(fixture);
        doReturn(formatter).when(fixture).getFormatter();

        // Test
        fixture.setMaxNumberOfLoops();

        // Validate
        assertEquals("Default should not have changed", -1, fixture.milliSecondsDelayPerLoop);
        verify(formatter).exception(cell, "You must pass a number to set");
        verifyNoMoreInteractions(formatter);
    }

    @SuppressWarnings("rawtypes")
    @Test
    public final void testSetContinueIfLoopFails()
    {
        // Setup
        final boolean maxNumberOfLoops = true;

        final CellWrapper cell = mock(CellWrapper.class);
        when(cell.text()).thenReturn(Boolean.toString(maxNumberOfLoops));

        final RowWrapper row = mock(RowWrapper.class);
        when(row.getCell(1)).thenReturn(cell);

        final CellFormatter formatter = mock(CellFormatter.class);

        LoopUntilRestFixture fixture = new LoopUntilRestFixture();
        fixture.row = row;
        fixture = spy(fixture);
        doReturn(formatter).when(fixture).getFormatter();

        // Test
        fixture.setContinueIfLoopFails();

        // Validate
        assertTrue("Failed to set", fixture.continueIfLoopFails);
        verifyZeroInteractions(formatter);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public final void testSetContinueIfLoopFailsNoCell()
    {
        // Setup
        final CellWrapper cell = mock(CellWrapper.class);
        when(cell.text()).thenReturn("Not A Number");

        final RowWrapper row = mock(RowWrapper.class);
        when(row.getCell(0)).thenReturn(cell);
        when(row.getCell(1)).thenReturn(null);

        final CellFormatter formatter = mock(CellFormatter.class);

        LoopUntilRestFixture fixture = new LoopUntilRestFixture();
        fixture.row = row;
        fixture = spy(fixture);
        doReturn(formatter).when(fixture).getFormatter();

        // Test
        fixture.setContinueIfLoopFails();

        // Validate
        assertFalse("Default should not have changed", fixture.continueIfLoopFails);
        verify(formatter).exception(cell, "You must pass a value to set");
        verifyNoMoreInteractions(formatter);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public final void testLOOPNotEnoughCells()
    {
        // Setup
        final CellWrapper cell = mock(CellWrapper.class);

        final RowWrapper row = mock(RowWrapper.class);
        when(row.size()).thenReturn(4);
        when(row.getCell(0)).thenReturn(cell);

        final CellFormatter formatter = mock(CellFormatter.class);

        LoopUntilRestFixture fixture = new LoopUntilRestFixture();
        fixture.row = row;
        fixture = spy(fixture);
        doReturn(formatter).when(fixture).getFormatter();
        doNothing().when(fixture).debugMethodCall(anyString());

        // Test
        fixture.LOOP();

        // Validate
        verify(fixture, times(2)).debugMethodCall(anyString());
        verify(formatter).exception(cell, "LOOPs must have an until condition and a command to loop (5 or more cells)");
        verifyNoMoreInteractions(formatter);
    }

    @SuppressWarnings({ "rawtypes" })
    @Test
    public final void testLOOP()
    {
        // Setup
        final CellWrapper cell1 = mock(CellWrapper.class);
        final CellWrapper cell2 = mock(CellWrapper.class);
        final CellWrapper cell3 = mock(CellWrapper.class);
        final CellWrapper cell4 = mock(CellWrapper.class);
        final CellWrapper cell5 = mock(CellWrapper.class);

        final RowWrapper row = mock(RowWrapper.class);
        when(row.size()).thenReturn(5);
        when(row.getCell(0)).thenReturn(cell1);
        when(row.getCell(1)).thenReturn(cell2);
        when(row.getCell(2)).thenReturn(cell3);
        when(row.getCell(3)).thenReturn(cell4);
        when(row.getCell(4)).thenReturn(cell5);

        final LoopResultsCellFormatter formatter = mock(LoopResultsCellFormatter.class);

        LoopUntilRestFixture fixture = new LoopUntilRestFixture();
        fixture.row = row;
        fixture = spy(fixture);
        doReturn(formatter).when(fixture).getLoopFormatter();
        doNothing().when(fixture).debugMethodCall(anyString());
        Answer answer = new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable
            {
                RowShiftWrapper shifted = (RowShiftWrapper)invocation.getArguments()[1];
                assertEquals("Incorrect size for wrapper row", 1, shifted.size());
                assertEquals("Incorrect cell", cell5, shifted.getCell(0));
                return null;
            }
        };
        doAnswer(answer).when(fixture).performLoop(any(RowWrapper.class), any(RowShiftWrapper.class));

        InOrder inOrder = inOrder(fixture, formatter);

        // Test
        fixture.LOOP();

        // Validate
        inOrder.verify(fixture).debugMethodCall(anyString());
        inOrder.verify(formatter).resetResults();
        inOrder.verify(fixture).performLoop(eq(row), any(RowShiftWrapper.class));
        inOrder.verify(formatter).addResultsToTheCurrentRow(fixture.continueIfLoopFails);
        inOrder.verify(fixture).debugMethodCall(anyString());
    }

    @SuppressWarnings({ "rawtypes" })
    @Test
    public final void testPerformLoop()
    {
        // Setup
        final RowWrapper completeRow = mock(RowWrapper.class);
        final RowShiftWrapper loopActionRow = mock(RowShiftWrapper.class);

        LoopUntilRestFixture fixture = new LoopUntilRestFixture();
        fixture = spy(fixture);
        doNothing().when(fixture).pauseBeforeNextLoopAction();
        doNothing().when(fixture).performLoopAction(loopActionRow);
        doReturn(true).when(fixture).shouldTheLoopContinue(0, completeRow);
        doReturn(true).when(fixture).shouldTheLoopContinue(1, completeRow);
        doReturn(false).when(fixture).shouldTheLoopContinue(2, completeRow);

        InOrder inOrder = inOrder(fixture);

        // Test
        fixture.performLoop(completeRow, loopActionRow);

        // Validate
        verify(fixture, times(3)).shouldTheLoopContinue(anyInt(), eq(completeRow));
        verify(fixture, times(2)).pauseBeforeNextLoopAction();
        verify(fixture, times(2)).performLoopAction(loopActionRow);
        inOrder.verify(fixture, times(1)).pauseBeforeNextLoopAction();
        inOrder.verify(fixture, times(1)).performLoopAction(loopActionRow);
        inOrder.verify(fixture, times(1)).pauseBeforeNextLoopAction();
        inOrder.verify(fixture, times(1)).performLoopAction(loopActionRow);
    }

    @SuppressWarnings("rawtypes")
    @Test
    public final void testShouldTheLoopContinueNotExceededMaxNotFailedNotSucceeded()
    {
        // Setup
        final int iterationCount = 6;
        final int maxNumberOfLoops = 7;
        final boolean continueIfLoopFails = false;
        final boolean stopBecauseOfFailedLoopAction = false;
        final boolean stopBecauseLoopSucceeded = false;

        final CellWrapper loopUntilResponseCode = mock(CellWrapper.class);
        final CellWrapper loopUntilHeaders = mock(CellWrapper.class);
        final CellWrapper loopUntilCondition = mock(CellWrapper.class);

        final RowWrapper completeRow = mock(RowWrapper.class);
        when(completeRow.getCell(1)).thenReturn(loopUntilResponseCode);
        when(completeRow.getCell(2)).thenReturn(loopUntilHeaders);
        when(completeRow.getCell(3)).thenReturn(loopUntilCondition);

        final LoopResultsCellFormatter loopFormatter = mock(LoopResultsCellFormatter.class);
        when(loopFormatter.haveAllTestsPassed()).thenReturn(!stopBecauseOfFailedLoopAction);

        LoopUntilRestFixture fixture = new LoopUntilRestFixture();
        fixture.maxNumberOfLoops = maxNumberOfLoops;
        fixture.continueIfLoopFails = continueIfLoopFails;
        fixture = spy(fixture);
        doReturn(loopFormatter).when(fixture).getLoopFormatter();
        doReturn(stopBecauseLoopSucceeded).when(fixture).hasTheUntilBeenMet(
            loopUntilResponseCode,
            loopUntilHeaders,
            loopUntilCondition);
        doNothing().when(fixture).setLoopUntilStatus(anyInt(), eq(loopUntilResponseCode), eq(loopUntilHeaders), eq(loopUntilCondition), anyBoolean(), anyBoolean());

        // Test
        final boolean actual = fixture.shouldTheLoopContinue(iterationCount, completeRow);

        // Validate
        assertTrue("The Loop should continue", actual);
        verify(fixture, times(0)).setLoopUntilStatus(anyInt(), eq(loopUntilResponseCode), eq(loopUntilHeaders), eq(loopUntilCondition), anyBoolean(), anyBoolean());
    }

    @SuppressWarnings("rawtypes")
    @Test
    public final void testShouldTheLoopContinueNotExceededMaxFailedButContinueNotSucceeded()
    {
        // Setup
        final int iterationCount = 6;
        final int maxNumberOfLoops = 7;
        final boolean continueIfLoopFails = true;
        final boolean stopBecauseOfFailedLoopAction = true;
        final boolean stopBecauseLoopSucceeded = false;

        final CellWrapper loopUntilResponseCode = mock(CellWrapper.class);
        final CellWrapper loopUntilHeaders = mock(CellWrapper.class);
        final CellWrapper loopUntilCondition = mock(CellWrapper.class);

        final RowWrapper completeRow = mock(RowWrapper.class);
        when(completeRow.getCell(1)).thenReturn(loopUntilResponseCode);
        when(completeRow.getCell(2)).thenReturn(loopUntilHeaders);
        when(completeRow.getCell(3)).thenReturn(loopUntilCondition);

        final LoopResultsCellFormatter loopFormatter = mock(LoopResultsCellFormatter.class);
        when(loopFormatter.haveAllTestsPassed()).thenReturn(!stopBecauseOfFailedLoopAction);

        LoopUntilRestFixture fixture = new LoopUntilRestFixture();
        fixture.maxNumberOfLoops = maxNumberOfLoops;
        fixture.continueIfLoopFails = continueIfLoopFails;
        fixture = spy(fixture);
        doReturn(loopFormatter).when(fixture).getLoopFormatter();
        doReturn(stopBecauseLoopSucceeded).when(fixture).hasTheUntilBeenMet(
            loopUntilResponseCode,
            loopUntilHeaders,
            loopUntilCondition);
        doNothing().when(fixture).setLoopUntilStatus(anyInt(), eq(loopUntilResponseCode), eq(loopUntilHeaders), eq(loopUntilCondition), anyBoolean(), anyBoolean());

        // Test
        final boolean actual = fixture.shouldTheLoopContinue(iterationCount, completeRow);

        // Validate
        assertTrue("The Loop should continue even though the condition failed", actual);
        verify(fixture, times(0)).setLoopUntilStatus(anyInt(), eq(loopUntilResponseCode), eq(loopUntilHeaders), eq(loopUntilCondition), anyBoolean(), anyBoolean());
    }

    @SuppressWarnings("rawtypes")
    @Test
    public final void testShouldTheLoopContinueFailedDontContinue()
    {
        // Setup
        final int iterationCount = 6;
        final int maxNumberOfLoops = 7;
        final boolean continueIfLoopFails = false;
        final boolean stopBecauseOfFailedLoopAction = true;
        final boolean stopBecauseLoopSucceeded = false;

        final CellWrapper loopUntilResponseCode = mock(CellWrapper.class);
        final CellWrapper loopUntilHeaders = mock(CellWrapper.class);
        final CellWrapper loopUntilCondition = mock(CellWrapper.class);

        final RowWrapper completeRow = mock(RowWrapper.class);
        when(completeRow.getCell(1)).thenReturn(loopUntilResponseCode);
        when(completeRow.getCell(2)).thenReturn(loopUntilHeaders);
        when(completeRow.getCell(3)).thenReturn(loopUntilCondition);

        final LoopResultsCellFormatter loopFormatter = mock(LoopResultsCellFormatter.class);
        when(loopFormatter.haveAllTestsPassed()).thenReturn(!stopBecauseOfFailedLoopAction);

        LoopUntilRestFixture fixture = new LoopUntilRestFixture();
        fixture.maxNumberOfLoops = maxNumberOfLoops;
        fixture.continueIfLoopFails = continueIfLoopFails;
        fixture = spy(fixture);
        doReturn(loopFormatter).when(fixture).getLoopFormatter();
        doReturn(stopBecauseLoopSucceeded).when(fixture).hasTheUntilBeenMet(
            loopUntilResponseCode,
            loopUntilHeaders,
            loopUntilCondition);
        doNothing().when(fixture).setLoopUntilStatus(anyInt(), eq(loopUntilResponseCode), eq(loopUntilHeaders), eq(loopUntilCondition), anyBoolean(), anyBoolean());

        // Test
        final boolean actual = fixture.shouldTheLoopContinue(iterationCount, completeRow);

        // Validate
        assertFalse("The Looping failed", actual);
        verify(fixture, times(1)).setLoopUntilStatus(anyInt(), eq(loopUntilResponseCode), eq(loopUntilHeaders), eq(loopUntilCondition), anyBoolean(), anyBoolean());
        verify(fixture, times(1)).setLoopUntilStatus(iterationCount, loopUntilResponseCode, loopUntilHeaders, loopUntilCondition, stopBecauseOfFailedLoopAction, stopBecauseLoopSucceeded);
    }

    @SuppressWarnings("rawtypes")
    @Test
    public final void testShouldTheLoopContinueMaxLoop()
    {
        // Setup
        final int iterationCount = 6;
        final int maxNumberOfLoops = 6;
        final boolean continueIfLoopFails = false;
        final boolean stopBecauseOfFailedLoopAction = false;
        final boolean stopBecauseLoopSucceeded = false;

        final CellWrapper loopUntilResponseCode = mock(CellWrapper.class);
        final CellWrapper loopUntilHeaders = mock(CellWrapper.class);
        final CellWrapper loopUntilCondition = mock(CellWrapper.class);

        final RowWrapper completeRow = mock(RowWrapper.class);
        when(completeRow.getCell(1)).thenReturn(loopUntilResponseCode);
        when(completeRow.getCell(2)).thenReturn(loopUntilHeaders);
        when(completeRow.getCell(3)).thenReturn(loopUntilCondition);

        final LoopResultsCellFormatter loopFormatter = mock(LoopResultsCellFormatter.class);
        when(loopFormatter.haveAllTestsPassed()).thenReturn(!stopBecauseOfFailedLoopAction);

        LoopUntilRestFixture fixture = new LoopUntilRestFixture();
        fixture.maxNumberOfLoops = maxNumberOfLoops;
        fixture.continueIfLoopFails = continueIfLoopFails;
        fixture = spy(fixture);
        doReturn(loopFormatter).when(fixture).getLoopFormatter();
        doReturn(stopBecauseLoopSucceeded).when(fixture).hasTheUntilBeenMet(
            loopUntilResponseCode,
            loopUntilHeaders,
            loopUntilCondition);
        doNothing().when(fixture).setLoopUntilStatus(anyInt(), eq(loopUntilResponseCode), eq(loopUntilHeaders), eq(loopUntilCondition), anyBoolean(), anyBoolean());

        // Test
        final boolean actual = fixture.shouldTheLoopContinue(iterationCount, completeRow);

        // Validate
        assertFalse("Max Loop Reached", actual);
        verify(fixture, times(1)).setLoopUntilStatus(anyInt(), eq(loopUntilResponseCode), eq(loopUntilHeaders), eq(loopUntilCondition), anyBoolean(), anyBoolean());
        verify(fixture, times(1)).setLoopUntilStatus(iterationCount, loopUntilResponseCode, loopUntilHeaders, loopUntilCondition, stopBecauseOfFailedLoopAction, stopBecauseLoopSucceeded);
    }

    @SuppressWarnings("rawtypes")
    @Test
    public final void testShouldTheLoopContinueNoSucceeded()
    {
        // Setup
        final int iterationCount = 6;
        final int maxNumberOfLoops = 7;
        final boolean continueIfLoopFails = false;
        final boolean stopBecauseOfFailedLoopAction = false;
        final boolean stopBecauseLoopSucceeded = true;

        final CellWrapper loopUntilResponseCode = mock(CellWrapper.class);
        final CellWrapper loopUntilHeaders = mock(CellWrapper.class);
        final CellWrapper loopUntilCondition = mock(CellWrapper.class);

        final RowWrapper completeRow = mock(RowWrapper.class);
        when(completeRow.getCell(1)).thenReturn(loopUntilResponseCode);
        when(completeRow.getCell(2)).thenReturn(loopUntilHeaders);
        when(completeRow.getCell(3)).thenReturn(loopUntilCondition);

        final LoopResultsCellFormatter loopFormatter = mock(LoopResultsCellFormatter.class);
        when(loopFormatter.haveAllTestsPassed()).thenReturn(!stopBecauseOfFailedLoopAction);

        LoopUntilRestFixture fixture = new LoopUntilRestFixture();
        fixture.maxNumberOfLoops = maxNumberOfLoops;
        fixture.continueIfLoopFails = continueIfLoopFails;
        fixture = spy(fixture);
        doReturn(loopFormatter).when(fixture).getLoopFormatter();
        doReturn(stopBecauseLoopSucceeded).when(fixture).hasTheUntilBeenMet(
            loopUntilResponseCode,
            loopUntilHeaders,
            loopUntilCondition);
        doNothing().when(fixture).setLoopUntilStatus(anyInt(), eq(loopUntilResponseCode), eq(loopUntilHeaders), eq(loopUntilCondition), anyBoolean(), anyBoolean());

        // Test
        final boolean actual = fixture.shouldTheLoopContinue(iterationCount, completeRow);

        // Validate
        assertFalse("Loop completed", actual);
        verify(fixture, times(1)).setLoopUntilStatus(anyInt(), eq(loopUntilResponseCode), eq(loopUntilHeaders), eq(loopUntilCondition), anyBoolean(), anyBoolean());
        verify(fixture, times(1)).setLoopUntilStatus(iterationCount, loopUntilResponseCode, loopUntilHeaders, loopUntilCondition, stopBecauseOfFailedLoopAction, stopBecauseLoopSucceeded);
    }

    @SuppressWarnings("rawtypes")
    @Test
    public final void testSetLoopUntilStatusFailedDontContinue()
    {
        // Setup
        final int iterationCount = 7;

        final boolean continueIfLoopFails = false;
        final boolean stopBecauseOfFailedLoopAction = true;
        final boolean stopBecauseLoopSucceeded = false;

        final String statusCode = "Test Status Code";
        final String headersString = "Test Headers String";
        final String bodyString = "Test Body String";

        final CellWrapper loopUntilResponseCode = mock(CellWrapper.class);
        final CellWrapper loopUntilHeaders = mock(CellWrapper.class);
        final CellWrapper loopUntilCondition = mock(CellWrapper.class);

        LoopUntilRestFixture fixture = new LoopUntilRestFixture();
        fixture.continueIfLoopFails = continueIfLoopFails;
        fixture = spy(fixture);
        doReturn(statusCode).when(fixture).getLastResponseStatusCode();
        doReturn(headersString).when(fixture).getLastResponseHeadersString();
        doReturn(bodyString).when(fixture).getLastResponseBody();
        doNothing().when(fixture).setLoopToFailed(anyString(), any(CellWrapper.class), any(CellWrapper.class), any(CellWrapper.class));
        doNothing().when(fixture).setCellToPassed(any(CellWrapper.class), anyString(), anyString());

        // Test
        fixture.setLoopUntilStatus(
            iterationCount,
            loopUntilResponseCode,
            loopUntilHeaders,
            loopUntilCondition,
            stopBecauseOfFailedLoopAction,
            stopBecauseLoopSucceeded);

        // Validate
        verify(fixture, times(1)).setLoopToFailed(anyString(), any(CellWrapper.class), any(CellWrapper.class), any(CellWrapper.class));
        verify(fixture, times(1)).setLoopToFailed("Loop Action failed!", loopUntilResponseCode, loopUntilHeaders, loopUntilCondition);
        verify(fixture, times(0)).setLoopToFailed("Max number of loops exceeded!", loopUntilResponseCode, loopUntilHeaders, loopUntilCondition);

        verify(fixture, times(0)).setCellToPassed(any(CellWrapper.class), anyString(), anyString());
        verify(fixture, times(0)).setCellToPassed(loopUntilResponseCode, statusCode, "Succeeded after 7 iterations");
        verify(fixture, times(0)).setCellToPassed(loopUntilHeaders, headersString, "Succeeded after 7 iterations");
        verify(fixture, times(0)).setCellToPassed(loopUntilCondition, bodyString, "Succeeded after 7 iterations");
    }

    @SuppressWarnings("rawtypes")
    @Test
    public final void testSetLoopUntilStatusFailedContinue()
    {
        // Setup
        final int iterationCount = 7;

        final boolean continueIfLoopFails = true;
        final boolean stopBecauseOfFailedLoopAction = true;
        final boolean stopBecauseLoopSucceeded = true;

        final String statusCode = "Test Status Code";
        final String headersString = "Test Headers String";
        final String bodyString = "Test Body String";

        final CellWrapper loopUntilResponseCode = mock(CellWrapper.class);
        final CellWrapper loopUntilHeaders = mock(CellWrapper.class);
        final CellWrapper loopUntilCondition = mock(CellWrapper.class);

        LoopUntilRestFixture fixture = new LoopUntilRestFixture();
        fixture.continueIfLoopFails = continueIfLoopFails;
        fixture = spy(fixture);
        doReturn(statusCode).when(fixture).getLastResponseStatusCode();
        doReturn(headersString).when(fixture).getLastResponseHeadersString();
        doReturn(bodyString).when(fixture).getLastResponseBody();
        doNothing().when(fixture).setLoopToFailed(anyString(), any(CellWrapper.class), any(CellWrapper.class), any(CellWrapper.class));
        doNothing().when(fixture).setCellToPassed(any(CellWrapper.class), anyString(), anyString());

        // Test
        fixture.setLoopUntilStatus(
            iterationCount,
            loopUntilResponseCode,
            loopUntilHeaders,
            loopUntilCondition,
            stopBecauseOfFailedLoopAction,
            stopBecauseLoopSucceeded);

        // Validate
        verify(fixture, times(0)).setLoopToFailed(anyString(), any(CellWrapper.class), any(CellWrapper.class), any(CellWrapper.class));
        verify(fixture, times(0)).setLoopToFailed("Loop Action failed!", loopUntilResponseCode, loopUntilHeaders, loopUntilCondition);
        verify(fixture, times(0)).setLoopToFailed("Max number of loops exceeded!", loopUntilResponseCode, loopUntilHeaders, loopUntilCondition);

        verify(fixture, times(3)).setCellToPassed(any(CellWrapper.class), anyString(), anyString());
        verify(fixture, times(1)).setCellToPassed(loopUntilResponseCode, statusCode, "Succeeded after 7 iterations");
        verify(fixture, times(1)).setCellToPassed(loopUntilHeaders, headersString, "Succeeded after 7 iterations");
        verify(fixture, times(1)).setCellToPassed(loopUntilCondition, bodyString, "Succeeded after 7 iterations");
    }

    @SuppressWarnings("rawtypes")
    @Test
    public final void testSetLoopUntilStatusSucceeded()
    {
        // Setup
        final int iterationCount = 7;

        final boolean continueIfLoopFails = false;
        final boolean stopBecauseOfFailedLoopAction = false;
        final boolean stopBecauseLoopSucceeded = true;

        final String statusCode = "Test Status Code";
        final String headersString = "Test Headers String";
        final String bodyString = "Test Body String";

        final CellWrapper loopUntilResponseCode = mock(CellWrapper.class);
        final CellWrapper loopUntilHeaders = mock(CellWrapper.class);
        final CellWrapper loopUntilCondition = mock(CellWrapper.class);

        LoopUntilRestFixture fixture = new LoopUntilRestFixture();
        fixture.continueIfLoopFails = continueIfLoopFails;
        fixture = spy(fixture);
        doReturn(statusCode).when(fixture).getLastResponseStatusCode();
        doReturn(headersString).when(fixture).getLastResponseHeadersString();
        doReturn(bodyString).when(fixture).getLastResponseBody();
        doNothing().when(fixture).setLoopToFailed(anyString(), any(CellWrapper.class), any(CellWrapper.class), any(CellWrapper.class));
        doNothing().when(fixture).setCellToPassed(any(CellWrapper.class), anyString(), anyString());

        // Test
        fixture.setLoopUntilStatus(
            iterationCount,
            loopUntilResponseCode,
            loopUntilHeaders,
            loopUntilCondition,
            stopBecauseOfFailedLoopAction,
            stopBecauseLoopSucceeded);

        // Validate
        verify(fixture, times(0)).setLoopToFailed(anyString(), any(CellWrapper.class), any(CellWrapper.class), any(CellWrapper.class));
        verify(fixture, times(0)).setLoopToFailed("Loop Action failed!", loopUntilResponseCode, loopUntilHeaders, loopUntilCondition);
        verify(fixture, times(0)).setLoopToFailed("Max number of loops exceeded!", loopUntilResponseCode, loopUntilHeaders, loopUntilCondition);

        verify(fixture, times(3)).setCellToPassed(any(CellWrapper.class), anyString(), anyString());
        verify(fixture, times(1)).setCellToPassed(loopUntilResponseCode, statusCode, "Succeeded after 7 iterations");
        verify(fixture, times(1)).setCellToPassed(loopUntilHeaders, headersString, "Succeeded after 7 iterations");
        verify(fixture, times(1)).setCellToPassed(loopUntilCondition, bodyString, "Succeeded after 7 iterations");
    }

    @SuppressWarnings("rawtypes")
    @Test
    public final void testSetLoopUntilStatusTooManyLoops()
    {
        // Setup
        final int iterationCount = 7;

        final boolean continueIfLoopFails = false;
        final boolean stopBecauseOfFailedLoopAction = false;
        final boolean stopBecauseLoopSucceeded = false;

        final String statusCode = "Test Status Code";
        final String headersString = "Test Headers String";
        final String bodyString = "Test Body String";

        final CellWrapper loopUntilResponseCode = mock(CellWrapper.class);
        final CellWrapper loopUntilHeaders = mock(CellWrapper.class);
        final CellWrapper loopUntilCondition = mock(CellWrapper.class);

        LoopUntilRestFixture fixture = new LoopUntilRestFixture();
        fixture.continueIfLoopFails = continueIfLoopFails;
        fixture = spy(fixture);
        doReturn(statusCode).when(fixture).getLastResponseStatusCode();
        doReturn(headersString).when(fixture).getLastResponseHeadersString();
        doReturn(bodyString).when(fixture).getLastResponseBody();
        doNothing().when(fixture).setLoopToFailed(anyString(), any(CellWrapper.class), any(CellWrapper.class), any(CellWrapper.class));
        doNothing().when(fixture).setCellToPassed(any(CellWrapper.class), anyString(), anyString());

        // Test
        fixture.setLoopUntilStatus(
            iterationCount,
            loopUntilResponseCode,
            loopUntilHeaders,
            loopUntilCondition,
            stopBecauseOfFailedLoopAction,
            stopBecauseLoopSucceeded);

        // Validate
        verify(fixture, times(1)).setLoopToFailed(anyString(), any(CellWrapper.class), any(CellWrapper.class), any(CellWrapper.class));
        verify(fixture, times(0)).setLoopToFailed("Loop Action failed!", loopUntilResponseCode, loopUntilHeaders, loopUntilCondition);
        verify(fixture, times(1)).setLoopToFailed("Max number of loops exceeded!", loopUntilResponseCode, loopUntilHeaders, loopUntilCondition);

        verify(fixture, times(0)).setCellToPassed(any(CellWrapper.class), anyString(), anyString());
        verify(fixture, times(0)).setCellToPassed(loopUntilResponseCode, statusCode, "Succeeded after 7 iterations");
        verify(fixture, times(0)).setCellToPassed(loopUntilHeaders, headersString, "Succeeded after 7 iterations");
        verify(fixture, times(0)).setCellToPassed(loopUntilCondition, bodyString, "Succeeded after 7 iterations");
    }

    @SuppressWarnings("rawtypes")
    @Test
    public final void testSetLoopToFailed()
    {
        // Setup
        final String statusCode = "Test Status Code";
        final String headersString = "Test Headers String";
        final String bodyString = "Test Body String";

        final String message = "Test Message";
        final CellWrapper loopUntilResponseCode = mock(CellWrapper.class);
        final CellWrapper loopUntilHeaders = mock(CellWrapper.class);
        final CellWrapper loopUntilCondition = mock(CellWrapper.class);

        LoopUntilRestFixture fixture = new LoopUntilRestFixture();
        fixture = spy(fixture);
        doReturn(statusCode).when(fixture).getLastResponseStatusCode();
        doReturn(headersString).when(fixture).getLastResponseHeadersString();
        doReturn(bodyString).when(fixture).getLastResponseBody();
        doNothing().when(fixture).setCellToFailed(any(CellWrapper.class), anyString(), anyString());

        // Test
        fixture.setLoopToFailed(message, loopUntilResponseCode, loopUntilHeaders, loopUntilCondition);

        // Validate
        verify(fixture).setCellToFailed(loopUntilResponseCode, statusCode, message);
        verify(fixture).setCellToFailed(loopUntilHeaders, headersString, message);
        verify(fixture).setCellToFailed(loopUntilCondition, bodyString, message);
    }

    @Test
    public final void testGetLastResponseBody()
    {
        // Setup
        final String expected = "Test Body";

        RestResponse lastResponse = mock(RestResponse.class);
        when(lastResponse.getBody()).thenReturn(expected);

        LoopUntilRestFixture fixture = new LoopUntilRestFixture();
        fixture = spy(fixture);
        doReturn(lastResponse).when(fixture).getLastResponse();

        // Test
        String actual = fixture.getLastResponseBody();

        // Validate
        assertEquals("Incorrect response body", expected.toString(), actual);
    }

    @Test
    public final void testGetLastResponseStatusCode()
    {
        // Setup
        final Integer expected = 99;

        RestResponse lastResponse = mock(RestResponse.class);
        when(lastResponse.getStatusCode()).thenReturn(expected);

        LoopUntilRestFixture fixture = new LoopUntilRestFixture();
        fixture = spy(fixture);
        doReturn(lastResponse).when(fixture).getLastResponse();

        // Test
        String actual = fixture.getLastResponseStatusCode();

        // Validate
        assertEquals("Incorrect status code", expected.toString(), actual);
    }

    @Test
    public final void testGetLastResponseHeadersString()
    {
        // Setup
        final String header1Str = "Test Header 1 String";
        final String header2Str = "Test Header 2 String";
        final String expected = header1Str + "/r/n" + header2Str + "/r/n";
        Header header1 = mock(Header.class);
        Header header2 = mock(Header.class);
        when(header1.toString()).thenReturn(header1Str);
        when(header2.toString()).thenReturn(header2Str);
        List<Header> lastHeaders = new ArrayList<Header>();
        lastHeaders.add(header1);
        lastHeaders.add(header2);

        RestResponse lastResponse = mock(RestResponse.class);
        when(lastResponse.getHeaders()).thenReturn(lastHeaders);

        LoopUntilRestFixture fixture = new LoopUntilRestFixture();
        fixture = spy(fixture);
        doReturn(lastResponse).when(fixture).getLastResponse();

        // Test
        String actual = fixture.getLastResponseHeadersString();

        // Validate
        assertEquals("Incorrect Headers representation", expected, actual);
    }

    @SuppressWarnings("rawtypes")
    @Test
    public final void testSetCellToPassedSkipNoBody()
    {
        // Setup
        CellWrapper loopUntilCondition = mock(CellWrapper.class);
        String actual = "TestActual";
        final String message = "TestMessage";

        when(loopUntilCondition.body()).thenReturn("     ");

        CellFormatter formatter = mock(CellFormatter.class);

        LoopUntilRestFixture fixture = new LoopUntilRestFixture();
        fixture = spy(fixture);
        doReturn(formatter).when(fixture).getFormatter();

        // Test
        fixture.setCellToPassed(loopUntilCondition, actual, message);

        // Validate
        verifyZeroInteractions(formatter);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public final void testSetCellToPassed()
    {
        // Setup
        final String expectedBody = "TestExpectedBody";
        CellWrapper loopUntilCondition = mock(CellWrapper.class);
        final String actual = "TestActual";
        final String message = "TestMessage";

        when(loopUntilCondition.body()).thenReturn(expectedBody);

        CellFormatter formatter = mock(CellFormatter.class);
        when(formatter.isDisplayActual()).thenReturn(true);

        Answer answer = new Answer()
        {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable
            {
                String newMsg = invocation.getArguments()[1].toString();
                assertTrue("Missing the expected", newMsg.contains(expectedBody));
                assertTrue("Missing the message", newMsg.contains(message));
                assertTrue("Missing the actual", newMsg.contains(actual));
                return null;
            }
        };
        doAnswer(answer).when(formatter).right(eq(loopUntilCondition), anyString());

        LoopUntilRestFixture fixture = new LoopUntilRestFixture();
        fixture = spy(fixture);
        doReturn(formatter).when(fixture).getFormatter();

        // Test
        fixture.setCellToPassed(loopUntilCondition, actual, message);

        // Validate
        verify(formatter).right(eq(loopUntilCondition), anyString());
    }

    @SuppressWarnings("rawtypes")
    @Test
    public final void testSetCellToFailedSkipNoBody()
    {
        // Setup
        CellWrapper loopUntilCondition = mock(CellWrapper.class);
        String actual = "TestActual";
        final String message = "TestMessage";

        when(loopUntilCondition.body()).thenReturn("     ");

        CellFormatter formatter = mock(CellFormatter.class);

        LoopUntilRestFixture fixture = new LoopUntilRestFixture();
        fixture = spy(fixture);
        doReturn(formatter).when(fixture).getFormatter();

        // Test
        fixture.setCellToFailed(loopUntilCondition, actual, message);

        // Validate
        verifyZeroInteractions(formatter);
    }

    /**
     * This test is a mess because mockito does not have the capabilities to mock the static
     * access to Tools, so must use an Answer class to verify the behavior
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public final void testSetCellToFailed()
    {
        // Setup
        final String expectedBody = "TestExpectedBody";
        CellWrapper loopUntilCondition = mock(CellWrapper.class);
        final String actual = "TestActual";
        final String message = "TestMessage";

        when(loopUntilCondition.body()).thenReturn(expectedBody);

        CellFormatter formatter = mock(CellFormatter.class);
        when(formatter.isDisplayActual()).thenReturn(true);

        Answer answer = new Answer()
        {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable
            {
                String newMsg = invocation.getArguments()[1].toString();
                assertTrue("Missing the expected", newMsg.contains(expectedBody));
                assertTrue("Missing the message", newMsg.contains(message));
                assertTrue("Missing the actual", newMsg.contains(actual));
                return null;
            }
        };
        doAnswer(answer).when(formatter).wrong(eq(loopUntilCondition), anyString());

        LoopUntilRestFixture fixture = new LoopUntilRestFixture();
        fixture = spy(fixture);
        doReturn(formatter).when(fixture).getFormatter();

        // Test
        fixture.setCellToFailed(loopUntilCondition, actual, message);

        // Validate
        verify(formatter).wrong(eq(loopUntilCondition), anyString());
    }

    @SuppressWarnings("rawtypes")
    @Test
    public final void testHasTheUntilBeenMetAllTrue()
    {
        // Setup
        final boolean hasTheUntilResponseBeenMet = true;
        final boolean hasTheUntilHeadersBeenMet = true;
        final boolean hasTheUntilConditionBeenMet = true;

        CellWrapper loopUntilResponseCode = mock(CellWrapper.class);
        CellWrapper loopUntilHeaders = mock(CellWrapper.class);
        CellWrapper loopUntilCondition = mock(CellWrapper.class);

        LoopUntilRestFixture fixture = new LoopUntilRestFixture();
        fixture = spy(fixture);
        doReturn(hasTheUntilResponseBeenMet).when(fixture).hasTheUntilResponseBeenMet(loopUntilResponseCode);
        doReturn(hasTheUntilHeadersBeenMet).when(fixture).hasTheUntilHeadersBeenMet(loopUntilHeaders);
        doReturn(hasTheUntilConditionBeenMet).when(fixture).hasTheUntilBodyBeenMet(loopUntilCondition);

        // Test
        boolean actual = fixture.hasTheUntilBeenMet(loopUntilResponseCode, loopUntilHeaders, loopUntilCondition);

        // Validate
        assertTrue("Return true if all conditions are met", actual);
    }

    @SuppressWarnings("rawtypes")
    @Test
    public final void testHasTheUntilBeenMetResponseDoesNotMatch()
    {
        // Setup
        final boolean hasTheUntilResponseBeenMet = false;
        final boolean hasTheUntilHeadersBeenMet = true;
        final boolean hasTheUntilConditionBeenMet = true;

        CellWrapper loopUntilResponseCode = mock(CellWrapper.class);
        CellWrapper loopUntilHeaders = mock(CellWrapper.class);
        CellWrapper loopUntilCondition = mock(CellWrapper.class);

        LoopUntilRestFixture fixture = new LoopUntilRestFixture();
        fixture = spy(fixture);
        doReturn(hasTheUntilResponseBeenMet).when(fixture).hasTheUntilResponseBeenMet(loopUntilResponseCode);
        doReturn(hasTheUntilHeadersBeenMet).when(fixture).hasTheUntilHeadersBeenMet(loopUntilHeaders);
        doReturn(hasTheUntilConditionBeenMet).when(fixture).hasTheUntilBodyBeenMet(loopUntilCondition);

        // Test
        boolean actual = fixture.hasTheUntilBeenMet(loopUntilResponseCode, loopUntilHeaders, loopUntilCondition);

        // Validate
        assertFalse("All conditions must be true", actual);
    }

    @SuppressWarnings("rawtypes")
    @Test
    public final void testHasTheUntilBeenMetHeadersDoesNotMatch()
    {
        // Setup
        final boolean hasTheUntilResponseBeenMet = true;
        final boolean hasTheUntilHeadersBeenMet = false;
        final boolean hasTheUntilConditionBeenMet = true;

        CellWrapper loopUntilResponseCode = mock(CellWrapper.class);
        CellWrapper loopUntilHeaders = mock(CellWrapper.class);
        CellWrapper loopUntilCondition = mock(CellWrapper.class);

        LoopUntilRestFixture fixture = new LoopUntilRestFixture();
        fixture = spy(fixture);
        doReturn(hasTheUntilResponseBeenMet).when(fixture).hasTheUntilResponseBeenMet(loopUntilResponseCode);
        doReturn(hasTheUntilHeadersBeenMet).when(fixture).hasTheUntilHeadersBeenMet(loopUntilHeaders);
        doReturn(hasTheUntilConditionBeenMet).when(fixture).hasTheUntilBodyBeenMet(loopUntilCondition);

        // Test
        boolean actual = fixture.hasTheUntilBeenMet(loopUntilResponseCode, loopUntilHeaders, loopUntilCondition);

        // Validate
        assertFalse("All conditions must be true", actual);
    }

    @SuppressWarnings("rawtypes")
    @Test
    public final void testHasTheUntilBeenMetBodyDoesNotMatch()
    {
        // Setup
        final boolean hasTheUntilResponseBeenMet = true;
        final boolean hasTheUntilHeadersBeenMet = true;
        final boolean hasTheUntilConditionBeenMet = false;

        CellWrapper loopUntilResponseCode = mock(CellWrapper.class);
        CellWrapper loopUntilHeaders = mock(CellWrapper.class);
        CellWrapper loopUntilCondition = mock(CellWrapper.class);

        LoopUntilRestFixture fixture = new LoopUntilRestFixture();
        fixture = spy(fixture);
        doReturn(hasTheUntilResponseBeenMet).when(fixture).hasTheUntilResponseBeenMet(loopUntilResponseCode);
        doReturn(hasTheUntilHeadersBeenMet).when(fixture).hasTheUntilHeadersBeenMet(loopUntilHeaders);
        doReturn(hasTheUntilConditionBeenMet).when(fixture).hasTheUntilBodyBeenMet(loopUntilCondition);

        // Test
        boolean actual = fixture.hasTheUntilBeenMet(loopUntilResponseCode, loopUntilHeaders, loopUntilCondition);

        // Validate
        assertFalse("All conditions must be true", actual);
    }

    @SuppressWarnings("rawtypes")
    @Test
    public final void testHasTheUntilResponseBeenMet()
    {
        // Setup
        CellWrapper loopUntilResponseCode = mock(CellWrapper.class);
        String lastStatusCode = "Test Status Code";

        LoopUntilRestFixture fixture = new LoopUntilRestFixture();
        fixture = spy(fixture);
        doReturn(lastStatusCode).when(fixture).getLastResponseStatusCode();
        doReturn(true).when(fixture).doesResponsePass(eq(loopUntilResponseCode), eq(lastStatusCode), any(StatusCodeTypeAdapter.class));

        // Test
        boolean actual = fixture.hasTheUntilResponseBeenMet(loopUntilResponseCode);

        // Validate
        assertTrue("Should return value from doesResponsePass()", actual);
        verify(fixture, times(1)).doesResponsePass(any(CellWrapper.class), any(Object.class), any(RestDataTypeAdapter.class));
        verify(fixture, times(1)).doesResponsePass(eq(loopUntilResponseCode), eq(lastStatusCode), any(StatusCodeTypeAdapter.class));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public final void testHasTheUntilHeadersBeenMet()
    {
        // Setup
        CellWrapper loopUntilHeaders = mock(CellWrapper.class);
        List<Header> lastHeaders = mock(List.class);

        RestResponse lastResponse = mock(RestResponse.class);
        when(lastResponse.getHeaders()).thenReturn(lastHeaders);

        LoopUntilRestFixture fixture = new LoopUntilRestFixture();
        fixture = spy(fixture);
        doReturn(lastResponse).when(fixture).getLastResponse();
        doReturn(true).when(fixture).doesResponsePass(eq(loopUntilHeaders), eq(lastHeaders), any(HeadersTypeAdapter.class));

        // Test
        boolean actual = fixture.hasTheUntilHeadersBeenMet(loopUntilHeaders);

        // Validate
        assertTrue("Should return value from doesResponsePass()", actual);
        verify(fixture, times(1)).doesResponsePass(any(CellWrapper.class), any(Object.class), any(RestDataTypeAdapter.class));
        verify(fixture, times(1)).doesResponsePass(eq(loopUntilHeaders), eq(lastHeaders), any(HeadersTypeAdapter.class));
    }

    @SuppressWarnings("rawtypes")
    @Test
    public final void testHasTheUntilConditionBeenMetBinaryResponse()
    {
        // Setup
        CellWrapper untilCondition = mock(CellWrapper.class);
        BodyTypeAdapter bodyTypeAdapter = mock(BodyTypeAdapter.class);

        byte[] actualResponse = new byte[]{};

        RestResponse lastResponse = mock(RestResponse.class);
        when(lastResponse.getRawBody()).thenReturn(actualResponse);

        when(bodyTypeAdapter.isTextResponse()).thenReturn(false);

        LoopUntilRestFixture fixture = new LoopUntilRestFixture();
        fixture = spy(fixture);
        doReturn(bodyTypeAdapter).when(fixture).createBodyTypeAdapter();
        doReturn(lastResponse).when(fixture).getLastResponse();
        doReturn(true).when(fixture).doesResponsePass(untilCondition, actualResponse, bodyTypeAdapter);

        // Test
        boolean actual = fixture.hasTheUntilBodyBeenMet(untilCondition);

        // Validate
        assertTrue("Should return value from doesResponsePass()", actual);
        verify(fixture, times(1)).doesResponsePass(any(CellWrapper.class), any(Object.class), any(RestDataTypeAdapter.class));
        verify(fixture, times(1)).doesResponsePass(untilCondition, actualResponse, bodyTypeAdapter);
    }

    @SuppressWarnings("rawtypes")
    @Test
    public final void testHasTheUntilConditionBeenMetTextResponse()
    {
        // Setup
        CellWrapper untilCondition = mock(CellWrapper.class);
        BodyTypeAdapter bodyTypeAdapter = mock(BodyTypeAdapter.class);
        String actualResponse = "Test Body";

        when(bodyTypeAdapter.isTextResponse()).thenReturn(true);

        LoopUntilRestFixture fixture = new LoopUntilRestFixture();
        fixture = spy(fixture);
        doReturn(bodyTypeAdapter).when(fixture).createBodyTypeAdapter();
        doReturn(actualResponse).when(fixture).getLastResponseBody();
        doReturn(true).when(fixture).doesResponsePass(untilCondition, actualResponse, bodyTypeAdapter);

        // Test
        boolean actual = fixture.hasTheUntilBodyBeenMet(untilCondition);

        // Validate
        assertTrue("Should return value from doesResponsePass()", actual);
        verify(fixture, times(1)).doesResponsePass(any(CellWrapper.class), any(Object.class), any(BodyTypeAdapter.class));
        verify(fixture, times(1)).doesResponsePass(untilCondition, actualResponse, bodyTypeAdapter);
    }

    @SuppressWarnings("rawtypes")
    @Test
    public final void testDoesResponsePassSkipBlankBody() throws Exception
    {
        // Setup
        CellWrapper expectedResponse = mock(CellWrapper.class);
        Object actualResponse = mock(Object.class);
        RestDataTypeAdapter responseEvaluator = mock(RestDataTypeAdapter.class);

        when(expectedResponse.body()).thenReturn("       ");

        LoopUntilRestFixture fixture = new LoopUntilRestFixture();
        fixture = spy(fixture);
        doReturn(true).when(fixture).evaluateExpected(expectedResponse, actualResponse, responseEvaluator);

        // Test
        boolean actual = fixture.doesResponsePass(expectedResponse, actualResponse, responseEvaluator);

        // Validate
        assertFalse("Should return false when skipping", actual);
        verify(fixture, times(0)).evaluateExpected(any(CellWrapper.class), any(Object.class), any(RestDataTypeAdapter.class));
    }

    @SuppressWarnings("rawtypes")
    @Test
    public final void testDoesResponsePass() throws Exception
    {
        // Setup
        CellWrapper expectedResponse = mock(CellWrapper.class);
        Object actualResponse = mock(Object.class);
        RestDataTypeAdapter responseEvaluator = mock(RestDataTypeAdapter.class);

        when(expectedResponse.body()).thenReturn("Test Body");

        LoopUntilRestFixture fixture = new LoopUntilRestFixture();
        fixture = spy(fixture);
        doReturn(true).when(fixture).evaluateExpected(expectedResponse, actualResponse, responseEvaluator);

        // Test
        boolean actual = fixture.doesResponsePass(expectedResponse, actualResponse, responseEvaluator);

        // Validate
        assertTrue("Should return the value from evaluteExpected()", actual);
        verify(fixture, times(1)).evaluateExpected(any(CellWrapper.class), any(Object.class), any(RestDataTypeAdapter.class));
        verify(fixture, times(1)).evaluateExpected(expectedResponse, actualResponse, responseEvaluator);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public final void testDoesResponsePassEvaluationFails() throws Exception
    {
        // Setup
        CellWrapper expectedResponse = mock(CellWrapper.class);
        Object actualResponse = mock(Object.class);
        RestDataTypeAdapter responseEvaluator = mock(RestDataTypeAdapter.class);

        when(expectedResponse.body()).thenReturn("Test Body");

        Exception exception = mock(Exception.class);
        CellFormatter formatter = mock(CellFormatter.class);

        LoopUntilRestFixture fixture = new LoopUntilRestFixture();
        fixture = spy(fixture);
        doReturn(formatter).when(fixture).getFormatter();
        doThrow(exception).when(fixture).evaluateExpected(expectedResponse, actualResponse, responseEvaluator);

        // Test
        boolean actual = fixture.doesResponsePass(expectedResponse, actualResponse, responseEvaluator);

        // Validate
        assertFalse("Should false when evaluteExpected() fails", actual);
        verify(formatter).exception(expectedResponse, exception);
        verify(fixture, times(1)).evaluateExpected(any(CellWrapper.class), any(Object.class), any(RestDataTypeAdapter.class));
        verify(fixture, times(1)).evaluateExpected(expectedResponse, actualResponse, responseEvaluator);
    }

    @SuppressWarnings("rawtypes")
    @Test
    public final void testPerformLoopAction()
    {
        // Setup
        final LoopUntilRestFixture fixture = spy(new LoopUntilRestFixture());

        Answer checkLoopStatus = new Answer()
        {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable
            {
                assertTrue(
                    "The loop flag should be set during processing",
                    fixture.currentlyPerformingLoopAction);
                return null;
            }
        };

        RowShiftWrapper loopActionRow = mock(RowShiftWrapper.class);

        doAnswer(checkLoopStatus).when(fixture).processRow(loopActionRow);

        assertFalse(
            "The loop flag should not be set before processing",
            fixture.currentlyPerformingLoopAction);

        // Test
        fixture.performLoopAction(loopActionRow);

        // Validate
        assertFalse(
            "The loop flag should not be set after processing",
            fixture.currentlyPerformingLoopAction);
        verify(fixture).processRow(loopActionRow);
    }

    @Test
    public final void testPauseBeforeNextLoopActionNoPause()
    {
        // Setup
        final long noPause = -1;

        LoopUntilRestFixture fixture = new LoopUntilRestFixture();
        fixture.milliSecondsDelayPerLoop = noPause;

        final long beforePause = System.currentTimeMillis();

        // Test
        fixture.pauseBeforeNextLoopAction();

        // Validate
        final long afterPause = System.currentTimeMillis();
        final long expectedLengthOfPause = 5;
        final long actualLengthOfPause = (afterPause - beforePause);
        assertTrue(
            "Consider anything under 5 milliseconds to be no pause, in case the thread looses the cpu",
            expectedLengthOfPause >= actualLengthOfPause);
    }

//    @Test
//    public final void testPauseBeforeNextLoopActionPause()
//    {
//        // Setup
//        final long expectedLengthOfPause = 25;
//
//        LoopUntilRestFixture fixture = new LoopUntilRestFixture();
//        fixture.milliSecondsDelayPerLoop = expectedLengthOfPause;
//
//        final long beforePause = System.currentTimeMillis();
//
//        // Test
//        fixture.pauseBeforeNextLoopAction();
//
//        // Validate
//        final long afterPause = System.currentTimeMillis();
//        final long actualLengthOfPause = (afterPause - beforePause);
//        assertTrue("Did not pause for long enough, Expected: " + expectedLengthOfPause +
//                   "  Actual: " + actualLengthOfPause, expectedLengthOfPause <= actualLengthOfPause);
//    }

    @SuppressWarnings({ "rawtypes" })
    @Test
    public final void testGetFormatterCurrentlyLooping()
    {
        // Setup
        LoopUntilRestFixture fixture = new LoopUntilRestFixture();
        fixture.currentlyPerformingLoopAction = true;

        LoopResultsCellFormatter loopFormatter = mock(LoopResultsCellFormatter.class);

        fixture = spy(fixture);
        when(fixture.getLoopFormatter()).thenReturn(loopFormatter);

        // Test
        CellFormatter actual = fixture.getFormatter();

        // Validate
        assertSame("Should have returned the loop formatter", loopFormatter, actual);
    }

    @SuppressWarnings({ "rawtypes" })
    @Test
    public final void testGetFormatterNotLooping()
    {
        // Setup
        LoopUntilRestFixture fixture = new LoopUntilRestFixture();
        fixture.currentlyPerformingLoopAction = false;

        LoopResultsCellFormatter loopFormatter = mock(LoopResultsCellFormatter.class);

        fixture = spy(fixture);
        when(fixture.getLoopFormatter()).thenReturn(loopFormatter);

        // Test
        CellFormatter actual = fixture.getFormatter();

        // Validate
        assertNotSame("Should NOT have returned the loop formatter", loopFormatter, actual);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public final void testGetLoopFormatter()
    {
        // Setup
        CellFormatter formatter = mock(CellFormatter.class);

        LoopUntilRestFixture fixture = new LoopUntilRestFixture();
        fixture = spy(fixture);
        when(fixture.getFormatter()).thenReturn(formatter);

        // Test
        LoopResultsCellFormatter actual = fixture.getLoopFormatter();

        // Validate
        assertNotNull("Formatter must never be null", actual);
        assertSame("Should always return the same formatter", actual, fixture.getLoopFormatter());
    }

}
