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
package com.turn.camino.util;

/**
 * Factory interface to create exception
 *
 * Useful for custom exception in Validation
 *
 * @author llo
 */
public interface ExceptionFactory<E extends Throwable> {

	/**
	 * Creates new exception
	 *
	 * @param message message of exception
	 * @return new exception
	 */
	E newException(String message);

	/**
	 * Creates new exception
	 *
	 * @param message message of exception
	 * @param cause underlying cause of exception
	 * @return new exception
	 */
	E newException(String message, Throwable cause);

	/**
	 * Creates new exception
	 *
	 * @param cause underlying cause of exception
	 * @return new exception
	 */
	E newException(Throwable cause);

}
