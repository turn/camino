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

import com.turn.camino.Context;
import com.turn.camino.render.Function;
import com.turn.camino.render.FunctionCallException;
import com.turn.camino.render.FunctionCallExceptionFactory;
import com.turn.camino.render.TimeValue;
import com.turn.camino.util.Validation;

import static com.turn.camino.util.Message.*;

import com.google.common.collect.ImmutableMap;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Time functions
 *
 * @author llo
 */
public class TimeFunctions implements FunctionFamily {

	private final static Validation<FunctionCallException> VALIDATION =
			new Validation<FunctionCallException>(new FunctionCallExceptionFactory());

	private final static long MILLISECS_PER_DAY = 24 * 60 * 60 * 1000L;

	/**
	 * Constructor
	 */
	public TimeFunctions() {
	}

	/**
	 * Gets functions in this family
	 *
	 * @return map of functions
	 */
	@Override
	public Map<String, Function> getFunctions() {
		return ImmutableMap.<String, Function>builder()
				.put("now", new Now())
				.put("today", new Today())
				.put("yesterday", new Yesterday())
				.put("timeAdd", new TimeAdd())
				.put("timeFormat", new TimeFormat())
				.put("timeParse", new TimeParse())
				.put("timeToUnixDay", new TimeToUnixDay())
				.put("unixDayToTime", new UnixDayToTime())
				.build();
	}

	/**
	 * Supported time units
	 */
	public static enum Unit {
		YEAR("y", Calendar.YEAR),
		MONTH("M", Calendar.MONTH),
		DAY("D", Calendar.DATE),
		HOUR("h", Calendar.HOUR),
		MINUTE("m", Calendar.MINUTE),
		SECOND("s", Calendar.SECOND),
		MILLISECOND("S", Calendar.MILLISECOND);

		private final String symbol;
		private final int unit;
		private Unit(String symbol, int unit) {
			this.symbol = symbol;
			this.unit = unit;
		}
		public String getSymbol() {
			return symbol;
		}
		public int getUnit() {
			return unit;
		}
	}

	/**
	 * Time units
	 */
	private final static Map<String, Unit> TIME_UNITS = ImmutableMap.<String, Unit>builder()
			.put(Unit.YEAR.getSymbol(), Unit.YEAR)
			.put(Unit.MONTH.getSymbol(), Unit.MONTH)
			.put(Unit.DAY.getSymbol(), Unit.DAY)
			.put(Unit.HOUR.getSymbol(), Unit.HOUR)
			.put(Unit.MINUTE.getSymbol(), Unit.MINUTE)
			.put(Unit.SECOND.getSymbol(), Unit.SECOND)
			.put(Unit.MILLISECOND.getSymbol(), Unit.MILLISECOND)
			.build();

	/**
	 * Function to returns current time
	 *
	 * Function returns current time in either system default time zone if no argument is
	 * given, or in specified time zone.
	 *
	 * @author llo
	 */
	public static class Now implements Function {

		@Override
		public Object invoke(List<?> params, Context context) throws FunctionCallException {
			VALIDATION.requireListSize(params, 0, 1, prefix("parameters"));
			long time = context.getGlobalInstanceTime();
			TimeZone timeZone;
			if (params.size() == 0) {
				timeZone = VALIDATION.requireNotNull(context.getEnv().getTimeZone(),
						full("System time zone is undefined"));
			} else {
				String timeZoneName = VALIDATION.requireType(VALIDATION.requireNotNull(
						params.get(0), prefix("Time zone")), String.class, prefix("Time zone"));
				timeZone = VALIDATION.requireNotNull(TimeZone.getTimeZone(timeZoneName),
						full(String.format("Invalid time zone %s", timeZoneName)));
			}
			return new TimeValue(timeZone, time);
		}
	}

	/**
	 * Function to return midnight of today
	 *
	 * @author llo
	 */
	public static class Today extends Now {
		@Override
		public Object invoke(List<?> params, Context context) throws FunctionCallException {
			TimeValue timeValue = (TimeValue) super.invoke(params, context);
			Calendar calendar = Calendar.getInstance(timeValue.getTimeZone());
			calendar.setTimeInMillis(timeValue.getTime());
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			return new TimeValue(timeValue.getTimeZone(), calendar.getTimeInMillis());
		}
	}

	/**
	 * Function to return midnight of yesterday
	 */
	public static class Yesterday extends Today {
		@Override
		public Object invoke(List<?> params, Context context) throws FunctionCallException {
			TimeValue timeValue = (TimeValue) super.invoke(params, context);
			Calendar calendar = Calendar.getInstance(timeValue.getTimeZone());
			calendar.setTimeInMillis(timeValue.getTime());
			calendar.add(Calendar.DATE, -1);
			return new TimeValue(timeValue.getTimeZone(), calendar.getTimeInMillis());
		}
	}

	/**
	 * Function to perform time arithmetic
	 *
	 * Adds amount of time specified by time unit to given time value
	 *
	 * @author llo
	 */
	public static class TimeAdd implements Function {

