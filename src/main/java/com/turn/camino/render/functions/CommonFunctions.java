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
 * Common functions
 *
 * @author llo
 */
public class CommonFunctions {

	private final static Validation<FunctionCallException> VALIDATION =
			new Validation<>(new FunctionCallExceptionFactory());

	public static class Compare implements Function {
		@Override
		@SuppressWarnings("unchecked")
		public Object invoke(List<?> params, Context context) throws FunctionCallException {
			VALIDATION.requireListSize(params, 2, 2, prefix("parameters"));
			Comparable value1 = VALIDATION.requireType(params.get(0), Comparable.class, prefix("value1"));
			Comparable value2 = VALIDATION.requireType(params.get(1), Comparable.class, prefix("value2"));
			return value1.compareTo(value2);
		}
	}
}
