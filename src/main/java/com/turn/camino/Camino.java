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

import com.turn.camino.config.*;
import com.turn.camino.render.RenderException;
import com.turn.camino.render.Renderer;
import com.turn.camino.render.TimeValue;
import com.turn.camino.util.Message;
import com.turn.camino.util.MessageExceptionFactory;
import com.turn.camino.util.Validation;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;

/**
 * Main Camino class
 *
 * Computes metrics given environment and configuration
 *
 * @author llo
 */
public class Camino {

	private final Env env;
	private final Config config;
	private final Validation<WrongTypeException> validation =
			new Validation<>(new MessageExceptionFactory<WrongTypeException>() {
				@Override
				public WrongTypeException newException(String message) {
					return new WrongTypeException(message);
				}
			});

	/**
	 * Constructor
	 *
	 * Note that if environment or configuration is null, the constructor will throw a
	 * null pointer exception
	 *
	 * @param env environment
	 * @param config configuration
	 */
	public Camino(Env env, Config config) {
		Preconditions.checkNotNull(env);
		Preconditions.checkNotNull(config);
		this.env = env;
		this.config = config;
	}

	/**
	 * Run Camino
	 *
	 * @throws RenderException
	 */
	public List<PathMetrics> getPathMetrics() throws InvalidNameException, WrongTypeException,
			RenderException, IOException {

		Renderer renderer = env.getRenderer();
		Context context = env.newContext();
		boolean shutdownExecutor = false;
		ExecutorService executorService = env.getExecutorService();
		List<Future<PathMetrics>> futures = Lists.newLinkedList();

		try {
			// if executor service was not specified, create a single-thread executor
			if (executorService == null ){
				executorService = Executors.newSingleThreadExecutor();
				shutdownExecutor = true;
			}

			// render properties
			for (Property property : config.getProperties()) {
				renderProperty(property, renderer, context);
			}

			// render and materialize paths and compute metrics
			processPathMetrics(config.getPaths(), renderer, context, executorService,
					futures);

			// process repeats
			for (Repeat repeat : config.getRepeats()) {
				processRepeat(repeat, renderer, context, executorService, futures);
			}

			// return computed metrics
			List<PathMetrics> pathMetrics = Lists.newArrayListWithCapacity(futures.size());
			for (Future<PathMetrics> future : futures) {
				try {
					pathMetrics.add(future.get());
				} catch (Throwable error) {
					env.getErrorHandler().onWaitError(error);
				}
			}
			return pathMetrics;
		} finally {
			if (shutdownExecutor) {
				executorService.shutdown();
			}
		}
	}

	/**
	 * Render property
	 *
	 * @param property property instance
	 * @param renderer renderer
	 * @param context context
	 * @throws RenderException
	 */
	protected void renderProperty(Property property, Renderer renderer, Context context)
			throws InvalidNameException, WrongTypeException, RenderException {
		String name = renderName(property.getName(), renderer, context);
		Object value = renderer.render(property.getValue(), context);
		context.setProperty(name, value);
	}

	/**
	 * Renders an identifier name and check it's valid
	 *
	 * Renders an expression to a potential identifier name. Checks that the rendered output
	 * is a string, conforms to identifier naming standard (starting with letter or underscore
	 * and followed by zero or more letter, number, or underscore).
	 *
	 * @param name name expression
	 * @param renderer renderer
	 * @param context context
	 * @return rendered identifier name
	 * @throws InvalidNameException
	 * @throws WrongTypeException
	 * @throws RenderException
	 */
	protected String renderName(String name, Renderer renderer, Context context)
			throws InvalidNameException, WrongTypeException, RenderException {
		String rendered = renderString(name, renderer, context);
		checkIdentifier(rendered);
		return rendered;
	}

	/**
	 * Render string
	 *
	 * @param expr expression to render
	 * @param renderer renderer
	 * @param context context
	 * @return rendered string
	 * @throws InvalidNameException
	 * @throws WrongTypeException
	 * @throws RenderException
	 */
	protected String renderString(String expr, Renderer renderer, Context context)
			throws InvalidNameException, WrongTypeException, RenderException {
		return validation.requireType(renderer.render(expr, context), String.class,
				Message.prefix(expr));
	}

	/**
	 * Process path and metrics
	 *
	 * Renders and materializes path, then computes metrics on actual paths
	 *
	 * @param paths paths to process
	 * @param renderer renderer
	 * @param context context
	 * @throws InvalidNameException
	 * @throws WrongTypeException
	 * @throws RenderException
	 * @throws IOException
	 */
	protected void processPathMetrics(List<Path> paths, final Renderer renderer, final Context context,
			ExecutorService executorService, List<Future<PathMetrics>> futures)
			throws InvalidNameException, WrongTypeException, RenderException, IOException {

		for (final Path path : paths) {
			Callable<PathMetrics> callable = new Callable<PathMetrics>() {
				@Override
				public PathMetrics call() throws Exception {
					try {
						return computePathMetrics(path, renderer, context);
					} catch (Throwable error) {
						context.getEnv().getErrorHandler().onPathError(path, error);
						return new PathMetrics(path, null, null);
					}
				}
			};
			futures.add(executorService.submit(callable));
		}
	}

