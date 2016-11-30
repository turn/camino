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
package com.turn.camino.render;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.turn.camino.Context;
import com.turn.camino.Env;
import com.turn.camino.EnvBuilder;
import com.turn.camino.WrongTypeException;

import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.turn.camino.render.functions.TimeFunctions;
import org.apache.hadoop.fs.FileSystem;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 * Test for RendererImpl
 *
 * @author llo
 */
@Test
public class RendererImplTest {

	private final static long TIME = 1408858139724L;
	private final static TimeZone TIME_ZONE = TimeZone.getTimeZone("GMT");

	private final Renderer renderer = new RendererImpl();
	private Context context;

	/**
	 * Set up test environment
	 */
	@BeforeClass
	public void setUp() throws WrongTypeException {
		context = mock(Context.class);
		Env env = mock(Env.class);
		when(context.getEnv()).thenReturn(env);
		when(context.getGlobalInstanceTime()).thenReturn(TIME);
		when(context.getProperty("now")).thenReturn(new TimeFunctions.Now());
		when(context.getProperty("now", Function.class)).thenReturn(new TimeFunctions.Now());
		when(context.getProperty("timeAdd")).thenReturn(new TimeFunctions.TimeAdd());
		when(context.getProperty("timeAdd", Function.class)).thenReturn(new TimeFunctions.TimeAdd());
		when(env.getCurrentTime()).thenReturn(TIME);
		when(env.getTimeZone()).thenReturn(TIME_ZONE);
	}

	/**
	 * Test literals
	 *
	 * @throws RenderException
	 */
	@Test
	public void testLiteral() throws RenderException {

		// test text
		Object value = renderer.render("a", context);
		assertEquals(value.getClass(), String.class);
		assertEquals(value, "a");

		// test long
		value = renderer.render("<%=12345%>", context);
		assertEquals(value.getClass(), Long.class);
		assertEquals(((Long) value).longValue(), 12345L);

		// test negative long
		value = renderer.render("<%=-99999%>", context);
		assertEquals(value.getClass(), Long.class);
		assertEquals(((Long) value).longValue(), -99999L);

		// test double
		value = renderer.render("<%=248.12%>", context);
		assertEquals(value.getClass(), Double.class);
		assertEquals((Double) value, 248.12, 1e-6);

		// test string
		value = renderer.render("<%='foo'%>", context);
		assertEquals(value.getClass(), String.class);
		assertEquals((String) value, "foo");
	}

	/**
	 * Test a block
	 *
	 * Expectation is that the block will render to text
	 *
	 * @throws RenderException
	 */
	@Test
	public void testBlock() throws RenderException {
		Object value = renderer.render("foo <%=666%> bar", context);
		assertEquals(value.getClass(), String.class);
		assertEquals(value, "foo 666 bar");
	}

	/**
	 * Test calling functions
	 *
	 * @throws RenderException
	 */
	@Test
	public void testFunctionCall() throws RenderException {

		// test calling now() with no argument
		Object value = renderer.render("<%=now()%>", context);
		assertEquals(value.getClass(), TimeValue.class);
		TimeValue timeValue = (TimeValue) value;
		assertEquals(timeValue.getTime(), TIME);
		assertEquals(timeValue.getTimeZone(), TIME_ZONE);

		// test calling now() with argument
		value = renderer.render("<%=now('US/Eastern')%>", context);
		assertEquals(value.getClass(), TimeValue.class);
		timeValue = (TimeValue) value;
		assertEquals(timeValue.getTime(), TIME);
		assertEquals(timeValue.getTimeZone(), TimeZone.getTimeZone("US/Eastern"));

		// test timeAdd() function
		value = renderer.render("<%=timeAdd(now(), 1, 'h')%>", context);
		assertEquals(value.getClass(), TimeValue.class);
		timeValue = (TimeValue) value;
		assertEquals(timeValue.getTime(), TIME + 60 * 60 * 1000);

		// test timeAdd() with negative time
		value = renderer.render("<%=timeAdd(now(), -30, 'm')%>", context);
		assertEquals(value.getClass(), TimeValue.class);
		timeValue = (TimeValue) value;
		assertEquals(timeValue.getTime(), TIME - 30 * 60 * 1000);
	}

