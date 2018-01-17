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

import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;

/**
 * Test for DictionaryLiteral class
 *
 * @author llo
 */
@Test
public class DictionaryLiteralTest {

	private Location location = new Location(20, 5);

	/**
	 * Test dictionary literal
	 */
	@Test
	public void testDictionaryLiteral() {
		List<DictionaryLiteral.Entry> entries = ImmutableList.of(
				new DictionaryLiteral.Entry(new StringLiteral(location, "foo"),
						new LongLiteral(location, 1)),
				new DictionaryLiteral.Entry(new StringLiteral(location, "bar"),
						new LongLiteral(location, 4)));
		DictionaryLiteral dictionaryLiteral = new DictionaryLiteral(location, entries);
		assertEquals(dictionaryLiteral.getEntries().size(), 2);
		assertEquals(dictionaryLiteral.getEntries().get(0).getKey().getClass(), StringLiteral.class);
		assertEquals(((StringLiteral) dictionaryLiteral.getEntries().get(0).getKey()).getValue(), "foo");
		assertEquals(dictionaryLiteral.getEntries().get(0).getValue().getClass(), LongLiteral.class);
		assertEquals(((LongLiteral) dictionaryLiteral.getEntries().get(0).getValue()).longValue(), 1);
		assertEquals(dictionaryLiteral.getEntries().get(1).getKey().getClass(), StringLiteral.class);
		assertEquals(((StringLiteral) dictionaryLiteral.getEntries().get(1).getKey()).getValue(), "bar");
		assertEquals(dictionaryLiteral.getEntries().get(1).getValue().getClass(), LongLiteral.class);
		assertEquals(((LongLiteral) dictionaryLiteral.getEntries().get(1).getValue()).longValue(), 4);
	}

	/**
	 * Test dictionary literal builder
	 */
	@Test
	public void testDictionaryLiteralBuilder() {
		DictionaryLiteral.Builder builder = new DictionaryLiteral.Builder(location);
		builder.put(new StringLiteral(location, "a"), new LongLiteral(location, 3));
		DictionaryLiteral dictionaryLiteral = builder.build();
		assertEquals(dictionaryLiteral.getEntries().size(), 1);
	}

}
