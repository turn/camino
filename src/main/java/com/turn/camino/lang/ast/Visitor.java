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
 * Visitor
 *
 * This is a simple visitor pattern to visit each expression type in the AST.
 *
 * @author llo
 */
public interface Visitor<O, C, E extends Throwable> {

	/**
	 * Visits a block
	 *
	 * @param block block to visit
	 * @param context context of visit
	 * @return value of visit
	 * @throws E custom exception
	 */
	public O visit(Block block, C context) throws E;

	/**
	 * Visits a double literal
	 *
	 * @param doubleLiteral double literal to visit
	 * @param context context of visit
	 * @return value of visit
	 * @throws E custom exception
	 */
	public O visit(DoubleLiteral doubleLiteral, C context) throws E;

	/**
	 * Visits a function call
	 *
	 * @param functionCall function call to visit
	 * @param context context of visit
	 * @return value of visit
	 * @throws E custom exception
	 */
	public O visit(FunctionCall functionCall, C context) throws E;

	/**
	 * Visits an identifier
	 *
	 * @param identifier identifier to visit
	 * @param context context of visit
	 * @return value of visit
	 * @throws E custom exception
	 */
	public O visit(Identifier identifier, C context) throws E;

	/**
	 * Visits a long literal
	 *
	 * @param longLiteral long literal to visit
	 * @param context context of visit
	 * @return value of visit
	 * @throws E custom exception
	 */
	public O visit(LongLiteral longLiteral, C context) throws E;

	/**
	 * Visits a string literal
	 *
	 * @param stringLiteral string literal to visit
	 * @param context context of visit
	 * @return value of visit
	 * @throws E custom exception
	 */
	public O visit(StringLiteral stringLiteral, C context) throws E;

	/**
	 * Visits a ternary if
	 *
	 * @param ternaryIf ternary if to visit
	 * @param context context of visit
	 * @return value of visit
	 * @throws E custom exception
	 */
	public O visit(TernaryIf ternaryIf, C context) throws E;

	/**
	 * Visits a dictionary initializer
	 *
	 * @param dictionaryLiteral dictionary literal to visit
	 * @param context context of visit
	 * @return value of visit
	 * @throws E customer exception
	 */
	public O visit(DictionaryLiteral dictionaryLiteral, C context) throws E;

	/**
	 * Visits a list initializer
	 *
	 * @param listLiteral list literal to visit
	 * @param context context of visit
	 * @return value of visit
	 * @throws E customer exception
	 */
	public O visit(ListLiteral listLiteral, C context) throws E;

	/**
	 * Visits a collectiona ccess
	 *
	 * @param collectionAccess
	 * @param context
	 * @return
	 * @throws E
	 */
	public O visit(CollectionAccess collectionAccess, C context) throws E;

	/**
	 * Visits a member access
	 *
	 * @param memberAccess member access to visit
	 * @param context context of visit
	 * @return value of visit
	 * @throws E custom exception
	 */
	public O visit(MemberAccess memberAccess, C context) throws E;
}
