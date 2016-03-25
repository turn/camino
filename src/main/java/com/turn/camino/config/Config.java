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

import java.net.URI;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.ImmutableList;

/**
 * Top-level Camino configuration
 *
 * @author llo
 */
public class Config {

	private final URI location;
	private final List<String> includes;
	private final List<Property> properties;
	private final List<Path> paths;
	private final List<Repeat> repeats;

	/**
	 * Constructor
	 */
	@JsonCreator
	public Config(@JacksonInject("location") URI location,
			@JsonProperty("includes") List<String> includes,
			@JsonProperty("properties") @JsonDeserialize(as = LinkedHashMap.class, keyAs = String.class,
					contentAs = String.class) Map<String, String> properties,
			@JsonProperty("paths") @JsonDeserialize(contentAs = Path.class)	List<Path> paths,
			@JsonProperty("repeats") @JsonDeserialize(contentAs = Repeat.class) List<Repeat> repeats) {
		this(location, includes, properties == null ? null : ConfigUtil.mapToList(properties,
				new ConfigUtil.Transformer<String, String, Property>() {
					@Override
					public Property transform(String key, String value) {
						return new Property(key, value);
					}
				}),
				paths, repeats);
	}

	/**
	 * Constructor
	 */
	public Config(URI location, List<String> includes, List<Property> properties,
			List<Path> paths, List<Repeat> repeats) {
		this.location = location;
		this.includes = ImmutableList.copyOf(includes != null ? includes :
				Collections.<String>emptyList());
		this.properties = ImmutableList.copyOf(properties != null ? properties :
				Collections.<Property>emptyList());
		this.paths = ImmutableList.copyOf(paths != null ? paths : Collections.<Path>emptyList());
		this.repeats = ImmutableList.copyOf(repeats != null ? repeats :
				Collections.<Repeat>emptyList());
	}

	/**
	 * Returns location of this config
	 *
	 * @return location of this config
	 */
	public URI getLocation() {
		return location;
	}

	/**
	 * Returns paths of configs to include
	 *
	 * @return list of configs to include
	 */
	public List<String> getIncludes() {
		return includes;
	}

	/**
	 * Returns properties
	 *
	 * Note that returned list is immutable
	 *
	 * @return list of properties
	 */
	public List<Property> getProperties() {
		return properties;
	}

	/**
	 * Returns paths
	 *
	 * Note that returned list is immutable
	 *
	 * Note that a copy of list is made
	 *
	 * @return list of paths
	 */
	public List<Path> getPaths() {
		return paths;
	}

	/**
	 * Returns repeats
	 *
	 * Note that returned list is immutable
	 *
	 * @return list of repeats
	 */
	public List<Repeat> getRepeats() {
		return repeats;
	}

}
