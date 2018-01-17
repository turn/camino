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
 * Identifier
 *
 * @author llo
 */
public class Identifier extends Expression {

	private final String name;

	/**
	 * Constructor
	 *
	 * @param location location of identifier
	 * @param name name of identifier
	 */
	public Identifier(Location location, String name) {
		super(location);
		this.name = name;
	}

	/**
	 * Gets name of identifier
	 *
	 * @return name of identifier
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Accepts a visitor to this identifier
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
