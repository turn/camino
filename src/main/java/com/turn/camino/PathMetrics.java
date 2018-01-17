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

import com.google.common.collect.ImmutableList;
import com.turn.camino.config.Path;

import java.util.List;

/**
 * Path metrics
 *
 * @author llo
 */
public class PathMetrics {

	private final Path path;
	private final PathStatus pathStatus;
	private final List<MetricDatum> metricData;

	/**
	 * Constructor
	 *
	 * @param path path
	 * @param pathStatus path status
	 * @param metricData metric data
	 */
	public PathMetrics(Path path, PathStatus pathStatus, List<MetricDatum> metricData) {
		this.path = path;
		this.pathStatus = pathStatus;
		this.metricData = metricData != null ? ImmutableList.copyOf(metricData) : null;
	}

	/**
	 * Get path
	 *
	 * @return path
	 */
	public Path getPath() {
		return path;
	}

	/**
	 * Get path status
	 *
	 * @return path status
	 */
	public PathStatus getPathStatus() {
		return pathStatus;
	}

	/**
	 * Get metric data
	 *
	 * @return list of metric data
	 */
	public List<MetricDatum> getMetricData() {
		return metricData;
	}

}
