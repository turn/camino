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
 * Double literal
 *
 * @author llo
 */
public class DoubleLiteral extends NumberLiteral {

	private final double value;

	/**
	 * Constructor
	 *
	 * @param location location of double literal
	 * @param value value of double literal
	 */
	public DoubleLiteral(Location location, double value) {
		super(location);
		this.value = value;
	}

	/**
	 * Returns value converted to long
	 *
	 * @return long value
	 */
	@Override
	public long longValue() {
		return Double.valueOf(value).longValue();
	}

	/**
	 * Returns native double value
	 *
	 * @return double value
	 */
	@Override
	public double doubleValue() {
		return value;
	}

	/**
	 * Returns Number representation of double
	 *
	 * @return Double object
	 */
	@Override
	public Number getNumber() {
		return Double.valueOf(value);
	}

	/**
	 * Accepts a visitor to this double literal
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
