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

import com.turn.camino.Context;
import com.turn.camino.Env;
import com.turn.camino.render.FunctionCallException;

import java.util.TimeZone;

import com.google.common.collect.ImmutableList;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.*;

/**
 * Unit test for math functions
 *
 * @author llo
 */
@Test
public class MathFunctionsTest {

	private Context context;
	private final MathFunctions.Add add = new MathFunctions.Add();
	private final MathFunctions.Subtract subtract = new MathFunctions.Subtract();
	private final MathFunctions.Multiply multiply = new MathFunctions.Multiply();
	private final MathFunctions.Divide divide = new MathFunctions.Divide();

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
	 * Test add function
	 *
	 * @throws FunctionCallException
	 */
	@Test
	public void testAdd() throws FunctionCallException {

		// test adding longs
		Object value = add.invoke(ImmutableList.of(2L, 4L), context);
		assertEquals(value.getClass(), Long.class);
		long longValue = (Long) value;
		assertEquals(longValue, 6L);

		// test adding doubles
		value = add.invoke(ImmutableList.of(2.5, 4.6), context);
		assertEquals(value.getClass(), Double.class);
		double doubleValue = (Double) value;
		assertEquals(doubleValue, 7.1, 1e-6);

		// test adding long and double
		value = add.invoke(ImmutableList.of(2L, 4.6), context);
		assertEquals(value.getClass(), Double.class);
		doubleValue = (Double) value;
		assertEquals(doubleValue, 6.6, 1e-6);
	}

	/**
	 * Test subtract function
	 *
	 * @throws FunctionCallException
	 */
	@Test
	public void testSubtract() throws FunctionCallException {

		// test subtracting longs
		Object value = subtract.invoke(ImmutableList.of(2L, 4L), context);
		assertEquals(value.getClass(), Long.class);
		long longValue = (Long) value;
		assertEquals(longValue, -2L);

		// test subtracting doubles
		value = subtract.invoke(ImmutableList.of(4.5, 2.6), context);
		assertEquals(value.getClass(), Double.class);
		double doubleValue = (Double) value;
		assertEquals(doubleValue, 1.9, 1e-6);

		// test subtracting long and double
		value = subtract.invoke(ImmutableList.of(5L, 4.6), context);
		assertEquals(value.getClass(), Double.class);
		doubleValue = (Double) value;
		assertEquals(doubleValue, 0.4, 1e-6);
	}

	/**
	 * Test multiply function
	 *
	 * @throws FunctionCallException
	 */
	@Test
	public void testMultiply() throws FunctionCallException {

		// test multiplying longs
		Object value = multiply.invoke(ImmutableList.of(2L, 4L), context);
		assertEquals(value.getClass(), Long.class);
		long longValue = (Long) value;
		assertEquals(longValue, 8L);

		// test multiplying doubles
		value = multiply.invoke(ImmutableList.of(2.5, 4.6), context);
		assertEquals(value.getClass(), Double.class);
		double doubleValue = (Double) value;
		assertEquals(doubleValue, 11.5, 1e-6);

		// test multiplying long and double
		value = multiply.invoke(ImmutableList.of(2L, 4.6), context);
		assertEquals(value.getClass(), Double.class);
		doubleValue = (Double) value;
		assertEquals(doubleValue, 9.2, 1e-6);
	}

	/**
	 * Test divide function
	 *
	 * @throws FunctionCallException
	 */
	@Test
	public void testDivide() throws FunctionCallException {

		// test dividing longs
		Object value = divide.invoke(ImmutableList.of(2L, 4L), context);
		assertEquals(value.getClass(), Long.class);
		long longValue = (Long) value;
		assertEquals(longValue, 0L);

		// test dividing doubles
		value = divide.invoke(ImmutableList.of(2.5, 4), context);
		assertEquals(value.getClass(), Double.class);
		double doubleValue = (Double) value;
		assertEquals(doubleValue, 0.625, 1e-6);

		// test dividing long and double
		value = divide.invoke(ImmutableList.of(4L, 2.5), context);
		assertEquals(value.getClass(), Double.class);
		doubleValue = (Double) value;
		assertEquals(doubleValue, 1.6, 1e-6);
	}

	/**
	 * Test divide by zero
	 *
	 * @throws FunctionCallException
	 */
	@Test(expectedExceptions = FunctionCallException.class)
	public void testDivideByLongZero() throws FunctionCallException {
		divide.invoke(ImmutableList.of(3L, 0L), context);
	}

	/**
	 * Test divide by zero
	 *
	 * @throws FunctionCallException
	 */
	@Test(expectedExceptions = FunctionCallException.class)
	public void testDivideByDoubleZero() throws FunctionCallException {
		divide.invoke(ImmutableList.of(3.0, 0.0), context);
	}

	/**
	 * Test too few arguments
	 *
	 * @throws FunctionCallException
	 */
	@Test(expectedExceptions = FunctionCallException.class)
	public void testTooFewArgs() throws FunctionCallException {
		add.invoke(ImmutableList.of(1L), context);
	}

	/**
	 * Test too many arguments
	 *
	 * @throws FunctionCallException
	 */
	@Test(expectedExceptions = FunctionCallException.class)
	public void testTooManyArgs() throws FunctionCallException {
		add.invoke(ImmutableList.of(1L, 2L, 3L), context);
	}

	/**
	 * Test wrong type on argument 0
	 *
	 * @throws FunctionCallException
	 */
	@Test(expectedExceptions = FunctionCallException.class)
	public void testWrongTypeArg0() throws FunctionCallException {
		add.invoke(ImmutableList.of("a", 4L), context);
	}

	/**
	 * Test wrong type on argument 1
	 * @throws FunctionCallException
	 */
	@Test(expectedExceptions = FunctionCallException.class)
	public void testWrongTypeArg1() throws FunctionCallException {
		add.invoke(ImmutableList.of(2.5, true), context);
	}

}