	/**
	 * Renders and materializes one path, then computes its metrics
	 *
	 * @param path path to process
	 * @param renderer renderer
	 * @param context context
	 * @throws InvalidNameException
	 * @throws WrongTypeException
	 * @throws RenderException
	 * @throws IOException
	 */
	protected PathMetrics computePathMetrics(Path path, Renderer renderer, Context context)
			throws InvalidNameException, WrongTypeException, RenderException, IOException {

		// render and materialize path
		PathStatus pathStatus = renderAndMaterializePath(path, renderer, context,
				context.getEnv().getFileSystem());

		// add default metrics
		List<Metric> metrics = Lists.newLinkedList();
		metrics.addAll(getDefaultMetrics(pathStatus));

		// add custom metrics
		metrics.addAll(pathStatus.getPath().getMetrics());

		// compute metrics
		List<MetricDatum> metricData = Lists.newArrayListWithCapacity(metrics.size());
		for (Metric metric : metrics) {
			MetricDatum metricDatum = computeMetric(metric, pathStatus, renderer, context);
			metricData.add(metricDatum);
		}
		return new PathMetrics(path, pathStatus, metricData);
	}

	/**
	 * Get default metrics for a path status
	 *
	 * @param pathStatus path status
	 * @return list of default metrics
	 */
	protected List<Metric> getDefaultMetrics(PathStatus pathStatus) {

		List<Metric> metrics = Lists.newLinkedList();

		// add simple metrics
		metrics.add(new Metric("age", "age", "max"));
		metrics.add(new Metric("size", "size", "sum"));
		metrics.add(new Metric("count", "count", null));

		// add agg metrics if path contains wild card
		if (containsWildcard(pathStatus.getValue())) {
			metrics.add(new Metric("maxAge", "age", "max"));
			metrics.add(new Metric("minAge", "age", "min"));
			metrics.add(new Metric("avgAge", "age", "avg"));
			metrics.add(new Metric("maxSize", "size", "max"));
			metrics.add(new Metric("minSize", "size", "min"));
			metrics.add(new Metric("avgSize", "size", "avg"));
			metrics.add(new Metric("sumSize", "size", "sum"));
		}

		// add creation delay if expected creation time is defined
		if (pathStatus.getExpectedCreationTime() != null) {
			metrics.add(new Metric("creationDelay", "creationDelay", null));
		}

		return metrics;
	}

	/**
	 * Process repeat
	 *
	 * Iterate through each value of the list and process all paths and metrics for each
	 * repeat.
	 *
	 * @param repeat repeat
	 * @param renderer renderer
	 * @param context context
	 * @throws InvalidNameException
	 * @throws WrongTypeException
	 * @throws RenderException
	 * @throws IOException
	 */
	protected void processRepeat(final Repeat repeat, final Renderer renderer, final Context context,
			final ExecutorService executorService, final List<Future<PathMetrics>> futures)
			throws InvalidNameException, WrongTypeException, RenderException, IOException {

		try {
			// render repeat
			checkIdentifier(repeat.getVar());
			List<?> list = validation.requireType(renderer.render(repeat.getList(), context),
					List.class, Message.prefix(repeat.getList()));

			// iterate through list and process paths
			for (Object value : list) {

				// create child context with list element
				Context repeatContext = context.createChild();
				repeatContext.setProperty(repeat.getVar(), value);

				// process path and metrics
				processPathMetrics(repeat.getPaths(), renderer, repeatContext, executorService,
						futures);

				// process nested repeats
				for (Repeat childRepeat : repeat.getRepeats()) {
					processRepeat(childRepeat, renderer, repeatContext, executorService, futures);
				}
			}
		} catch (Throwable error) {
			// log error
			context.getEnv().getErrorHandler().onRepeatError(repeat, error);
		}
	}

	/**
	 * Render and materialize path
	 *
	 * @param path path to render and materialize
	 * @param renderer renderer
	 * @param context context
	 * @param fileSystem file system
	 * @return path status
	 * @throws InvalidNameException
	 * @throws WrongTypeException
	 * @throws RenderException
	 * @throws IOException
	 */
	protected PathStatus renderAndMaterializePath(Path path, Renderer renderer, Context context,
			FileSystem fileSystem) throws InvalidNameException, WrongTypeException,
			RenderException, IOException {

		String name = renderName(path.getName(), renderer, context);
		String value = validation.requireType(renderer.render(path.getValue(), context),
				String.class, Message.prefix(String.format("Value of %s", path.getName())));
		TimeValue expectedCreationTime = null;
		if (path.getExpectedCreationTime() != null) {
			expectedCreationTime = validation.requireType(renderer
					.render(path.getExpectedCreationTime(), context), TimeValue.class,
					Message.prefix(String.format("Expected creation time %s must be a time value",
					path.getExpectedCreationTime())));
		}
		return new PathStatus(name, value, path, materializePath(value, fileSystem),
				expectedCreationTime);
	}

