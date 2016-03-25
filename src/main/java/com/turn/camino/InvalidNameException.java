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
package com.turn.camino;

/**
 * Invalid name exception
 *
 * Exception thrown when a name does not conform to correct syntax
 *
 * Created by llo on 8/6/14.
 */
public class InvalidNameException extends Exception {

	/**
	 * Constructor
	 *
	 * @param message error message
	 */
	public InvalidNameException(String message) {
		super(message);
	}

	/**
	 * Constructor
	 *
	 * @param message error message
	 * @param throwable underlying error
	 */
	public InvalidNameException(String message, Throwable throwable) {
		super(message, throwable);
	}

	/**
	 * Constructor
	 *
	 * @param throwable underlying error
	 */
	public InvalidNameException(Throwable throwable) {
		super(throwable);
	}

}
