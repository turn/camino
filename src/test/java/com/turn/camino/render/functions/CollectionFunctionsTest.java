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

import com.google.common.collect.ImmutableMap;
import com.turn.camino.Context;
import com.turn.camino.Env;
import com.turn.camino.render.FunctionCallException;

import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.google.common.collect.ImmutableList;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

/**
 * Unit test for CollectionFunctions
 *
 * @author llo
 */
@Test
public class CollectionFunctionsTest {

	private Context context;
	private CollectionFunctions.ListCreate listCreate = new CollectionFunctions.ListCreate();
	private CollectionFunctions.ListGet listGet = new CollectionFunctions.ListGet();
	private CollectionFunctions.ListFirst listFirst = new CollectionFunctions.ListFirst();
	private CollectionFunctions.ListLast listLast = new CollectionFunctions.ListLast();
	private CollectionFunctions.DictCreate dictCreate = new CollectionFunctions.DictCreate();
	private CollectionFunctions.DictGet dictGet = new CollectionFunctions.DictGet();

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

	/**
	 * Test creating a list
	 */
	@Test
	public void testListCreate() throws FunctionCallException {
		Object result = listCreate.invoke(ImmutableList.of("a", "b", "c"), context);
		assertTrue(result instanceof List);
		List<?> list = (List<?>) result;
		assertEquals(list.size(), 3);
		assertEquals(list.get(0), "a");
		assertEquals(list.get(1), "b");
		assertEquals(list.get(2), "c");
	}

	/**
	 * Test getting an element from a list
	 *
	 * @throws FunctionCallException
	 */
	@Test
	public void testListGet() throws FunctionCallException {
		List<?> list = ImmutableList.of(1L, 2L, 3L, 4L);
		Object result = listGet.invoke(ImmutableList.of(list, 0L), context);
		assertTrue(result instanceof Long);
		Long value = (Long) result;
		assertEquals(value.longValue(), 1L);
		result = listGet.invoke(ImmutableList.of(list, 2L), context);
		assertTrue(result instanceof Long);
		value = (Long) result;
		assertEquals(value.longValue(), 3L);
	}

	/**
	 * Test getting an element at negative index
	 *
	 * @throws FunctionCallException
	 */
	@Test(expectedExceptions = FunctionCallException.class)
	public void testListGetNegativeIndex() throws FunctionCallException {
		List<?> list = ImmutableList.of(1L, 2L, 3L, 4L);
		listGet.invoke(ImmutableList.of(list, -1L), context);
	}

	/**
	 * Test getting an element at out-of-bound index
	 *
	 * @throws FunctionCallException
	 */
	@Test(expectedExceptions = FunctionCallException.class)
	public void testListGetOutofBoundIndex() throws FunctionCallException {
		List<?> list = ImmutableList.of(1L, 2L, 3L, 4L);
		listGet.invoke(ImmutableList.of(list, 10L), context);
	}

	/**
	 * Test getting first element from a list
	 *
	 * @throws FunctionCallException
	 */
	@Test
	public void testListFirst() throws FunctionCallException {

		// test with a non-empty list
		List<?> list = ImmutableList.of(1L, 2L, 3L, 4L);
		Object result = listFirst.invoke(ImmutableList.of(list), context);
		assertTrue(result instanceof Long);
		assertEquals(((Long) result).longValue(), 1L);

		// test with empty list and default value
		list = ImmutableList.of();
		result = listFirst.invoke(ImmutableList.of(list, -2L), context);
		assertTrue(result instanceof Long);
		assertEquals(((Long) result).longValue(), -2L);
	}

	/**
	 * Test getting first element from an empty list without a default value
	 *
	 * Expects to throw an exception
	 *
	 * @throws FunctionCallException
	 */
	@Test(expectedExceptions = FunctionCallException.class)
	public void testListFirstEmptyList() throws FunctionCallException {
		List<?> list = ImmutableList.of();
		listFirst.invoke(ImmutableList.of(list), context);
	}

	/**
	 * Test getting last element from a list
	 *
	 * @throws FunctionCallException
	 */
	@Test
	public void testListLast() throws FunctionCallException {

		// test with a non-empty list
		List<?> list = ImmutableList.of(1L, 2L, 3L, 4L);
		Object result = listLast.invoke(ImmutableList.of(list), context);
		assertTrue(result instanceof Long);
		assertEquals(((Long) result).longValue(), 4L);

		// test with empty list and default value
		list = ImmutableList.of();
		result = listFirst.invoke(ImmutableList.of(list, -2L), context);
		assertTrue(result instanceof Long);
		assertEquals(((Long) result).longValue(), -2L);
	}

	/**
	 * Test getting first element from an empty list without a default value
	 *
	 * Expects to throw an exception
	 *
	 * @throws FunctionCallException
	 */
	@Test(expectedExceptions = FunctionCallException.class)
	public void testListLastEmptyList() throws FunctionCallException {
		List<?> list = ImmutableList.of();
		listLast.invoke(ImmutableList.of(list), context);
	}

	/**
	 * Test creating a dictionary (map)
	 *
	 * @throws FunctionCallException
	 */
	@Test
	public void testDictCreate() throws FunctionCallException {
		Object result = dictCreate.invoke(ImmutableList.of("a", 10L, "b", 20L), context);
		assertTrue(result instanceof Map);
		Map<?,?> dict = (Map<?,?>) result;
		assertEquals(dict.size(), 2);
		assertEquals(dict.get("a"), 10L);
		assertEquals(dict.get("b"), 20L);
		assertNull(dict.get("c"));
	}

	/**
	 * Test creating a dictionary with missing value
	 *
	 * @throws FunctionCallException
	 */
	@Test(expectedExceptions = FunctionCallException.class)
	public void testDictCreateMissingValue() throws FunctionCallException {
		dictCreate.invoke(ImmutableList.of("a", 10L, "b", 20L, "c"), context);
	}

	/**
	 * Test getting values from a dictionary
	 *
	 * @throws FunctionCallException
	 */
	@Test
	public void testDictGet() throws FunctionCallException {
		Map<?,?> dict = ImmutableMap.builder().put("a", 14.5).put("b", 11.3).build();
		Object result = dictGet.invoke(ImmutableList.of(dict, "a"), context);
		assertTrue(result instanceof Double);
		assertEquals((Double) result, 14.5, 1e-6);
		result = dictGet.invoke(ImmutableList.of(dict, "b"), context);
		assertTrue(result instanceof Double);
		assertEquals((Double) result, 11.3, 1e-6);
		result = dictGet.invoke(ImmutableList.of(dict, "c"), context);
		assertNull(result);
	}
}