	/**
	 * Materialize path
	 *
	 * Converts a path or path pattern into zero or more actual paths
	 *
	 * @param value rendered value of path
	 * @param fileSystem file system
	 * @return path status
	 * @throws IOException
	 */
	protected List<PathDetail> materializePath(String value, FileSystem fileSystem)
			throws IOException {

		// using value to find path
		FileStatus[] fss = fileSystem.globStatus(new org.apache.hadoop.fs.Path(value));

		// path doesn't exist
		if (fss == null || fss.length == 0) {
			return Collections.emptyList();
		}

		// found match(es)
		List<PathDetail> pathDetails = Lists.newArrayListWithExpectedSize(fss.length);
		for (FileStatus fs : fss) {
			PathDetail pathDetail = new PathDetail(fs.getPath().toString(), fs.isDirectory(),
					fs.getLen(), fs.getModificationTime());
			pathDetails.add(pathDetail);
		}

		// return path details
		return pathDetails;
	}

	/**
	 * Compute metric
	 *
	 * @param metric metric definition
	 * @param pathStatus path status
	 * @param renderer renderer
	 * @param context context
	 * @return MetricData containing name and value of metric
	 * @throws InvalidNameException
	 * @throws WrongTypeException
	 * @throws RenderException
	 */
	protected MetricDatum computeMetric(Metric metric, PathStatus pathStatus, Renderer renderer,
			Context context) throws InvalidNameException, WrongTypeException, RenderException {

		// get metric ID
		MetricId metricId = getMetricId(metric, pathStatus, renderer, context);

		// invoke metric function
		Context metricContext = context.createChild();
		metricContext.setProperty("metric", metric);
		metricContext.setProperty("pathStatus", pathStatus);
		String code = String.format("<%%=%s(metric,pathStatus)%%>", metric.getFunction());
		double value = validation.requireType(renderer.render(code, metricContext),
				Double.class, Message.prefix("expected double"));

		// return metric data
		return new MetricDatum(metricId, metric, pathStatus, value);
	}

	/**
	 * Get metric ID
	 *
	 * Returns attributed that identify a metric, including metric name (either specified by
	 * configuration or just lowercase version of metric type), rendered path name, and rendered
	 * tags.
	 *
	 * @param metric metric
	 * @param pathStatus path status
	 * @param renderer renderer
	 * @param context context
	 * @return metric ID
	 */
	protected MetricId getMetricId(Metric metric, PathStatus pathStatus, Renderer renderer, Context context)
			throws RenderException, WrongTypeException, InvalidNameException {

		// metric name
		String name = metric.getName();
		if (name == null) {
			name = metric.getFunction();
		}

		// render tags
		List<Tag> tags = pathStatus.getPath().getTags();
		List<Tag> renderedTags = Lists.newArrayListWithCapacity(tags.size());
		for (Tag tag : tags) {
			String key = renderName(tag.getKey(), renderer, context);
			String value = renderString(tag.getValue(), renderer, context);
			renderedTags.add(new Tag(key, value));
		}

		return new MetricId(name, pathStatus.getName(), renderedTags);
	}

	/**
	 * Checks identifier name
	 *
	 * Identifier name is valid if it starts with a letter or underscore, and followed by
	 * zero or more letters, numbers, or underscores.
	 *
	 * Throws {@link com.turn.camino.InvalidNameException} if name is invalid.
	 *
	 * @param name name to check
	 * @throws InvalidNameException
	 */
	protected void checkIdentifier(String name) throws InvalidNameException {
		if (name.length() == 0) {
			throw new InvalidNameException("Identifier has zero length");
		}
		if (!name.matches("^[a-zA-Z$_][a-zA-Z0-9$_]*$")) {
			throw new InvalidNameException(String.format("Identifier \"%s\" is not alphanumeric",
					name));
		}
	}

	/**
	 * Returns true if value contains wild cards
	 *
	 * @param value string to check for wild cards
	 * @return true if value contains wild cards, false otherwise
	 */
	protected boolean containsWildcard(String value) {
		return value.matches(".*(([^\\\\][\\*\\?])|([^\\\\]\\[.*[^\\\\]\\])|([^\\\\]\\{.*[^\\\\]\\})).*");
	}

}