	/**
	 * Test ternary if expression
	 *
	 * @throws RenderException
	 * @throws WrongTypeException
	 */
	@Test
	public void testTernaryIf() throws RenderException, WrongTypeException {

		// set up context with properties
		Context testContext = mock(Context.class);
		when(testContext.getProperty("a")).thenReturn(true);
		when(testContext.getProperty("a", Boolean.class)).thenReturn(true);
		when(testContext.getProperty("b")).thenReturn(false);
		when(testContext.getProperty("b", Boolean.class)).thenReturn(false);

		// test when condition is true
		Object value = renderer.render("<%=if(a,1,2)%>", testContext);
		assertEquals(value.getClass(), Long.class);
		assertEquals(((Long) value).longValue(), 1);

		// test when condition is false
		value = renderer.render("<%=if(b,'x','y')%>", testContext);
		assertEquals(value.getClass(), String.class);
		assertEquals((String) value, "y");
	}

	/**
	 * Test dictionary literal
	 *
	 * @throws RenderException
	 * @throws WrongTypeException
	 */
	@Test
	public void testDictionaryLiteral() throws RenderException, WrongTypeException {

		// set up context with properties
		Context testContext = mock(Context.class);
		when(testContext.getProperty("a")).thenReturn("abc");
		when(testContext.getProperty("a", String.class)).thenReturn("abc");
		when(testContext.getProperty("b")).thenReturn(3.12);
		when(testContext.getProperty("b", Double.class)).thenReturn(3.12);

		// test when condition is true
		Object value = renderer.render("<%={'zzz':135,a:b}%>", testContext);
		assertTrue(value instanceof Map);
		Map<?, ?> map = Map.class.cast(value);
		assertTrue(map.containsKey("zzz"));
		assertEquals(map.get("zzz"), 135L);
		assertTrue(map.containsKey("abc"));
		assertEquals((Double) map.get("abc"), 3.12, 1e-6);
	}

	/**
	 * Test list literal
	 *
	 * @throws RenderException
	 * @throws WrongTypeException
	 */
	@Test
	public void testListLiteral() throws RenderException, WrongTypeException {

		// set up context with properties
		Context testContext = mock(Context.class);
		when(testContext.getProperty("x")).thenReturn(1.23);
		when(testContext.getProperty("x", Double.class)).thenReturn(1.23);

		// test when condition is true
		Object value = renderer.render("<%=['zzz',135,x]%>", testContext);
		assertTrue(value instanceof List);
		List<?> list = List.class.cast(value);
		assertEquals(list.size(), 3);
		assertEquals(list.get(0), "zzz");
		assertEquals(list.get(1), 135L);
		assertEquals((Double) list.get(2), 1.23, 1e-6);
	}

	/**
	 * Test dictionary access
	 *
	 * @throws RenderException
	 * @throws WrongTypeException
	 */
	@Test
	public void testDictionaryAccess() throws RenderException, WrongTypeException {

		// set up context with properties
		Context testContext = mock(Context.class);
		Map<String,Long> dictionary = ImmutableMap.of("a", 1L, "b", 2L);
		when(testContext.getProperty("dict")).thenReturn(dictionary);
		when(testContext.getProperty("dict", Map.class)).thenReturn(dictionary);

		Object value = renderer.render("<%=dict['a']%>", testContext);
		assertTrue(value instanceof Long);
		assertEquals(((Long) value).longValue(), 1L);
		value = renderer.render("<%=dict['b']%>", testContext);
		assertTrue(value instanceof Long);
		assertEquals(((Long) value).longValue(), 2L);
	}

	/**
	 * Test list access
	 *
	 * @throws RenderException
	 * @throws WrongTypeException
	 */
	@Test
	public void testListAccess() throws RenderException, WrongTypeException {

		// set up context with properties
		Context testContext = mock(Context.class);
		List<?> list = ImmutableList.of("x", 987L);
		when(testContext.getProperty("list")).thenReturn(list);
		when(testContext.getProperty("list", List.class)).thenReturn(list);

		Object value = renderer.render("<%=list[0]%>", testContext);
		assertTrue(value instanceof String);
		assertEquals(((String) value), "x");
		value = renderer.render("<%=list[1]%>", testContext);
		assertTrue(value instanceof Long);
		assertEquals(((Long) value).longValue(), 987L);
	}

	/**
	 * Test nested collection access
	 *
	 * @throws RenderException
	 * @throws WrongTypeException
	 */
	@Test
	public void testNestedCollectionAccess() throws RenderException, WrongTypeException {

		// set up context with properties
		Context testContext = mock(Context.class);
		Map<String, Long> dictionary = ImmutableMap.of("p", 104L, "q", 115L);
		List<?> list = ImmutableList.of(dictionary);
		when(testContext.getProperty("list")).thenReturn(list);
		when(testContext.getProperty("list", List.class)).thenReturn(list);

		Object value = renderer.render("<%=list[0]['p']%>", testContext);
		assertTrue(value instanceof Long);
		assertEquals(((Long) value).longValue(), 104L);
		value = renderer.render("<%=list[0]['q']%>", testContext);
		assertTrue(value instanceof Long);
		assertEquals(((Long) value).longValue(), 115L);
	}

