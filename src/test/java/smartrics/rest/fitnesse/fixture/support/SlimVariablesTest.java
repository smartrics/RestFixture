package smartrics.rest.fitnesse.fixture.support;

import fitnesse.slim.StatementExecutorInterface;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

/**
 * Created by 702161900 on 17/05/2016.
 */
public class SlimVariablesTest {

    private static final String NULL = "NULL";
    private SlimVariables variables;
    private StatementExecutorInterface exec;

    @Before
    public void setup() {
        Config c = Config.getConfig("TESTTEST");
        c.add("restfixture.null.value.representation", NULL);
        exec = mock(StatementExecutorInterface.class);
        variables = new SlimVariables(c, exec);
    }

    @Test
    public void unsetSymbolsReturnNull() {
        when(exec.getSymbol("n")).thenReturn(null);
        assertThat(variables.get("n"), is(equalTo(NULL)));
    }

    @Test
    public void setSymbolsAreReturned() {
        when(exec.getSymbol("x")).thenReturn("1");
        assertThat(variables.get("x"), is(equalTo("1")));
    }

    @Test
    public void setToNullAssignsNull() {
        variables.put("a", null);
        verify(exec).assign("a", null);
    }

    @Test
    public void setToNullRepresentationAssignsNull() {
        variables.put("a", NULL);
        verify(exec).assign("a", null);
    }

    @Test
    public void symbolsCanBeSet() {
        variables.put("a", "2");
        verify(exec).assign("a", "2");
        verifyNoMoreInteractions(exec);
    }

}