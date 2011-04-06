package smartrics.rest.fitnesse.fixture.support;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import smartrics.rest.fitnesse.fixture.SlimCell;

public class SlimCellTest {
    private SlimCell c;

    @Before
    public void setUp() {
        c = new SlimCell("content");
    }

    @Test
    public void testConstruction() {
        assertThat(c.body(), is(equalTo("content")));
    }

    @Test
    public void testAddBodyAppendsContentToExisting() {
        c.addToBody("_more");
        assertThat(c.body(), is(equalTo("content_more")));
    }

    @Test
    public void textAndBodyAreEquivalent() {
        assertThat(c.body(), is(equalTo(c.text())));
    }

    @Test
    public void shouldBeAbleToOverrideTheContent() {
        c.body("another");
        assertThat("another", is(equalTo(c.body())));
    }

    @Test
    public void wrappedObjectIsTheStringUsedForConstruction() {
        assertThat("content", is(equalTo(c.getWrapped())));
    }
}
