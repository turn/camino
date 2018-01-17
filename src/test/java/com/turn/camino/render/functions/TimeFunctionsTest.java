/*
 * Copyright (C) 2014-2018, Amobee Inc. All Rights Reserved.
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

import com.turn.camino.Context;
import com.turn.camino.Env;
import com.turn.camino.render.FunctionCallException;
import com.turn.camino.render.TimeTestUtil;
import com.turn.camino.render.TimeValue;

import java.text.ParseException;
import java.util.Collections;
import java.util.TimeZone;

import com.google.common.collect.ImmutableList;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Unit test for time functions
 *
 * @author llo
 */
@Test
public class TimeFunctionsTest {

	private TimeZone timeZoneGmt = TimeZone.getTimeZone("GMT");
	private TimeZone timeZoneUsEastern = TimeZone.getTimeZone("US/Eastern");
	private long currentTime;
	private long midnightGmt;
	private long midnightUsEastern;
	private Context context;
	private TimeValue timeValue;

	private final TimeFunctions.Now now = new TimeFunctions.Now();
	private final TimeFunctions.Today today = new TimeFunctions.Today();
	private final TimeFunctions.Yesterday yesterday = new TimeFunctions.Yesterday();
	private final TimeFunctions.TimeAdd timeAdd = new TimeFunctions.TimeAdd();
	private final TimeFunctions.TimeFormat timeFormat = new TimeFunctions.TimeFormat();
	private final TimeFunctions.TimeParse timeParse = new TimeFunctions.TimeParse();
	private final TimeFunctions.TimeToUnixDay timeToUnixDay = new TimeFunctions.TimeToUnixDay();
	private final TimeFunctions.UnixDayToTime unixDayToTime = new TimeFunctions.UnixDayToTime();

	/**
	 * Sets up test environment
	 */
	@BeforeClass
	public void setUp() throws ParseException {

		// set up testing parameters
		currentTime = TimeTestUtil.parseTime("2014/08/20 20:18:21",
			TimeZone.getTimeZone("US/Pacific"));
		midnightGmt = TimeTestUtil.parseTime("2014/08/21 00:00:00", timeZoneGmt);
		midnightUsEastern = TimeTestUtil.parseTime("2014/08/20 00:00:00", timeZoneUsEastern);
		timeValue = new TimeValue(timeZoneGmt, TimeTestUtil.parseTime("2014/08/20 03:12:15.384",
				timeZoneGmt));

		// mock environment
		context = mock(Context.class);
		Env env = mock(Env.class);
		when(context.getEnv()).thenReturn(env);
		when(context.getGlobalInstanceTime()).thenReturn(currentTime);
		when(env.getCurrentTime()).thenReturn(currentTime);
		when(env.getTimeZone()).thenReturn(timeZoneGmt);
	}

	/**
	 * Test now() with no argument
	 *
	 * @throws com.turn.camino.render.FunctionCallException
	 */
	@Test
	public void testNowNoArg() throws FunctionCallException {
		Object value = now.invoke(Collections.emptyList(), context);
		assertEquals(value.getClass(), TimeValue.class);
		TimeValue timeValue = (TimeValue) value;
		assertEquals(timeValue.getTime(), currentTime);
		assertEquals(timeValue.getTimeZone(), timeZoneGmt);
	}

	/**
	 * Test now() with time zone
	 *
	 * @throws FunctionCallException
	 */
	@Test
	public void testNowWithTimeZone() throws FunctionCallException {
		Object value = now.invoke(Collections.singletonList("US/Eastern"), context);
		assertEquals(value.getClass(), TimeValue.class);
		TimeValue timeValue = (TimeValue) value;
		assertEquals(timeValue.getTime(), currentTime);
		assertEquals(timeValue.getTimeZone(), timeZoneUsEastern);
	}

	/**
	 * Test today() with no argument
	 *
	 * @throws FunctionCallException
	 */
	@Test
	public void testTodayNoArg() throws FunctionCallException {
		Object value = today.invoke(Collections.emptyList(), context);
		assertEquals(value.getClass(), TimeValue.class);
		TimeValue timeValue = (TimeValue) value;
		assertEquals(timeValue.getTime(), midnightGmt);
		assertEquals(timeValue.getTimeZone(), timeZoneGmt);
	}

	/**
	 * Test today() with time zone
	 *
	 * @throws FunctionCallException
	 */
	@Test
	public void testTodayWithTimeZone() throws FunctionCallException {
		Object value = today.invoke(Collections.singletonList("US/Eastern"), context);
		assertEquals(value.getClass(), TimeValue.class);
		TimeValue timeValue = (TimeValue) value;
		assertEquals(timeValue.getTime(), midnightUsEastern);
		assertEquals(timeValue.getTimeZone(), timeZoneUsEastern);
	}

