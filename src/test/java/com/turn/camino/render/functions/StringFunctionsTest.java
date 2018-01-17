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

import java.util.List;
import java.util.TimeZone;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

/**
 * Tests for string functions
 *
 * @author llo
 */
@Test
public class StringFunctionsTest {

	private Context context;
	private StringFunctions.Replace replace = new StringFunctions.Replace();
	private StringFunctions.ReplaceRegex replaceRegex = new StringFunctions.ReplaceRegex();
	private StringFunctions.Split split = new StringFunctions.Split();

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
	public void testReplace() throws FunctionCallException {
		Object result = replace.invoke(ImmutableList.of("hello person!", "person", "jessie"),
				context);
		assertEquals(result.getClass(), String.class);
		assertEquals((String) result, "hello jessie!");
		result = replace.invoke(ImmutableList.of("hello person!", "dude", "jessie"),
				context);
		assertEquals((String) result, "hello person!");
		result = replace.invoke(ImmutableList.of("hello person!", "person", ""),
				context);
		assertEquals((String) result, "hello !");
	}

	@Test
	public void testReplaceRegex() throws FunctionCallException {
		Object result = replaceRegex.invoke(ImmutableList.of("hello person!", "[eo]", "#"),
				context);
		assertEquals(result.getClass(), String.class);
		assertEquals((String) result, "h#ll# p#rs#n!");
		result = replaceRegex.invoke(ImmutableList.of("hello person!", "l", "0"),
				context);
		assertEquals((String) result, "he00o person!");
		result = replaceRegex.invoke(ImmutableList.of("hello person!", "el*", "/"),
				context);
		assertEquals((String) result, "h/o p/rson!");
		result = replaceRegex.invoke(ImmutableList.of("hello person!", "abc", "jessie"),
				context);
		assertEquals((String) result, "hello person!");
		result = replaceRegex.invoke(ImmutableList.of("hello person!", "person", ""),
				context);
		assertEquals((String) result, "hello !");
		result = replaceRegex.invoke(ImmutableList.of("not-an.identifier", "[\\-\\.]", "_"),
				context);
		assertEquals((String) result, "not_an_identifier");
	}

	@Test
	public void testSplit() throws FunctionCallException {
		List<?> list = (List) split.invoke(ImmutableList.of("a/b/c", "/"), context);
		assertEquals(list.size(), 3);
		assertEquals(list.get(0), "a");
		assertEquals(list.get(1), "b");
		assertEquals(list.get(2), "c");
	}

}
