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

import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Config Builder
 *
 * @author llo
 */
public class ConfigBuilder {

	private URI location;
	private List<String> includeBuilder = Lists.newLinkedList();
	private Map<String,Property> propertyBuilder = Maps.newLinkedHashMap();
	private List<Path> pathBuilder = Lists.newLinkedList();
	private List<Repeat> repeatBuilder = Lists.newLinkedList();

	/**
	 * Convenience method to create new ConfigBuilder
	 *
	 * @return new instance of ConfigBuilder
	 */
	public static ConfigBuilder create() {
		return new ConfigBuilder();
	}

	/**
	 * Reads config from a location
	 *
	 * @param location location of config
	 * @return this
	 * @throws IOException
	 */
	public ConfigBuilder from(URI location) throws IOException {
		InjectableValues inject = new InjectableValues.Std()
				.addValue("location", location);
		addConfig(new ObjectMapper().configure(JsonParser.Feature.ALLOW_COMMENTS, true)
				.setInjectableValues(inject).readValue(location.toURL(), Config.class));
		return this;
	}

	/**
	 * Reads config from a reader
	 *
	 * @param reader reader containing JSON config
	 * @return this
	 * @throws IOException
	 */
	public ConfigBuilder from(Reader reader) throws IOException {
		InjectableValues inject = new InjectableValues.Std()
				.addValue("location", null);
		addConfig(new ObjectMapper().configure(JsonParser.Feature.ALLOW_COMMENTS, true)
				.setInjectableValues(inject).readValue(reader, Config.class));
		return this;
	}

	/**
	 * Add another config
	 *
	 * @param config config to add to builder
	 * @return this
	 */
	public ConfigBuilder addConfig(Config config) {
		this.location = config.getLocation();
		addIncludes(config.getIncludes());
		addProperties(config.getProperties());
		addPaths(config.getPaths());
		addRepeats(config.getRepeats());
		return this;
	}

	/**
	 * Add includes
	 *
	 * @param includes includes to add
	 * @return this
	 */
	public ConfigBuilder addIncludes(Collection<String> includes) {
		includeBuilder.addAll(includes);
		return this;
	}

	/**
	 * Add properties
	 *
	 * @param properties properties to add
	 * @return this
	 */
	public ConfigBuilder addProperties(Collection<Property> properties) {
		for (Property property : properties) {
			propertyBuilder.put(property.getName(), property);
		}
		return this;
	}

	/**
	 * Add paths
	 *
	 * @param paths paths to add
	 * @return this
	 */
	public ConfigBuilder addPaths(Collection<Path> paths) {
		pathBuilder.addAll(paths);
		return this;
	}

	/**
	 * Add repeats
	 *
	 * @param repeats repeats to add
	 * @return this
	 */
	public ConfigBuilder addRepeats(Collection<Repeat> repeats) {
		repeatBuilder.addAll(repeats);
		return this;
	}

	/**
	 * Build a shallow config, not expanding included configs
	 *
	 * @return config instance
	 */
	public Config buildLocal() {
		return new Config(location, includeBuilder, ImmutableList.copyOf(propertyBuilder.values()),
				pathBuilder, repeatBuilder);
	}

	/**
	 * Build a config by combining all included configs into one
	 */
	public Config build() throws IOException {
		if (includeBuilder.isEmpty()) {
			return buildLocal();
		}

		ConfigBuilder expanded = ConfigBuilder.create();
		for (String include : includeBuilder) {
			URI inclLocation = resolveInclude(include, location);
			Config inclConfig = create().from(inclLocation).build();
			expanded.addProperties(inclConfig.getProperties());
			expanded.addPaths(inclConfig.getPaths());
			expanded.addRepeats(inclConfig.getRepeats());
		}
		expanded.addProperties(propertyBuilder.values());
		expanded.addPaths(pathBuilder);
		expanded.addRepeats(repeatBuilder);

		return expanded.buildLocal();
	}

	/**
	 * Resolves include config to context location
	 *
	 * @param include include config
	 * @param context URI context
	 * @return resolved URI
	 * @throws IOException
	 */
	protected URI resolveInclude(String include, URI context) throws IOException {
		URI uri = URI.create(include);
		if (uri.getScheme() != null) {
			return uri;
		}
		if (context != null) {
			return context.resolve(uri);
		}
		throw new IOException("Cannot find config " + include);
	}

}
