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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Utility methods for time-related tests
 *
 * @author llo
 */
public class TimeTestUtil {

	/**
	 * Parses time string into epoch milliseconds
	 *
	 * @param timeString time string
	 * @param timeZone time zone to convert to
	 * @return epoch milliseconds
	 * @throws ParseException
	 */
	public static long parseTime(String timeString, TimeZone timeZone) throws ParseException {
		DateFormat dateFormat;
		if (timeString.contains(".")) {
			dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
		} else {
			dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		}
		dateFormat.setTimeZone(timeZone);
		Date date = dateFormat.parse(timeString);
		return date != null ? date.getTime() : -1;
	}

}
