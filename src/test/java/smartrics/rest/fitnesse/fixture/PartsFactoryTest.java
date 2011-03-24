package smartrics.rest.fitnesse.fixture;

import org.junit.Test;

public class PartsFactoryTest {

    @Test(expected = IllegalArgumentException.class)
    public void cannotBuildACellFormatterForANullRunner() {
        PartsFactory f = new PartsFactory();
        f.buildCellFormatter(null);
    }

    @Test(expected = IllegalStateException.class)
    public void cantBuildACellFormatterForNonFitOrSlimRunner() {
        PartsFactory f = new PartsFactory();
        f.buildCellFormatter(RestFixture.Runner.OTHER);
    }
    
}
