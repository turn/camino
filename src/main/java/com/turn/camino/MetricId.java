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

import com.turn.camino.config.Tag;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * Metric identifier
 *
 * @author llo
 */
public class MetricId {

	private final String name;
	private final String pathName;
	private final List<Tag> tags;

	/**
	 * Constructor
	 *
	 * @param name name of metric
	 * @param pathName path name of metric
	 * @param tags tags
	 */
	public MetricId(String name, String pathName, List<Tag> tags) {
		this.name = name;
		this.pathName = pathName;
		this.tags = tags == null ? ImmutableList.of() : ImmutableList.copyOf(tags);
	}

	/**
	 * Return name of metric ID
	 *
	 * @return name of metric ID
	 */
	public String getName() {
		return name;
	}

	/**
	 * Return path name of metric ID
	 *
	 * @return path name of metric ID
	 */
	public String getPathName() {
		return pathName;
	}

	/**
	 * Return rendered tags of metric ID
	 *
	 * @return rendered tags of metric ID
	 */
	public List<Tag> getTags() {
		return tags;
	}

	/**
	 * Returns full name of metric ID, essentially pathName.name
	 *
	 * @return full name of metric ID
	 */
	public String getFullName() {
		return String.format("%s.%s", pathName, name);
	}

}
