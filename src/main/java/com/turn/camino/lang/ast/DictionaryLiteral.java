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
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Statically initialized dictionary
 *
 * @author llo
 */
public class DictionaryLiteral extends Literal {

	private final List<Entry> entries;

	/**
	 * Constructor
	 *
	 * @param location location of
	 * @param entries entries of key/values to fill dictionary
	 */
	public DictionaryLiteral(Location location, List<Entry> entries) {
		super(location);
		this.entries = ImmutableList.copyOf(entries);
	}

	/**
	 * Get initializers for key/value pairs
	 *
	 * @return list of PairInitializers
	 */
	public List<Entry> getEntries() {
		return entries;
	}

	/**
	 * Accepts a visitor to this dictionary initializer
	 *
	 * @param visitor visitor
	 * @param context external context
	 * @param <O> return type
	 * @param <C> context type
	 * @return return value
	 * @throws E
	 */
	@Override
	public <O, C, E extends Throwable> O accept(Visitor<O, C, E> visitor, C context) throws E {
		return visitor.visit(this, context);
	}

	/**
	 * Builder usage pattern to create a dictionary
	 */
	public static class Builder {

		private final Location location;
		private List<Entry> entries = Lists.newLinkedList();

		/**
		 * Constructor
		 * @param location location of start of dictionary literal
		 */
		public Builder(Location location) {
			this.location = location;
		}

		/**
		 * Add entry into dictionary literal
		 *
		 * @param key key expression
		 * @param value value expression;
		 * @return this builder
		 */
		public Builder put(Expression key, Expression value) {
			entries.add(new Entry(key, value));
			return this;
		}

		/**
		 * Creates a dictionary literal
		 *
		 * @return dictionary literal
		 */
		public DictionaryLiteral build() {
			return new DictionaryLiteral(location, entries);
		}
	}

	/**
	 * An entry in a dictionary literal
	 */
	public static class Entry {

		private final Expression key;
		private final Expression value;

		/**
		 * Constructor
		 * @param key key expression
		 * @param value value expression
		 */
		public Entry(Expression key, Expression value) {
			this.key = key;
			this.value = value;
		}

		/**
		 * Gets key expression
		 */
		public Expression getKey() {
			return key;
		}

		/**
		 * Gets value expression
		 *
		 * @return
		 */
		public Expression getValue() {
			return value;
		}
	}

}
