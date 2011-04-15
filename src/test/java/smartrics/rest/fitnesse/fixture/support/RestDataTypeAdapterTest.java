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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import fit.Fixture;

public class RestDataTypeAdapterTest {
    private RestDataTypeAdapter adapter;

    @Before
    public void setUp() {
        adapter = new RestDataTypeAdapter() {

        };
    }

    @Test
    public void mustAllowStoringOfTheInstanceOfTheCellContent() {
        adapter.set("data");
        assertEquals("data", adapter.get());
    }

    @Test
    public void mustReturnStringRepresentationOfTheCellContent() {
        adapter.set("");
        assertEquals("blank", adapter.toString());
        adapter.set(null);
        assertEquals("null", adapter.toString());
        adapter.set(new Object() {
            public String toString() {
                return "x45";
            }
        });
        assertEquals("x45", adapter.toString());
    }

    @Test
    public void mustAllowStoringOfErrors() {
        adapter.addError("error");
        assertEquals("error", adapter.getErrors().get(0));
    }

    @Test
    public void mustDisallowAddToTheErrorsList() {
        try {
            adapter.getErrors().add("i am not allowed");
        } catch (UnsupportedOperationException e) {

        }
    }

    @Test
    public void mustDisallowRemoveFromTheErrorsList() {
        adapter.addError("error");
        try {
            adapter.getErrors().remove(0);
        } catch (UnsupportedOperationException e) {

        }
    }

    @Test
    public void mustDisallowOpsOnTheErrorsList() {
        adapter.addError("error1");
        adapter.addError("error2");
        try {
            Collections.sort(adapter.getErrors());
            fail("Should have thrown an exception");
        } catch (UnsupportedOperationException e) {
        }
    }

    @Test
    // this tests the fit.TypeAdapter behaviour
    public void fromRawStringDelegatesToParse() throws Exception {
        Fixture mockFixture = mock(Fixture.class);
        adapter.init(mockFixture, String.class);
        when(mockFixture.parse("ss", String.class)).thenReturn("parsed ss");
        adapter.fromString("ss");
        verify(mockFixture).parse("ss", String.class);
        verifyNoMoreInteractions(mockFixture);
    }

    @Test
    public void fromRawStringMapsCheckedExceptionsOfParseIntoRuntimeException() throws Exception {
        Fixture mockFixture = mock(Fixture.class);
        adapter.init(mockFixture, String.class);
        when(mockFixture.parse("ss", String.class)).thenThrow(new Exception("some badness happened!"));
        try {
            adapter.fromString("ss");
            fail("Should have thrown an exception");
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is(equalTo("Unable to parse as smartrics.rest.fitnesse.fixture.support.RestDataTypeAdapterTest$1: ss")));
        }
        verify(mockFixture).parse("ss", String.class);
        verifyNoMoreInteractions(mockFixture);
    }

}
