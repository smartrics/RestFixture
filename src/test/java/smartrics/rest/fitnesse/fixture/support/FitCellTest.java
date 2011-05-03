package smartrics.rest.fitnesse.fixture.support;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import smartrics.rest.fitnesse.fixture.FitCell;
import smartrics.rest.fitnesse.fixture.RestFixtureTestHelper;
import fit.Parse;

public class FitCellTest {
    private FitCell c;

    @Before
    public void setUp() throws Exception {
        RestFixtureTestHelper helper = new RestFixtureTestHelper();
        Parse p = helper.createSingleRowFitTable("justone");
        c = new FitCell(p.parts.parts);
    }

    @Test
    public void testConstruction() {
        assertThat(c.body(), is(equalTo("justone")));
    }

    @Test
    public void testAddBodyAppendsContentToExisting() {
        c.addToBody("_more");
        assertThat(c.body(), is(equalTo("justone_more")));
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
        assertThat("justone", is(equalTo(c.getWrapped().body)));
    }
}
