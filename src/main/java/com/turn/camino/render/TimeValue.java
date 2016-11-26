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
package com.turn.camino.render;

import com.turn.camino.annotation.Member;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Time Value
 *
 * Represents a time in a particular time zone.
 *
 * @author llo
 */
public class TimeValue {

	public final static String DATE_FORMAT_STRING = "yyyy/MM/dd HH:mm:ss.SSS z";

	private final TimeZone timeZone;
	private final long time;
	private final transient String stringValue;

	/**
	 * Constructor
	 *
	 * @param timeZone time zone
	 * @param time time in UTC milliseconds
	 */
	public TimeValue(TimeZone timeZone, long time) {
		this.timeZone = timeZone;
		this.time = time;

		// compute string representation
		DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_STRING);
		dateFormat.setTimeZone(timeZone);
		this.stringValue = dateFormat.format(new Date(time));
	}

	public TimeZone getTimeZone() {
		return timeZone;
	}

	@Member("timeMillis")
	public long getTime() {
		return time;
	}

	@Member("timeZone")
	public String getTimeZoneId() {
		return timeZone.getID();
	}

	@Override
	public String toString() {
		return this.stringValue;
	}
}

