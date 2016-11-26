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
package com.turn.camino.render.functions;

import com.google.common.collect.ImmutableList;
import com.turn.camino.*;
import com.turn.camino.config.Metric;
import com.turn.camino.config.Path;
import com.turn.camino.render.Function;
import com.turn.camino.render.FunctionCallException;
import com.turn.camino.render.TimeValue;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.TimeZone;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

/**
 * Tests for metrics
 *
 * @author llo
 */
@Test
public class MetricFunctionsTest {

	private final static double EPSILON = 1e-6;
	private MetricFunctions.MetricAggregateFunction metricAgg = new MetricFunctions.MetricAggregateFunction();
	private MetricFunctions.Age age = new MetricFunctions.Age();
	private MetricFunctions.Count count = new MetricFunctions.Count();
	private MetricFunctions.Size size = new MetricFunctions.Size();
	private MetricFunctions.CreationDelay creationDelay = new MetricFunctions.CreationDelay();
	private Context context;
	private PathStatus pathStatus, emptyPathStatus;
	private long now;

	/**
	 * Set up environment
	 */
	@BeforeClass
	public void setUp() throws WrongTypeException {
		now = System.currentTimeMillis();
		Env env = mock(Env.class);
		when(env.getCurrentTime()).thenReturn(now);
		pathStatus = new PathStatus("boo", "foo", mock(Path.class), ImmutableList.of(
				new PathDetail("/data/1.txt", false, 100, now - 4000),
				new PathDetail("/data/2.txt", false, 135, now - 500),
				new PathDetail("/data/3.txt", false, 200, now - 19000)), null);
		emptyPathStatus = new PathStatus("goo", "zoo", mock(Path.class),
				ImmutableList.<PathDetail>of(), null);
		context = mock(Context.class);
		when(context.getEnv()).thenReturn(env);
		when(context.getGlobalInstanceTime()).thenReturn(now);
		when(context.getProperty("age")).thenReturn(age);
		when(context.getProperty("age", Function.class)).thenReturn(age);
		when(context.getProperty("size")).thenReturn(size);
		when(context.getProperty("size", Function.class)).thenReturn(size);
	}

	/**
	 * Test age function
	 *
	 * @throws FunctionCallException
	 */
	@Test
	public void testAge() throws FunctionCallException {
		double value = age.invoke(new Metric("minAge", "age", "min", null, 0),
				pathStatus.getPathDetails().get(1), context);
		assertEquals(value, 500, EPSILON);
	}

	/**
	 * Test computing minimum age
	 *
	 * @throws FunctionCallException
	 */
	@Test
	public void testMinAge() throws FunctionCallException {
		double value = metricAgg.invoke(new Metric("minAge", "age", "min", null, 0),
				pathStatus, context);
		assertEquals(value, 500, EPSILON);
	}

	/**
	 * Test computing maximum age
	 *
	 * @throws FunctionCallException
	 */
	@Test
	public void testMaxAge() throws FunctionCallException {
		double value = metricAgg.invoke(new Metric("maxAge", "age", "max", null, 0),
				pathStatus, context);
		assertEquals(value, 19000, EPSILON);
	}

	/**
	 * Test computing average age
	 *
	 * @throws FunctionCallException
	 */
	@Test
	public void testAvgAge() throws FunctionCallException {
		double value = metricAgg.invoke(new Metric("avgAge", "age", "avg", null, 0),
				pathStatus, context);
		assertEquals(value, (double) (4000 + 500 + 19000) / 3, EPSILON);
	}

	/**
	 * Test age on empty paths
	 *
	 * @throws FunctionCallException
	 */
	@Test
	public void testAgeEmptyPaths() throws FunctionCallException {
		double value = metricAgg.invoke(new Metric("maxAge", "age", "max", null, 0),
				emptyPathStatus, context);
		assertEquals(value, 0, EPSILON);
	}

	/**
	 * Test that an invalid function throws exception
	 *
	 * @throws FunctionCallException
	 */
	@Test(expectedExceptions = FunctionCallException.class)
	public void testInvalidFunction() throws FunctionCallException {
		metricAgg.invoke(new Metric("badAge", "foobar", "sum", null, 0), emptyPathStatus, context);
	}

	/**
	 * Test that an invalid aggregate throws exception
	 *
	 * @throws FunctionCallException
	 */
	@Test(expectedExceptions = FunctionCallException.class)
	public void testInvalidAggregate() throws FunctionCallException {
		metricAgg.invoke(new Metric("badAge", "age", "foobar", null, 0), emptyPathStatus, context);
	}

