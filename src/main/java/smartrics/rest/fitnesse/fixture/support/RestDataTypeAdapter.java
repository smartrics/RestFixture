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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import fit.TypeAdapter;

/**
 * Base class for all Type Adapters used by RestFixture.
 * 
 * @author smartrics
 * 
 */
public abstract class RestDataTypeAdapter extends TypeAdapter implements fitnesse.slim.Converter {
    private final List<String> errors = new ArrayList<String>();

    private Object actual;

    private Map<String, String> context;

    @Override
    public String toString() {
        return toString(get());
    }

    @Override
    public void set(Object a) {
        this.actual = a;
    }

    @Override
    public Object get() {
        return actual;
    }

    protected void addError(String e) {
        errors.add(e);
    }

    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    /**
     * Used to pass some form of context to the adapter.
     * 
     * @param context
     */
    public void setContext(Map<String, String> c) {
        this.context = c;
    }

    protected Map<String, String> getContext() {
        return context;
    }

    public Object fromString(String o) {
        try {
            return this.parse(o);
        } catch (Exception e) {
            throw new RuntimeException("Unable to parse as " + this.getClass().getName() + ": " + o);
        }
    }
}
