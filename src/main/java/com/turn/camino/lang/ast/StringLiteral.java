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
 * String literal
 *
 * @author llo
 */
public class StringLiteral extends Literal {

	private final String value;

	/**
	 * Constructor
	 *
	 * @param location location of string literal
	 * @param value value of string literal
	 */
	public StringLiteral(Location location, String value) {
		super(location);
		this.value = value;
	}

	/**
	 * Gets string value
	 *
	 * @return string value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Accepts a visitor to this string literal
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
