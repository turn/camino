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
package com.turn.camino;

/**
 * Context for Camino's execution
 *
 * @author llo
 */
public interface Context {

	/**
	 * Get system environment
	 *
	 * @return system environment
	 */
	Env getEnv();

	/**
	 * Creates child context
	 *
	 * @return new context
	 */
	Context createChild();

	/**
	 * Gets parent context
	 *
	 * Returns a parent context, or null if current context is global
	 *
	 * @return parent context
	 */
	Context getParent();

	/**
	 * Gets global context
	 *
	 * Returns the global (ancestral) context
	 *
	 * @return global context
	 */
	Context getGlobal();

	/**
	 * Puts name and value of property into the current context
	 *
	 * @param name name of property
	 * @param value value of property
	 */
	void setProperty(String name, Object value);

	/**
	 * Gets a value of a property given a name
	 *
	 * Note that if name is not found in the current context, the expected behavior is to
	 * recursively search in paren context. Null is returned only if name is not found in
	 * any context.
	 *
	 * @param name name of property
	 * @return value of property
	 */
	Object getProperty(String name);

	/**
	 * Gets a value of a property given a name
	 *
	 * Note that if name is not found in the current context, the expected behavior is to
	 * recursively search in paren context. Null is returned only if name is not found in
	 * any context.
	 *
	 * @param name name of value
	 * @param type type of value
	 * @return value
	 * @throws WrongTypeException
	 */
	<T> T getProperty(String name, Class<T> type) throws WrongTypeException;

	/**
	 * Gets time the global instance was created
	 *
	 * The time supplied by Env.getCurrentTime() will become the value of global instance time
	 * when the global context is created from Env. All child contexts under this global context
	 * will inherit the same global instance time.
	 *
	 * @return UTC time in milliseconds
	 */
	long getGlobalInstanceTime();

}
