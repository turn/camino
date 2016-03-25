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
package com.turn.camino;

import com.turn.camino.config.Metric;
import com.turn.camino.config.Path;
import com.turn.camino.config.Tag;

import java.util.Collections;

import com.google.common.collect.Lists;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 * Unit test for MetricData
 *
 * @author llo
 */
@Test
public class MetricDataTest {

	private static final double EPSILON = 1e-6;

	@Test
	public void testConstructor() {
		double metricValue = 123456.789;
		long now = System.currentTimeMillis();
		Metric metric = mock(Metric.class);
		PathStatus pathStatus = new PathStatus("p1", "v1", mock(Path.class),
				Lists.newArrayList(new PathDetail("v1", false, 12431, now)), null);
		MetricDatum metricDatum = new MetricDatum(new MetricId("m1", "p1", Collections.<Tag>emptyList()),
				metric, pathStatus, metricValue);
		assertEquals(metricDatum.getMetricId().getName(), "m1");
		assertEquals(metricDatum.getMetricId().getPathName(), "p1");
		assertEquals(metricDatum.getValue(), metricValue, EPSILON);
		assertEquals(metricDatum.getMetric(), metric);
		assertEquals(metricDatum.getPathStatus(), pathStatus);
	}

}
