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
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Unit test for ConfigBuilder
 *
 * @author llo
 */
@Test
public class ConfigBuilderTest {

	private String json =
			"{\"properties\":{\"ppp\":\"qqq\"}," +
			"\"paths\":[{\"name\":\"foo\",\"value\":\"/bar\",\"tags\":{\"k\":\"1\",\"v\":\"x\"}}," +
			"{\"name\":\"baz\",\"value\":\"/woot\",\"metrics\":" +
			"[{\"name\":\"m1\",\"function\":\"size\",\"aggregate\":\"sum\"}]}]," +
			"\"repeats\":[{\"var\":\"v\",\"list\":\"l\"," +
			"\"paths\":[{\"name\":\"p1\",\"value\":\"/nnn\"}]," +
			"\"repeats\":[{\"var\":\"innerVar\",\"list\":\"innerList\"}]}]}";

	/**
	 * Test creating one-level config from location
	 *
	 * @throws IOException
	 */
	@Test
	public void testFromLocationBuildLocal() throws IOException, URISyntaxException {
		File root = new File(ConfigBuilderTest.class.getProtectionDomain().getCodeSource()
				.getLocation().getPath()).getParentFile().getParentFile();
		File file = new File(root, "src/test/config/test-config.json");
		URI location = file.toURI();
		Config config = ConfigBuilder.create().from(location).buildLocal();
		assertEquals(config.getLocation(), location);
		assertNotNull(config.getIncludes());
		assertEquals(config.getIncludes().size(), 1);
		assertEquals(config.getIncludes().get(0), "common.json");
	}

	/**
	 * Test building a config recursively
	 *
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	@Test
	public void testFromLocationBuildRecursive() throws IOException, URISyntaxException {
		File root = new File(ConfigBuilderTest.class.getProtectionDomain().getCodeSource()
				.getLocation().getPath()).getParentFile().getParentFile();
		File file = new File(root, "src/test/config/test-config.json");
		URI location = file.toURI();
		Config config = ConfigBuilder.create().from(location).build();
		assertEquals(config.getProperties().size(), 4);
		assertEquals(config.getProperties().get(0).getName(), "user");
		assertEquals(config.getProperties().get(0).getValue(), "llo");
		assertEquals(config.getProperties().get(1).getName(), "score");
		assertEquals(config.getProperties().get(1).getValue(), "2.2");
		assertEquals(config.getProperties().get(2).getName(), "type");
		assertEquals(config.getProperties().get(2).getValue(), "uint64");
		assertEquals(config.getProperties().get(3).getName(), "values");
		assertEquals(config.getProperties().get(3).getValue(), "<%=['a','b']%>");
		assertEquals(config.getPaths().size(), 2);
		assertEquals(config.getPaths().get(0).getName(), "home");
		assertEquals(config.getPaths().get(1).getName(), "data");
		assertEquals(config.getRepeats().size(), 1);
	}

	/**
	 * Test creating config from reader
	 *
	 * @throws IOException
	 */
	@Test
	public void testFromReader() throws IOException {
		Config config = ConfigBuilder.create()
				.from(new StringReader(json)).build();
		assertNotNull(config);
		assertNotNull(config.getProperties());
		assertEquals(config.getProperties().size(), 1);
		assertEquals(config.getProperties().get(0).getName(), "ppp");
		assertEquals(config.getProperties().get(0).getValue(), "qqq");
		assertNotNull(config.getPaths());
		assertEquals(config.getPaths().size(), 2);
		assertEquals(config.getPaths().get(0).getName(), "foo");
		assertEquals(config.getPaths().get(0).getValue(), "/bar");
		assertNotNull(config.getPaths().get(0).getTags());
		assertEquals(config.getPaths().get(0).getTags().size(), 2);
		assertEquals(config.getPaths().get(0).getTags().get(0).getKey(), "k");
		assertEquals(config.getPaths().get(0).getTags().get(0).getValue(), "1");
		assertEquals(config.getPaths().get(0).getTags().get(1).getKey(), "v");
		assertEquals(config.getPaths().get(0).getTags().get(1).getValue(), "x");
		assertEquals(config.getPaths().get(0).getMetrics().size(), 0);
		assertEquals(config.getPaths().get(1).getName(), "baz");
		assertEquals(config.getPaths().get(1).getValue(), "/woot");
		assertEquals(config.getPaths().get(1).getMetrics().size(), 1);
		assertEquals(config.getPaths().get(1).getMetrics().get(0).getFunction(), "size");
		assertEquals(config.getPaths().get(1).getMetrics().get(0).getAggregate(), "sum");
		assertNotNull(config.getRepeats());
		assertEquals(config.getRepeats().size(), 1);
		assertEquals(config.getRepeats().get(0).getVar(), "v");
		assertEquals(config.getRepeats().get(0).getList(), "l");
		assertEquals(config.getRepeats().get(0).getPaths().size(), 1);
		assertEquals(config.getRepeats().get(0).getPaths().get(0).getName(), "p1");
		assertEquals(config.getRepeats().get(0).getPaths().get(0).getValue(), "/nnn");
		assertNotNull(config.getRepeats().get(0).getRepeats());
		assertEquals(config.getRepeats().get(0).getRepeats().size(), 1);
		assertEquals(config.getRepeats().get(0).getRepeats().get(0).getVar(), "innerVar");
		assertEquals(config.getRepeats().get(0).getRepeats().get(0).getList(), "innerList");
	}

