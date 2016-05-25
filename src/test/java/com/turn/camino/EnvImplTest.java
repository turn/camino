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

import com.turn.camino.render.Renderer;

import java.util.TimeZone;
import java.util.concurrent.ExecutorService;

import org.apache.hadoop.fs.FileSystem;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * Unit test for EnvImpl
 *
 * @author llo
 */
@Test
public class EnvImplTest {

	@Test
	public void testConstructor() {

		// create env
		TimeZone timeZone = TimeZone.getTimeZone("GMT");
		FileSystem fileSystem = mock(FileSystem.class);
		ExecutorService executorService = mock(ExecutorService.class);
		ErrorHandler errorHandler = mock(ErrorHandler.class);
		EnvImpl env = new EnvImpl(timeZone, fileSystem, executorService, errorHandler);

		// test for current time
		long t0 = System.currentTimeMillis();
		long currentTime = env.getCurrentTime();
		long t1 = System.currentTimeMillis();
		assertTrue(currentTime >= t0);
		assertTrue(currentTime <= t1);

		// test system services
		assertEquals(env.getTimeZone(), timeZone);
		assertEquals(env.getFileSystem(), fileSystem);
		Context context = env.newContext();
		assertNotNull(context);
		Renderer renderer = env.getRenderer();
		assertNotNull(renderer);

		// sleep for 10 millisecond and test context instance time
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			// ignore
		}
		Context context1 = env.newContext();
		assertTrue(context1.getGlobalInstanceTime() > context.getGlobalInstanceTime());
	}
}
