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

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.ImmutableList;

/**
 * Repeat config element
 *
 * The semantics of repeat is to iterate over every element in the
 * list expression and create paths for each element.
 *
 * @author llo
 */
public class Repeat {

	private final String var;
	private final String list;
	private final List<Path> paths;
	private final List<Repeat> repeats;

	/**
	 * Constructor
	 *
	 * @param var name of variable
	 * @param list list to iterate over
	 * @param paths list of paths
	 */
	@JsonCreator
	public Repeat(@JsonProperty("var") String var, @JsonProperty("list") String list,
			@JsonProperty("paths") @JsonDeserialize(contentAs = Path.class) List<Path> paths,
			@JsonProperty("repeats") @JsonDeserialize(contentAs = Repeat.class)	List<Repeat> repeats) {
		this.var = var;
		this.list = list;
		this.paths = ImmutableList.copyOf(paths != null ? paths : Collections.<Path>emptyList());
		this.repeats = ImmutableList.copyOf(repeats != null ? repeats :
				Collections.<Repeat>emptyList());
	}

	/**
	 * Gets name of variable
	 *
	 * @return variable name
	 */
	public String getVar() {
		return var;
	}

	/**
	 * Gets list to iterate over
	 *
	 * @return list to iterate over
	 */
	public String getList() {
		return list;
	}

	/**
	 * Gets list of paths
	 *
	 * Note list is immutable
	 *
	 * @return list of paths
	 */
	public List<Path> getPaths() {
		return paths;
	}

	/**
	 * Get repeats
	 *
	 * @return list of repeats
	 */
	public List<Repeat> getRepeats() {
		return repeats;
	}

}
