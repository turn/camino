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

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 * Unit test for Identifier
 *
 * @author llo
 */
@Test
public class IdentifierTest {

	private Location location = new Location(2, 3);

	@Test
	public void testIdentifier() throws RuntimeException {
		Identifier identifier = new Identifier(location, "abc");
		assertEquals(identifier.getLocation(), location);
		assertEquals(identifier.getName(), "abc");
		TestVisitor visitor = mock(TestVisitor.class);
		identifier.accept(visitor, "here");
		verify(visitor).visit(identifier, "here");
	}

}
