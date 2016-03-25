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

import com.turn.camino.config.Metric;
import com.turn.camino.config.Path;
import com.turn.camino.config.Tag;

import java.util.Collections;

import com.google.common.collect.ImmutableList;
import org.junit.BeforeClass;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 * Unit test for ContextImpl
 *
 * @author llo
 */
@Test
public class ContextImplTest {

	private Env env = mock(Env.class);
	private long lastModifiedTime = System.currentTimeMillis() - 60 * 1000;
	private PathStatus pathStatus = new PathStatus("myPath", "/foo/*", mock(Path.class),
			ImmutableList.of(new PathDetail("/foo/bar", false, 1567, lastModifiedTime)), null);

	@BeforeClass
	public void setUp() {
		when(env.getCurrentTime()).thenReturn(1411435681000L);
	}

	/**
	 * Test context chain
	 */
	@Test
	public void testContextChain() {

		// test global context
		ContextImpl context = new ContextImpl(env, null);
		assertNull(context.getParent());
		assertEquals(context.getGlobal(), context);
		assertEquals(context.getEnv(), env);

		// test child context
		Context childContext = context.createChild();
		assertNotNull(childContext.getParent());
		assertEquals(childContext.getParent(), context);
		assertEquals(childContext.getGlobal(), context);

		// test child context to child context
		Context grandchildContext = childContext.createChild();
		assertNotNull(grandchildContext.getParent());
		assertEquals(grandchildContext.getParent(), childContext);
		assertEquals(grandchildContext.getGlobal(), context);
	}

	/**
	 * Test setting and getting properties
	 *
	 * @throws WrongTypeException
	 */
	@Test
	public void testProperty() throws WrongTypeException {
		Context context = new ContextImpl(env, null);
		context.setProperty("foo", 456L);
		assertEquals(context.getProperty("foo").getClass(), Long.class);
		assertEquals(((Long) context.getProperty("foo")).longValue(), 456L);
		assertEquals(context.getProperty("foo", Long.class).longValue(), 456L);
		assertNull(context.getProperty("bar"));
		assertNull(context.getProperty("bar", Long.class));
	}

	/**
	 * Test getting property in parent
	 *
	 * @throws WrongTypeException
	 */
	@Test
	public void testParentProperty() throws WrongTypeException {
		Context context = new ContextImpl(env, null);
		context.setProperty("foo", 456L);
		Context child = context.createChild();
		assertNotNull(child.getProperty("foo"));
		assertEquals(context.getProperty("foo").getClass(), Long.class);
		assertEquals(((Long) child.getProperty("foo")).longValue(), 456L);
	}

	/**
	 * Test getting wrong property type
	 *
	 * @throws WrongTypeException
	 */
	@Test(expectedExceptions = WrongTypeException.class)
	public void testPropertyWrongType() throws WrongTypeException {
		Context context = new ContextImpl(env, null);
		context.setProperty("foo", 456L);
		assertEquals(context.getProperty("foo", Double.class), 456.00, 1e-6);
	}

	/**
	 * Test instance time of a context
	 */
	@Test
	public void testGlobalInstanceTime() {

		long t0 = 1411435681000L;
		Env env = mock(Env.class);
		when(env.getCurrentTime()).thenReturn(t0);

		// create global context
		Context context = new ContextImpl(env, null);
		assertEquals(context.getGlobalInstanceTime(), t0);

		// simulate that 10 seconds have passed
		when(env.getCurrentTime()).thenReturn(t0 + 10000);
		assertEquals(context.getGlobalInstanceTime(), t0);

		// create child context
		Context childContext = new ContextImpl(env, context);
		assertEquals(childContext.getGlobalInstanceTime(), t0);

		// create new global context
		when(env.getCurrentTime()).thenReturn(t0 + 15000);
		Context context2 = new ContextImpl(env, null);
		assertNotEquals(context.getGlobalInstanceTime(), context2.getGlobalInstanceTime());
	}

}
