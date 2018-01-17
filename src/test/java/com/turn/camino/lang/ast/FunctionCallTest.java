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
package com.turn.camino.lang.ast;

import java.util.List;

import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Unit test for FunctionCall
 *
 * @author llo
 */
@Test
public class FunctionCallTest {

	private Location location = new Location(2, 3);

	@Test
	public void testFunctionCall() throws RuntimeException {
		List<Expression> arguments = ImmutableList.<Expression>of(
				new StringLiteral(location, "foo"), new LongLiteral(location, 1),
				new StringLiteral(location, "bar"));
		FunctionCall functionCall = new FunctionCall(location, new Identifier(location, "myFunc"),
				arguments);
		assertEquals(functionCall.getLocation(), location);
		assertTrue(functionCall.getFunctionValue() instanceof Identifier);
		assertEquals(((Identifier) functionCall.getFunctionValue()).getName(), "myFunc");
		assertEquals(functionCall.getArguments().size(), 3);
		TestVisitor visitor = mock(TestVisitor.class);
		functionCall.accept(visitor, "abc");
		verify(visitor).visit(functionCall, "abc");
	}
}