	/**
	 * Test immutability of list field
	 *
	 * @throws IOException
	 */
	@Test(expectedExceptions = UnsupportedOperationException.class)
	public void testImmutability() throws IOException {
		Config config = ConfigBuilder.create()
				.from(new StringReader(json)).build();
		config.getPaths().add(new Path("abc", "xyz", Collections.<Metric>emptyList(),
				Collections.<Tag>emptyList(), null));
	}

	/**
	 * Test adding properties to config builder
	 */
	@Test
	public void testAddProperties() throws IOException {
		Config config = ConfigBuilder.create()
				.addProperties(Lists.newArrayList(
						new Property("abc", "123"),
						new Property("def", "666")))
				.build();
		assertNotNull(config);
		assertNotNull(config.getProperties());
		assertEquals(config.getProperties().size(), 2);
		assertEquals(config.getProperties().get(0).getName(), "abc");
		assertEquals(config.getProperties().get(0).getValue(), "123");
		assertEquals(config.getProperties().get(1).getName(), "def");
		assertEquals(config.getProperties().get(1).getValue(), "666");
	}

	/**
	 * Test adding paths to config builder
	 */
	@Test
	public void testAddPaths() throws IOException {
		Config config = ConfigBuilder.create()
				.addPaths(Lists.newArrayList(
						new Path("abc", "123", Lists.newArrayList(new Metric("m1",
								"age", "max")),
								ImmutableList.of(new Tag("pathName", "m1")), null),
						new Path("def", "666", Lists.<Metric>newArrayList(),
								Collections.<Tag>emptyList(), null)))
				.build();
		assertNotNull(config);
		assertNotNull(config.getPaths());
		assertEquals(config.getPaths().size(), 2);
		assertEquals(config.getPaths().get(0).getName(), "abc");
		assertEquals(config.getPaths().get(0).getValue(), "123");
		assertEquals(config.getPaths().get(0).getMetrics().size(), 1);
		assertEquals(config.getPaths().get(1).getName(), "def");
		assertEquals(config.getPaths().get(1).getValue(), "666");
		assertEquals(config.getPaths().get(1).getMetrics().size(), 0);
	}

	/**
	 * Test resolving location
	 *
	 * @throws IOException
	 */
	@Test
	public void testResolveInclude() throws IOException {
		ConfigBuilder cb = ConfigBuilder.create();
		URI uri = cb.resolveInclude("x.json", URI.create("file:///a/b/c.json"));
		assertNotNull(uri);
		assertEquals(uri.getScheme(), "file");
		assertEquals(uri.getPath(), "/a/b/x.json");
		uri = cb.resolveInclude("http://turn.com/x/y/z.json", URI.create("file:///a/b/c.json"));
		assertNotNull(uri);
		assertEquals(uri.getScheme(), "http");
		assertEquals(uri.getHost(), "turn.com");
		assertEquals(uri.getPath(), "/x/y/z.json");
		uri = cb.resolveInclude("hdfs://cluster:9000/p/q/r.json", null);
		assertNotNull(uri);
		assertEquals(uri.getScheme(), "hdfs");
		assertEquals(uri.getHost(), "cluster");
		assertEquals(uri.getPort(), 9000);
		assertEquals(uri.getPath(), "/p/q/r.json");
	}

	/**
	 * Test resolving location with error
	 *
	 * @throws IOException
	 */
	@Test(expectedExceptions = IOException.class)
	public void testResolveIncludeError() throws IOException {
		ConfigBuilder cb = ConfigBuilder.create();
		cb.resolveInclude("x.json", null);
	}

}
