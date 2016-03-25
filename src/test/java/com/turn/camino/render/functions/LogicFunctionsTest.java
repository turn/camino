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
package com.turn.camino.render.functions;

import com.google.common.collect.ImmutableList;
import com.turn.camino.Context;
import com.turn.camino.Env;
import com.turn.camino.render.FunctionCallException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.TimeZone;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.*;

/**
 * Unit test for logic functions
 *
 * @author llo
 */
@Test
public class LogicFunctionsTest {

	private Context context;
	private final LogicFunctions.Eq eq = new LogicFunctions.Eq();
	private final LogicFunctions.Ne ne = new LogicFunctions.Ne();
	private final LogicFunctions.Lt lt = new LogicFunctions.Lt();
	private final LogicFunctions.Gt gt = new LogicFunctions.Gt();
	private final LogicFunctions.LtEq ltEq = new LogicFunctions.LtEq();
	private final LogicFunctions.GtEq gtEq = new LogicFunctions.GtEq();
	private final LogicFunctions.Not not = new LogicFunctions.Not();

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
	 * Test equal function
	 *
	 * @throws FunctionCallException
	 */
	@Test
	public void testEq() throws FunctionCallException {

		// test equality
		Object result = eq.invoke(ImmutableList.of("a", "a"), context);
		assertEquals(result.getClass(), Boolean.class);
		assertTrue((Boolean) result);

		// test inequality
		result = eq.invoke(ImmutableList.of("a", "b"), context);
		assertEquals(result.getClass(), Boolean.class);
		assertFalse((Boolean) result);

		// test inequality
		result = eq.invoke(ImmutableList.of("a", 1L), context);
		assertEquals(result.getClass(), Boolean.class);
		assertFalse((Boolean) result);
	}

	/**
	 * Test not equal function
	 *
	 * @throws FunctionCallException
	 */
	@Test
	public void testNe() throws FunctionCallException {

		// test equality
		Object result = ne.invoke(ImmutableList.of("a", "a"), context);
		assertEquals(result.getClass(), Boolean.class);
		assertFalse((Boolean) result);

		// test inequality
		result = ne.invoke(ImmutableList.of("a", "b"), context);
		assertEquals(result.getClass(), Boolean.class);
		assertTrue((Boolean) result);

		// test inequality
		result = ne.invoke(ImmutableList.of("a", 1L), context);
		assertEquals(result.getClass(), Boolean.class);
		assertTrue((Boolean) result);
	}

	/**
	 * Test less-than function
	 *
	 * @throws FunctionCallException
	 */
	@Test
	public void testLt() throws FunctionCallException {

		// test 1 < 2
		Object result = lt.invoke(ImmutableList.of(1L, 2L), context);
		assertEquals(result.getClass(), Boolean.class);
		assertTrue((Boolean) result);

		// test 2 < 2
		result = lt.invoke(ImmutableList.of(2L, 2L), context);
		assertEquals(result.getClass(), Boolean.class);
		assertFalse((Boolean) result);

		// test 3 < 2
		result = lt.invoke(ImmutableList.of(3L, 2L), context);
		assertEquals(result.getClass(), Boolean.class);
		assertFalse((Boolean) result);

		// test 1 < 2
		result = lt.invoke(ImmutableList.of(1.0, 2.0), context);
		assertEquals(result.getClass(), Boolean.class);
		assertTrue((Boolean) result);

		// test 2 < 2
		result = lt.invoke(ImmutableList.of(2.0, 2.0), context);
		assertEquals(result.getClass(), Boolean.class);
		assertFalse((Boolean) result);

		// test 3 < 2
		result = lt.invoke(ImmutableList.of(3.0, 2.0), context);
		assertEquals(result.getClass(), Boolean.class);
		assertFalse((Boolean) result);

		// test 1 < 2
		result = lt.invoke(ImmutableList.of(1L, 2.0), context);
		assertEquals(result.getClass(), Boolean.class);
		assertTrue((Boolean) result);

		// test 2 < 2
		result = lt.invoke(ImmutableList.of(2L, 2.0), context);
		assertEquals(result.getClass(), Boolean.class);
		assertFalse((Boolean) result);

		// test 3 < 2
		result = lt.invoke(ImmutableList.of(3L, 2.0), context);
		assertEquals(result.getClass(), Boolean.class);
		assertFalse((Boolean) result);
	}

	/**
	 * Test greater-than function
	 *
	 * @throws FunctionCallException
	 */
	@Test
	public void testGt() throws FunctionCallException {

		// test 1 > 2
		Object result = gt.invoke(ImmutableList.of(1L, 2L), context);
		assertEquals(result.getClass(), Boolean.class);
		assertFalse((Boolean) result);

		// test 2 > 2
		result = gt.invoke(ImmutableList.of(2L, 2L), context);
		assertEquals(result.getClass(), Boolean.class);
		assertFalse((Boolean) result);

		// test 3 > 2
		result = gt.invoke(ImmutableList.of(3L, 2L), context);
		assertEquals(result.getClass(), Boolean.class);
		assertTrue((Boolean) result);

		// test 1 > 2
		result = gt.invoke(ImmutableList.of(1.0, 2.0), context);
		assertEquals(result.getClass(), Boolean.class);
		assertFalse((Boolean) result);

		// test 2 > 2
		result = gt.invoke(ImmutableList.of(2.0, 2.0), context);
		assertEquals(result.getClass(), Boolean.class);
		assertFalse((Boolean) result);

		// test 3 > 2
		result = gt.invoke(ImmutableList.of(3.0, 2.0), context);
		assertEquals(result.getClass(), Boolean.class);
		assertTrue((Boolean) result);

		// test 1 > 2
		result = gt.invoke(ImmutableList.of(1L, 2.0), context);
		assertEquals(result.getClass(), Boolean.class);
		assertFalse((Boolean) result);

		// test 2 > 2
		result = gt.invoke(ImmutableList.of(2L, 2.0), context);
		assertEquals(result.getClass(), Boolean.class);
		assertFalse((Boolean) result);

		// test 3 > 2
		result = gt.invoke(ImmutableList.of(3L, 2.0), context);
		assertEquals(result.getClass(), Boolean.class);
		assertTrue((Boolean) result);
	}

