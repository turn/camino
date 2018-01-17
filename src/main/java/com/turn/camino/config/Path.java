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
package com.turn.camino.config;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.turn.camino.annotation.Member;

/**
 * Path element in configuration
 *
 * @author llo
 */
public class Path {

	private final String name;
	private final String value;
	private final List<Metric> metrics;
	private final List<Tag> tags;
	private final String expectedCreationTime;

	/**
	 * Constructor
	 *
	 * @param name name of path
	 * @param value value of path
	 * @param metrics metrics under this path
	 * @param tags tags of this path as ordered map of tag key/value pairs
	 * @param expectedCreationTime expression to calculate creation time of this path
	 */
	@JsonCreator
	public Path(@JsonProperty("name") String name, @JsonProperty("value") String value,
			@JsonProperty("metrics") @JsonDeserialize(contentAs = Metric.class) List<Metric> metrics,
			@JsonProperty("tags") @JsonDeserialize(as = LinkedHashMap.class, keyAs = String.class,
					contentAs = String.class) Map<String, String> tags,
			@JsonProperty("expectedCreationTime") String expectedCreationTime) {
		this(name, value, metrics, tags == null ? null : ConfigUtil.mapToList(tags,
				Tag::new), expectedCreationTime);
	}

	/**
	 * Constructor
	 *
	 * @param name name of path
	 * @param value value of path
	 * @param metrics metrics under this path
	 * @param tags tags of this path as list of Tag objects
	 * @param expectedCreationTime expression to calculate creation time of this path
	 */
	protected Path(String name, String value, List<Metric> metrics, List<Tag> tags,
			String expectedCreationTime) {
		Preconditions.checkNotNull(name, "Path name cannot be null");
		Preconditions.checkNotNull(value, "Path value cannot be null");
		this.name = name;
		this.value = value;
		this.metrics = ImmutableList.copyOf(metrics != null ? metrics :
				Collections.emptyList());
		this.tags = ImmutableList.copyOf(tags != null ? tags : Collections.emptyList());
		this.expectedCreationTime = expectedCreationTime;
	}

	/**
	 * Constructor
	 *
	 * @param name name of path
	 * @param value value of path
	 */
	public Path(String name, String value) {
		this(name, value, null, (List<Tag>) null, null);
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
	 * Returns metrics under this path
	 *
	 * Note that returned list is immutable
	 *
	 * @return immutable list of metrics
	 */
	@Member("metrics")
	public List<Metric> getMetrics() {
		return metrics;
	}

	/**
	 * Gets tags of this path
	 *
	 * @return immutable list of tags (key and value pairs)
	 */
	@Member("tags")
	public List<Tag> getTags() { return tags; }

	/**
	 * Gets expected creation time of this path
	 *
	 * @return expression to compute expected creation time
	 */
	@Member("expectedCreationTime")
	public String getExpectedCreationTime() {
		return expectedCreationTime;
	}
}
