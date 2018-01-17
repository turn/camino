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
import com.turn.camino.util.Message;
import com.turn.camino.util.Validation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

/**
 * Collection functions
 *
 * @author llo
 */
public class CollectionFunctions {

	private final static Validation<FunctionCallException> VALIDATION =
			new Validation<>(new FunctionCallExceptionFactory());

	/**
	 * Function to create a list
	 */
	public static class ListCreate implements Function {
		@Override
		public Object invoke(List<?> params, Context context) throws FunctionCallException {
			return new ArrayList<>(params);
		}
	}

	/**
	 * Function to get a list element
	 */
	public static class ListGet implements Function {
		@Override
		public Object invoke(List<?> params, Context context) throws FunctionCallException {
			VALIDATION.requireListSize(params, 2, 2, Message.prefix("parameters"));
			List<?> list = VALIDATION.requireType(params.get(0), List.class,
					Message.prefix("list"));
			Number indexNumber = VALIDATION.requireType(params.get(1), Number.class,
					Message.prefix("index"));
			int index = indexNumber.intValue();
			if (index < 0) {
				throw new FunctionCallException(String.format("Negative list index %s", index));
			}
			if (index >= list.size()) {
				throw new FunctionCallException(String.format("Out-of-bound index %s, size %s",
						index, list.size()));
			}
			return list.get(index);
		}
	}

	/**
	 * Function to get first element
	 */
	public static class ListFirst implements Function {
		@Override
		public Object invoke(List<?> params, Context context) throws FunctionCallException {
			VALIDATION.requireListSize(params, 1, 2, Message.prefix("parameters"));
			List<?> list = VALIDATION.requireType(params.get(0), List.class,
					Message.prefix("list"));
			if (list.size() > 0) {
				return list.get(0);
			} else if (params.size() > 1) {
				return params.get(1);
			} else {
				throw new FunctionCallException("Cannot get first element of empty list");
			}
		}
	}

	/**
	 * Function to get last element
	 */
	public static class ListLast implements Function {
		@Override
		public Object invoke(List<?> params, Context context) throws FunctionCallException {
			VALIDATION.requireListSize(params, 1, 2, Message.prefix("parameters"));
			List<?> list = VALIDATION.requireType(params.get(0), List.class,
					Message.prefix("list"));
			if (list.size() > 0) {
				return list.get(list.size() - 1);
			} else if (params.size() > 1) {
				return params.get(1);
			} else {
				throw new FunctionCallException("Cannot get last element of empty list");
			}
		}
	}

	/**
	 * Function to create a dictionary
	 */
	public static class DictCreate implements Function {
		@Override
		public Object invoke(List<?> params, Context context) throws FunctionCallException {
			if (params.size() % 2 != 0) {
				throw new FunctionCallException(String.format("Missing value for key %s",
						params.get(params.size() - 1)));
			}
			Map<Object, Object> dict = Maps.newHashMap();
			for (Iterator<?> iter = params.iterator(); iter.hasNext(); ) {
				Object key = iter.next();
				Object value = iter.next();
				dict.put(key, value);
			}
			return dict;
		}
	}

	/**
	 * Function to get a value in a dictionary given a key
	 */
	public static class DictGet implements Function {
		@Override
		public Object invoke(List<?> params, Context context) throws FunctionCallException {
			VALIDATION.requireListSize(params, 2, 2, Message.prefix("parameters"));
			Map<?, ?> dict = VALIDATION.requireType(params.get(0), Map.class,
					Message.prefix("dict"));
			Object key = params.get(1);
			return dict.get(key);
		}
	}

}
