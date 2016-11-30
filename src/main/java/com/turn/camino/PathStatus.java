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

import com.turn.camino.annotation.Member;
import com.turn.camino.config.Path;

import com.google.common.collect.ImmutableList;
import com.turn.camino.render.TimeValue;

import java.util.List;

/**
 * Path status
 *
 * Current status of a path
 *
 * @author llo
 */
public class PathStatus {

	private final String name;
	private final String value;
	private final Path path;
	private final List<PathDetail> pathDetails;
	private final TimeValue expectedCreationTime;

	/**
	 * Constructor
	 *
	 * @param name name of path
	 * @param value value of path
	 * @param path path configuration
	 * @param pathDetails path details
	 */
	public PathStatus(String name, String value, Path path, List<PathDetail> pathDetails,
			TimeValue expectedCreationTime) {
		this.name = name;
		this.value = value;
		this.path = path;
		this.pathDetails = ImmutableList.copyOf(pathDetails);
		this.expectedCreationTime = expectedCreationTime;
	}

	/**
	 * Gets name of path
	 *
	 * @return name of path
	 */
	@Member("name")
	public String getName() {
		return name;
	}

	/**
	 * Gets value of path
	 *
	 * @return value of path
	 */
	@Member("value")
	public String getValue() {
		return value;
	}

	/**
	 * Gets path configuration
	 *
	 * @return path configuration
	 */
	@Member("path")
	public Path getPath() {
		return path;
	}

	/**
	 * Gets path details
	 *
	 * @return immutable list of path details
	 */
	@Member("pathDetails")
	public List<PathDetail> getPathDetails() {
		return pathDetails;
	}

	/**
	 * Gets expected creation time
	 *
	 * @return expected creation time
	 */
	@Member("expectedCreationTime")
	public TimeValue getExpectedCreationTime() {
		return expectedCreationTime;
	}

}
