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

/**
 * Ternary if operator
 *
 * @author llo
 */
public class TernaryIf extends Expression {

	private final Expression condition;
	private final Expression thenValue;
	private final Expression elseValue;

	/**
	 * Constructor
	 *
	 * @param location location of if operator
	 * @param condition condition of if operator
	 * @param thenValue value if condition is true
	 * @param elseValue value if condition is false
	 */
	public TernaryIf(Location location, Expression condition,
					 Expression thenValue, Expression elseValue) {
		super(location);
		this.condition = condition;
		this.thenValue = thenValue;
		this.elseValue = elseValue;
	}

	/**
	 * Gets condition of ternary if
	 *
	 * @return condition expression
	 */
	public Expression getCondition() {
		return condition;
	}

	/**
	 * Gets "then" of ternary if
	 *
	 * @return "then" expression
	 */
	public Expression getThenValue() {
		return thenValue;
	}

	/**
	 * Gets "else" of ternary if
	 *
	 * @return "else" expression
	 */
	public Expression getElseValue() {
		return elseValue;
	}

	/**
	 * Accepts a visitor to this ternary if
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
