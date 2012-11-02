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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import smartrics.rest.fitnesse.fixture.loopsupport.LoopResultsCellFormatter.AggregateLoopResults;
import smartrics.rest.fitnesse.fixture.support.CellFormatter;
import smartrics.rest.fitnesse.fixture.support.CellWrapper;
import smartrics.rest.fitnesse.fixture.support.RestDataTypeAdapter;

/**
 * @author Adam Roberts (aroberts@alum.rit.edu)
 */
public class LoopResultsCellFormatterTest
{

    final int collapseLength = 50;
    @Mock CellFormatter<Object> mockFormatter;
    @Mock PrintStream errorOutput;

    @Before
    public void setUp() throws Exception
    {
        MockitoAnnotations.initMocks(this);

        when(mockFormatter.getMinLenghtForToggleCollapse()).thenReturn(collapseLength);
        when(mockFormatter.isDisplayActual()).thenReturn(true);

        System.setErr(errorOutput);
    }

    @After
    public void teardown()
    {
        System.setErr(null);
    }

    @Test
    public final void testHaveAllTestsPassedNoTestsShouldPass()
    {
        // Setup
        LoopResultsCellFormatter<Object> formatter = new LoopResultsCellFormatter<Object>(mockFormatter);

        // Test & Validate
        assertTrue("If nothing has been tested, then all tests passed", formatter.haveAllTestsPassed());
    }

    @SuppressWarnings("unchecked")
    @Test
    public final void testHaveAllTestsPassedShouldPass()
    {
        // Setup
        LoopResultsCellFormatter<Object> formatter = new LoopResultsCellFormatter<Object>(mockFormatter);

        formatter.right(mock(CellWrapper.class), "Passed1");
        formatter.right(mock(CellWrapper.class), "Passed2");

        // Test & Validate
        assertTrue("If only 'right' has been called, then all tests passed", formatter.haveAllTestsPassed());
    }

    @SuppressWarnings("unchecked")
    @Test
    public final void testHaveAllTestsPassedHasWrongShouldFail()
    {
        // Setup
        LoopResultsCellFormatter<Object> formatter = new LoopResultsCellFormatter<Object>(mockFormatter);

        formatter.right(mock(CellWrapper.class), "Passed1");
        formatter.wrong(mock(CellWrapper.class), "Passed2");
        formatter.right(mock(CellWrapper.class), "Passed2");

        // Test & Validate
        assertFalse("If even one 'wrong' has been called, then all tests have not passed", formatter.haveAllTestsPassed());
    }

    @SuppressWarnings("unchecked")
    @Test
    public final void testHaveAllTestsPassedHasExceptionShouldFail()
    {
        // Setup
        LoopResultsCellFormatter<Object> formatter = new LoopResultsCellFormatter<Object>(mockFormatter);

        formatter.right(mock(CellWrapper.class), "Passed1");
        formatter.exception(mock(CellWrapper.class), "Passed2");
        formatter.right(mock(CellWrapper.class), "Passed2");

        // Test & Validate
        assertFalse("If even one 'exception' has been called, then all tests have not passed", formatter.haveAllTestsPassed());
    }

    @SuppressWarnings("unchecked")
    @Test
    public final void testHaveAllTestsPassedHasWrongAndExceptionShouldFail()
    {
        // Setup
        LoopResultsCellFormatter<Object> formatter = new LoopResultsCellFormatter<Object>(mockFormatter);

        formatter.right(mock(CellWrapper.class), "Passed1");
        formatter.wrong(mock(CellWrapper.class), "Passed2");
        formatter.right(mock(CellWrapper.class), "Passed2");
        formatter.exception(mock(CellWrapper.class), "Passed2");
        formatter.right(mock(CellWrapper.class), "Passed2");

        // Test & Validate
        assertFalse("If even one 'wrong' or one 'exception' has been called, then all tests have not passed", formatter.haveAllTestsPassed());
    }

