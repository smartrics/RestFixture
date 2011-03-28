package smartrics.rest.fitnesse.fixture.support;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import fit.Parse;

public class TextBodyTypeAdapter extends BodyTypeAdapter {

    @Override
    public boolean equals(Object r1, Object r2) {
        if (r1 == null || r2 == null)
            return false;
        String expected = r1.toString();
        if (r1 instanceof Parse) {
            expected = ((Parse) r1).text();
        }
        String actual = (String) r2;
        try {
            if (!Pattern.matches(expected, actual)) {
                addError("not match: " + expected);
            }
        } catch (PatternSyntaxException e) {
            // lets try to string match just to be kind
            if (!expected.equals(actual)) {
                addError("not found: " + expected);
            }
        }
        return getErrors().size() == 0;
    }

    @Override
    public Object parse(String s) {
        if (s == null)
            return "null";
        return s.trim();
    }

    @Override
    public String toXmlString(String content) {
        return "<text>" + content + "</text>";
    }

}
