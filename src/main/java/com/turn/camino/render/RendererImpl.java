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
package com.turn.camino.render;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.turn.camino.Context;
import com.turn.camino.annotation.Member;
import com.turn.camino.lang.ast.*;
import com.turn.camino.lang.parser.ParseException;
import com.turn.camino.lang.parser.Parser;
import com.turn.camino.lang.parser.TokenMgrError;
import com.turn.camino.util.Message;
import com.turn.camino.util.MessageExceptionFactory;
import com.turn.camino.util.Validation;

import static com.turn.camino.util.Message.full;
import static com.turn.camino.util.Message.prefix;

import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Renderer implementation
 *
 * @author llo
 */
public class RendererImpl implements Renderer {

	private final static Validation<FunctionCallException> VALIDATION =
			new Validation<>(new FunctionCallExceptionFactory());

	/**
	 * Renders expression string into a Java value
	 *
	 * @param expression expression to render
	 * @param context context
	 * @return Java object
	 * @throws RenderException
	 */
	@Override
	public Object render(String expression, Context context) throws RenderException {

		// parse expression into ast
		Block block;
		Parser parser = new Parser(new StringReader(expression));
		try {
			block = parser.block();
		} catch (ParseException e) {
			throw new RenderException("Parse error", e);
		} catch (TokenMgrError e) {
			throw new RenderException("Lexical error", e);
		}

		// evaluate block
		Evaluator evaluator = new Evaluator();
		return evaluator.visit(block, context);
	}

	/**
	 * Evaluates expression
	 */
	protected static class Evaluator implements Visitor<Object, Context, RenderException> {

		/**
		 * Validation that throws visit exception
		 */
		private Validation<RenderException> validation = new Validation<>(
				new MessageExceptionFactory<RenderException>() {
					@Override
					public RenderException newException(String message) {
						return new RenderException(message);
					}
				}
		);

		/**
		 * Evaluates a block
		 *
		 * If the block only contains one element, then evaluates and return its value.
		 * Otherwise, evaluates all elements and concatenate their string forms.
		 *
		 * @param block block of code
		 * @param context render context
		 * @return element of block
		 * @throws RenderException
		 */
		@Override
		public Object visit(Block block, Context context) throws RenderException {
			List<Object> values = Lists.newArrayListWithExpectedSize(block.getExpressions()
					.size());
			for (Expression expression : block.getExpressions()) {
				values.add(expression.accept(this, context));
			}
			if (values.size() == 1) {
				return values.get(0);
			} else {
				StringBuilder sb = new StringBuilder();
				for (Object value : values) {
					sb.append(value.toString());
				}
				return sb.toString();
			}
		}

		/**
		 * Evaluates a double literal
		 *
		 * Returns the double value
		 *
		 * @param doubleLiteral double literal to evaluate
		 * @param context context
		 * @return double value
		 * @throws RenderException
		 */
		@Override
		public Object visit(DoubleLiteral doubleLiteral, Context context)
				throws RenderException {
			return doubleLiteral.getNumber();
		}

		/**
		 * Evaluates a function call
		 *
		 * @param functionCall function call
		 * @param context context
		 * @return result of function call
		 * @throws RenderException
		 */
		@Override
		public Object visit(FunctionCall functionCall, Context context)
				throws RenderException {
			Expression funcExpr = functionCall.getFunctionValue();
			Object object = funcExpr.accept(Evaluator.this, context);
			Function function = validation.requireType(validation.requireNotNull(object,
					Message.full(String.format("Expression %s not a function", funcExpr))),
					Function.class,
					Message.full(String.format("Function %s undefined", funcExpr)));
			List<Object> params = Lists.newArrayListWithExpectedSize(functionCall.getArguments()
					.size());
			for (Expression argument : functionCall.getArguments()) {
				params.add(argument.accept(this, context));
			}
			return function.invoke(params, context);
		}

		/**
		 * Evaluates an identifier
		 *
		 * Returns the value of identifier in context
		 *
		 * @param identifier identifier
		 * @param context context
		 * @return identifier's value
		 * @throws RenderException
		 */
		@Override
		public Object visit(Identifier identifier, Context context) throws RenderException {
			return validation.requireNotNull(context.getProperty(identifier.getName()),
					Message.full(String.format("Unknown property %s", identifier.getName())));
		}

		/**
		 * Evaluates a long literal
		 *
		 * Returns the long value
		 *
		 * @param longLiteral long literal to evaluate
		 * @param context context
		 * @return long value
		 * @throws RenderException
		 */
		@Override
		public Object visit(LongLiteral longLiteral, Context context) throws RenderException {
			return longLiteral.getNumber();
		}

		/**
		 * Evaluates a string literal
		 *
		 * Returns the string value
		 *
		 * @param stringLiteral string literal to evaluate
		 * @param context context
		 * @return string value
		 * @throws RenderException
		 */
		@Override
		public Object visit(StringLiteral stringLiteral, Context context) throws RenderException {
			return stringLiteral.getValue();
		}

