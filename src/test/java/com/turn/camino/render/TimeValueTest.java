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

import org.testng.annotations.Test;

import java.text.ParseException;
import java.util.TimeZone;

import static org.testng.Assert.*;

/**
 * Unit test for TimeValue
 *
 * @author llo
 */
@Test
public class TimeValueTest {

	/**
	 * Test constructor and getters
	 *
	 * @throws ParseException
	 */
	@Test
	public void testConstructor() throws ParseException {
		TimeZone timeZone = TimeZone.getTimeZone("GMT");
		long time = TimeTestUtil.parseTime("2014/08/22 19:25:11.611", timeZone);
		TimeValue timeValue = new TimeValue(timeZone, time);
		assertEquals(timeValue.getTime(), time);
		assertEquals(timeValue.getTimeZone(), timeZone);
		assertNotNull(timeValue.toString());
		assertTrue(timeValue.toString().contains("2014"));
		assertTrue(timeValue.toString().contains("08"));
		assertTrue(timeValue.toString().contains("22"));
		assertTrue(timeValue.toString().contains("19"));
		assertTrue(timeValue.toString().contains("25"));
		assertTrue(timeValue.toString().contains("11"));
		assertTrue(timeValue.toString().contains("611"));
		assertTrue(timeValue.toString().contains("GMT"));
	}
}
