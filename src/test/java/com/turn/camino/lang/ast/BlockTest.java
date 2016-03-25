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
import com.turn.camino.Context;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertEquals;

/**
 * Unit test for Block
 *
 * @author llo
 */
@Test
public class BlockTest {

	private Location location = new Location(2, 3);

	@Test
	public void testBlock() throws RuntimeException {
		Block block = new Block(location, ImmutableList.<Expression>of(
				new StringLiteral(location, "foo"),
				new LongLiteral(location, 1),
				new StringLiteral(location, "bar")));
		assertEquals(block.getLocation(), location);
		assertEquals(block.getExpressions().size(), 3);
		TestVisitor visitor = mock(TestVisitor.class);
		block.accept(visitor, "abc");
		verify(visitor).visit(block, "abc");
	}

}
