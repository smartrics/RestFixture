/*  Copyright 2011 Fabrizio Cannizzo
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
package smartrics.rest.fitnesse.fixture;

import org.junit.Test;
import smartrics.rest.fitnesse.fixture.support.StringTypeAdapter;
import smartrics.rest.fitnesse.fixture.support.TextBodyTypeAdapter;
import smartrics.rest.fitnesse.fixture.support.Tools;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SlimFormatterTest {

    @Test
    public void shouldDisplayGrayedActualOnCheckIfNoExpectedIsSpecified() {
        SlimCell c = new SlimCell("");
        SlimFormatter formatter = new SlimFormatter();
        StringTypeAdapter actual = new StringTypeAdapter();
        actual.set("2");
        formatter.check(c, actual);
        assertThat(c.body(), is(equalTo("report:<div>2</div>")));
    }

    @Test
    public void shouldDisplayNothingOnCheckIfNoExpectedIsSpecifiedAndActualIsNullOrEmpty() {
        SlimCell c = new SlimCell("");
        SlimFormatter formatter = new SlimFormatter();
        StringTypeAdapter actual = new StringTypeAdapter();
        formatter.check(c, actual);
        assertThat(c.body(), is(equalTo("")));
        actual.set("");
        assertThat(c.body(), is(equalTo("")));
    }

    @Test
    public void shouldDisplayPassOnCheckIfExpectedAndActualMatch() {
        SlimCell c = new SlimCell("abc123");
        SlimFormatter formatter = new SlimFormatter();
        StringTypeAdapter actual = new StringTypeAdapter();
        actual.set("abc123");
        formatter.check(c, actual);
        assertThat(c.body(), is(equalTo("pass:<div>abc123</div>")));
    }

    @Test
    public void shouldDisplayPassOnCheckIfExpectedAndActualMatch_whenDisplayingActual() {
        SlimCell c = new SlimCell("something matching logically abc123");
        SlimFormatter formatter = new SlimFormatter();
        formatter.setDisplayActual(true);
        // mockito seems not to be able to correctly allow
        // StringTypeAdapter actual = mock(StringTypeAdapter.class);
        // when(actual).equals(isA(Object.class, Object.class)).thenReturn(true)
        // as its not picking up the overridden method but
        // Object.equals(Object).
        StringTypeAdapter actual = new StringTypeAdapter() {
            @Override
            public boolean equals(Object a, Object b) {
                return true;
            }
        };
        actual.set("abc123");
        formatter.check(c, actual);

        assertThat(
                c.body(),
                is(equalTo("pass:<div>something&nbsp;matching&nbsp;logically&nbsp;abc123<br/><i><span class='fit_label'>expected</span></i><hr/><br/>abc123<br/><i><span class='fit_label'>actual</span></i></div>")));
    }

    @Test
    public void shouldDisplayFailOnCheckIfExpectedAndActualMatch_whenNotDisplayingActual() {
        SlimCell c = new SlimCell("abc123");
        SlimFormatter formatter = new SlimFormatter();
        formatter.setDisplayActual(false);
        StringTypeAdapter actual = new StringTypeAdapter();
        actual.set("def345");
        formatter.check(c, actual);
        assertThat(c.body(), is(equalTo("fail:<div>abc123</div>")));
    }

    @Test
    public void shouldDisplayFailOnCheckIfExpectedAndActualMatch_whenDisplayingActual() {
        SlimCell c = new SlimCell("abc123");
        SlimFormatter formatter = new SlimFormatter();
        formatter.setDisplayActual(true);
        StringTypeAdapter actual = new StringTypeAdapter();
        actual.set("def345");
        formatter.check(c, actual);

        assertThat(c.body(), is(equalTo("fail:<div>abc123<br/><i><span class='fit_label'>expected</span></i><hr/><br/>def345<br/><i><span class='fit_label'>actual</span></i></div>")));
    }

    @Test
    public void shouldDisplayXmlDataInActual() {
        SlimCell c = new SlimCell("<xml />");
        SlimFormatter formatter = new SlimFormatter();
        formatter.setDisplayActual(true);
        TextBodyTypeAdapter actual = new TextBodyTypeAdapter();
        actual.set("<xml />");
        formatter.check(c, actual);
        assertThat(c.body(), is(equalTo("pass:<div>" + Tools.toHtml("<xml />") + "</div>")));
    }

    @Test
    public void shouldRenderLinksAsGreyed() {
        SlimFormatter formatter = new SlimFormatter();
        SlimCell c = new SlimCell("abc123");
        formatter.asLink(c, "http://localhost", "text");
        assertThat(c.body(), is(equalTo("report:<div><a href='http://localhost'>text</a></div>")));
    }

    @Test
    public void shouldRenderExceptionsAsSlimErrorCellWithStackTracesInCode() {
        SlimFormatter formatter = new SlimFormatter();
        Throwable t = new Throwable();
        SlimCell c = new SlimCell("abc123");
        ByteArrayOutputStream bais = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(bais);
        t.printStackTrace(ps);
        String trace = Tools.toHtml(new String(bais.toByteArray()));

        formatter.exception(c, t);
        
        assertThat(c.body().startsWith("error:"), is(true));
        assertThat(c.body().contains("<code>"), is(true));
        assertThat(c.body().contains("</code>"), is(true));
        assertThat(c.body().contains(trace), is(true));
    }
}
