package smartrics.rest.fitnesse.fixture.support;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.junit.Test;

public class StringTypeAdapterTest {

	@Test
	public void shouldEqualsTwoNullStrings() {
		assertTrue(new StringTypeAdapter().equals(null, null));
	}

	@Test
	public void shouldEqualsTwoEqualStrings() {
		assertTrue(new StringTypeAdapter().equals("a", "a"));
		assertFalse(new StringTypeAdapter().equals("a", "b"));
	}

	@Test
	public void shouldParseNullString() {
		assertNull(new StringTypeAdapter().parse("null"));
	}

	@Test
	public void shouldParseEmptyString() {
		assertEquals("", new StringTypeAdapter().parse("blank"));
	}

	@Test
	public void shouldParseAnyString() {
		assertEquals("any", new StringTypeAdapter().parse("any"));
	}

	@Test
	public void shouldConvertAnyString() {
		assertEquals("any", new StringTypeAdapter().toString("any"));
	}

	@Test
	public void shouldConvertNullString() {
		assertEquals("null", new StringTypeAdapter().toString(null));
	}

	@Test
	public void shouldConvertEmptyString() {
		assertEquals("blank", new StringTypeAdapter().toString(""));
	}

}
