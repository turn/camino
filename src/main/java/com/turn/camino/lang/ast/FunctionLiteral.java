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

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * Function literal
 *
 * Definition of a user-defined function
 *
 * @author llo
 */
public class FunctionLiteral extends Literal {

	private final List<Identifier> parameters;
	private final Block body;

	/**
	 * Constructor
	 *
	 * @param location location of literal
	 */
	public FunctionLiteral(Location location, List<Identifier> parameters, Block body) {
		super(location);
		this.parameters = ImmutableList.copyOf(parameters);
		this.body = body;
	}

	/**
	 * Get function parameters
	 *
	 * @return list of identifiers as the parameters of this function
	 */
	public List<Identifier> getParameters() {
		return parameters;
	}

	/**
	 * Gets the body of function
	 *
	 * @return body of function
	 */
	public Block getBody() {
		return body;
	}

	/**
	 * Accepts a visitor to this list initializer
	 *
	 * @param visitor visitor
	 * @param context external context
	 * @param <O> return type
	 * @param <C> context type
	 * @return return value
	 * @throws E
	 */
	@Override
	public <O, C, E extends Throwable> O accept(Visitor<O, C, E> visitor, C context) throws E {
		return visitor.visit(this, context);
	}

}
