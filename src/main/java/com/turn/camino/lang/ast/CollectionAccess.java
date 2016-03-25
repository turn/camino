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

/**
 * Operator to access an element in a collection
 *
 * @author llo
 */
public class CollectionAccess extends Expression {

	private final Expression collection;
	private final Expression key;

	/**
	 * Constructor
	 *
	 * @param location location of collection access
	 * @param collection collection expression
	 * @param key key expression
	 */
	public CollectionAccess(Location location, Expression collection, Expression key) {
		super(location);
		this.collection = collection;
		this.key = key;
	}

	/**
	 * Get collection
	 *
	 * @return collection expression
	 */
	public Expression getCollection() {
		return collection;
	}

	/**
	 * Get key
	 *
	 * @return key expression
	 */
	public Expression getKey() {
		return key;
	}

	/**
	 * Accepts a visitor to this collection access operator
	 *
	 * @param visitor visitor
	 * @param context external context
	 * @return result of visit
	 * @throws E
	 */
	@Override
	public <O, C, E extends Throwable> O accept(Visitor<O, C, E> visitor, C context) throws E {
		return visitor.visit(this, context);
	}
}
