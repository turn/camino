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
package com.turn.camino.render.functions;

import com.turn.camino.Context;
import com.turn.camino.Env;
import com.turn.camino.render.FunctionCallException;

import com.google.common.collect.ImmutableList;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.TimeZone;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;


/**
 * Tests common functions
 *
 * @author llo
 */
public class CommonFunctionsTest {

	private Context context;
	private CommonFunctions.Compare compare = new CommonFunctions.Compare();

	/**
	 * Set up environment
	 */
	@BeforeClass
	public void setUp() {
		// mock environment
		context = mock(Context.class);
		Env env = mock(Env.class);
		when(context.getEnv()).thenReturn(env);
		when(env.getCurrentTime()).thenReturn(1409389256296L);
		when(env.getTimeZone()).thenReturn(TimeZone.getTimeZone("GMT"));
	}

	@Test
	public void testCompare() throws FunctionCallException {
		Object result = compare.invoke(ImmutableList.of("a", "b"), context);
		assertTrue(result instanceof Integer);
		assertTrue(((Integer) result) < 0);
		result = compare.invoke(ImmutableList.of("b", "a"), context);
		assertTrue(result instanceof Integer);
		assertTrue(((Integer) result) > 0);
		result = compare.invoke(ImmutableList.of("x", "x"), context);
		assertTrue(result instanceof Integer);
		assertEquals(result, 0);
		result = compare.invoke(ImmutableList.of(6, 4), context);
		assertTrue(result instanceof Integer);
		assertTrue(((Integer) result) > 0);
	}

}
