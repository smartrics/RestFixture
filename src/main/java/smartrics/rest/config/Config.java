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
package smartrics.rest.config;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple implementation of a named configuration store.
 * 
 * The interface implements a named map, with the ability of using a default
 * name if not passed. The named maps are singletons (implemented as a private
 * static field), so beware!
 */
public final class Config {
	/**
	 * the default name of the named config.
	 */
	public final static String DEFAULT_CONFIG_NAME = "default";

	/**
	 * the static bucket where the config data is stored
	 */
	private final static Map<String, Map<String, String>> CONFIGURATIONS = new HashMap<String, Map<String, String>>();

	/**
	 * this instance name
	 */
	private final String name;

	/**
	 * the constructor for the configuration with default name
	 * {@link Config.DEFAULT_CONFIG_NAME};
	 */
	public Config() {
		this(DEFAULT_CONFIG_NAME);
	}

	/**
	 * This config name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * creates a config with a given name.
	 * 
	 * @param name
	 *            the name of this config
	 */
	public Config(final String name) {
		this.name = name;
		lazyCreateNamedConfig();
	}

	/**
	 * Adds a key/value pair to a named configuration.
	 * 
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	public void add(String key, String value) {
		Map<String, String> namedConfig = getNamedConfig();
		namedConfig.put(key, value);
	}

	/**
	 * Returns a key/value from a named config.
	 * 
	 * @param key
	 *            the key
	 * @return the value
	 */
	public String get(String key) {
		Map<String, String> namedConfig = getNamedConfig();
		return namedConfig.get(key);
	}

	/**
	 * returns a key/value from a named config, parsed as Long
	 * 
	 * @param key
	 *            the key
	 * @param def
	 *            the default value for value not existent or not parseable
	 * @return a Long representing the value, def if the value cannot be parsed
	 *         as Long
	 */
	public Long getAsLong(String key, Long def) {
		String val = get(key);
		try {
			return Long.parseLong(val);
		} catch (NumberFormatException e) {
			return def;
		}
	}

	/**
	 * returns a key/value from a named config, parsed as Boolean
	 * 
	 * @param key
	 *            the key
	 * @param def
	 *            the default value for value not existent or not parseable
	 * @return a Boolean representing the value, def if the value cannot be
	 *         parsed as Boolean
	 */
	public Boolean getAsBoolean(String key, Boolean def) {
		String val = get(key);
		try {
			if(val==null)
				return def;
			return Boolean.parseBoolean(val);
		} catch (NumberFormatException e) {
			return def;
		}
	}

	/**
	 * returns a key/value from a named config, parsed as Integer
	 * 
	 * @param key
	 *            the key
	 * @param def
	 *            the default value for value not existent or not parseable
	 * @return a Integer representing the value, def if the value cannot be
	 *         parsed as Integer
	 */
	public Integer getAsInteger(String key, Integer def) {
		String val = get(key);
		try {
			return Integer.parseInt(val);
		} catch (NumberFormatException e) {
			return def;
		}
	}

	/**
	 * Clears a named config store.
	 * 
	 * @param cName
	 *            the named config to clear
	 */
	public void clear() {
		Map<String, String> namedConfig = getNamedConfig();
		namedConfig.clear();
	}

	private Map<String, String> getNamedConfig() {
		return CONFIGURATIONS.get(name);
	}
	
	private Map<String, String> lazyCreateNamedConfig() {
		Map<String, String> namedConfig = CONFIGURATIONS.get(name);
		if (namedConfig == null) {
			namedConfig = new HashMap<String, String>();
			CONFIGURATIONS.put(name, namedConfig);
		}
		return namedConfig;
	}

	@Override
	public String toString() {
		return "name=" + getName() + ". Configurations: "
				+ CONFIGURATIONS.toString() + " this: " + this.hashCode()
				+ ", CONF: " + CONFIGURATIONS.hashCode();
	}
}
