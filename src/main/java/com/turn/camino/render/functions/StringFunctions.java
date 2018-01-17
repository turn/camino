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

import com.google.common.base.Splitter;
import com.turn.camino.Context;
import com.turn.camino.render.Function;
import com.turn.camino.render.FunctionCallException;
import com.turn.camino.render.FunctionCallExceptionFactory;
import com.turn.camino.util.Validation;

import java.util.List;

import static com.turn.camino.util.Message.prefix;

/**
 * String functions
 *
 * @author llo
 */
public class StringFunctions {

	private final static Validation<FunctionCallException> VALIDATION =
			new Validation<>(new FunctionCallExceptionFactory());

	public static class Replace implements Function {
		@Override
		public Object invoke(List<?> params, Context context) throws FunctionCallException {
			VALIDATION.requireListSize(params, 3, 3, prefix("parameters"));
			String string = VALIDATION.requireType(params.get(0), String.class, prefix("arg0"));
			String pattern = VALIDATION.requireType(params.get(1), String.class, prefix("arg1"));
			String replacement = VALIDATION.requireType(params.get(2), String.class, prefix("arg2"));
			return string.replace(pattern, replacement);
		}
	}

	public static class ReplaceRegex implements Function {
		@Override
		public Object invoke(List<?> params, Context context) throws FunctionCallException {
			VALIDATION.requireListSize(params, 3, 3, prefix("parameters"));
			String string = VALIDATION.requireType(params.get(0), String.class, prefix("arg0"));
			String pattern = VALIDATION.requireType(params.get(1), String.class, prefix("arg1"));
			String replacement = VALIDATION.requireType(params.get(2), String.class, prefix("arg2"));
			return string.replaceAll(pattern, replacement);
		}
	}

	public static class Split implements Function {
		@Override
		public Object invoke(List<?> params, Context context) throws FunctionCallException {
			VALIDATION.requireListSize(params, 2, 2, prefix("parameters"));
			String string = VALIDATION.requireType(params.get(0), String.class, prefix("arg0"));
			String pattern = VALIDATION.requireType(params.get(1), String.class, prefix("arg1"));
			return Splitter.on(pattern).splitToList(string);
		}
	}
}
