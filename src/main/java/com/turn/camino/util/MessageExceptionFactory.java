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
 * Exception factory to create message-only exceptions
 *
 * All other methods are overridden so only the method with message argument is needed.
 *
 * @author llo
 */
public abstract class MessageExceptionFactory<E extends Throwable> implements ExceptionFactory<E> {

	/**
	 * Creates new exception using only the message
	 *
	 * @param message message of exception
	 * @param cause underlying cause of exception
	 * @return new exception
	 */
	public E newException(String message, Throwable cause) {
		return newException(message);
	}

	/**
	 * Creates new exception with message of underlying cause
	 *
	 * The message of the cause throwable is used as message of the exception
	 *
	 * @param cause underlying cause of exception
	 * @return new exception
	 */
	public E newException(Throwable cause) {
		return newException(cause.getMessage());
	}
}
