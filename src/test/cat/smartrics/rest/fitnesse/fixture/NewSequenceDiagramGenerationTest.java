package smartrics.rest.fitnesse.fixture;

import org.junit.Before;
import org.junit.Test;

public class NewSequenceDiagramGenerationTest {

    private JUnitHelperWrapper helper;

    @Before
    public void setUp() {
        helper = new JUnitHelperWrapper();
        helper.setDebugMode(false);
    }

    @Test
    public void runsGetTest() throws Exception {
        helper.assertSuitePasses("RestFixtureTests.FitTests.NewSequenceDiagramGenerationTests");
    }


}
