package smartrics.rest.fitnesse.fixture;

import fit.Parse;
import fit.exception.FitParseException;

public class FitTestSupport {
    public static String createFitTestRow(String... cells) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<table>");
        buffer.append("<tr>");
        for (String c : cells) {
            buffer.append("<td>").append(c).append("</td>");
        }
        buffer.append("</tr>");
        buffer.append("</table>");
        return buffer.toString();
    }

    public static Parse buildEmptyParse() {
        try {
            return new Parse("<table><tr><td>&nbsp;</td></tr></table>");
        } catch (FitParseException e) {
            throw new RuntimeException(e);
        }
    }

}
