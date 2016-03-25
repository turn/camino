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
	private final Throwable error;

	/**
	 * Constructor
	 *
	 * @param path path
	 * @param error error during evaluation
	 * @param pathStatus path status
	 * @param metricData metric data
	 */
	public PathMetrics(Path path, PathStatus pathStatus, List<MetricDatum> metricData, Throwable error) {
		this.path = path;
		this.pathStatus = pathStatus;
		this.metricData = metricData != null ? ImmutableList.copyOf(metricData) : null;
		this.error = error;
	}

	public Path getPath() {
		return path;
	}

	public Throwable getError() {
		return error;
	}

	public PathStatus getPathStatus() {
		return pathStatus;
	}

	public List<MetricDatum> getMetricData() {
		return metricData;
	}

}
