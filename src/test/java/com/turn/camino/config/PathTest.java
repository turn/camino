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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Unit test for Path
 *
 * @author llo
 */
@Test
public class PathTest {

	/**
	 * Test constructor with tag map
	 */
	@Test
	public void testConstructorWithTagMap() {
		List<Metric> metrics = Lists.newArrayList(new Metric("m1", "size", "max", "metricAgg", 0));
		Map<String, String> tags = ImmutableMap.of("pk", "abc");
		Path path = new Path("abc", "xyz", metrics, tags, "<%=now()%>");
		assertEquals(path.getName(), "abc");
		assertEquals(path.getValue(), "xyz");
		assertEquals(path.getMetrics().size(), 1);
		assertEquals(path.getMetrics().get(0).getName(), "m1");
		assertEquals(path.getMetrics().get(0).getFunction(), "size");
		assertEquals(path.getMetrics().get(0).getAggregate(), "max");
		assertEquals(path.getMetrics().get(0).getAggFunction(), "metricAgg");
		assertEquals(path.getMetrics().get(0).getDefaultValue(), 0, 1e-5);
		assertEquals(path.getTags().size(), 1);
		assertEquals(path.getTags().get(0).getKey(), "pk");
		assertEquals(path.getTags().get(0).getValue(), "abc");
		assertEquals(path.getExpectedCreationTime(), "<%=now()%>");
	}

	/**
	 * Test constructor with tag list
	 */
	@Test
	public void testConstructorWithTagList() {
		List<Metric> metrics = Lists.newArrayList(new Metric("m1", "size", "max", "metricAgg", 0));
		List<Tag> tags = Lists.newArrayList(new Tag("pk", "abc"));
		Path path = new Path("abc", "xyz", metrics, tags, "<%=now()%>");
		assertEquals(path.getName(), "abc");
		assertEquals(path.getValue(), "xyz");
		assertEquals(path.getMetrics().size(), 1);
		assertEquals(path.getMetrics().get(0).getName(), "m1");
		assertEquals(path.getMetrics().get(0).getFunction(), "size");
		assertEquals(path.getMetrics().get(0).getAggregate(), "max");
		assertEquals(path.getMetrics().get(0).getAggFunction(), "metricAgg");
		assertEquals(path.getMetrics().get(0).getDefaultValue(), 0, 1e-5);
		assertEquals(path.getTags().size(), 1);
		assertEquals(path.getTags().get(0).getKey(), "pk");
		assertEquals(path.getTags().get(0).getValue(), "abc");
		assertEquals(path.getExpectedCreationTime(), "<%=now()%>");
	}

	/**
	 * Test passing null to metrics parameter
	 *
	 * Expected behavior is that null would be converted to empty list
	 */
	@Test
	public void testConstructorNullMetrics() {
		Path path = new Path("abc", "xyz", null, Collections.<Tag>emptyList(), null);
		assertNotNull(path.getMetrics());
		assertEquals(path.getMetrics().size(), 0);
		assertNotNull(path.getTags());
		assertEquals(path.getTags().size(), 0);
	}

	/**
	 * Test creating a path with null name
	 */
	@Test(expectedExceptions = NullPointerException.class)
	public void testConstructorNullName() {
		new Path(null, "/test/path", null, (Map<String,String>) null, null);
	}

	/**
	 * Test creating a path with null value
	 */
	@Test(expectedExceptions = NullPointerException.class)
	public void testConstructorNullValue() {
		new Path("testPath", null, null, (Map<String,String>) null, null);
	}

	/**
	 * Test immutability of metrics
	 */
	@Test(expectedExceptions = UnsupportedOperationException.class)
	public void testImmutableMetrics() {
		Path path = new Path("none", "none", Lists.<Metric>newArrayList(),
				Lists.<Tag>newArrayList(), null);
		path.getMetrics().add(new Metric("m2", "size", "max", null, 0));
	}

}
