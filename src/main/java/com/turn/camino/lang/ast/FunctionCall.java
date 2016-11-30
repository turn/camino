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
 * Function call
 *
 * @author llo
 */
public class FunctionCall extends Expression {

	private final Expression functionValue;
	private final List<Expression> arguments;

	/**
	 * Constructor
	 *
	 * @param location location of function call
	 * @param functionValue function to invoke
	 * @param arguments arguments of function call
	 */
	public FunctionCall(Location location, Expression functionValue, List<Expression> arguments) {
		super(location);
		this.functionValue = functionValue;
		this.arguments = ImmutableList.copyOf(arguments);
	}

	/**
	 * Gets function of this function call
	 *
	 * @return expression
	 */
	public Expression getFunctionValue() {
		return functionValue;
	}

	/**
	 * Gets arguments of this function call
	 *
	 * @return list of expressions representing arguments of function call
	 */
	public List<Expression> getArguments() {
		return arguments;
	}

	/**
	 * Accepts a visitor to this function call
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
