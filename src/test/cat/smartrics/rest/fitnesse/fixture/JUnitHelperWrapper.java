package smartrics.rest.fitnesse.fixture;

import fitnesse.junit.JUnitHelper;

/**
 * This assumes that the build process that copies and filters the fitnesse
 * pages has been executed
 * 
 * @author fabrizio
 * 
 */
public class JUnitHelperWrapper {

    private JUnitHelper helper;
    
    public JUnitHelperWrapper() {
        helper = new JUnitHelper("build/fitnesse", "build/fitnesse/output");
    }

    public void setDebugMode(boolean enabled) {
        helper.setDebugMode(enabled);
    }

    public void assertTestPasses(String testName) throws Exception {
        helper.assertTestPasses(testName);
    }

    public void assertSuitePasses(String suiteName) throws Exception {
        helper.assertSuitePasses(suiteName);
    }

    public void assertSuitePasses(String suiteName, String suiteFilter) throws Exception {
        helper.assertSuitePasses(suiteName, suiteFilter);
    }

    public void assertPasses(String pageName, String pageType, String suiteFilter) throws Exception {
        helper.assertPasses(pageName, pageType, suiteFilter);
    }

    public int hashCode() {
        return helper.hashCode();
    }

    public boolean equals(Object obj) {
        return helper.equals(obj);
    }

    public String toString() {
        return helper.toString();
    }

}
