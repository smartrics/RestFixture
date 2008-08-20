package smartrics.rest.fitnesse.fixture.support;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class StatusCodeTypeAdapterTest {

	private StatusCodeTypeAdapter adapter = new StatusCodeTypeAdapter();

	@Test
	public void shouldParseStatusCodesAsTrimmedStrings() {
		assertEquals("100", (String)adapter.parse(" 100 "));
		assertEquals("null", (String)adapter.parse(null));
	}

	@Test
	public void shouldRenderCellContentAsStrings(){
		assertEquals("200", adapter.toString("200"));
		assertEquals("blank", adapter.toString(" "));
		assertEquals("null", adapter.toString(null));
	}

	@Test
	public void expectedShouldBeTreatedAsRegularExpression(){
		assertTrue("expected is not treated as regular expression", adapter.equals("20\\d", "201"));
		assertTrue("expected is not treated as regular expression", adapter.equals("20\\d", "202"));
	}

	@Test
	public void shouldNotEqualiseIfExpectedOrActualAreNull(){
		assertFalse(adapter.equals(null, "201"));
		assertFalse(adapter.equals("20\\d", null));
	}

	@Test
	public void whenExpectedIsNotMatchedAnErrorShouldBeAdded(){
		adapter.equals("20\\d", "404");
		assertEquals(1, adapter.getErrors().size());
		assertEquals("not match: 20\\d", adapter.getErrors().get(0));
	}

}
