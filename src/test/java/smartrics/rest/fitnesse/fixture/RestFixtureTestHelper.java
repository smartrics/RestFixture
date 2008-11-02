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
			t = new Parse(rBuff.toString(), new String[] { "table", "row",
					"col" }, 2, 0);
		} catch (FitParseException e) {
			fail("Unable to build Parse object");
		}
		return t;
	}

	public String createFitTestRow(String cell1, String cell2, String cell3,
			String cell4, String cell5) {
		String row = String
				.format(
						"<row><col>%s</col><col>%s</col><col>%s</col><col>%s</col><col>%s</col></row>",
						cell1, cell2, cell3, cell4, cell5);
		return row;
	}

}
