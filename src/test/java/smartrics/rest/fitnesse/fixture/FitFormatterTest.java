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
package smartrics.rest.fitnesse.fixture;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import smartrics.rest.fitnesse.fixture.support.RestDataTypeAdapter;
import smartrics.rest.fitnesse.fixture.support.StringTypeAdapter;
import fit.ActionFixture;
import fit.Parse;
import fit.exception.FitParseException;

public class FitFormatterTest {

    private ActionFixture mockDelegate = null;
    private FitFormatter formatter;
    private Parse dummyParse;
    private RestFixtureTestHelper helper;

    @Before
    public void setUp() throws FitParseException {
        mockDelegate = mock(ActionFixture.class);
        helper = new RestFixtureTestHelper();
        formatter = new FitFormatter();
        formatter.setActionFixtureDelegate(mockDelegate);
        // unless otherwise indicated this is the default - for simplicity
        formatter.setDisplayActual(false);
        dummyParse = helper.createSingleRowFitTable("some", "content");
        dummyParse.body = "somebody";
    }

    @Test
    public void delegatesCallToException() {
        Throwable exception = new Throwable();
        formatter.exception(new FitCell(dummyParse), exception);
        verify(mockDelegate).exception(dummyParse, exception);
        verifyNoMoreInteractions(mockDelegate);
    }

    @Test
    public void delegatesCallToCheck() {
        StringTypeAdapter ta = new StringTypeAdapter();
        formatter.check(new FitCell(dummyParse), ta);
        verify(mockDelegate).check(dummyParse, ta);
        verifyNoMoreInteractions(mockDelegate);
    }

    @Test
    public void fullyRendersWrong_noActualIfDisplayActualIsFalse_noErrorsDisplayedIfNone() {
        formatter.setDisplayActual(false);
        RestDataTypeAdapter typeAdapter = mock(RestDataTypeAdapter.class, "typeAdapter");
        when(typeAdapter.toString()).thenReturn("actual");
        List<String> errors = new ArrayList<String>();
        when(typeAdapter.getErrors()).thenReturn(errors);
        formatter.wrong(new FitCell(dummyParse), typeAdapter);

        verify(mockDelegate).wrong(dummyParse);
        verify(typeAdapter).getErrors();

        assertTrue("does not display expected content", dummyParse.body.indexOf("somebody") >= 0);
        assertFalse("does not display expected", dummyParse.body.indexOf("expected") >= 0);
        assertFalse("does display errors label", dummyParse.body.indexOf("errors") >= 0);
        assertFalse("does display actual", dummyParse.body.indexOf("actual") >= 0);

        verifyNoMoreInteractions(mockDelegate);
        verifyNoMoreInteractions(typeAdapter);
    }

    @Test
    public void fullyRendersWrong_withActualIfDisplayActualIsTrue() {
        formatter.setDisplayActual(true);
        RestDataTypeAdapter typeAdapter = mock(RestDataTypeAdapter.class, "typeAdapter");
        when(typeAdapter.toString()).thenReturn("the_content");
        List<String> errors = new ArrayList<String>();
        when(typeAdapter.getErrors()).thenReturn(errors);
        formatter.wrong(new FitCell(dummyParse), typeAdapter);

        verify(mockDelegate).wrong(dummyParse);
        verify(typeAdapter).getErrors();

        assertTrue("does not display expected content", dummyParse.body.indexOf("somebody") >= 0);
        assertTrue("does not display expected", dummyParse.body.indexOf("expected") >= 0);
        assertFalse("does display errors label", dummyParse.body.indexOf("errors") >= 0);
        assertTrue("does not display actual", dummyParse.body.indexOf("actual") >= 0);
        assertTrue("does not display actual content", dummyParse.body.indexOf("the_content") >= 0);

        verifyNoMoreInteractions(mockDelegate);
        verifyNoMoreInteractions(typeAdapter);
    }

    @Test
    public void fullyRendersRight_withActualIfDisplayActualIsTrue() {
        formatter.setDisplayActual(true);
        RestDataTypeAdapter typeAdapter = mock(RestDataTypeAdapter.class, "typeAdapter");
        when(typeAdapter.toString()).thenReturn("the_content");
        formatter.right(new FitCell(dummyParse), typeAdapter);

        verify(mockDelegate).right(dummyParse);

        assertTrue("does not display expected content", dummyParse.body.indexOf("somebody") >= 0);
        assertTrue("does not display expected", dummyParse.body.indexOf("expected") >= 0);
        assertTrue("does not display actual", dummyParse.body.indexOf("actual") >= 0);
        assertTrue("does not display actual content", dummyParse.body.indexOf("the_content") >= 0);

        verifyNoMoreInteractions(mockDelegate);
        verifyNoMoreInteractions(typeAdapter);
    }

    @Test
    public void fullyRendersRight_noActualIfDisplayActualIsFalse() {
        formatter.setDisplayActual(false);
        RestDataTypeAdapter typeAdapter = mock(RestDataTypeAdapter.class, "typeAdapter");
        when(typeAdapter.toString()).thenReturn("the_content");
        formatter.right(new FitCell(dummyParse), typeAdapter);

        verify(mockDelegate).right(dummyParse);

        assertTrue("does not display expected content", dummyParse.body.indexOf("somebody") >= 0);
        // expected label is only displayed if there's an actual
        assertFalse("does display expected label", dummyParse.body.indexOf("expected") >= 0);
        assertFalse("does display actual", dummyParse.body.indexOf("actual") >= 0);
        assertFalse("does display actual content", dummyParse.body.indexOf("the_content") >= 0);

        verifyNoMoreInteractions(mockDelegate);
        verifyNoMoreInteractions(typeAdapter);
    }

    @Test
    public void fullyRendersWrong_noActualIfDisplayActualIsFalse_errorsDisplayedIfAny() {
        formatter.setDisplayActual(false);
        RestDataTypeAdapter typeAdapter = mock(RestDataTypeAdapter.class, "typeAdapter");
        when(typeAdapter.toString()).thenReturn("actual");
        List<String> errors = new ArrayList<String>();
        errors.add("err1");
        when(typeAdapter.getErrors()).thenReturn(errors);
        formatter.wrong(new FitCell(dummyParse), typeAdapter);

        verify(mockDelegate).wrong(dummyParse);
        verify(typeAdapter).getErrors();

        assertTrue("does not display expected content", dummyParse.body.indexOf("somebody") >= 0);
        // expected label is only displayed if there's a actual
        assertFalse("does display expected", dummyParse.body.indexOf("expected") >= 0);
        assertTrue("does not display errors", dummyParse.body.indexOf("err1") >= 0);
        assertTrue("does not display errors label", dummyParse.body.indexOf("errors") >= 0);
        assertFalse("does display actual", dummyParse.body.indexOf("actual") >= 0);

        verifyNoMoreInteractions(mockDelegate);
        verifyNoMoreInteractions(typeAdapter);
    }

    @Test
    public void rendersTextAsHtmlLink() {
        FitCell cell = new FitCell(dummyParse);
        formatter.asLink(cell, "http://localhost", "http://localhost", "linked");
        assertThat(cell.body(), is(equalTo("<a href='http://localhost'>linked</a>")));
    }

    @Test
    public void fromRawIsSimplyABypassAsAllHandlingIsDoneByTheParse() {
        assertThat(formatter.fromRaw("a"), is(equalTo("a")));
    }

    @Test
    public void greyCellAreSpansWithAnAssignedClass() {
        assertThat(formatter.gray("area").trim(), is(equalTo("<span class=\"fit_grey\">area</span>")));
    }
}

