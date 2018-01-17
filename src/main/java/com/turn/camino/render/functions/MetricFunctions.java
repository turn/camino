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
package com.turn.camino.render.functions;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.turn.camino.Context;
import com.turn.camino.PathDetail;
import com.turn.camino.PathStatus;
import com.turn.camino.WrongTypeException;
import com.turn.camino.config.Metric;
import com.turn.camino.render.Function;
import com.turn.camino.render.FunctionCallException;
import com.turn.camino.render.FunctionCallExceptionFactory;
import com.turn.camino.util.Validation;

import java.util.List;

import static com.turn.camino.util.Message.prefix;

/**
 * Metric functions
 *
 * @author llo
 */
public class MetricFunctions {

	private final static Validation<FunctionCallException> VALIDATION =
			new Validation<>(new FunctionCallExceptionFactory());

	private final static ImmutableMap<String, AggregateFactory> AGGREGATES = ImmutableMap.<String, AggregateFactory>builder()
			.put("sum", () -> new Aggregate() {
				double sum = 0;
				@Override
				public void put(double value) {
					sum += value;
				}
				@Override
				public double get() {
					return sum;
				}
			})
			.put("avg", () -> new Aggregate() {
				double sum = 0;
				double count = 0;
				@Override
				public void put(double value) {
					sum += value;
					count++;
				}
				@Override
				public double get() {
					return count > 0 ? sum / count : 0;
				}
			})
			.put("max", () -> new Aggregate() {
				double maxValue = -Double.MAX_VALUE;
				@Override
				public void put(double value) {
					maxValue = Math.max(maxValue, value);
				}
				@Override
				public double get() {
					return maxValue;
				}
			})
			.put("min", () -> new Aggregate() {
				double minValue = Double.MAX_VALUE;
				@Override
				public void put(double value) {
					minValue = Math.min(minValue, value);
				}
				@Override
				public double get() {
					return minValue;
				}
			})
			.build();

	/**
	 * Simple aggregate interface
	 */
	interface Aggregate {
		void put(double value);
		double get();
	}

	/**
	 * Aggregate factory
	 */
	interface AggregateFactory {
		Aggregate newInstance();
	}

	/**
	 * Metric function
	 */
	public static abstract class MetricFunction implements Function {

		/**
		 * Invokes metric function
		 *
		 * @param params actual parameters to function call
		 * @param context context in which the function operates
		 * @return metric value
		 * @throws FunctionCallException
		 */
		@Override
		public Object invoke(List<?> params, Context context) throws FunctionCallException {
			VALIDATION.requireListSize(params, 2, 2, prefix("parameters"));
			return invoke(VALIDATION.requireType(params.get(0), Metric.class, prefix("metric")),
					VALIDATION.requireType(params.get(1), PathStatus.class, prefix("pathStatus")),
					context);
		}

		/**
		 * Invoke on path status
		 *
		 * @param metric metric
		 * @param pathStatus path status
		 * @param context context
		 * @return metric value
		 * @throws FunctionCallException
		 */
		public abstract double invoke(Metric metric, PathStatus pathStatus, Context context)
			throws FunctionCallException;

	}

	/**
	 * Metric that aggregate over path details
	 */
	public static class MetricAggregateFunction extends MetricFunction {

		@Override
		public double invoke(Metric metric, PathStatus pathStatus, Context context)
				throws FunctionCallException {
			Function function;
			try {
				function = VALIDATION.requireNotNull(context.getProperty(metric.getFunction(),
						Function.class), prefix(metric.getFunction() + " is not a function"));
			} catch (WrongTypeException e) {
				throw new FunctionCallException(e);
			}
			AggregateFactory aggregateFactory = AGGREGATES.get(metric.getAggregate());
			if (aggregateFactory == null) {
				throw new FunctionCallException("Unknown aggregate " + metric.getAggregate());
			}
			Aggregate aggregate = aggregateFactory.newInstance();
			if (pathStatus.getPathDetails().isEmpty()) {
				return metric.getDefaultValue();
			}
			for (PathDetail pathDetail : pathStatus.getPathDetails()) {
				aggregate.put(((Number) function.invoke(Lists.newArrayList(metric, pathDetail),
						context)).doubleValue());
			}
			return aggregate.get();
		}
	}

	/**
	 * Computes creation delay metric
	 *
	 * Algorithm is if path exists, then the value is 0. Otherwise, creation delay is the
	 * difference between current time and creation time.
	 *
	 * @author llo
	 */
	public static class CreationDelay extends MetricFunction {

		/**
		 * Invokes creation delay
		 * @param metric metric definition
		 * @param pathStatus path status required by metric
		 * @param context context
		 * @return time difference in milliseconds
		 */
		@Override
		public double invoke(Metric metric, PathStatus pathStatus, Context context)
				throws FunctionCallException {

			// creationTime is a required property
			long creationTime;
			if (pathStatus.getExpectedCreationTime() != null) {
				creationTime = pathStatus.getExpectedCreationTime().getTime();
			} else {
				throw new FunctionCallException("Expected creation time not defined");
			}

			List<PathDetail> pathDetails = pathStatus.getPathDetails();

			// if path exists, then no delay
			if (!pathDetails.isEmpty()) {
				return 0;
			} else {
				return context.getGlobalInstanceTime() - creationTime;
			}
		}
	}

	/**
	 * Count number of resolved paths
	 */
	public static class Count extends MetricFunction {
		@Override
		public double invoke(Metric metric, PathStatus pathStatus, Context context)
				throws FunctionCallException {
			return pathStatus.getPathDetails().size();
		}
	}

	/**
	 * Path detail function
	 */
	public static abstract class PathDetailFunction implements Function {

		/**
		 * Invokes path detail function
		 *
		 * @param params  actual parameters to function call
		 * @param context context in which the function operates
		 * @return metric value
		 * @throws FunctionCallException
		 */
		@Override
		public Object invoke(List<?> params, Context context) throws FunctionCallException {
			VALIDATION.requireListSize(params, 2, 2, prefix("parameters"));
			return invoke(VALIDATION.requireType(params.get(0), Metric.class, prefix("metric")),
					VALIDATION.requireType(params.get(1), PathDetail.class, prefix("pathDetail")),
					context);
		}

		public abstract double invoke(Metric metric, PathDetail pathDetail, Context context)
				throws FunctionCallException;
	}

	/**
	 * Computes age of path
	 *
	 * If path has multiple actual paths, will rely on aggregate property to compute age
	 */
	public static class Age extends PathDetailFunction {
		@Override
		public double invoke(Metric metric, PathDetail pathDetail, Context context)
				throws FunctionCallException {
			return context.getGlobalInstanceTime() - pathDetail.getLastModifiedTime();
		}
	}

	/**
	 * Computes size of a path
	 *
	 * If multiple paths are resolved, then it is the sum of sizes of all paths
	 */
	public static class Size extends PathDetailFunction {
		@Override
		public double invoke(Metric metric, PathDetail pathDetail, Context context)
				throws FunctionCallException {
			return pathDetail.getLength();
		}
	}
}
