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
 * Member access
 *
 * Represents access of a member of an object. Essentially <i>object.member</i>.
 *
 * @author llo
 */
public class MemberAccess extends Expression {

	private final Expression parent;
	private final Expression child;

	/**
	 * Constructor
	 *
	 * @param location location of member access
	 * @param parent parent of memeber access
	 * @param child child of member access
	 */
	public MemberAccess(Location location, Expression parent, Expression child) {
		super(location);
		this.parent = parent;
		this.child = child;
	}

	/**
	 * Returns expression to compute object
	 *
	 * @return parent expression
	 */
	public Expression getParent() {
		return parent;
	}

	/**
	 * Returns expression to access child (either identifier or function call)
	 *
	 * @return child expression
	 */
	public Expression getChild() {
		return child;
	}

	/**
	 * Accepts visitor to this member access
	 *
	 * @param visitor visitor
	 * @param context external context
	 * @param <O> return type
	 * @param <C> context type
	 * @return output of accessing member
	 * @throws E
	 */
	@Override
	public <O,C,E extends Throwable> O accept(Visitor<O,C,E> visitor, C context) throws E {
		return visitor.visit(this, context);
	}

}