		@Override
		public Object invoke(List<?> params, Context context) throws FunctionCallException {

			// check parameters
			VALIDATION.requireListSize(params, 3, 3, prefix("paramters"));
			TimeValue timeValue = VALIDATION.requireType(params.get(0), TimeValue.class,
					prefix("timeValue"));
			long amount = VALIDATION.requireType(params.get(1), Long.class, prefix("amount"));
			String unitCode = VALIDATION.requireType(params.get(2), String.class,
					prefix("timeUnit"));

			// determine time unit
			Unit unit = VALIDATION.requireNotNull(TIME_UNITS.get(unitCode),
					full(String.format("Invalid time unit %s", unitCode)));

			// perform time arithmetic
			Calendar cal = Calendar.getInstance(timeValue.getTimeZone());
			cal.setTimeInMillis(timeValue.getTime());
			cal.add(unit.getUnit(), (int) amount);

			// return new time value
			return new TimeValue(timeValue.getTimeZone(), cal.getTimeInMillis());
		}
	}

	/**
	 * Function to format time
	 *
	 * @author llo
	 */
	public static class TimeFormat implements Function {

		@Override
		public Object invoke(List<?> params, Context context) throws FunctionCallException {

			// check parameters
			VALIDATION.requireListSize(params, 2, 2, prefix("parameters"));
			TimeValue timeValue = VALIDATION.requireType(params.get(0), TimeValue.class,
					prefix("timeValue"));
			String formatString = VALIDATION.requireType(params.get(1), String.class,
					prefix("formatString"));

			// format time
			DateFormat dateFormat = new SimpleDateFormat(formatString);
			dateFormat.setTimeZone(timeValue.getTimeZone());
			return dateFormat.format(new Date(timeValue.getTime()));
		}
	}

	/**
	 * Function to parse time
	 *
	 * @author llo
	 */
	public static class TimeParse implements Function {

		@Override
		public Object invoke(List<?> params, Context context) throws FunctionCallException {

			// check parameters
			VALIDATION.requireListSize(params, 2, 3, prefix("parameters"));
			String timeString = VALIDATION.requireType(params.get(0), String.class,
					prefix("timeString"));
			String formatString = VALIDATION.requireType(params.get(1), String.class,
					prefix("formatString"));
			TimeZone timeZone = context.getEnv().getTimeZone();
			if (params.size() == 3) {
				String timeZoneString = VALIDATION.requireType(params.get(2), String.class,
						prefix("timeZone"));
				timeZone = VALIDATION.requireNotNull(TimeZone.getTimeZone(timeZoneString),
						full(String.format("Time zone %s is unknown", timeZoneString)));
			}

			// format time
			DateFormat dateFormat = new SimpleDateFormat(formatString);
			dateFormat.setTimeZone(timeZone);
			try {
				return new TimeValue(timeZone, VALIDATION.requireNotNull(dateFormat
						.parse(timeString), full(String.format("Time '%s' is not parseable",
						timeString))).getTime());
			} catch (java.text.ParseException e) {
				throw new FunctionCallException(String.format("Cannot parse time string %s",
						timeString));
			}
		}
	}

	/**
	 * Function to convert time to unix day
	 *
	 * @author llo
	 */
	public static class TimeToUnixDay implements Function {

		@Override
		public Object invoke(List<?> params, Context context) throws FunctionCallException {
			VALIDATION.requireListSize(params, 1, 1, prefix("parameters"));
			TimeValue timeValue = VALIDATION.requireType(params.get(0), TimeValue.class,
					prefix("timeValue"));
			Calendar calendar = Calendar.getInstance(timeValue.getTimeZone());
			calendar.setTimeInMillis(timeValue.getTime());
			long offset = calendar.get(Calendar.ZONE_OFFSET);
			long dstOffset = calendar.get(Calendar.DST_OFFSET);
			long unixDay = (long) Math
					.floor((double) (timeValue.getTime() + offset + dstOffset) /
							((double) MILLISECS_PER_DAY));
			return unixDay;
		}
	}

	/**
	 * Function to convert unix day to time
	 *
	 * @author llo
	 */
	public static class UnixDayToTime implements Function {

		@Override
		public Object invoke(List<?> params, Context context) throws FunctionCallException {
			VALIDATION.requireListSize(params, 1, 2, prefix("parameters"));
			long unixDay = VALIDATION.requireType(params.get(0), Long.class, prefix("unixDay"));
			TimeZone timeZone;
			if (params.size() == 1) {
				timeZone = VALIDATION.requireNotNull(context.getEnv().getTimeZone(),
						full("System time zone is undefined"));
			} else {
				String timeZoneName = VALIDATION.requireType(VALIDATION.requireNotNull(
						params.get(1), prefix("Time zone")), String.class, prefix("Time zone"));
				timeZone = VALIDATION.requireNotNull(TimeZone.getTimeZone(timeZoneName),
						full(String.format("Invalid time zone %s", timeZoneName)));
			}
			Calendar calendar = Calendar.getInstance(timeZone);
			calendar.setTimeInMillis(0);
			long offset = calendar.get(Calendar.ZONE_OFFSET);
			long dstOffset = calendar.get(Calendar.DST_OFFSET);
			calendar.setTimeInMillis(-1 * (offset + dstOffset));
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.add(Calendar.DAY_OF_YEAR, (int) unixDay);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			return new TimeValue(timeZone, calendar.getTimeInMillis());
		}
	}

}
