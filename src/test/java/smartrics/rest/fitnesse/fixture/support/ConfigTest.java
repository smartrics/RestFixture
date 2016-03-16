/*  Copyright 2015 Fabrizio Cannizzo
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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;


public class ConfigTest {
    private Config namedConfig;
    private Config defaultNamedConfig;

    @Before
    public void setUp() {
        namedConfig = Config.getConfig("configName");
        defaultNamedConfig = Config.getConfig();
        namedConfig.add("key", "value");
    }

    @After
    public void tearDown() {
        namedConfig.clear();
        defaultNamedConfig.clear();
    }

    @Test
    public void mustInitializeNameOfConfigFromCtorInput() {
        assertThat(namedConfig.getName(), is(equalTo("configName")));
        assertThat(defaultNamedConfig.getName(), is(equalTo(Config.DEFAULT_CONFIG_NAME)));
    }

    @Test
    public void mustInitializeNameToDefaultIfGetterArgIsNull() {
        assertThat(Config.getConfig(null).getName(), is(equalTo(Config.DEFAULT_CONFIG_NAME)));
    }

    @Test
    public void mustAddConfigDataToANamedConfig() {
        namedConfig.add("key1", "value1");
        assertEquals("value1", namedConfig.get("key1"));
    }

    @Test
    public void mustAddConfigDataToDefaultConfigIfConfigNameIsNull() {
        defaultNamedConfig.add("key1", "value1");
        assertEquals("value1", defaultNamedConfig.get("key1"));
    }

    @Test
    public void mustReturnNullForRequestsOnExistentNamedConfigsWithNonExistentKey() {
        assertNull(namedConfig.get("non.existent.key"));
    }

    @Test
    public void mustReturnDefaultForRequestsOnExistentNamedConfigsWithNonExistentKey() {
        assertEquals("val", namedConfig.get("non.existent.key", "val"));
    }

    @Test
    public void mustClearNamedConfig() {
        namedConfig.clear();
        assertNull(namedConfig.get("key"));
    }

    @Test
    public void mustClearDefaultNamedConfigIfConfigNameIsNull() {
        defaultNamedConfig.clear();
        assertNull(defaultNamedConfig.get("key"));
    }

    @Test
    public void mustAddToDefaultNamedConfigWhenConfigNameIsNotAvailable() {
        defaultNamedConfig.add("key.in.default", "value.in.default");
        assertEquals("value.in.default", defaultNamedConfig.get("key.in.default"));
    }

    @Test
    public void mustReturnNullForRequestsOnDefaultNamedConfigsWithNonExistentKey() {
        assertNull(defaultNamedConfig.get("non.existent.key"));
    }

    @Test
    public void mustClearDefaultConfig() {
        defaultNamedConfig.clear();
        assertNull(defaultNamedConfig.get("key"));
    }

    @Test
    public void mustGetDataParsedAsLong() {
        defaultNamedConfig.add("long", "100");
        defaultNamedConfig.add("long-x", "x");
        assertEquals(Long.valueOf(100), defaultNamedConfig.getAsLong("long", Long.valueOf(10)));
        assertEquals(Long.valueOf(10), defaultNamedConfig.getAsLong("long-not-there", Long.valueOf(10)));
        assertEquals(Long.valueOf(10), defaultNamedConfig.getAsLong("long-x", Long.valueOf(10)));
    }

    @Test
    public void mustGetDataParsedAsMap() {
        defaultNamedConfig.add("prop", "a=1\nb=2\n");
        final HashMap<String, String> def = new HashMap<String, String>();
        def.put("c", "3");
        Map<String, String> res = defaultNamedConfig.getAsMap("prop", def);
        assertThat(res.get("a"), is("1"));
        assertThat(res.get("b"), is("2"));
        assertThat(res.get("c"), is("3"));
    }

    @Test
    public void mustGetDataParsedAsBoolean() {
        defaultNamedConfig.add("bool", "false");
        defaultNamedConfig.add("bool-x", "x");
        assertEquals(Boolean.FALSE, defaultNamedConfig.getAsBoolean("bool", Boolean.TRUE));
        assertEquals(Boolean.TRUE, defaultNamedConfig.getAsBoolean("bool-not-there", Boolean.TRUE));
        // note that "x" parsed as Boolean is FALSE
        assertEquals(Boolean.FALSE, defaultNamedConfig.getAsBoolean("bool-x", Boolean.TRUE));
    }

    @Test
    public void mustGetDataParsedAsInteger() {
        defaultNamedConfig.add("int", "19");
        defaultNamedConfig.add("int-x", "x");
        assertEquals(Integer.valueOf(19), defaultNamedConfig.getAsInteger("int", Integer.valueOf(10)));
        assertEquals(Integer.valueOf(10), defaultNamedConfig.getAsInteger("int-not-there", Integer.valueOf(10)));
        assertEquals(Integer.valueOf(10), defaultNamedConfig.getAsInteger("int-x", Integer.valueOf(10)));
    }

    @Test
    public void mustContainAtLeastNameInStringRepresentation() {
        assertTrue("Does not contain name", defaultNamedConfig.toString().contains(defaultNamedConfig.getName()));
        assertTrue("Does not contain name", namedConfig.toString().contains(namedConfig.getName()));
    }

}
