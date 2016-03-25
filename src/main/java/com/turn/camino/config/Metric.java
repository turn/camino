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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Metric element in configuration
 *
 * @author llo
 */
public class Metric {

	private final String name;
	private final String function;
	private final String aggregate;

	/**
	 * Constructor
	 *
	 * @param name name of metric
	 * @param function function of metric
	 * @param aggregate aggregate of metric
	 */
	@JsonCreator
	public Metric(@JsonProperty("name") String name, @JsonProperty("function") String function,
				  @JsonProperty("aggregate") String aggregate) {
		this.name = name;
		this.function = function;
		this.aggregate = aggregate;
	}

	/**
	 * Gets name of metric
	 *
	 * @return name of metric
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets function of metric
	 *
	 * @return type of metric
	 */
	public String getFunction() {
		return function;
	}

	/**
	 * Gets metric aggregate
	 *
	 * @return metric aggregate
	 */
	public String getAggregate() {
		return aggregate;
	}

}
