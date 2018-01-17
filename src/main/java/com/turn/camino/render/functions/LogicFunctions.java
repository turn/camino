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
package com.turn.camino.render.functions;

import com.turn.camino.Context;
import com.turn.camino.render.Function;
import com.turn.camino.render.FunctionCallException;
import com.turn.camino.render.FunctionCallExceptionFactory;
import com.turn.camino.util.Validation;

import java.util.List;

import static com.turn.camino.util.Message.prefix;

/**
 * Logic functions
 *
 * @author llo
 */
public class LogicFunctions {

	private final static Validation<FunctionCallException> VALIDATION =
			new Validation<>(new FunctionCallExceptionFactory());

	public static class Not implements Function {
		@Override
		public Object invoke(List<?> params, Context context) throws FunctionCallException {
			VALIDATION.requireListSize(params, 1, 1, prefix("parameters"));
			boolean arg = VALIDATION.requireType(params.get(0), Boolean.class, prefix("arg"));
			return !arg;
		}
	}

	public static class Eq implements Function {
		@Override
		public Object invoke(List<?> params, Context context) throws FunctionCallException {
			VALIDATION.requireListSize(params, 2, 2, prefix("parameters"));
			return params.get(0).equals(params.get(1));
		}
	}

	public static class Ne implements Function {
		@Override
		public Object invoke(List<?> params, Context context) throws FunctionCallException {
			VALIDATION.requireListSize(params, 2, 2, prefix("parameters"));
			return !params.get(0).equals(params.get(1));
		}
	}

	public static class Lt implements Function {
		@Override
		public Object invoke(List<?> params, Context context) throws FunctionCallException {
			VALIDATION.requireListSize(params, 2, 2, prefix("parameters"));
			Number arg0 = VALIDATION.requireType(params.get(0), Number.class, prefix("arg0"));
			Number arg1 = VALIDATION.requireType(params.get(1), Number.class, prefix("arg1"));
			if (arg0 instanceof Long && arg1 instanceof Long) {
				return arg0.longValue() < arg1.longValue();
			} else {
				return arg0.doubleValue() < arg1.doubleValue();
			}
		}
	}

	public static class Gt implements Function {
		@Override
		public Object invoke(List<?> params, Context context) throws FunctionCallException {
			VALIDATION.requireListSize(params, 2, 2, prefix("parameters"));
			Number arg0 = VALIDATION.requireType(params.get(0), Number.class, prefix("arg0"));
			Number arg1 = VALIDATION.requireType(params.get(1), Number.class, prefix("arg1"));
			if (arg0 instanceof Long && arg1 instanceof Long) {
				return arg0.longValue() > arg1.longValue();
			} else {
				return arg0.doubleValue() > arg1.doubleValue();
			}
		}
	}

	public static class LtEq implements Function {
		@Override
		public Object invoke(List<?> params, Context context) throws FunctionCallException {
			VALIDATION.requireListSize(params, 2, 2, prefix("parameters"));
			Number arg0 = VALIDATION.requireType(params.get(0), Number.class, prefix("arg0"));
			Number arg1 = VALIDATION.requireType(params.get(1), Number.class, prefix("arg1"));
			if (arg0 instanceof Long && arg1 instanceof Long) {
				return arg0.longValue() <= arg1.longValue();
			} else {
				return arg0.doubleValue() <= arg1.doubleValue();
			}
		}
	}

	public static class GtEq implements Function {
		@Override
		public Object invoke(List<?> params, Context context) throws FunctionCallException {
			VALIDATION.requireListSize(params, 2, 2, prefix("parameters"));
			Number arg0 = VALIDATION.requireType(params.get(0), Number.class, prefix("arg0"));
			Number arg1 = VALIDATION.requireType(params.get(1), Number.class, prefix("arg1"));
			if (arg0 instanceof Long && arg1 instanceof Long) {
				return arg0.longValue() >= arg1.longValue();
			} else {
				return arg0.doubleValue() >= arg1.doubleValue();
			}
		}
	}
}
