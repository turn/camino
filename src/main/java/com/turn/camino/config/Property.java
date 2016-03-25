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
package com.turn.camino.config;

import com.google.common.base.Preconditions;

/**
 * Property element in config
 *
 * @author llo
 */
public class Property {

	private final String name;
	private final String value;

	/**
	 * Constructor
	 *
	 * @param name name of property
	 * @param value value of property
	 */
	public Property(String name, String value) {
		Preconditions.checkNotNull(name, "Property name cannot be null");
		Preconditions.checkNotNull(value, "Property value cannot be null");
		this.name = name;
		this.value = value;
	}

	/**
	 * Gets name of property
	 *
	 * @return name of property
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets value of property
	 *
	 * @return value of property
	 */
	public String getValue() {
		return value;
	}

}
