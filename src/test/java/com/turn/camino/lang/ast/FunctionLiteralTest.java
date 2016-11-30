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
package com.turn.camino.lang.ast;

import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertEquals;

/**
 * Unit test for function literal
 *
 * @author llo
 */
@Test
public class FunctionLiteralTest {

	private Location location = new Location(2, 3);

	@Test
	public void testFunctionLiteral() {
		List<Identifier> parameters = ImmutableList.of(new Identifier(location, "x"));
		Block body = new Block(location, Collections.<Expression>emptyList());
		FunctionLiteral functionLiteral = new FunctionLiteral(location, parameters, body);
		assertEquals(functionLiteral.getLocation(), location);
		assertEquals(functionLiteral.getParameters().size(), 1);
		assertEquals(functionLiteral.getBody().getExpressions().size(), 0);
		TestVisitor visitor = mock(TestVisitor.class);
		functionLiteral.accept(visitor, "here");
		verify(visitor).visit(functionLiteral, "here");
	}
}
