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

import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertEquals;

/**
 * Unit test for LongLiteral
 *
 * @author llo
 */
@Test
public class LongLiteralTest {

	private Location location = new Location(2, 3);

	@Test
	public void testLongLiteral() throws RuntimeException {
		long value = 824;
		double epsilon = 1e-6;
		LongLiteral longLiteral = new LongLiteral(location, value);
		assertEquals(longLiteral.getLocation(), location);
		assertEquals(longLiteral.doubleValue(), 824.0, epsilon);
		assertEquals(longLiteral.longValue(), value);
		assertEquals(longLiteral.getNumber().getClass(), Long.class);
		assertEquals(longLiteral.getNumber().longValue(), value);
		TestVisitor visitor = mock(TestVisitor.class);
		longLiteral.accept(visitor, "here");
		verify(visitor).visit(longLiteral, "here");
	}

}
