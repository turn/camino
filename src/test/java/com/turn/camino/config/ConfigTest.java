/**
 * Copyright (C) 2014-2016, Turn Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 */
package com.turn.camino.config;

import java.io.File;
import java.net.URI;
import java.util.Collections;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Unit test for Config
 *
 * @author llo
 */
@Test
public class ConfigTest {

	private URI location = new File("/a/b/c").toURI();
	private String include = "/a/b/d";
	private Property property = new Property("prop1", "value1");
	private Path path = new Path("abc", "xyz", Collections.<Metric>emptyList(),
			Collections.<String,String>emptyMap(), null);
	private Repeat repeat = new Repeat("my_var", "my_list", Collections.<Path>emptyList(),
			null);

	/**
	 * Test setters and getters
	 */
	@Test
	public void testConstructorWithPropertyMap() {

		Config config = new Config(location, Lists.newArrayList(include),
				ImmutableMap.of(property.getName(), property.getValue()), Lists.newArrayList(path),
				Lists.newArrayList(repeat));

		assertEquals(config.getLocation(), location);

		assertEquals(config.getIncludes().size(), 1);
		assertEquals(config.getIncludes().get(0), include);

		assertEquals(config.getProperties().size(), 1);
		assertEquals(config.getProperties().get(0).getName(), "prop1");
		assertEquals(config.getProperties().get(0).getValue(), "value1");

		assertEquals(config.getPaths().size(), 1);
		assertEquals(config.getPaths().get(0).getName(), "abc");
		assertEquals(config.getPaths().get(0).getValue(), "xyz");

		assertEquals(config.getRepeats().size(), 1);
		assertEquals(config.getRepeats().get(0).getVar(), "my_var");
		assertEquals(config.getRepeats().get(0).getList(), "my_list");
	}

	/**
	 * Test setters and getters
	 */
	@Test
	public void testConstructorWithPropertyList() {

		Config config = new Config(location, Lists.newArrayList(include),
				Lists.newArrayList(property), Lists.newArrayList(path),
				Lists.newArrayList(repeat));

		assertEquals(config.getLocation(), location);

		assertEquals(config.getIncludes().size(), 1);
		assertEquals(config.getIncludes().get(0), include);

		assertEquals(config.getProperties().size(), 1);
		assertEquals(config.getProperties().get(0).getName(), "prop1");
		assertEquals(config.getProperties().get(0).getValue(), "value1");

		assertEquals(config.getPaths().size(), 1);
		assertEquals(config.getPaths().get(0).getName(), "abc");
		assertEquals(config.getPaths().get(0).getValue(), "xyz");

		assertEquals(config.getRepeats().size(), 1);
		assertEquals(config.getRepeats().get(0).getVar(), "my_var");
		assertEquals(config.getRepeats().get(0).getList(), "my_list");
	}

	/**
	 * Test passing null to parameters
	 *
	 * Expected behavior is that null would be converted to empty list
	 */
	@Test
	public void testConstructorNullParameters() {
		Config config = new Config(null, null, (Map<String,String>) null, null, null);
		assertNotNull(config.getProperties());
		assertEquals(config.getProperties().size(), 0);
		assertNotNull(config.getPaths());
		assertEquals(config.getPaths().size(), 0);
		assertNotNull(config.getRepeats());
		assertEquals(config.getRepeats().size(), 0);
	}


	/**
	 * Test immutability of properties
	 */
	@Test(expectedExceptions = UnsupportedOperationException.class)
	public void testImmutableProperties() {
		Config config = new Config(null, Collections.<String>emptyList(),
				Collections.<Property>emptyList(), Collections.<Path>emptyList(),
				Collections.<Repeat>emptyList());
		config.getProperties().add(property);
	}

	/**
	 * Test immutability of paths
	 */
	@Test(expectedExceptions = UnsupportedOperationException.class)
	public void testImmutablePaths() {
		Config config = new Config(null, Collections.<String>emptyList(),
				Collections.<Property>emptyList(), Collections.<Path>emptyList(),
				Collections.<Repeat>emptyList());
		config.getPaths().add(path);
	}

	/**
	 * Test immutability of repeats
	 */
	@Test(expectedExceptions = UnsupportedOperationException.class)
	public void testImmutableRepeats() {
		Config config = new Config(null, Collections.<String>emptyList(),
				Collections.<Property>emptyList(), Collections.<Path>emptyList(),
				Collections.<Repeat>emptyList());
		config.getRepeats().add(repeat);
	}

}