    @SuppressWarnings("unchecked")
    @Test
    public final void testResetResults()
    {
        // Setup
        LoopResultsCellFormatter<Object> formatter = new LoopResultsCellFormatter<Object>(mockFormatter);

        CellWrapper<Object> cellWrapper = mock(CellWrapper.class);
        LoopResultsCellFormatter<Object>.AggregateLoopResults preResetValue = formatter.getLoopResults(cellWrapper);

        // Test
        formatter.resetResults();

        // Validate
        assertNotSame(
            "After reset there should be a new results object",
            preResetValue,
            formatter.getLoopResults(cellWrapper));
    }

    @SuppressWarnings("unchecked")
    @Test
    public final void testAddResultsToTheCurrentRowNoCellResults()
    {
        // Setup
        LoopResultsCellFormatter<Object> formatter = new LoopResultsCellFormatter<Object>(mockFormatter);
        formatter = spy(formatter);

        // Test
        formatter.addResultsToTheCurrentRow(true);

        // Validate
        verify(formatter, times(0)).setCellResultAsIgnored(any(CellWrapper.class), any(AggregateLoopResults.class));
        verify(formatter, times(0)).setCellResultAsFailedWithException(any(CellWrapper.class), any(AggregateLoopResults.class));
        verify(formatter, times(0)).setCellResultsAsFailed(any(CellWrapper.class), any(AggregateLoopResults.class));
        verify(formatter, times(0)).setCellResultAsPassed(any(CellWrapper.class), any(AggregateLoopResults.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    public final void testAddResultsToTheCurrentRowContinueIfLoopFails()
    {
        // Setup
        LoopResultsCellFormatter<Object> formatter = new LoopResultsCellFormatter<Object>(mockFormatter);

        CellWrapper<Object> cellWrapperPassed = mock(CellWrapper.class);
        CellWrapper<Object> cellWrapperFailed = mock(CellWrapper.class);
        CellWrapper<Object> cellWrapperException = mock(CellWrapper.class);

        formatter.right(cellWrapperPassed, "");
        formatter.wrong(cellWrapperFailed, "");
        formatter.exception(cellWrapperException, "");

        formatter = spy(formatter);
        doNothing().when(formatter).setCellResultAsIgnored(any(CellWrapper.class), any(AggregateLoopResults.class));

        // Test
        formatter.addResultsToTheCurrentRow(true);

        // Validate
        verify(formatter, times(3)).setCellResultAsIgnored(any(CellWrapper.class), any(AggregateLoopResults.class));
        verify(formatter, times(1)).setCellResultAsIgnored(eq(cellWrapperPassed), any(AggregateLoopResults.class));
        verify(formatter, times(1)).setCellResultAsIgnored(eq(cellWrapperFailed), any(AggregateLoopResults.class));
        verify(formatter, times(1)).setCellResultAsIgnored(eq(cellWrapperException), any(AggregateLoopResults.class));
        verify(formatter, times(0)).setCellResultAsFailedWithException(any(CellWrapper.class), any(AggregateLoopResults.class));
        verify(formatter, times(0)).setCellResultsAsFailed(any(CellWrapper.class), any(AggregateLoopResults.class));
        verify(formatter, times(0)).setCellResultAsPassed(any(CellWrapper.class), any(AggregateLoopResults.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    public final void testAddResultsToTheCurrentRow()
    {
        // Setup
        LoopResultsCellFormatter<Object> formatter = new LoopResultsCellFormatter<Object>(mockFormatter);

        CellWrapper<Object> cellWrapperPassed = mock(CellWrapper.class);
        CellWrapper<Object> cellWrapperFailed = mock(CellWrapper.class);
        CellWrapper<Object> cellWrapperException = mock(CellWrapper.class);

        formatter.right(cellWrapperPassed, "");
        formatter.wrong(cellWrapperFailed, "");
        formatter.exception(cellWrapperException, "");

        formatter = spy(formatter);
        doNothing().when(formatter).setCellResultAsFailedWithException(any(CellWrapper.class), any(AggregateLoopResults.class));
        doNothing().when(formatter).setCellResultsAsFailed(any(CellWrapper.class), any(AggregateLoopResults.class));
        doNothing().when(formatter).setCellResultAsPassed(any(CellWrapper.class), any(AggregateLoopResults.class));

        // Test
        formatter.addResultsToTheCurrentRow(false);

        // Validate
        verify(formatter, times(0)).setCellResultAsIgnored(any(CellWrapper.class), any(AggregateLoopResults.class));
        verify(formatter, times(1)).setCellResultAsFailedWithException(any(CellWrapper.class), any(AggregateLoopResults.class));
        verify(formatter, times(1)).setCellResultAsFailedWithException(eq(cellWrapperException), any(AggregateLoopResults.class));
        verify(formatter, times(1)).setCellResultsAsFailed(any(CellWrapper.class), any(AggregateLoopResults.class));
        verify(formatter, times(1)).setCellResultsAsFailed(eq(cellWrapperFailed), any(AggregateLoopResults.class));
        verify(formatter, times(1)).setCellResultAsPassed(any(CellWrapper.class), any(AggregateLoopResults.class));
        verify(formatter, times(1)).setCellResultAsPassed(eq(cellWrapperPassed), any(AggregateLoopResults.class));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public final void testSetCellResultAsIgnored()
    {
        // Setup
        LoopResultsCellFormatter<Object> formatter = new LoopResultsCellFormatter<Object>(mockFormatter);

        CellWrapper<Object> cellWrapper = mock(CellWrapper.class);

        final String loopResultStr = "TestLoopResults";
        AggregateLoopResults loopResults = mock(AggregateLoopResults.class);
        when(loopResults.toString()).thenReturn(loopResultStr);

        final String greyCellBody = "Expected Formatted Result";
        when(mockFormatter.gray(loopResultStr)).thenReturn(greyCellBody);

        // Test
        formatter.setCellResultAsIgnored(cellWrapper, loopResults);

        // Validate
        verify(mockFormatter).gray(loopResultStr);
        verify(cellWrapper).addToBody(greyCellBody);
    }

    /**
     * This test is a mess because mockito does not have the capabilities to mock the static
     * access to Tools, so must use an Answer class to verify the behavior
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public final void testSetCellResultAsFailedWithException()
    {
        // Setup
        LoopResultsCellFormatter<Object> formatter = new LoopResultsCellFormatter<Object>(mockFormatter);

        final String cellBody = "TestCellBody";
        CellWrapper<Object> cellWrapper = mock(CellWrapper.class);
        when(cellWrapper.body()).thenReturn(cellBody);

        final String loopResultStr = "TestLoopResults";
        final String exceptionMsgStr = "TestErrorMsg";
        final String failureMsgStr = "TestFailureMsg";

        AggregateLoopResults loopResults = spy(formatter.new AggregateLoopResults());
        when(loopResults.toString()).thenReturn(loopResultStr);
        loopResults.exceptions.add(exceptionMsgStr);
        loopResults.wrong.add(failureMsgStr);

        Answer answer = new Answer()
        {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable
            {
                String newMsg = invocation.getArguments()[1].toString();
                assertFalse("Cell body is not displayed on exceptions", newMsg.contains(cellBody));
                assertTrue("Missing the loop results", newMsg.contains(loopResultStr));
                assertTrue("Missing the Error Msg", newMsg.contains(exceptionMsgStr));
                assertTrue("Missing the Failure Msg", newMsg.contains(failureMsgStr));
                return null;
            }
        };
        doAnswer(answer).when(mockFormatter).exception(eq(cellWrapper), anyString());

        // Test
        formatter.setCellResultAsFailedWithException(cellWrapper, loopResults);

        // Validate
        verify(mockFormatter).exception(eq(cellWrapper), anyString());
    }

    /**
     * This test is a mess because mockito does not have the capabilities to mock the static
     * access to Tools, so must use an Answer class to verify the behavior
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public final void testSetCellResultsAsFailed()
    {
        // Setup
        LoopResultsCellFormatter<Object> formatter = new LoopResultsCellFormatter<Object>(mockFormatter);

        final String cellBody = "TestCellBody";
        CellWrapper<Object> cellWrapper = mock(CellWrapper.class);
        when(cellWrapper.body()).thenReturn(cellBody);

        final String loopResultStr = "TestLoopResults";
        final String exceptionMsgStr = "TestErrorMsg";
        final String failureMsgStr = "TestFailureMsg";

        AggregateLoopResults loopResults = spy(formatter.new AggregateLoopResults());
        when(loopResults.toString()).thenReturn(loopResultStr);
        loopResults.exceptions.add(exceptionMsgStr);
        loopResults.wrong.add(failureMsgStr);

        Answer answer = new Answer()
        {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable
            {
                String newMsg = invocation.getArguments()[1].toString();
                assertTrue("Missing the cell body", newMsg.contains(cellBody));
                assertTrue("Missing the loop results", newMsg.contains(loopResultStr));
                assertFalse("Error Msg are not handled", newMsg.contains(exceptionMsgStr));
                assertTrue("Missing the Failure Msg", newMsg.contains(failureMsgStr));
                return null;
            }
        };
        doAnswer(answer).when(mockFormatter).wrong(eq(cellWrapper), anyString());

        // Test
        formatter.setCellResultsAsFailed(cellWrapper, loopResults);

        // Validate
        verify(mockFormatter).wrong(eq(cellWrapper), anyString());
    }

    /**
     * This test is a mess because mockito does not have the capabilities to mock the static
     * access to Tools, so must use an Answer class to verify the behavior
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public final void testSetCellResultAsPassed()
    {
        // Setup
        final String cellBody = "TestCellBody";
        CellWrapper<Object> cellWrapper = mock(CellWrapper.class);
        when(cellWrapper.body()).thenReturn(cellBody);

        final String loopResultStr = "TestLoopResults";
        AggregateLoopResults loopResults = mock(AggregateLoopResults.class);
        when(loopResults.toString()).thenReturn(loopResultStr);

        Answer answer = new Answer()
        {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable
            {
                String newMsg = invocation.getArguments()[1].toString();
                assertTrue("Missing the cell body", newMsg.contains(cellBody));
                assertTrue("Missing the loop results", newMsg.contains(loopResultStr));
                return null;
            }
        };
        doAnswer(answer).when(mockFormatter).right(eq(cellWrapper), anyString());

        LoopResultsCellFormatter<Object> formatter = new LoopResultsCellFormatter<Object>(mockFormatter);

        // Test
        formatter.setCellResultAsPassed(cellWrapper, loopResults);

        // Validate
        verify(mockFormatter).right(eq(cellWrapper), anyString());
    }

    @SuppressWarnings("unchecked")
    @Test
    public final void testGetLoopResultsCellWrapperNotCurrentlyInMap()
    {
        // Setup
        LoopResultsCellFormatter<Object> formatter = new LoopResultsCellFormatter<Object>(mockFormatter);

        CellWrapper<Object> cellWrapper = mock(CellWrapper.class);

        // Test
        LoopResultsCellFormatter<Object>.AggregateLoopResults actual = formatter.getLoopResults(cellWrapper);

        // Validate
        assertNotNull("AggregateLoopResults should be created if doesn't already exist", actual);
    }

    @SuppressWarnings("unchecked")
    @Test
    public final void testGetLoopResultsCellWrapperAlreadyInMap()
    {
        // Setup
        LoopResultsCellFormatter<Object> formatter = new LoopResultsCellFormatter<Object>(mockFormatter);

        CellWrapper<Object> cellWrapper = mock(CellWrapper.class);
        LoopResultsCellFormatter<Object>.AggregateLoopResults expected = formatter.getLoopResults(cellWrapper);

        // Test
        LoopResultsCellFormatter<Object>.AggregateLoopResults actual = formatter.getLoopResults(cellWrapper);

        // Validate
        assertSame("Two requests with the same key should return the same object", expected, actual);
    }

    @Test
    public final void testGetLoopResults()
    {
        // Setup
        LoopResultsCellFormatter<Object> formatter = new LoopResultsCellFormatter<Object>(mockFormatter);

        // Test & Validate
        assertNotNull("Loop Results must never be null", formatter.getLoopResults());
    }

    @SuppressWarnings("unchecked")
    @Test
    public final void testExceptionCellWrapperOfEThrowable()
    {
        // Setup
        CellWrapper<Object> expected = mock(CellWrapper.class);
        when(expected.body()).thenReturn("Body Text");

        String exceptionMessage = "Test Message";
        List<String> exceptionList = new ArrayList<String>();
        exceptionList.add(exceptionMessage);

        Throwable exception = mock(Throwable.class);
        when(exception.getMessage()).thenReturn(exceptionMessage);

        LoopResultsCellFormatter<Object> formatter = new LoopResultsCellFormatter<Object>(mockFormatter);
        LoopResultsCellFormatter<Object>.AggregateLoopResults loopResults = formatter.new AggregateLoopResults();
        assertEquals("Confirm that the results start empty", 0, loopResults.right);

        formatter = spy(formatter);
        doReturn(loopResults).when(formatter).getLoopResults(expected);

        // Test
        formatter.exception(expected, exception);

        // Validate
        assertEquals("Loop Results should have 'right' at zero", 0, loopResults.right);
        assertEquals("Loop Results should have 'wrong' at zero", 0, loopResults.wrong.size());
        assertEquals("Loop Results should have 'errors' incremented", 1, loopResults.exceptions.size());
        assertEquals("Loop 'errors' should have matched", exceptionList, loopResults.exceptions);
        verify(errorOutput).println(exceptionMessage);
        verify(errorOutput).println("Body Text");
        verify(exception).printStackTrace(errorOutput);
    }

    @SuppressWarnings("unchecked")
    @Test
    public final void testExceptionCellWrapperOfEString()
    {
        // Setup
        CellWrapper<Object> expected = mock(CellWrapper.class);
        when(expected.body()).thenReturn("Body Text");

        String exceptionMessage = "Test Message";
        List<String> exceptionList = new ArrayList<String>();
        exceptionList.add(exceptionMessage);

        LoopResultsCellFormatter<Object> formatter = new LoopResultsCellFormatter<Object>(mockFormatter);
        LoopResultsCellFormatter<Object>.AggregateLoopResults loopResults = formatter.new AggregateLoopResults();
        assertEquals("Confirm that the results start empty", 0, loopResults.right);

        formatter = spy(formatter);
        doReturn(loopResults).when(formatter).getLoopResults(expected);

        // Test
        formatter.exception(expected, exceptionMessage);

        // Validate
        assertEquals("Loop Results should have 'right' at zero", 0, loopResults.right);
        assertEquals("Loop Results should have 'wrong' at zero", 0, loopResults.wrong.size());
        assertEquals("Loop Results should have 'errors' incremented", 1, loopResults.exceptions.size());
        assertEquals("Loop 'errors' should have matched", exceptionList, loopResults.exceptions);
        verify(errorOutput).println(exceptionMessage);
        verify(errorOutput).println("Body Text");
    }

    @SuppressWarnings("unchecked")
    @Test
    public final void testWrongCellWrapperOfERestDataTypeAdapter()
    {
        // Setup
        List<String> errorList = new ArrayList<String>();
        errorList.add("Error1");
        errorList.add("Error2");
        errorList.add("Error3");
        CellWrapper<Object> expected = mock(CellWrapper.class);
        when(expected.body()).thenReturn("Body Text");
        RestDataTypeAdapter typeAdapter = mock(RestDataTypeAdapter.class);
        when(typeAdapter.get()).thenReturn("Test Response Actual");
        when(typeAdapter.getErrors()).thenReturn(errorList);

        LoopResultsCellFormatter<Object> formatter = new LoopResultsCellFormatter<Object>(mockFormatter);
        LoopResultsCellFormatter<Object>.AggregateLoopResults loopResults = formatter.new AggregateLoopResults();
        assertEquals("Confirm that the results start empty", 0, loopResults.right);

        formatter = spy(formatter);
        doReturn(loopResults).when(formatter).getLoopResults(expected);

        // Test
        formatter.wrong(expected, typeAdapter);

        // Validate
        assertEquals("Loop Results should have 'right' at zero", 0, loopResults.right);
        assertEquals("Loop Results should have 'wrong' incremented", 3, loopResults.wrong.size());
        assertEquals("Loop 'wrong' should have matched", errorList, loopResults.wrong);
        assertEquals("Loop Results should have 'errors' at zero", 0, loopResults.exceptions.size());
        verify(errorOutput).println("Error1");
        verify(errorOutput).println("Error2");
        verify(errorOutput).println("Error3");
        verify(errorOutput).println("Body Text");
        verify(errorOutput).println("Test Response Actual");
    }

    @SuppressWarnings("unchecked")
    @Test
    public final void testWrongCellWrapperOfEString()
    {
        // Setup
        CellWrapper<Object> expected = mock(CellWrapper.class);
        when(expected.body()).thenReturn("Body Text");

        String failureMessage = "Test Message";
        List<String> errorList = new ArrayList<String>();
        errorList.add(failureMessage);

        LoopResultsCellFormatter<Object> formatter = new LoopResultsCellFormatter<Object>(mockFormatter);
        LoopResultsCellFormatter<Object>.AggregateLoopResults loopResults = formatter.new AggregateLoopResults();
        assertEquals("Confirm that the results start empty", 0, loopResults.right);

        formatter = spy(formatter);
        doReturn(loopResults).when(formatter).getLoopResults(expected);

        // Test
        formatter.wrong(expected, failureMessage);

        // Validate
        assertEquals("Loop Results should have 'right' at zero", 0, loopResults.right);
        assertEquals("Loop Results should have 'wrong' incremented", 1, loopResults.wrong.size());
        assertEquals("Loop 'wrong' should have matched", errorList, loopResults.wrong);
        assertEquals("Loop Results should have 'errors' at zero", 0, loopResults.exceptions.size());
        verify(errorOutput).println(failureMessage);
        verify(errorOutput).println("Body Text");
    }

    @SuppressWarnings("unchecked")
    @Test
    public final void testRightCellWrapperOfERestDataTypeAdapter()
    {
        // Setup
        CellWrapper<Object> expected = mock(CellWrapper.class);
        RestDataTypeAdapter typeAdapter = mock(RestDataTypeAdapter.class);

        LoopResultsCellFormatter<Object> formatter = new LoopResultsCellFormatter<Object>(mockFormatter);
        LoopResultsCellFormatter<Object>.AggregateLoopResults loopResults = formatter.new AggregateLoopResults();
        assertEquals("Confirm that the results start empty", 0, loopResults.right);

        formatter = spy(formatter);
        doReturn(loopResults).when(formatter).getLoopResults(expected);

        // Test
        formatter.right(expected, typeAdapter);

        // Validate
        assertEquals("Loop Results should have 'right' incremented", 1, loopResults.right);
        assertEquals("Loop Results should have 'wrong' at zero", 0, loopResults.wrong.size());
        assertEquals("Loop Results should have 'errors' at zero", 0, loopResults.exceptions.size());
    }

    @SuppressWarnings("unchecked")
    @Test
    public final void testRightCellWrapperOfEString()
    {
        // Setup
        CellWrapper<Object> expected = mock(CellWrapper.class);
        String message = "Test Message";

        LoopResultsCellFormatter<Object> formatter = new LoopResultsCellFormatter<Object>(mockFormatter);
        LoopResultsCellFormatter<Object>.AggregateLoopResults loopResults = formatter.new AggregateLoopResults();
        assertEquals("Confirm that the results start empty", 0, loopResults.right);

        formatter = spy(formatter);
        doReturn(loopResults).when(formatter).getLoopResults(expected);

        // Test
        formatter.right(expected, message);

        // Validate
        assertEquals("Loop Results should have 'right' incremented", 1, loopResults.right);
        assertEquals("Loop Results should have 'wrong' at zero", 0, loopResults.wrong.size());
        assertEquals("Loop Results should have 'errors' at zero", 0, loopResults.exceptions.size());
    }

    @Test
    public final void testCheck()
    {
        // Setup
        @SuppressWarnings("unchecked")
        CellWrapper<Object> valueCell = mock(CellWrapper.class);
        RestDataTypeAdapter adapter = mock(RestDataTypeAdapter.class);

        LoopResultsCellFormatter<Object> formatter = new LoopResultsCellFormatter<Object>(mockFormatter);

        // Test
        formatter.check(valueCell, adapter);

        // Validate
        verify(mockFormatter).check(valueCell, adapter);
    }

    @Test
    public final void testLabel()
    {
        // Setup
        final String messageToFormat = "Test Msg";
        final String formattedMessage = "Formatted Test Msg";

        LoopResultsCellFormatter<Object> formatter = new LoopResultsCellFormatter<Object>(mockFormatter);
        when(mockFormatter.label(messageToFormat)).thenReturn(formattedMessage);

        // Test & Validate
        assertEquals("Failed to wrap the label method", formattedMessage, formatter.label(messageToFormat));
    }

    @Test
    public final void testGray()
    {
        // Setup
        final String messageToFormat = "Test Msg";
        final String formattedMessage = "Formatted Test Msg";

        LoopResultsCellFormatter<Object> formatter = new LoopResultsCellFormatter<Object>(mockFormatter);
        when(mockFormatter.gray(messageToFormat)).thenReturn(formattedMessage);

        // Test & Validate
        assertEquals("Failed to wrap the gray method", formattedMessage, formatter.gray(messageToFormat));
    }

    @Test
    public final void testAsLink()
    {
        // Setup
        @SuppressWarnings("unchecked")
        CellWrapper<Object> cell = mock(CellWrapper.class);
        String link = "link";
        String text = "text";

        LoopResultsCellFormatter<Object> formatter = new LoopResultsCellFormatter<Object>(mockFormatter);

        // Test
        formatter.asLink(cell, link, text);

        // Validate
        verify(mockFormatter).asLink(cell, link, text);
    }

    @Test
    public final void testSetDisplayActual()
    {
        // Setup
        boolean displayActual = true;

        LoopResultsCellFormatter<Object> formatter = new LoopResultsCellFormatter<Object>(mockFormatter);

        // Test
        formatter.setDisplayActual(displayActual);

        // Validate
        verify(mockFormatter).setDisplayActual(displayActual);
    }

    @Test
    public final void testSetMinLenghtForToggleCollapse()
    {
        // Setup
        int minLen = 47;

        LoopResultsCellFormatter<Object> formatter = new LoopResultsCellFormatter<Object>(mockFormatter);

        // Test
        formatter.setMinLenghtForToggleCollapse(minLen);

        // Validate
        verify(mockFormatter).setMinLenghtForToggleCollapse(minLen);
    }

    @Test
    public final void testGetMinLenghtForToggleCollapse()
    {
        // Setup
        int minLen = 47;

        LoopResultsCellFormatter<Object> formatter = new LoopResultsCellFormatter<Object>(mockFormatter);
        when(mockFormatter.getMinLenghtForToggleCollapse()).thenReturn(minLen);

        // Test & Validate
        assertEquals("Failed to wrap the getMinLenghtForToggleCollapse method", minLen, formatter.getMinLenghtForToggleCollapse());
    }

    @Test
    public final void testIsDisplayActual()
    {
        // Setup
        boolean displayActual = true;

        LoopResultsCellFormatter<Object> formatter = new LoopResultsCellFormatter<Object>(mockFormatter);
        when(mockFormatter.isDisplayActual()).thenReturn(displayActual);

        // Test & Validate
        assertEquals("Failed to wrap the isDisplayActual method", displayActual, formatter.isDisplayActual());
    }

    @Test
    public final void testFromRaw()
    {
        // Setup
        final String messageToFormat = "Test Msg";
        final String formattedMessage = "Formatted Test Msg";

        LoopResultsCellFormatter<Object> formatter = new LoopResultsCellFormatter<Object>(mockFormatter);
        when(mockFormatter.fromRaw(messageToFormat)).thenReturn(formattedMessage);

        // Test & Validate
        assertEquals("Failed to wrap the fromRaw method", formattedMessage, formatter.fromRaw(messageToFormat));
    }

}
