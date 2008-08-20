package smartrics.rest.fitnesse.fixture.support;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;

import smartrics.rest.client.RestData.Header;

public class HeadersTypeAdapterTest {

	private HeadersTypeAdapter adapter = new HeadersTypeAdapter();
	private String ls = System.getProperty("line.separator");
	private Header h0 = new Header("n0", "v0");
	private Header h1 = new Header("n1", "v1");
	private Header h = new Header("n", "v");
	private Header h2 = new Header("n1", "v1");
	private Header h3 = new Header("n3", "v3");
	private Collection<Header> expected = Arrays.asList(h, h2, h3);
	private Collection<Header> actualSubset = Arrays.asList(h, h2);
	private Collection<Header> actualSame = Arrays.asList(h, h2, h3);
	private Collection<Header> actualSuperset = Arrays.asList(h0, h1, h, h2, h3);
	private String headersAsHtmlString = " n : v <br/> n1: v1 <br  /> n3 :v3 ";
	private String headersAsOutputString = "n : v" + ls + "n1 : v1" + ls + "n3 : v3";

	@Test
	public void shouldIdentifyContentObjectsWithNoBodyAsBeingEqual(){
		assertFalse(adapter.equals(null, null));
	}

	@Test
	public void actualHeadersMustBeASupersetOfExpectedHeaders(){
		assertTrue(adapter.equals(expected, actualSame));
		assertTrue(adapter.equals(expected, actualSuperset));
	}

	@Test
	public void actualHeadersShouldntBeASupersetOfExpectedHeaders(){
		assertFalse(adapter.equals(expected, actualSubset));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldParseHeadersInHtmlFormatAsProperListOfHeaders(){
		try{
			Collection<Header> result = (Collection<Header>)adapter.parse(headersAsHtmlString);
			assertEquals(expected, result);
		} catch(Exception e){
			fail();
		}
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldNotifyClientOfBadHeaderSyntax(){
		try {
			adapter.parse("a:b<br /> c;d");
		} catch (Exception e) {
			if(e instanceof RuntimeException){
				throw (RuntimeException) e;
			}
			fail("should have thrown the exception");
		}
	}

	@Test
	public void shouldTransformACollectionOfHeadersIntoAHtmlString(){
		String result = adapter.toString(expected);
		assertEquals(Tools.toHtml(headersAsOutputString), result);
	}

}
