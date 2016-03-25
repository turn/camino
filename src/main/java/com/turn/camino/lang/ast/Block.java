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

import java.util.List;

import com.google.common.collect.ImmutableList;

/**
 * A block is a group of expressions
 *
 * @author llo
 */
public class Block extends Expression {

	private final List<Expression> expressions;

	/**
	 * Constructor
	 *
	 * @param location location of expression
	 * @param expressions expressions in this template
	 */
	public Block(Location location, List<Expression> expressions) {
		super(location);
		this.expressions = ImmutableList.copyOf(expressions);
	}

	/**
	 * Gets expressions in this block
	 *
	 * @return expressions
	 */
	public List<Expression> getExpressions() {
		return expressions;
	}

	/**
	 * Accepts a visitor to this block
	 *
	 * @param visitor visitor
	 * @param context external context
	 * @param <O> return type
	 * @param <C> context type
	 * @return return value
	 * @throws E
	 */
	@Override
	public <O,C,E extends Throwable> O accept(Visitor<O,C,E> visitor, C context) throws E {
		return visitor.visit(this, context);
	}
}
