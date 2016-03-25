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

import com.turn.camino.config.Metric;

/**
 * Metric datum
 *
 * @author llo
 */
public class MetricDatum {

	private final MetricId metricId;
	private final Metric metric;
	private final PathStatus pathStatus;
	private final double value;

	/**
	 * Constructor
	 *
	 * @param metricId metric identifier
	 * @param metric metric definition
	 * @param pathStatus path status
	 * @param value metric value
	 */
	public MetricDatum(MetricId metricId, Metric metric, PathStatus pathStatus, double value) {
		this.metricId = metricId;
		this.metric = metric;
		this.pathStatus = pathStatus;
		this.value = value;
	}

	/**
	 * Get metric ID
	 *
	 * @return metric ID
	 */
	public MetricId getMetricId() {
		return metricId;
	}

	/**
	 * Gets metric definition
	 *
	 * @return metric definition
	 */
	public Metric getMetric() {
		return metric;
	}

	/**
	 * Gets path status used as input to this metric
	 *
	 * @return path status
	 */
	public PathStatus getPathStatus() {
		return pathStatus;
	}

	/**
	 * Gets value of metric
	 *
	 * @return value of metric
	 */
	public double getValue() {
		return value;
	}

}
