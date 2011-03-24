package smartrics.rest.fitnesse.fixture.support;

import java.util.regex.Pattern;

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
        if (!Pattern.matches(expected, actual)) {
            addError("not match: " + expected);
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
