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

    @Before
    public void setUp() throws FitParseException {
        mockDelegate = mock(ActionFixture.class);
        formatter = new FitFormatter();
        formatter.setActionFixtureDelegate(mockDelegate);
        // unless otherwise indicated this is the default - for simplicity
        formatter.setDisplayActual(false);
        dummyParse = new Parse(FitTestSupport.createFitTestRow("some", "content"));
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
        formatter.asLink(cell, "http://localhost", "linked");
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