	/**
	 * Test yesterday() with time zone
	 *
	 * @throws FunctionCallException
	 * @throws ParseException
	 */
	@Test
	public void testYesterday() throws FunctionCallException, ParseException {
		TimeValue timeValue = (TimeValue) yesterday.invoke(ImmutableList.of("US/Eastern"),
				context);
		assertEquals(timeValue.getTimeZone(), timeZoneUsEastern);
		assertEquals(timeValue.getTime(), TimeTestUtil.parseTime("2014/08/19 00:00:00",
				timeZoneUsEastern));
	}

	/**
	 * Test adding milliseconds to time value
	 *
	 * @throws FunctionCallException
	 * @throws ParseException
	 */
	@Test
	public void testTimeAddMillisecond() throws FunctionCallException, ParseException {
		Object outcome = timeAdd.invoke(ImmutableList.of(timeValue, 55L,
				TimeFunctions.Unit.MILLISECOND.getSymbol()), context);
		assertNotNull(outcome);
		assertEquals(outcome.getClass(), TimeValue.class);
		TimeValue timeValue1 = (TimeValue) outcome;
		assertEquals(timeValue1.getTime(), TimeTestUtil.parseTime("2014/08/20 03:12:15.439",
				timeZoneGmt));
	}

	/**
	 * Test adding seconds to time value
	 *
	 * @throws FunctionCallException
	 * @throws ParseException
	 */
	@Test
	public void testTimeAddSecond() throws FunctionCallException, ParseException {
		TimeValue timeValue1 = (TimeValue) timeAdd.invoke(ImmutableList.of(timeValue, 21L,
				TimeFunctions.Unit.SECOND.getSymbol()), context);
		assertEquals(timeValue1.getTime(), TimeTestUtil.parseTime("2014/08/20 03:12:36.384",
				timeZoneGmt));
	}

	/**
	 * Test adding minutes to time value
	 *
	 * @throws FunctionCallException
	 * @throws ParseException
	 */
	@Test
	public void testTimeAddMinute() throws FunctionCallException, ParseException {
		TimeValue timeValue1 = (TimeValue) timeAdd.invoke(ImmutableList.of(timeValue, 51L,
				TimeFunctions.Unit.MINUTE.getSymbol()), context);
		assertEquals(timeValue1.getTime(), TimeTestUtil.parseTime("2014/08/20 04:03:15.384",
				timeZoneGmt));
	}

	/**
	 * Test adding hours to time value
	 *
	 * @throws FunctionCallException
	 * @throws ParseException
	 */
	@Test
	public void testTimeAddHour() throws FunctionCallException, ParseException {
		TimeValue timeValue1 = (TimeValue) timeAdd.invoke(ImmutableList.of(timeValue, 8L,
				TimeFunctions.Unit.HOUR.getSymbol()), context);
		assertEquals(timeValue1.getTime(), TimeTestUtil.parseTime("2014/08/20 11:12:15.384",
				timeZoneGmt));
	}

	/**
	 * Test adding days to time value
	 *
	 * @throws FunctionCallException
	 * @throws ParseException
	 */
	@Test
	public void testTimeAddDay() throws FunctionCallException, ParseException {
		TimeValue timeValue1 = (TimeValue) timeAdd.invoke(ImmutableList.of(timeValue, 12L,
				TimeFunctions.Unit.DAY.getSymbol()), context);
		assertEquals(timeValue1.getTime(), TimeTestUtil.parseTime("2014/09/01 03:12:15.384",
				timeZoneGmt));
	}

	/**
	 * Test adding months to time value
	 *
	 * @throws FunctionCallException
	 * @throws ParseException
	 */
	@Test
	public void testTimeAddMonth() throws FunctionCallException, ParseException {
		TimeValue timeValue1 = (TimeValue) timeAdd.invoke(ImmutableList.of(timeValue, 6L,
				TimeFunctions.Unit.MONTH.getSymbol()), context);
		assertEquals(timeValue1.getTime(), TimeTestUtil.parseTime("2015/02/20 03:12:15.384",
				timeZoneGmt));
	}

	/**
	 * Test adding years to time value
	 *
	 * @throws FunctionCallException
	 * @throws ParseException
	 */
	@Test
	public void testTimeAddYear() throws FunctionCallException, ParseException {
		TimeValue timeValue1 = (TimeValue) timeAdd.invoke(ImmutableList.of(timeValue, 2L,
				TimeFunctions.Unit.YEAR.getSymbol()), context);
		assertEquals(timeValue1.getTime(), TimeTestUtil.parseTime("2016/08/20 03:12:15.384",
				timeZoneGmt));
	}

	/**
	 * Test adding negative arguments to time value
	 *
	 * @throws FunctionCallException
	 * @throws ParseException
	 */
	@Test
	public void testTimeAddNegative() throws FunctionCallException, ParseException {
		TimeValue timeValue1 = (TimeValue) timeAdd.invoke(ImmutableList.of(timeValue, -74L,
				TimeFunctions.Unit.MINUTE.getSymbol()), context);
		assertEquals(timeValue1.getTime(), TimeTestUtil.parseTime("2014/08/20 01:58:15.384",
				timeZoneGmt));
	}