	/**
	 * Test count
	 *
	 * @throws FunctionCallException
	 */
	@Test
	public void testCount() throws FunctionCallException {
		double value = count.invoke(mock(Metric.class), pathStatus, context);
		assertEquals(value, 3, EPSILON);
	}

	/**
	 * Test count on empty path status
	 *
	 * @throws FunctionCallException
	 */
	@Test
	public void testZeroCount() throws FunctionCallException {
		double value = count.invoke(mock(Metric.class), emptyPathStatus, context);
		assertEquals(value, 0, EPSILON);
	}

	/**
	 * Test min size
	 *
	 * @throws FunctionCallException
	 */
	@Test
	public void testMinSize() throws FunctionCallException {
		double value = metricAgg.invoke(new Metric("size", "size", "min", null, 0),
				pathStatus, context);
		assertEquals(value, 100, EPSILON);
	}

	/**
	 * Test max size
	 *
	 * @throws FunctionCallException
	 */
	@Test
	public void testMaxSize() throws FunctionCallException {
		double value = metricAgg.invoke(new Metric("size", "size", "max", null, 0),
				pathStatus, context);
		assertEquals(value, 200, EPSILON);
	}

	/**
	 * Test sum size
	 *
	 * @throws FunctionCallException
	 */
	@Test
	public void testSumSize() throws FunctionCallException {
		double value = metricAgg.invoke(new Metric("size", "size", "sum", null, 0),
				pathStatus, context);
		assertEquals(value, 435, EPSILON);
	}

	/**
	 * Test average size
	 *
	 * @throws FunctionCallException
	 */
	@Test
	public void testAvgSize() throws FunctionCallException {
		double value = metricAgg.invoke(new Metric("size", "size", "avg", null, 0),
				pathStatus, context);
		assertEquals(value, 145, EPSILON);
	}

	/**
	 * Test size when no paths are found
	 *
	 * @throws FunctionCallException
	 */
	@Test
	public void testZeroSize() throws FunctionCallException {
		double value = metricAgg.invoke(new Metric("size", "size", "sum", null, 0),
				emptyPathStatus, context);
		assertEquals(value, 0, EPSILON);
	}

	/**
	 * Test creation delay for when file exists
	 *
	 * If file already exists, then creation delay is 0 regardless of creation time
	 *
	 * @throws FunctionCallException
	 */
	@Test
	public void testCreationDelayFileExists() throws FunctionCallException {

		Metric metric = new Metric("creationDelay", null, null, "creationDelay", 0);

		// test when creation time is in the past
		PathStatus testPathStatus = new PathStatus("boo", "foo", mock(Path.class),
				pathStatus.getPathDetails(),
				new TimeValue(TimeZone.getTimeZone("GMT"), now - 60 * 1000));
		double value = creationDelay.invoke(metric, testPathStatus, context);
		assertEquals(value, 0, EPSILON);

		// test when creation time is in the future
		testPathStatus = new PathStatus("boo", "foo", mock(Path.class),
				pathStatus.getPathDetails(),
				new TimeValue(TimeZone.getTimeZone("GMT"), now + 60 * 1000));
		value = creationDelay.invoke(metric, testPathStatus, context);
		assertEquals(value, 0, EPSILON);
	}

	/**
	 * Test creation delay for when file does not exist
	 *
	 * In this case, if creation time is in the future, then metric returns zero. If
	 * creation time is already past, then return milliseconds of difference between
	 * system time and creation time.
	 *
	 * @throws FunctionCallException
	 */
	@Test
	public void testCreationDelayFileNotExists() throws FunctionCallException {

		Metric metric = new Metric("creationDelay", null, null, "creationDelay", 0);

		// test when creation time is in the past
		PathStatus testPathStatus = new PathStatus("boo", "foo", mock(Path.class),
				emptyPathStatus.getPathDetails(),
				new TimeValue(TimeZone.getTimeZone("GMT"), now - 60 * 1000));
		double value = creationDelay.invoke(metric, testPathStatus, context);
		assertEquals(value, 60 * 1000, EPSILON);

		// test when creation time is in the future
		testPathStatus = new PathStatus("boo", "foo", mock(Path.class),
				emptyPathStatus.getPathDetails(),
				new TimeValue(TimeZone.getTimeZone("GMT"), now + 60 * 1000));
		value = creationDelay.invoke(metric, testPathStatus, context);
		assertEquals(value, -60 * 1000, EPSILON);
	}

	/**
	 * Test missing expectedCreationTime property
	 *
	 * @throws FunctionCallException
	 */
	@Test(expectedExceptions = FunctionCallException.class)
	public void testMissingExpectedCreationTime() throws FunctionCallException {
		creationDelay.invoke(mock(Metric.class), emptyPathStatus, context);
	}

}
