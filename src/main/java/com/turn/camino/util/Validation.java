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
package com.turn.camino.util;

import com.google.common.base.Optional;

import java.util.List;

/**
 * Validation utility
 *
 * Validates various conditions. Allows custom exception to be thrown using exception factory.
 *
 * @author llo
 */
public class Validation<E extends Throwable> {

	private ExceptionFactory<E> exceptionFactory;

	/**
	 * Constructor
	 *
	 * @param exceptionFactory factory to create exception
	 */
	public Validation(ExceptionFactory<E> exceptionFactory) {
		this.exceptionFactory = exceptionFactory;
	}

	/**
	 * Requires object to cast to type
	 *
	 * @param object object to cast
	 * @param type target class to cast object to
	 * @param message error message
	 * @param <T> object type
	 * @return object cast into instance of T
	 * @throws E when object cannot be cast as T
	 */
	public <T> T requireType(Object object, Class<T> type, Message message) throws E {
		if (type.isAssignableFrom(object.getClass())) {
			return type.cast(object);
		}
		throw exceptionFactory.newException(message.reformat("Expected type %s but got %s",
				type, object.getClass()).toString());
	}

	/**
	 * Request that object be cast to type
	 * @param object object to cast
	 * @param type target class to cast object to
	 * @param <T> object type
	 * @return optional containing cast object if type is assignable from object,
	 * 	or absent optional otherwise
	 */
	public <T> Optional<T> requestType(Object object, Class<T> type) {
		if (type.isAssignableFrom(object.getClass())) {
			return Optional.of(type.cast(object));
		} else {
			return Optional.absent();
		}
	}

	/**
	 * Requires object to be not null
	 *
	 * @param object object to check
	 * @param message error message
	 * @param <T> object type
	 * @return object itself
	 * @throws E when object is null
	 */
	public <T> T requireNotNull(T object, Message message) throws E {
		if (object == null) {
			throw exceptionFactory.newException(message.reformat("Cannot be null").toString());
		}
		return object;
	}

	/**
	 * Requires list to be between min and max size
	 *
	 * @param list list to check
	 * @param minSize minimum size of list
	 * @param maxSize maximum size of list
	 * @param message error message
	 * @return list itself
	 * @throws E when list is not between min and max size
	 */
	public List<?> requireListSize(List<?> list, int minSize, int maxSize, Message message)
			throws E {
		if (list.size() < minSize) {
			throw exceptionFactory.newException(message.reformat("Has too few elements")
					.toString());
		}
		if (list.size() > maxSize) {
			throw exceptionFactory.newException(message.reformat("Has too many elements")
					.toString());
		}
		return list;
	}

}
