package smartrics.rest.fitnesse.fixture;

import static org.junit.Assert.fail;
import fit.Parse;
import fit.exception.FitParseException;

public class RestFixtureTestHelper {
	public Parse createFitTestInstance(String... rows) {
		Parse t = null;
		StringBuffer rBuff = new StringBuffer();
		rBuff.append("<table>");
		for (String r : rows) {
			rBuff.append(r);
		}
		rBuff.append("</table>");
		try {
            t = new Parse(rBuff.toString(), new String[] { "table", "row", "col" }, 2, 0);
		} catch (FitParseException e) {
			fail("Unable to build Parse object");
		}
		return t;
	}

	public String createFitTestRow(String... cells) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<row>");
		for (String c : cells) {
			buffer.append("<col>").append(c).append("</col>");
		}
		buffer.append("</row>");
		return buffer.toString();
	}

}
