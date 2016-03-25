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
package com.turn.camino.render;

import com.turn.camino.Context;

import java.util.List;

/**
 * Function interface
 *
 * @author llo
 */
public interface Function {

	/**
	 * Invokes function with given parameters and context
	 *
	 * @param params actual parameters to function call
	 * @param context context in which the function operates
	 * @return return value of function
	 * @throws FunctionCallException
	 */
	public Object invoke(List<?> params, Context context) throws FunctionCallException;

}
