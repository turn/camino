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

import com.google.common.collect.Lists;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Unit test for Repeat
 *
 * @author llo
 */
@Test
public class RepeatTest {

	/**
	 * Test constructor
	 */
	@Test
	public void testConstructor() {

		Repeat repeat = new Repeat("theVar", "theList",
				Lists.newArrayList(new Path("path1", "/mystuff",
						Collections.<Metric>emptyList(), (List<Tag>) null, null)),
				Lists.newArrayList(new Repeat("innerVar", "innerList",
						Collections.<Path>emptyList(), null)));
		assertEquals(repeat.getVar(), "theVar");
		assertEquals(repeat.getList(), "theList");
		assertEquals(repeat.getPaths().size(), 1);
		assertEquals(repeat.getPaths().get(0).getName(), "path1");
		assertEquals(repeat.getPaths().get(0).getValue(), "/mystuff");
		assertEquals(repeat.getRepeats().size(), 1);
		assertEquals(repeat.getRepeats().get(0).getVar(), "innerVar");
		assertEquals(repeat.getRepeats().get(0).getList(), "innerList");
	}

	/**
	 * Test passing null to paths parameter
	 *
	 * Expected behavior is that null would be converted to empty list
	 */
	@Test
	public void testConstructorNullPathsAndRepeats() {
		Repeat repeat = new Repeat("theVar", "theList", null, null);
		assertNotNull(repeat.getPaths());
		assertEquals(repeat.getPaths().size(), 0);
		assertNotNull(repeat.getRepeats());
		assertEquals(repeat.getRepeats().size(), 0);
	}

	/**
	 * Test immutability of paths
	 */
	@Test(expectedExceptions = UnsupportedOperationException.class)
	public void testImmutablePaths() {
		Repeat repeat = new Repeat("theVar", "theList", Lists.<Path>newArrayList(), null);
		repeat.getPaths().add(new Path("none", "none", Collections.<Metric>emptyList(),
				(List<Tag>) null, null));
	}

}
