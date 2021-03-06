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

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Test for collection access
 *
 * @author llo
 */
@Test
public class CollectionAccessTest {

	private Location location = new Location(2, 3);

	/**
	 * Test collection access
	 */
	@Test
	public void testCollectionAccess() {
		CollectionAccess collectionAccess = new CollectionAccess(location,
				new Identifier(location, "array"),
				new LongLiteral(location, 4L));
		assertEquals(collectionAccess.getLocation(), location);
		assertTrue(collectionAccess.getCollection() instanceof Identifier);
		assertEquals(((Identifier) collectionAccess.getCollection()).getName(), "array");
		assertTrue(collectionAccess.getKey() instanceof LongLiteral);
		assertEquals(((LongLiteral) collectionAccess.getKey()).longValue(), 4L);
	}

}
