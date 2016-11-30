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

import static org.testng.Assert.*;

import com.google.common.collect.Lists;
import org.testng.annotations.Test;

/**
 * Unit test for Metric
 *
 * @author llo
 */
@Test
public class MetricTest {

	/**
	 * Test constructor
	 */
	@Test
	public void testConstructor() {
		Metric metric = new Metric("abc", "age", "sum", "agg", 0);
		assertEquals(metric.getName(), "abc");
		assertEquals(metric.getFunction(), "age");
		assertEquals(metric.getAggregate(), "sum");
		assertEquals(metric.getAggFunction(), "agg");
		assertEquals(metric.getDefaultValue(), 0, 1e-5);
	}

}
