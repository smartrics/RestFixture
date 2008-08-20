package smartrics.rest.fitnesse.fixture.support;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.junit.Test;

public class BodyTypeAdapterTest {

	private BodyTypeAdapter adapter = new BodyTypeAdapter();
	private String xml0 = "<a><b>12</b><b>23</b><c>XY</c></a>";
	private String html1 = "&lt;a&gt;&nbsp;1&amp;&lt;/a&gt;";
	private String xml1 = "<a> 1&</a>";
	private List<String> xPaths = Arrays.asList("/a", "//b");
	private String xPathsAsString = "/a<br/>//b";

	@Test
	public void shouldIdentifyContentObjectsWithNoBodyAsBeingEqual(){
		assertTrue(adapter.equals("no-body", "no-body"));
		assertTrue(adapter.equals("no-body", ""));
		assertTrue(adapter.equals("no-body", null));
		assertFalse(adapter.equals("/a", null));
		assertFalse(adapter.equals("no-body", xml0));
	}

	@Test
	public void shouldIdentifyAsEqualsIfExpectedObjectIsAListOfXPathsAvailableInActual(){
		assertTrue(adapter.equals(Arrays.asList("/a/b[text()='12']"), xml0));
		assertTrue(adapter.equals(Arrays.asList("/a/b[text()='12']", "/a/c[text()='XY']"), xml0));
	}

	@Test
	public void shouldStoreNotFoundMessageForEveryExpressionNotFoundForEqualityCheck(){
		assertFalse(adapter.equals(Arrays.asList("/a/b[text()='zzz']", "/a/d[text()='next']", "/a/c[text()='XY']"), xml0));
		assertEquals(2, adapter.getErrors().size());
		assertEquals("not found: '/a/b[text()='zzz']'", adapter.getErrors().get(0));
		assertEquals("not found: '/a/d[text()='next']'", adapter.getErrors().get(1));
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldFailEqualityCheckIfAnyExpressionIsInvalid(){
		adapter.equals(Arrays.asList("invalid xpath", "/a/c[text()='XY']"), xml0);
	}

	@Test
	public void shouldReturnItsStringRepresentationAsPrintableHTML(){
		assertEquals(html1, adapter.toString(xml1));
	}

	@Test
	public void shouldReturnItsStringRepresentationAsPrintableHTMLEvenWhenEmpty(){
		assertEquals("no-body", adapter.toString(""));
	}

	@Test
	public void shoudlParseStringWithXPathInHtmlIntoAProperList() {
		try{
			// just make sure we have the right fata
			assertTrue(xPathsAsString.indexOf("<br/>") > -1);
			assertEquals(xPaths, adapter.parse(xPathsAsString));
		} catch(Exception e){
			fail("should have not raised an exception");
		}
	}

	@Test
	public void shoudlParseNoXPathsIntoAnEmptyList() {
		try{
			// just make sure we have the right fata
			assertEquals(new Vector<String>(), adapter.parse(null));
			assertEquals(new Vector<String>(), adapter.parse(""));
			assertEquals(new Vector<String>(), adapter.parse("no-body"));
		} catch(Exception e){
			fail("should have not raised an exception");
		}
	}

}
