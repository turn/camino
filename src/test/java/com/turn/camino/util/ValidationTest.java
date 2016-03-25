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
package com.turn.camino.util;

import java.util.List;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Unit test for Validation
 *
 * @author llo
 */
@Test
public class ValidationTest {

	private Validation<RuntimeException> validation = new Validation<RuntimeException>(
			new MessageExceptionFactory<RuntimeException>() {
				@Override
				public RuntimeException newException(String message) {
					return new RuntimeException(message);
				}
			});

	/**
	 * Test require object to be type
	 */
	@Test
	public void testRequireType() {
		Object object = "bee";
		String value = validation.requireType(object, String.class, Message.prefix("hello"));
		assertEquals(value, "bee");
	}

	/**
	 * Test require object to be superclass type
	 */
	@Test
	public void testRequireTypeSuperclass() {
		Object object = 234;
		Number value = validation.requireType(object, Number.class, Message.prefix("hello"));
		assertEquals(value.intValue(), 234);
	}

	/**
	 * Test require object to be incompatible type
	 */
	@Test(expectedExceptions = RuntimeException.class)
	public void testRequireTypeException() {
		Object object = new ValidationTest();
		validation.requireType(object, Double.class, Message.prefix("hello"));
	}

	/**
	 * Test request object to be type
	 */
	@Test
	public void testRequestType() {
		Object object = "baa";
		Optional<String> optional = validation.requestType(object, String.class);
		assertTrue(optional.isPresent());
		assertEquals(optional.get(), "baa");
		optional = validation.requestType(123L, String.class);
		assertFalse(optional.isPresent());
	}

	/**
	 * Test list size
	 */
	@Test
	public void testListSize() {
		List<?> list = ImmutableList.of(1, 2, 3);
		List<?> list1 = validation.requireListSize(list, 2, 4, Message.prefix("hello"));
		assertEquals(list, list1);
	}

	/**
	 * Test too few elements in list
	 */
	@Test(expectedExceptions = RuntimeException.class)
	public void testListSizeTooFew() {
		List<?> list = ImmutableList.of(1);
		validation.requireListSize(list, 2, 4, Message.prefix("hello"));
	}

	/**
	 * Test too many elements in list
	 */
	@Test(expectedExceptions = RuntimeException.class)
	public void testListSizeTooMany() {
		List<?> list = ImmutableList.of(1, 2, 3, 4, 5, 6);
		validation.requireListSize(list, 2, 4, Message.prefix("hello"));
	}

	/**
	 * Test not null
	 */
	@Test
	public void testNotNull() {
		String value = validation.requireNotNull("hello", Message.prefix("hello"));
		assertEquals(value, "hello");
	}

	/**
	 * Test not null throwing exception
	 */
	@Test(expectedExceptions = RuntimeException.class)
	public void testNotNullException() {
		validation.requireNotNull(null, Message.prefix("hello"));
	}

}
