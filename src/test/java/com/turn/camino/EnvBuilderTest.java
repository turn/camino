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
package com.turn.camino;

import java.util.TimeZone;
import java.util.concurrent.ExecutorService;

import org.apache.hadoop.fs.FileSystem;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertEquals;

/**
 * Unit test for EnvBuilder
 *
 * @author llo
 */
@Test
public class EnvBuilderTest {

	/**
	 * Test with all options
	 */
	@Test
	public void testWithAll() {
		TimeZone timeZone = TimeZone.getTimeZone("GMT");
		FileSystem fileSystem = mock(FileSystem.class);
		ExecutorService executorService = mock(ExecutorService.class);
		Env env = new EnvBuilder().withTimeZone(timeZone).withFileSystem(fileSystem)
				.withExecutorService(executorService).build();
		assertEquals(env.getFileSystem(), fileSystem);
		assertEquals(env.getTimeZone(), timeZone);
		assertEquals(env.getExecutorService(), executorService);
	}

	/**
	 * Test not specifying time zone
	 */
	@Test
	public void testDefaultTimeZone() {
		FileSystem fileSystem = mock(FileSystem.class);
		ExecutorService executorService = mock(ExecutorService.class);
		Env env = new EnvBuilder().withFileSystem(fileSystem)
				.withExecutorService(executorService).build();
		assertEquals(env.getFileSystem(), fileSystem);
		assertEquals(env.getTimeZone(), TimeZone.getDefault());
		assertEquals(env.getExecutorService(), executorService);
	}

	/**
	 * Test not specifying any options, throws exception
	 */
	@Test(expectedExceptions = NullPointerException.class)
	public void testMissingFileSystem() {
		new EnvBuilder().build();
	}

	/**
	 * Test not specifying executor service
	 */
	@Test(expectedExceptions = NullPointerException.class)
	public void testMissingExecutorService() {
		new EnvBuilder().withFileSystem(mock(FileSystem.class)).build();
	}

}
