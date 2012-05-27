/*  Copyright 2008 Fabrizio Cannizzo
 *
 *  This file is part of RestFixture.
 *
 *  RestFixture (http://code.google.com/p/rest-fixture/) is free software:
 *  you can redistribute it and/or modify it under the terms of the
 *  GNU Lesser General Public License as published by the Free Software Foundation,
 *  either version 3 of the License, or (at your option) any later version.
 *
 *  RestFixture is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with RestFixture.  If not, see <http://www.gnu.org/licenses/>.
 *
 *  If you want to contact the author please leave a comment here
 *  http://smartrics.blogspot.com/2008/08/get-fitnesse-with-some-rest.html
 */
package smartrics.rest.fitnesse.fixture.support;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import smartrics.rest.config.Config;
import fit.Fixture;

/**
 * Facade to FitNesse global symbols map.
 * 
 * @author fabrizio
 */
public class Variables {
    public static final Pattern VARIABLES_PATTERN = Pattern.compile("\\%([a-zA-Z0-9_]+)\\%");
    private static final String FIT_NULL_VALUE = fitSymbolForNull();
    private String nullValue = "null";

    public Variables() {
        this(Config.getConfig());
    }

    public Variables(Config c) {
        if (c != null) {
            this.nullValue = c.get("restfixture.null.value.representation", "null");
        }
    }

    public void put(String label, String val) {
        String l = fromFitNesseSymbol(label);
        Fixture.setSymbol(l, val);
    }

    public String get(String label) {
        String l = fromFitNesseSymbol(label);
        if (Fixture.hasSymbol(l)) {
            return Fixture.getSymbol(l).toString();
        }
        return null;
    }

    public void clearAll() {
        Fixture.ClearSymbols();
    }

    public String substitute(String text) {
        if (text == null) {
            return null;
        }
        Matcher m = VARIABLES_PATTERN.matcher(text);
        Map<String, String> replacements = new HashMap<String, String>();
        while (m.find()) {
            int gc = m.groupCount();
            if (gc == 1) {
                String g0 = m.group(0);
                String g1 = m.group(1);
                String value = get(g1);
                if (FIT_NULL_VALUE.equals(value)) {
                    value = nullValue;
                }
                replacements.put(g0, value);
            }
        }
        String newText = text;
        for (Entry<String, String> en : replacements.entrySet()) {
            String k = en.getKey();
            String replacement = replacements.get(k);
            if (replacement != null) {
                newText = newText.replaceAll(k, replacement);
            }
        }
        return newText;
    }

    private String fromFitNesseSymbol(String label) {
        String l = label;
        if (l.startsWith("$")) {
            // kept for backward compatibility
            System.err.println("The use of $ to reference labels for storage will be deprecated in the next major version of RestFixture (" + label + ")");
            l = l.substring(1);
        }
        return l;
    }

    private static String fitSymbolForNull() {
        final String k = "somerandomvaluetogettherepresentationofnull-1234567890";
        Fixture.setSymbol(k, null);
        return Fixture.getSymbol(k).toString();
    }

	public String replaceNull(String s) {
		if (s == null) {
			return nullValue;
		}
		return s;
	}
}