	/**
	 * Test timeFormat()
	 *
	 * @throws ParseException
	 * @throws FunctionCallException
	 */
	@Test
	public void testTimeFormat() throws ParseException, FunctionCallException {

		// set up test inputs
		TimeZone timeZone = TimeZone.getTimeZone("GMT");
		long time = TimeTestUtil.parseTime("2014/08/20 14:38:02", timeZone);
		TimeValue timeValue = new TimeValue(timeZone, time);

		// call timeFormat() and test for correctness
		Object outcome = timeFormat.invoke(ImmutableList.of(timeValue, "yyyy-MM-dd_HH-mm-ss"),
				mock(Context.class));
		assertEquals(outcome.getClass(), String.class);
		String formatted = (String) outcome;
		assertNotNull(formatted);
		assertEquals(formatted, "2014-08-20_14-38-02");
	}

	/**
	 * Test parsing time string
	 *
	 * @throws FunctionCallException
	 * @throws ParseException
	 */
	@Test
	public void testTimeParse() throws FunctionCallException, ParseException {
		Object outcome = timeParse.invoke(ImmutableList.of("2014/08/20 14:38:02",
				"yyyy/MM/dd HH:mm:ss"), context);
		assertEquals(outcome.getClass(), TimeValue.class);
		TimeValue timeValue1 = (TimeValue) outcome;
		assertEquals(timeValue1.getTime(), TimeTestUtil.parseTime("2014/08/20 14:38:02",
				timeZoneGmt));
	}

	/**
	 * Test parsing time string with time zone
	 *
	 * @throws FunctionCallException
	 * @throws ParseException
	 */
	@Test
	public void testTimeParseWithTimeZone() throws FunctionCallException, ParseException {
		TimeZone timeZone = TimeZone.getTimeZone("US/Pacific");
		Object outcome = timeParse.invoke(ImmutableList.of("2014/08/20 14:38:02",
				"yyyy/MM/dd HH:mm:ss", "US/Pacific"), context);
		assertEquals(outcome.getClass(), TimeValue.class);
		TimeValue timeValue1 = (TimeValue) outcome;
		assertEquals(timeValue1.getTime(), TimeTestUtil.parseTime("2014/08/20 14:38:02",
				timeZone));
	}

	/**
	 * Test incorrect format string
	 *
	 * @throws FunctionCallException
	 */
	@Test(expectedExceptions = FunctionCallException.class)
	public void testTimeParseWrongFormat() throws FunctionCallException {
		timeParse.invoke(ImmutableList.of("2014/08/20", "yyyy-MM-dd"), context);
	}

	/**
	 * Test incorrect time zone
	 *
	 * @throws FunctionCallException
	 */
	@Test(expectedExceptions = FunctionCallException.class)
	public void testTimeParseWrongTimeZone() throws FunctionCallException {
		timeParse.invoke(ImmutableList.of("2014/08/20", "yyyy-MM-dd", "MarsTimeZone"), context);
	}

	/**
	 * Test too few arguments
	 *
	 * @throws FunctionCallException
	 */
	@Test(expectedExceptions = FunctionCallException.class)
	public void testTooFewArgs() throws FunctionCallException {
		timeFormat.invoke(ImmutableList.of("2014/08/20"), mock(Context.class));
	}

	/**
	 * Test too many arguments
	 *
	 * @throws FunctionCallException
	 */
	@Test(expectedExceptions = FunctionCallException.class)
	public void testTooManyArgs() throws FunctionCallException {
		timeFormat.invoke(ImmutableList.of("2014/08/20", "yyyy/MM/dd", "extra"),
				mock(Context.class));
	}

	/**
	 * Test converting time value to unix day
	 *
	 * @throws FunctionCallException
	 */
	@Test
	public void testTimeToUnixDay() throws FunctionCallException {
		Object value = timeToUnixDay.invoke(ImmutableList.of(timeValue), mock(Context.class));
		assertEquals(value.getClass(), Long.class);
		assertEquals(Long.class.cast(value).longValue(), 16302L);
	}

	/**
	 * Test converting unix day to time value
	 *
	 * @throws FunctionCallException
	 */
	@Test
	public void testUnixDayToTime() throws FunctionCallException {
		Object value = unixDayToTime.invoke(ImmutableList.of(16303L, "GMT"), mock(Context.class));
		assertEquals(value.getClass(), TimeValue.class);
		TimeValue timeValue = TimeValue.class.cast(value);
		assertEquals(timeValue.getTimeZone(), timeZoneGmt);
		assertEquals(timeValue.getTime(), midnightGmt);
	}

}
