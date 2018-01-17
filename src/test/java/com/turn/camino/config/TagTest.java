/*
 * Copyright (C) 2014-2016, Amobee Inc. All Rights Reserved.
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

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Unit test for Tag
 *
 * @author llo
 */
@Test
public class TagTest {

	/**
	 * Test reading a JSON object
	 */
	@Test
	public void testConstructor() {
		Tag tag = new Tag("pp", "qq");
		assertEquals(tag.getKey(), "pp");
		assertEquals(tag.getValue(), "qq");
	}

	/**
	 * Test firstEntry with an empty object
	 */
	@Test(expectedExceptions = NullPointerException.class)
	public void testConstructorKeyNull() {
		new Tag(null, "qq");
	}

	/**
	 * Test firstEntry with object containing two many key/value pairs
	 */
	@Test(expectedExceptions = NullPointerException.class)
	public void testConstructoValueNull() {
		new Tag("pp", null);
	}

}
