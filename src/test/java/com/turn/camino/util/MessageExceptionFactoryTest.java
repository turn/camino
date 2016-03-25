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

import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * Unit test for MessageExceptionFactory
 *
 * @author llo
 */
@Test
public class MessageExceptionFactoryTest {

	private MessageExceptionFactory<RuntimeException> messageExceptionFactory =
			new MessageExceptionFactory<RuntimeException>() {
				@Override
				public RuntimeException newException(String message) {
					return new RuntimeException(message);
				}
			};

	/**
	 * Test creating new exception with message only
	 */
	@Test
	public void testMessageOnly() {
		RuntimeException runtimeException = messageExceptionFactory.newException("hello!");
		assertEquals(runtimeException.getMessage(), "hello!");
	}

	/**
	 * Test creating new exception with throwable only
	 *
	 * The message of the cause throwable is used as message of the exception
	 */
	@Test
	public void testThrowableOnly() {
		RuntimeException runtimeException = messageExceptionFactory.newException(
				new IllegalArgumentException("foobar"));
		assertEquals(runtimeException.getMessage(), "foobar");
	}

	/**
	 * Test creating new exception with both message and cause
	 *
	 * Only the message is used. The cause is discarded.
	 */
	@Test
	public void testMessageAndThrowable() {
		RuntimeException runtimeException = messageExceptionFactory.newException("byebye",
				new IllegalArgumentException("foobar"));
		assertEquals(runtimeException.getMessage(), "byebye");
	}

}