	/**
	 * Test less-than-or-equal function
	 *
	 * @throws FunctionCallException
	 */
	@Test
	public void testLtEq() throws FunctionCallException {

		// test 1 <= 2
		Object result = ltEq.invoke(ImmutableList.of(1L, 2L), context);
		assertEquals(result.getClass(), Boolean.class);
		assertTrue((Boolean) result);

		// test 2 <= 2
		result = ltEq.invoke(ImmutableList.of(2L, 2L), context);
		assertEquals(result.getClass(), Boolean.class);
		assertTrue((Boolean) result);

		// test 3 <= 2
		result = ltEq.invoke(ImmutableList.of(3L, 2L), context);
		assertEquals(result.getClass(), Boolean.class);
		assertFalse((Boolean) result);

		// test 1 <= 2
		result = ltEq.invoke(ImmutableList.of(1.0, 2.0), context);
		assertEquals(result.getClass(), Boolean.class);
		assertTrue((Boolean) result);

		// test 2 <= 2
		result = ltEq.invoke(ImmutableList.of(2.0, 2.0), context);
		assertEquals(result.getClass(), Boolean.class);
		assertTrue((Boolean) result);

		// test 3 <= 2
		result = ltEq.invoke(ImmutableList.of(3.0, 2.0), context);
		assertEquals(result.getClass(), Boolean.class);
		assertFalse((Boolean) result);

		// test 1 <= 2
		result = ltEq.invoke(ImmutableList.of(1L, 2.0), context);
		assertEquals(result.getClass(), Boolean.class);
		assertTrue((Boolean) result);

		// test 2 <= 2
		result = ltEq.invoke(ImmutableList.of(2L, 2.0), context);
		assertEquals(result.getClass(), Boolean.class);
		assertTrue((Boolean) result);

		// test 3 <= 2
		result = ltEq.invoke(ImmutableList.of(3L, 2.0), context);
		assertEquals(result.getClass(), Boolean.class);
		assertFalse((Boolean) result);
	}

	/**
	 * Test greater-than-or-equal function
	 *
	 * @throws FunctionCallException
	 */
	@Test
	public void testGtEq() throws FunctionCallException {

		// test 1 >= 2
		Object result = gtEq.invoke(ImmutableList.of(1L, 2L), context);
		assertEquals(result.getClass(), Boolean.class);
		assertFalse((Boolean) result);

		// test 2 >= 2
		result = gtEq.invoke(ImmutableList.of(2L, 2L), context);
		assertEquals(result.getClass(), Boolean.class);
		assertTrue((Boolean) result);

		// test 3 >= 2
		result = gtEq.invoke(ImmutableList.of(3L, 2L), context);
		assertEquals(result.getClass(), Boolean.class);
		assertTrue((Boolean) result);

		// test 1 >= 2
		result = gtEq.invoke(ImmutableList.of(1.0, 2.0), context);
		assertEquals(result.getClass(), Boolean.class);
		assertFalse((Boolean) result);

		// test 2 >= 2
		result = gtEq.invoke(ImmutableList.of(2.0, 2.0), context);
		assertEquals(result.getClass(), Boolean.class);
		assertTrue((Boolean) result);

		// test 3 >= 2
		result = gtEq.invoke(ImmutableList.of(3.0, 2.0), context);
		assertEquals(result.getClass(), Boolean.class);
		assertTrue((Boolean) result);

		// test 1 >= 2
		result = gtEq.invoke(ImmutableList.of(1L, 2.0), context);
		assertEquals(result.getClass(), Boolean.class);
		assertFalse((Boolean) result);

		// test 2 >= 2
		result = gtEq.invoke(ImmutableList.of(2L, 2.0), context);
		assertEquals(result.getClass(), Boolean.class);
		assertTrue((Boolean) result);

		// test 3 >= 2
		result = gtEq.invoke(ImmutableList.of(3L, 2.0), context);
		assertEquals(result.getClass(), Boolean.class);
		assertTrue((Boolean) result);
	}

	/**
	 * Test not function
	 *
	 * @throws FunctionCallException
	 */
	@Test
	public void testNot() throws FunctionCallException {

		// not true = false
		Object result = not.invoke(ImmutableList.of(true), context);
		assertEquals(result.getClass(), Boolean.class);
		assertFalse((Boolean) result);

		// not false = true
		result = not.invoke(ImmutableList.of(false), context);
		assertEquals(result.getClass(), Boolean.class);
		assertTrue((Boolean) result);
	}

}
