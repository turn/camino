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
 * Abstract numeral literal
 *
 * Parent class for long and double literal types.
 *
 * @author llo
 */
public abstract class NumberLiteral extends Literal {

	protected NumberLiteral(Location location) {
		super(location);
	}

	/**
	 * Returns long value
	 *
	 * @return long
	 */
	public abstract long longValue();

	/**
	 * Returns double value
	 *
	 * @return double
	 */
	public abstract double doubleValue();

	/**
	 * Returns generic boxed number
	 *
	 * @return Number
	 */
	public abstract Number getNumber();
}