	/**
	 * Test rendering function literal which should return a function
	 *
	 * @throws RenderException
	 * @throws WrongTypeException
	 */
	@Test
	public void testFunctionLiteral() throws RenderException, WrongTypeException {

		// test creating a function literal
		Object value = renderer.render("<%=fn(a,b) -> add(a,b)%>", context);
		assertTrue(value instanceof Function);

		// test creating and executing a function literal
		Env env = new EnvBuilder().withTimeZone(TimeZone.getDefault())
				.withFileSystem(mock(FileSystem.class)).build();
		value = renderer.render("<%=(fn(a,b) -> add(a,b))(3,4)%>", env.newContext());
		assertTrue(value instanceof Number);
		assertEquals(((Number) value).intValue(), 7);
	}

	/**
	 * Test member access
	 *
	 * Note that while the grammar supports accessing object members the renderer doesn't
	 */
	public void testMemberAccess() throws RenderException, WrongTypeException {

		// set up context with properties
		Context testContext = mock(Context.class);
		TimeValue timeValue = new TimeValue(TIME_ZONE, TIME);
		when(testContext.getProperty("t")).thenReturn(timeValue);
		when(testContext.getProperty("t", TimeValue.class)).thenReturn(timeValue);

		// test accessing member - throws exception
		Object value = renderer.render("<%=t.timeMillis%>", testContext);
		assertTrue(value instanceof Number);
		assertEquals(((Number) value).longValue(), TIME);
		value = renderer.render("<%=t.timeZone%>", testContext);
		assertTrue(value instanceof String);
		assertEquals((String) value, TIME_ZONE.getID());
	}

	/**
	 * Test for lexical error
	 *
	 * @throws RenderException
	 */
	@Test(expectedExceptions = RenderException.class,
			expectedExceptionsMessageRegExp = "Lexical.*")
	public void testLexicalError() throws RenderException {
		renderer.render("a/<%=a#a%>/b", context);
	}

	/**
	 * Test for parse error
	 *
	 * @throws RenderException
	 */
	@Test(expectedExceptions = RenderException.class,
			expectedExceptionsMessageRegExp = "Parse.*")
	public void testParseError() throws RenderException {
		renderer.render("a/<%=a(%>/b", context);
	}

	/**
	 * Test unknown property
	 *
	 * @throws RenderException
	 */
	@Test(expectedExceptions = RenderException.class)
	public void testUnknownProperty() throws RenderException {
		renderer.render("<%=boo%>", context);
	}

	/**
	 * Test unknown function
	 *
	 * @throws RenderException
	 */
	@Test(expectedExceptions = RenderException.class)
	public void testUnknownFunction() throws RenderException {
		renderer.render("<%=some_func('a')%>", context);
	}

	/**
	 * Test accessing a non-collection using [] operator
	 *
	 * @throws RenderException
	 * @throws WrongTypeException
	 */
	@Test(expectedExceptions = RenderException.class)
	public void testAccessNonCollection() throws RenderException, WrongTypeException {
		// set up context with properties
		Context testContext = mock(Context.class);
		when(testContext.getProperty("list")).thenReturn("foo");
		when(testContext.getProperty("list", String.class)).thenReturn("foo");
		renderer.render("<%=list[0]%>", testContext);
	}

	/**
	 * Test list access with out of bound index
	 *
	 * @throws RenderException
	 * @throws WrongTypeException
	 */
	@Test(expectedExceptions = RenderException.class)
	public void testListOutOfBoundAccess() throws RenderException, WrongTypeException {
		// set up context with properties
		Context testContext = mock(Context.class);
		List<?> list = ImmutableList.of("x", 987L);
		when(testContext.getProperty("list")).thenReturn(list);
		when(testContext.getProperty("list", List.class)).thenReturn(list);
		renderer.render("<%=list[9]%>", testContext);
	}

	/**
	 * Test list access with non-integer index
	 *
	 * @throws RenderException
	 * @throws WrongTypeException
	 */
	@Test(expectedExceptions = RenderException.class)
	public void testListNonIntegerAccess() throws RenderException, WrongTypeException {
		// set up context with properties
		Context testContext = mock(Context.class);
		List<?> list = ImmutableList.of("x", 987L);
		when(testContext.getProperty("list")).thenReturn(list);
		when(testContext.getProperty("list", List.class)).thenReturn(list);
		renderer.render("<%=list['a']%>", testContext);
	}

}
