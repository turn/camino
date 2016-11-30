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
package com.turn.camino.render.functions;

import com.turn.camino.Context;
import com.turn.camino.render.Function;
import com.turn.camino.render.FunctionCallException;
import com.turn.camino.render.FunctionCallExceptionFactory;
import com.turn.camino.util.Validation;

import static com.turn.camino.util.Message.*;

import java.util.List;

/**
 * Math functions
 *
 * @author llo
 */
public class MathFunctions {

	private final static Validation<FunctionCallException> VALIDATION =
			new Validation<>(new FunctionCallExceptionFactory());

	public static abstract class ArithmeticFunction implements Function {
		@Override
		public Object invoke(List<?> params, Context context) throws FunctionCallException {
			VALIDATION.requireListSize(params, 2, 2, prefix("parameters"));
			Number arg0 = VALIDATION.requireType(params.get(0), Number.class, prefix("arg0"));
			Number arg1 = VALIDATION.requireType(params.get(1), Number.class, prefix("arg1"));
			return invoke(arg0, arg1);
		}
		protected abstract Object invoke(Number arg0, Number arg1) throws FunctionCallException;
	}

	public static class Add extends ArithmeticFunction {
		@Override
		public Object invoke(Number arg0, Number arg1) throws FunctionCallException {
			if (arg0 instanceof Long && arg1 instanceof Long) {
				return arg0.longValue() + arg1.longValue();
			} else {
				return arg0.doubleValue() + arg1.doubleValue();
			}
		}
	}

	public static class Subtract extends ArithmeticFunction {
		@Override
		public Object invoke(Number arg0, Number arg1) throws FunctionCallException {
			if (arg0 instanceof Long && arg1 instanceof Long) {
				return arg0.longValue() - arg1.longValue();
			} else {
				return arg0.doubleValue() - arg1.doubleValue();
			}
		}
	}

	public static class Multiply extends ArithmeticFunction {
		@Override
		public Object invoke(Number arg0, Number arg1) throws FunctionCallException {
			if (arg0 instanceof Long && arg1 instanceof Long) {
				return arg0.longValue() * arg1.longValue();
			} else {
				return arg0.doubleValue() * arg1.doubleValue();
			}
		}
	}

	public static class Divide extends ArithmeticFunction {
		@Override
		public Object invoke(Number arg0, Number arg1) throws FunctionCallException {
			if (arg0 instanceof Long && arg1 instanceof Long) {
				if (arg1.longValue() == 0) {
					throw new FunctionCallException("Divide by zero");
				}
				return arg0.longValue() / arg1.longValue();
			} else {
				if (arg1.doubleValue() == 0) {
					throw new FunctionCallException("Divide by zero");
				}
				return arg0.doubleValue() / arg1.doubleValue();
			}
		}
	}

}
