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
 * Base expression class
 *
 * @author llo
 */
public abstract class Expression {

	private final Location location;

	/**
	 * Constructor
	 *
	 * @param location location of expression
	 */
	protected Expression(Location location) {
		this.location = location;
	}

	/**
	 * Returns location of expression
	 *
	 * @return location of expression
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * Accepts a visitor
	 *
	 * @param visitor visitor
	 * @param context external context
	 * @param <O> return type
	 * @param <C> context type
	 * @return meaningful return value
	 * @throws E
	 */
	public abstract <O,C,E extends Throwable> O accept(Visitor<O,C,E> visitor, C context) throws E;

}
