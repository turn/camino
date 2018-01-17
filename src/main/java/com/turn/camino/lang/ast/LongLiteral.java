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
 * Long literal
 *
 * @author llo
 */
public class LongLiteral extends NumberLiteral {

	private final long value;

	/**
	 * Constructor
	 *
	 * @param location location of long literal
	 * @param value value of long literal
	 */
	public LongLiteral(Location location, long value) {
		super(location);
		this.value = value;
	}

	/**
	 * Gets native long value
	 *
	 * @return long value
	 */
	@Override
	public long longValue() {
		return value;
	}

	/**
	 * Gets long converted to double
	 *
	 * @return double value
	 */
	@Override
	public double doubleValue() {
		return Double.valueOf(value).doubleValue();
	}

	/**
	 * Gets number representation of this long
	 *
	 * @return Long object
	 */
	@Override
	public Number getNumber() {
		return Long.valueOf(value);
	}

	/**
	 * Accepts a visitor to this long literal
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
