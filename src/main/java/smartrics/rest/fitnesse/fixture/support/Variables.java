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

import java.util.ArrayList;
import java.util.List;

import fit.Fixture;

public class Variables {
    private static List<String> symbolNamesCache = new ArrayList<String>();

	public void put(String label, String val) {
        String l = fromFitNesseSymbol(label);
        Fixture.setSymbol(l, val);
        symbolNamesCache.add(l);
	}

	public String get(String label) {
        String l = fromFitNesseSymbol(label);
        if (Fixture.hasSymbol(l)) {
            return Fixture.getSymbol(l).toString();
        }
        return null;
	}

	public void clearAll(){
        Fixture.ClearSymbols();
        symbolNamesCache.clear();
	}

	public String substitute(String text) {
		String textUpdatedWithVariableSubstitution = text;
        for (String entry : symbolNamesCache) {
            String qualifiedVariableName = "%" + entry + "%";
            if (textUpdatedWithVariableSubstitution.indexOf(qualifiedVariableName) >= 0) {
                System.err.println("The use of %label% will be deprecated in favour of $label in the next major version of RestFixture (" + qualifiedVariableName + ")");
            }
            textUpdatedWithVariableSubstitution = textUpdatedWithVariableSubstitution.replaceAll(qualifiedVariableName, get(entry));
		}
		return textUpdatedWithVariableSubstitution;
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
}
