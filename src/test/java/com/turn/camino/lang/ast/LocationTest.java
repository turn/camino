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
package com.turn.camino.lang.ast;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * Unit test for Location
 *
 * @author llo
 */
@Test
public class LocationTest {

	/**
	 * Creates Location object and performs validation
	 */
	@Test
	public void testLocation() {
		Location location = new Location(4, 6);
		assertEquals(location.getColumn(), 6);
		assertEquals(location.getLine(), 4);
		assertEquals(location, new Location(4, 6));
		assertNotEquals(location, new Location(4, 7));
		assertNotEquals(location, new Location(5, 6));
		assertFalse(location.equals("123"));
		assertFalse(location.equals(null));
		assertEquals(location.toString(), "4:6");
	}

}