		/**
		 * Evaluates a ternary if expression
		 *
		 * @param ternaryIf ternary if expression
		 * @param context context
		 * @return "then" value if condition is true, "else" value otherwise
		 * @throws RenderException
		 */
		@Override
		public Object visit(TernaryIf ternaryIf, Context context) throws RenderException {
			Object condition = ternaryIf.getCondition().accept(this, context);
			if (validation.requireType(condition, Boolean.class,
					full("Condition must be boolean expression"))) {
				return ternaryIf.getThenValue().accept(this, context);
			} else {
				return ternaryIf.getElseValue().accept(this, context);
			}
		}

		/**
		 * Evaluates a dictionary literal
		 *
		 * @param dictionaryLiteral dictionary literal
		 * @param context context
		 * @return a map representing dictionary
		 * @throws RenderException
		 */
		@Override
		public Object visit(DictionaryLiteral dictionaryLiteral, Context context)
				throws RenderException {
			Map<Object, Object> dict = Maps.newHashMap();
			for (DictionaryLiteral.Entry entry : dictionaryLiteral.getEntries()) {
				dict.put(entry.getKey().accept(this, context), entry.getValue().accept(this, context));
			}
			return dict;
		}

		/**
		 * Evaluates a list literal
		 *
		 * @param listLiteral list literal
		 * @param context context
		 * @return a list of elements
		 * @throws RenderException
		 */
		@Override
		public Object visit(ListLiteral listLiteral, Context context)
				throws RenderException {
			List<Object> elements = Lists.newArrayListWithExpectedSize(listLiteral.getElements()
					.size());
			for (Expression argument : listLiteral.getElements()) {
				elements.add(argument.accept(this, context));
			}
			return elements;
		}

		/**
		 * Evaluates a collection access operator
		 *
		 * @param collectionAccess collection access
		 * @param context context
		 * @return element in collection
		 * @throws RenderException
		 */
		@Override
		public Object visit(CollectionAccess collectionAccess, Context context)
				throws RenderException {

			Object collection = collectionAccess.getCollection().accept(this, context);

			// dictionary
			Optional<Map> mapOptional = validation.requestType(collection, Map.class);
			if (mapOptional.isPresent()) {
				return mapOptional.get().get(collectionAccess.getKey().accept(this, context));
			}

			// list
			Optional<List> listOptional = validation.requestType(collection, List.class);
			if (listOptional.isPresent()) {
				Long index = validation.requireType(collectionAccess.getKey().accept(this, context),
					Long.class, full("Array index must be integer"));
				if (index < 0 || index > listOptional.get().size()) {
					throw new RenderException("Array index out of bound");
				}
				return listOptional.get().get(index.intValue());
			}

			// error!
			throw new RenderException("[] operator must be used with dictionary or list");
		}

		/**
		 * Evaluates accessing of a member
		 *
		 * @param memberAccess member access
		 * @param context context
		 * @return value of accessing member
		 * @throws RenderException
		 */
		@Override
		public Object visit(MemberAccess memberAccess, Context context)
				throws RenderException {
			validation.requireNotNull(memberAccess.getParent(),
					full("Cannot access member of null"));

			// evaluate parent
			Object parent = memberAccess.getParent().accept(this, context);
			validation.requireNotNull(parent, full("Cannot access member of null"));

			// check that parent has child
			// TODO fix slow implementation
			for (Method method : parent.getClass().getMethods()) {
				Member member = method.getAnnotation(Member.class);
				if (member != null) {
					if (memberAccess.getChild().getName().equals(member.value())) {
						try {
							return method.invoke(parent);
						} catch (IllegalAccessException e) {
							throw new RenderException(e);
						} catch (InvocationTargetException e) {
							throw new RenderException(e);
						}
					}
				}
			}

			// member not found
			throw new RenderException(String.format("Member %s not found",
					memberAccess.getChild().getName()));
		}

		/**
		 * Visits function literal
		 * @param functionLiteral function literal to visit
		 * @param rc render context
		 * @return value of function literal
		 * @throws RenderException
		 */
		@Override
		public Object visit(final FunctionLiteral functionLiteral, final Context rc) throws RenderException {
			return (Function) (params, context) -> {
				// check parameters
				List<Identifier> paramNames = functionLiteral.getParameters();
				VALIDATION.requireListSize(params, paramNames.size(), paramNames.size(),
						prefix("parameters"));

				// push params into child context
				Context childContext = context.createChild();
				Identifier[] names = paramNames.toArray(new Identifier[paramNames.size()]);
				Object[] values = params.toArray();
				for (int i = 0; i < names.length; i++) {
					childContext.setProperty(names[i].getName(), values[i]);
				}

				// execute function body
				try {
					return visit(functionLiteral.getBody(), childContext);
				} catch (RenderException e) {
					throw new FunctionCallException(e);
				}
			};
		}

	}

}
