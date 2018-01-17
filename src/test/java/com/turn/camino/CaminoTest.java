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

import com.google.common.collect.ImmutableMap;
import com.turn.camino.config.*;
import com.turn.camino.render.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.google.common.collect.ImmutableList.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 * Unit test for Camino
 *
 * @author llo
 */
@Test
public class CaminoTest {

	private final static double EPSILON = 1e-6;
	private final static Map<String,String> NULL_TAGS = null;

	private Camino camino;
	private ExecutorService executorService;

	/**
	 * Setup environment
	 */
	@BeforeClass
	public void setUp() throws IOException {

		// mock file system
		FileSystem fileSystem = mock(FileSystem.class);

		// mock environment
		Env env = mock(Env.class);
		when(env.getFileSystem()).thenReturn(fileSystem);
		camino = new Camino(env, ConfigBuilder.create().buildLocal());

		// create executor service
		executorService = Executors.newSingleThreadExecutor();
		when(env.getExecutorService()).thenReturn(executorService);
	}

	/**
	 * Tear down environment
	 * @throws IOException
	 */
	@AfterClass
	public void tearDown() throws IOException {
		executorService.shutdown();
	}

	/**
	 * Test creating camino with null environment
	 */
	@Test(expectedExceptions = NullPointerException.class)
	public void testConstructorNullEnv() {
		new Camino(null, mock(Config.class));
	}

	/**
	 * Test creating camino with null config
	 */
	@Test(expectedExceptions = NullPointerException.class)
	public void testConstructorNullConfig() {
		new Camino(mock(Env.class), null);
	}

	/**
	 * Test rendering a property
	 *
	 * Tests that name and value returned from renderer are setProperty into context
	 *
	 * @throws RenderException
	 */
	@Test
	public void testRenderName() throws InvalidNameException, WrongTypeException,
			RenderException {
		Context context = mock(Context.class);
		Renderer renderer = mock(Renderer.class);
		when(renderer.render("foo<%=id%>", context)).thenReturn("foo123");
		String name = camino.renderName("foo<%=id%>", renderer, context);
		assertEquals(name, "foo123");
	}

	/**
	 * Test if name does not conform to identifier name standard
	 *
	 * @throws InvalidNameException
	 * @throws WrongTypeException
	 * @throws RenderException
	 */
	@Test(expectedExceptions = InvalidNameException.class)
	public void testRenderNameInvalidName() throws InvalidNameException, WrongTypeException,
			RenderException {
		Context context = mock(Context.class);
		Renderer renderer = mock(Renderer.class);
		when(renderer.render("foo", context)).thenReturn("123");
		camino.renderName("foo", renderer, context);
	}

	/**
	 * Test if name renders to a non-string type
	 *
	 * @throws InvalidNameException
	 * @throws WrongTypeException
	 * @throws RenderException
	 */
	@Test(expectedExceptions = WrongTypeException.class)
	public void testRenderNameIncorrectType() throws InvalidNameException, WrongTypeException,
			RenderException {
		Context context = mock(Context.class);
		Renderer renderer = mock(Renderer.class);
		when(renderer.render("foo", context)).thenReturn(123);
		camino.renderName("foo", renderer, context);
	}

	/**
	 * Test rendering a property
	 *
	 * Tests that name and value returned from renderer are setProperty into context
	 *
	 * @throws RenderException
	 */
	@Test
	public void testRenderProperty() throws InvalidNameException, WrongTypeException,
			RenderException {
		Context context = mock(Context.class);
		Renderer renderer = mock(Renderer.class);
		when(renderer.render("path", context)).thenReturn("path");
		when(renderer.render("<%=dir%>/<%=file%>", context)).thenReturn("/data/delta_1.log");
		camino.renderProperty(new Property("path", "<%=dir%>/<%=file%>"), renderer, context);
		verify(context).setProperty("path", "/data/delta_1.log");
	}

	/**
	 * Test materializing a path
	 *
	 * @throws IOException
	 */
	@Test
	public void testMaterializePath() throws IOException {

		long blockSize = 256L * 1024 * 1024;
		long now = System.currentTimeMillis();
		FileSystem fileSystem = mock(FileSystem.class);

		// path that results in one single file
		String pathValue1 = "/foo/bar";
		org.apache.hadoop.fs.Path path1 = new org.apache.hadoop.fs.Path(pathValue1);
		FileStatus[] fss1 = new FileStatus[] {
				new FileStatus(15000, false, 3, blockSize, now - 10000, path1)};
		when(fileSystem.globStatus(path1)).thenReturn(fss1);
		List<PathDetail> pathDetails1 = camino.materializePath(pathValue1, fileSystem);
		assertNotNull(pathDetails1);
		assertEquals(pathDetails1.size(), 1);
		assertEquals(pathDetails1.get(0).getLastModifiedTime(), fss1[0].getModificationTime());
		assertEquals(pathDetails1.get(0).getLength(), fss1[0].getLen());
		assertEquals(pathDetails1.get(0).isDirectory(), fss1[0].isDirectory());
		assertEquals(pathDetails1.get(0).getPathValue(), pathValue1);

		// path that results in no file
		String pathValue2 = "/foo/baz";
		org.apache.hadoop.fs.Path path2 = new org.apache.hadoop.fs.Path(pathValue2);
		when(fileSystem.globStatus(path2)).thenReturn(new FileStatus[] {});
		List<PathDetail> pathDetails2 = camino.materializePath(pathValue2, fileSystem);
		assertNotNull(pathDetails2);
		assertEquals(pathDetails2.size(), 0);

		// path whose parent doesn't exist (so globStatus returns null)
		String pathValue3 = "/goo/bao";
		org.apache.hadoop.fs.Path path3 = new org.apache.hadoop.fs.Path(pathValue3);
		when(fileSystem.globStatus(path3)).thenReturn(null);
		List<PathDetail> pathDetails3 = camino.materializePath(pathValue3, fileSystem);
		assertNotNull(pathDetails3);
		assertEquals(pathDetails3.size(), 0);

		// path that returns multiple files
		String pathValue4 = "/foo/bub_*";
		org.apache.hadoop.fs.Path path4 = new org.apache.hadoop.fs.Path(pathValue4);
		FileStatus[] fss4 = new FileStatus[] {
				new FileStatus(15000, false, 3, blockSize, now - 10000,
						new org.apache.hadoop.fs.Path("/foo/bub_1")),
				new FileStatus(24000, false, 3, blockSize, now - 15000,
						new org.apache.hadoop.fs.Path("/foo/bub_2")) };
		when(fileSystem.globStatus(path4)).thenReturn(fss4);
		List<PathDetail> pathDetails4 = camino.materializePath(pathValue4, fileSystem);
		assertNotNull(pathDetails4);
		assertEquals(pathDetails4.size(), 2);
		assertEquals(pathDetails4.get(0).getLastModifiedTime(), fss4[0].getModificationTime());
		assertEquals(pathDetails4.get(0).getLength(), fss4[0].getLen());
		assertEquals(pathDetails4.get(0).isDirectory(), fss4[0].isDirectory());
		assertEquals(pathDetails4.get(0).getPathValue(), "/foo/bub_1");
		assertEquals(pathDetails4.get(1).getLastModifiedTime(), fss4[1].getModificationTime());
		assertEquals(pathDetails4.get(1).getLength(), fss4[1].getLen());
		assertEquals(pathDetails4.get(1).isDirectory(), fss4[1].isDirectory());
		assertEquals(pathDetails4.get(1).getPathValue(), "/foo/bub_2");
	}

	/**
	 * Test render and materialize path
	 *
	 * Note that the materialize path is another test, so we only care about rendering the
	 * name nad value interaction.
	 *
	 * @throws InvalidNameException
	 * @throws WrongTypeException
	 * @throws RenderException
	 * @throws IOException
	 */
	@Test
	public void testRenderAndMaterializePath() throws InvalidNameException, WrongTypeException,
			RenderException, IOException {

		Renderer renderer = mock(Renderer.class);
		when(renderer.render(eq("a"), any(Context.class))).thenReturn("a");
		when(renderer.render(eq("b"), any(Context.class))).thenReturn("b");
		when(renderer.render(eq("<%=t%>"), any(Context.class)))
				.thenReturn(new TimeValue(TimeZone.getDefault(), System.currentTimeMillis()));
		Context context = mock(Context.class);
		FileSystem fileSystem = mock(FileSystem.class);
		PathStatus pathStatus = camino.renderAndMaterializePath(new Path("a", "b", null,
				NULL_TAGS, "<%=t%>"), renderer, context, fileSystem);
		assertNotNull(pathStatus);
		assertEquals(pathStatus.getName(), "a");
		assertEquals(pathStatus.getValue(), "b");
		assertEquals(pathStatus.getPathDetails().size(), 0);
	}

	/**
	 * Test expected time value not resolving to TimeValue object
	 *
	 * @throws InvalidNameException
	 * @throws WrongTypeException
	 * @throws RenderException
	 * @throws IOException
	 */
	@Test(expectedExceptions = WrongTypeException.class)
	public void testRenderAndMaterializePathInvalidExpectedCreationTime()
			throws InvalidNameException, WrongTypeException, RenderException, IOException {

		Renderer renderer = mock(Renderer.class);
		when(renderer.render(eq("a"), any(Context.class))).thenReturn("a");
		when(renderer.render(eq("b"), any(Context.class))).thenReturn("b");
		when(renderer.render(eq("<%=t%>"), any(Context.class))).thenReturn("abc");
		Context context = mock(Context.class);
		FileSystem fileSystem = mock(FileSystem.class);
		camino.renderAndMaterializePath(new Path("a", "b", null,
				NULL_TAGS, "<%=t%>"), renderer, context, fileSystem);
	}

	/**
	 * Test compute a metric
	 *
	 * @throws InvalidNameException
	 * @throws WrongTypeException
	 * @throws RenderException
	 */
	@Test
	public void testComputeMetric() throws InvalidNameException, WrongTypeException,
			RenderException {

		double metricValue = 456.78;

		// set up metric and associated path status
		Metric metric = new Metric("m1", "age", "sum", "testAgg", 0);
		long lastModifiedDate = System.currentTimeMillis();
		PathStatus pathStatus = new PathStatus("myPath", "/path/here/", mock(Path.class), of(
				new PathDetail("/foo/bar", false, 1000, lastModifiedDate)), null);

		// mock renderer to test metric-level property
		Renderer renderer = mock(Renderer.class);

		// set up context and environment
		Env env = mock(Env.class);
		Context context = mockGlobalContext(env);
		Function aggFunction = mockMetricFunction(context, "testAgg", metricValue);

		// compute metric
		MetricDatum metricDatum = camino.computeMetric(metric, pathStatus, renderer, context);

		// verify that aggregate function was called
		verify(aggFunction).invoke(any(List.class), any(Context.class));

		// verify that correct value was passed back
		assertEquals(metricDatum.getMetricId().getFullName(), "myPath.m1");
		assertEquals(metricDatum.getValue(), metricValue, EPSILON);
	}

	/**
	 * Test getMetricId()
	 */
	@Test
	public void testGetMetricId() throws WrongTypeException, RenderException, InvalidNameException {

		PathStatus pathStatus = new PathStatus("myPath", "/a/b", new Path("myPath", "/a/b", null,
				ImmutableMap.of("pk", "<%=pkv%>"), null),
				Lists.<PathDetail>newArrayList(), null);
		Renderer renderer = mock(Renderer.class);
		Context context = mock(Context.class);
		when(renderer.render(eq("pk"), any(Context.class))).thenReturn("pk");
		when(renderer.render(eq("<%=pkv%>"), any(Context.class))).thenReturn("1234");

		// test named metric
		Metric metric = new Metric("m1", "foo", "avg", null, 0);
		MetricId metricId = camino.getMetricId(metric, pathStatus, renderer, context);
		assertNotNull(metricId);
		assertEquals(metricId.getName(), "m1");
		assertEquals(metricId.getFullName(), "myPath.m1");
		assertEquals(metricId.getTags().size(), 1);
		assertEquals(metricId.getTags().get(0).getKey(), "pk");
		assertEquals(metricId.getTags().get(0).getValue(), "1234");

		// test unnamed metric
		metric = new Metric(null, "age", "max", null, 0);
		metricId = camino.getMetricId(metric, pathStatus, renderer, context);
		assertNotNull(metricId);
		assertEquals(metricId.getName(), "age");
		assertEquals(metricId.getFullName(), "myPath.age");
	}

	/**
	 * Test processPathMetrics
	 *
	 * @throws InvalidNameException
	 * @throws WrongTypeException
	 * @throws RenderException
	 * @throws IOException
	 */
	@Test
	public void testProcessPathMetrics() throws InvalidNameException, WrongTypeException,
			RenderException, IOException, ExecutionException, InterruptedException {

		// create test environment
		long now = System.currentTimeMillis();
		double metricValue = 123456;
		Env env = mock(Env.class);
		Context context = mockGlobalContext(env);

		// mock renderer
		Renderer renderer = mock(Renderer.class);
		mockMetricFunction(context, "age", metricValue);
		mockMetricFunction(context, "size", metricValue);
		mockMetricFunction(context, "count", metricValue);
		when(renderer.render(eq("big_data"), any(Context.class))).thenReturn("big_data");
		when(renderer.render(eq("/app/big_data"), any(Context.class))).thenReturn("/app/big_data");
		List<Path> paths = of(new Path("big_data", "/app/big_data"));

		// mock file system
		FileSystem fileSystem = mockFileSystem(env);
		org.apache.hadoop.fs.Path hadoopPath = new org.apache.hadoop.fs.Path("/app/big_data");
		when(fileSystem.globStatus(hadoopPath)).thenReturn(new FileStatus[] {
				new FileStatus(15000, false, 3, 256*1024*1024, now - 10000, hadoopPath) });

		// process path metrics
		List<Future<PathMetrics>> futures = Lists.newLinkedList();
		camino.processPathMetrics(paths, renderer, context, executorService, futures);

		// check that path status was resolved correctly
		PathMetrics pathMetrics = futures.get(0).get();
		PathStatus pathStatus = pathMetrics.getPathStatus();
		assertEquals(pathStatus.getName(), "big_data");
		assertEquals(pathStatus.getValue(), "/app/big_data");
		assertEquals(pathStatus.getPathDetails().size(), 1);
		assertEquals(pathStatus.getPathDetails().get(0).getPathValue(),
				"/app/big_data");
		assertFalse(pathStatus.getPathDetails().get(0).isDirectory());
		assertEquals(pathStatus.getPathDetails().get(0).getLength(), 15000);
		assertEquals(pathStatus.getPathDetails().get(0).getLastModifiedTime(),
				now - 10000);

		// check that metric data is expected
		List<MetricDatum> metricData = pathMetrics.getMetricData();
		assertEquals(metricData.size(), 3);
		assertEquals(metricData.get(0).getMetricId().getFullName(), "big_data.age");
		assertEquals(metricData.get(0).getValue(), metricValue, EPSILON);
		assertEquals(metricData.get(1).getMetricId().getFullName(), "big_data.size");
		assertEquals(metricData.get(1).getValue(), metricValue, EPSILON);
		assertEquals(metricData.get(2).getMetricId().getFullName(), "big_data.count");
		assertEquals(metricData.get(2).getValue(), metricValue, EPSILON);
	}

	/**
	 * Test processPathMetrics interaction with executor service and list of futures
	 *
	 * @throws IOException
	 * @throws InvalidNameException
	 * @throws WrongTypeException
	 * @throws RenderException
	 */
	@Test
	public void testProcessPathMetricsInteraction() throws IOException, InvalidNameException,
			WrongTypeException, RenderException {
		List<Path> paths = of(new Path("big_data", "/app/big_data"),
				new Path("small_data", "/app/small_data"));
		ExecutorService executorService = mock(ExecutorService.class);
		List<Future<PathMetrics>> futures = mock(PathMetricsFutureList.class);
		camino.processPathMetrics(paths, mock(Renderer.class), mock(Context.class),
				executorService, futures);
		verify(executorService, times(2)).submit(any(Callable.class));
		verify(futures, times(2)).add(any(PathMetricsFuture.class));
	}

	/**
	 * Test that default metrics are added for a single path
	 */
	@Test
	public void testGetDefaultMetricsForSinglePath() {
		Path path = new Path("testPath", "/test/path");
		PathStatus pathStatus = new PathStatus("foo", "/foo", path, ImmutableList.<PathDetail>of(), null);
		List<Metric> metrics = Lists.newArrayList(camino.getDefaultMetrics(pathStatus));
		assertEquals(metrics.size(), 3);
		Collections.sort(metrics, new Comparator<Metric>() {
			@Override
			public int compare(Metric m1, Metric m2) {
				return m1.getName().compareTo(m2.getName());
			}
		});
		assertEquals(metrics.get(0).getName(), "age");
		assertEquals(metrics.get(1).getName(), "count");
		assertEquals(metrics.get(2).getName(), "size");
	}

	/**
	 * Test that default metrics are added for a path with wildcard
	 */
	@Test
	public void testGetDefaultMetricsForWildcard() {
		Path path = new Path("testPath", "/test/path");
		PathStatus pathStatus = new PathStatus("foo", "/foo/*", path, ImmutableList.<PathDetail>of(), null);
		List<Metric> metrics = Lists.newArrayList(camino.getDefaultMetrics(pathStatus));
		assertEquals(metrics.size(), 10);
		Collections.sort(metrics, new Comparator<Metric>() {
			@Override
			public int compare(Metric m1, Metric m2) {
				return m1.getName().compareTo(m2.getName());
			}
		});
		assertEquals(metrics.get(0).getName(), "age");
		assertEquals(metrics.get(1).getName(), "avgAge");
		assertEquals(metrics.get(2).getName(), "avgSize");
		assertEquals(metrics.get(3).getName(), "count");
		assertEquals(metrics.get(4).getName(), "maxAge");
		assertEquals(metrics.get(5).getName(), "maxSize");
		assertEquals(metrics.get(6).getName(), "minAge");
		assertEquals(metrics.get(7).getName(), "minSize");
		assertEquals(metrics.get(8).getName(), "size");
		assertEquals(metrics.get(9).getName(), "sumSize");
	}

	/**
	 * Test that default metrics are added for a path with expected creation time
	 */
	@Test
	public void testGetDefaultMetricsForExpectedCreationTime() {
		Path path = new Path("testPath", "/test/path");
		PathStatus pathStatus = new PathStatus("foo", "/foo", path, ImmutableList.<PathDetail>of(),
				new TimeValue(TimeZone.getDefault(), System.currentTimeMillis()));
		List<Metric> metrics = Lists.newArrayList(camino.getDefaultMetrics(pathStatus));
		assertEquals(metrics.size(), 4);
		Collections.sort(metrics, new Comparator<Metric>() {
			@Override
			public int compare(Metric m1, Metric m2) {
				return m1.getName().compareTo(m2.getName());
			}
		});
		assertEquals(metrics.get(0).getName(), "age");
		assertEquals(metrics.get(1).getName(), "count");
		assertEquals(metrics.get(2).getName(), "creationDelay");
		assertEquals(metrics.get(3).getName(), "size");
	}

	/**
	 * Test processing an repeat
	 *
	 * @throws InvalidNameException
	 * @throws WrongTypeException
	 * @throws RenderException
	 * @throws IOException
	 */
	@Test
	public void testProcessRepeat() throws InvalidNameException, WrongTypeException,
			RenderException, IOException {

		// create repeat
		Repeat repeat = newRepeat("theVar", "theList", new Path("thePath", "/das/auto"));

		// mock renderer
		Renderer renderer = mock(Renderer.class);
		when(renderer.render(eq("theList"), any(Context.class))).thenReturn(Lists.
				newArrayList("a", "b"));
		when(renderer.render(eq("thePath"), any(Context.class))).thenReturn("thePath");
		when(renderer.render(eq("/das/auto"), any(Context.class))).thenReturn("/das/auto");

		// mock environment
		Env env = mock(Env.class);
		mockFileSystem(env);
		Context context = mockGlobalContext(env);
		List<Context> repeatContexts = mockChildContexts(2, context, context, env);
		mockChildContext(repeatContexts.get(0), context, env);
		mockChildContext(repeatContexts.get(1), context, env);

		// exercise processRepeat
		List<Future<PathMetrics>> futures = Lists.newLinkedList();
		camino.processRepeat(repeat, renderer, context, executorService, futures);

		// verify that the two values in the list are iterated over
		verify(repeatContexts.get(0)).setProperty("theVar", "a");
		verify(repeatContexts.get(1)).setProperty("theVar", "b");
	}

	/**
	 * Test process nested repeats recursively
	 *
	 * @throws InvalidNameException
	 * @throws WrongTypeException
	 * @throws RenderException
	 * @throws IOException
	 */
	@Test
	public void testProcessRepeatRecursive() throws InvalidNameException, WrongTypeException,
			RenderException, IOException {

		// create recursive repeat
		Repeat repeat = new Repeat("outerVar", "outerList", null, ImmutableList
				.of(new Repeat("innerVar", "innerList", ImmutableList.of(new Path("thePath",
						"/das/auto")), null)));

		// mock renderer
		Renderer renderer = mock(Renderer.class);
		when(renderer.render(eq("outerList"), any(Context.class))).thenReturn(Lists.
				newArrayList("a", "b"));
		when(renderer.render(eq("innerList"), any(Context.class))).thenReturn(Lists.
				newArrayList("x", "y"));
		when(renderer.render(eq("thePath"), any(Context.class))).thenReturn("thePath");
		when(renderer.render(eq("/das/auto"), any(Context.class))).thenReturn("/das/auto");

		// mock environment
		Env env = mock(Env.class);
		mockFileSystem(env);
		Context context = mockGlobalContext(env);
		List<Context> repeatContexts = mockChildContexts(2, context, context, env);
		List<Context> innerContexts1 = mockChildContexts(2, repeatContexts.get(0), context, env);
		mockChildContext(innerContexts1.get(0), context, env);
		mockChildContext(innerContexts1.get(1), context, env);
		List<Context> innerContexts2 = mockChildContexts(2, repeatContexts.get(1), context, env);
		mockChildContext(innerContexts2.get(0), context, env);
		mockChildContext(innerContexts2.get(1), context, env);

		// exercise processRepeat
		List<Future<PathMetrics>> futures = Lists.newLinkedList();
		camino.processRepeat(repeat, renderer, context, executorService, futures);

		// verify that the two values in the list are iterated over
		verify(repeatContexts.get(0)).setProperty("outerVar", "a");
		verify(repeatContexts.get(1)).setProperty("outerVar", "b");
		verify(innerContexts1.get(0)).setProperty("innerVar", "x");
		verify(innerContexts1.get(1)).setProperty("innerVar", "y");
		verify(innerContexts2.get(0)).setProperty("innerVar", "x");
		verify(innerContexts2.get(1)).setProperty("innerVar", "y");
	}

	/**
	 * Test invalid var name in repeat
	 *
	 * @throws InvalidNameException
	 * @throws WrongTypeException
	 * @throws RenderException
	 * @throws IOException
	 */
	@Test
	public void testProcessRepeatInvalidVarName() throws InvalidNameException, WrongTypeException,
			RenderException, IOException {
		Repeat repeat = newRepeat("3ab", "theList", new Path("thePath", "/das/auto"));

		// mock error handler
		Env env = mock(Env.class);
		ErrorHandler errorHandler = mock(ErrorHandler.class);
		when(env.getErrorHandler()).thenReturn(errorHandler);
		Context context = mock(Context.class);
		when(context.getEnv()).thenReturn(env);

		// call repeat with error
		camino.processRepeat(repeat, mock(Renderer.class), context, executorService,
				Lists.<Future<PathMetrics>>newLinkedList());

		// verify we handled error
		verify(errorHandler).onRepeatError(eq(repeat), any(InvalidNameException.class));
	}

	/**
	 * Test list not being a list
	 *
	 * @throws InvalidNameException
	 * @throws WrongTypeException
	 * @throws RenderException
	 * @throws IOException
	 */
	@Test
	public void testProcessRepeatWrongListType() throws InvalidNameException, WrongTypeException,
			RenderException, IOException {
		Repeat repeat = newRepeat("theVar", "theList", new Path("thePath", "/das/auto"));
		Renderer renderer = mock(Renderer.class);
		when(renderer.render(eq("theList"), any(Context.class))).thenReturn("aaa");

		// mock error handler
		Env env = mock(Env.class);
		ErrorHandler errorHandler = mock(ErrorHandler.class);
		when(env.getErrorHandler()).thenReturn(errorHandler);
		Context context = mock(Context.class);
		when(context.getEnv()).thenReturn(env);

		// call repeat with error
		camino.processRepeat(repeat, renderer, context, executorService,
				Lists.<Future<PathMetrics>>newLinkedList());

		// verify we handled error
		verify(errorHandler).onRepeatError(eq(repeat), any(WrongTypeException.class));
	}

	/**
	 * Test getPathMetrics()
	 *
	 * Set up is to have one global property, one top-level path, one repeat that results in two paths.
	 * No path will contain expected creation time nor wild card, thus only the three basic metrics (age,
	 * count, and size) will be included by default. Therefore it should yield three path status results
	 * and nine metric data.
	 */
	@Test
	public void testGetPathMetrics() throws InvalidNameException, WrongTypeException,
			RenderException, IOException {

		long now = System.currentTimeMillis();
		double ageValue = 12340000;
		double sizeValue = 10000;
		double countValue = 5;

		// build config
		List<Property> properties = of(new Property("root", "/app/data"));
		List<Path> paths = of(new Path("big_data", "/app/big_data"));
		List<Repeat> repeats = of(newRepeat("nom", "<%=list('foo','bar')%>",
				new Path("do_<%=nom%>", "<%=root%>/<%=nom%>")));
		Config config = ConfigBuilder.create().addProperties(properties).addPaths(paths)
				.addRepeats(repeats).buildLocal();

		// mock environment
		Env env = mock(Env.class);
		FileSystem fileSystem = mockFileSystem(env);
		org.apache.hadoop.fs.Path path1 = new org.apache.hadoop.fs.Path("/app/data");
		FileStatus[] fss = new FileStatus[] {
				new FileStatus(15000, false, 3, 64*1024*1024, now - 10000, path1)};
		when(fileSystem.globStatus(any(org.apache.hadoop.fs.Path.class)))
				.thenReturn(fss);
		Context context = mockGlobalContext(env);
		mockMetricFunction(context, "age", ageValue);
		mockMetricFunction(context, "size", sizeValue);
		mockMetricFunction(context, "count", countValue);
		List<Context> childContexts = mockChildContexts(2, context, context, env);
		for (Context childContext : childContexts) {
			mockMetricFunction(childContext, "age", ageValue);
			mockMetricFunction(childContext, "size", sizeValue);
			mockMetricFunction(childContext, "count", countValue);
		}

		// mock renderer
		Renderer renderer = mock(Renderer.class);
		when(renderer.render(eq("root"), any(Context.class))).thenReturn("root");
		when(renderer.render(eq("/app/data"), any(Context.class))).thenReturn("/app/data");
		when(renderer.render(eq("big_data"), any(Context.class))).thenReturn("big_data");
		when(renderer.render(eq("/app/big_data"), any(Context.class))).thenReturn("/app/big_data");
		when(renderer.render(eq("nom"), any(Context.class))).thenReturn("nom");
		when(renderer.render(eq("<%=list('foo','bar')%>"), any(Context.class)))
				.thenReturn(of("foo", "bar"));
		when(renderer.render(eq("do_<%=nom%>"), any(Context.class)))
				.thenReturn("do_foo", "do_bar");
		when(renderer.render(eq("<%=root%>/<%=nom%>"), any(Context.class)))
				.thenReturn("/t/foo", "/t/bar");
		when(env.getRenderer()).thenReturn(renderer);

		// set executor service
		when(env.getExecutorService()).thenReturn(executorService);

		// call camino
		Camino camino = new Camino(env, config);
		List<PathMetrics> pathMetrics = camino.getPathMetrics();

		// verify interactions
		verify(env).newContext();
		verify(env).getRenderer();
		verify(context).setProperty("root", "/app/data");
		verify(context, times(2)).createChild();

		// check for paths
		assertEquals(pathMetrics.size(), 3);
		assertEquals(pathMetrics.get(0).getPathStatus().getName(), "big_data");
		assertEquals(pathMetrics.get(0).getPathStatus().getValue(), "/app/big_data");
		assertEquals(pathMetrics.get(1).getPathStatus().getName(), "do_foo");
		assertEquals(pathMetrics.get(1).getPathStatus().getValue(), "/t/foo");
		assertEquals(pathMetrics.get(2).getPathStatus().getName(), "do_bar");
		assertEquals(pathMetrics.get(2).getPathStatus().getValue(), "/t/bar");

		// check for metric data
		assertEquals(pathMetrics.get(0).getMetricData().get(0).getMetricId().getFullName(), "big_data.age");
		assertEquals(pathMetrics.get(0).getMetricData().get(0).getValue(), ageValue);
		assertEquals(pathMetrics.get(0).getMetricData().get(1).getMetricId().getFullName(), "big_data.size");
		assertEquals(pathMetrics.get(0).getMetricData().get(1).getValue(), sizeValue);
		assertEquals(pathMetrics.get(0).getMetricData().get(2).getMetricId().getFullName(), "big_data.count");
		assertEquals(pathMetrics.get(0).getMetricData().get(2).getValue(), countValue);
		assertEquals(pathMetrics.get(1).getMetricData().get(0).getMetricId().getFullName(), "do_foo.age");
		assertEquals(pathMetrics.get(1).getMetricData().get(0).getValue(), ageValue);
		assertEquals(pathMetrics.get(1).getMetricData().get(1).getMetricId().getFullName(), "do_foo.size");
		assertEquals(pathMetrics.get(1).getMetricData().get(1).getValue(), sizeValue);
		assertEquals(pathMetrics.get(1).getMetricData().get(2).getMetricId().getFullName(), "do_foo.count");
		assertEquals(pathMetrics.get(1).getMetricData().get(2).getValue(), countValue);
		assertEquals(pathMetrics.get(2).getMetricData().get(0).getMetricId().getFullName(), "do_bar.age");
		assertEquals(pathMetrics.get(2).getMetricData().get(0).getValue(), ageValue);
		assertEquals(pathMetrics.get(2).getMetricData().get(1).getMetricId().getFullName(), "do_bar.size");
		assertEquals(pathMetrics.get(2).getMetricData().get(1).getValue(), sizeValue);
		assertEquals(pathMetrics.get(2).getMetricData().get(2).getMetricId().getFullName(), "do_bar.count");
		assertEquals(pathMetrics.get(2).getMetricData().get(2).getValue(), countValue);
	}

	/**
	 * Test calling gethPathMetrics without specified executor service
	 *
	 * @throws InvalidNameException
	 * @throws WrongTypeException
	 * @throws RenderException
	 * @throws IOException
	 */
	@Test
	public void testGetPathMetricsInternalExecutor() throws InvalidNameException, WrongTypeException,
			RenderException, IOException {

		// test data
		long now = System.currentTimeMillis();
		double ageValue = 12340000;
		double sizeValue = 10000;
		double countValue = 5;
		Config config = ConfigBuilder.create().addPaths(of(new Path("path", "value"))).buildLocal();

		// mock environment
		Env env = mock(Env.class);
		FileSystem fileSystem = mockFileSystem(env);
		org.apache.hadoop.fs.Path path1 = new org.apache.hadoop.fs.Path("/app/data");
		FileStatus[] fss = new FileStatus[] {
				new FileStatus(15000, false, 3, 64*1024*1024, now - 10000, path1)};
		when(fileSystem.globStatus(any(org.apache.hadoop.fs.Path.class)))
				.thenReturn(fss);
		Context context = mockGlobalContext(env);
		List<Context> childContexts = mockChildContexts(1, context, context, env);
		mockChildContexts(3, childContexts.get(0), context, env);

		// mock renderer
		Renderer renderer = mock(Renderer.class);
		when(renderer.render(eq("path"), any(Context.class))).thenReturn("path");
		when(renderer.render(eq("value"), any(Context.class))).thenReturn("value");
		mockMetricFunction(context, "age", ageValue);
		mockMetricFunction(context, "size", sizeValue);
		mockMetricFunction(context, "count", countValue);
		when(env.getRenderer()).thenReturn(renderer);

		Camino camino = new Camino(env, config);
		List<PathMetrics> pathMetrics = camino.getPathMetrics();
		assertEquals(pathMetrics.size(), 1);
		assertEquals(pathMetrics.get(0).getMetricData().size(), 3);
		assertEquals(pathMetrics.get(0).getMetricData().get(0).getMetricId().getName(), "age");
		assertEquals(pathMetrics.get(0).getMetricData().get(0).getValue(), ageValue);
		assertEquals(pathMetrics.get(0).getMetricData().get(1).getMetricId().getName(), "size");
		assertEquals(pathMetrics.get(0).getMetricData().get(1).getValue(), sizeValue);
		assertEquals(pathMetrics.get(0).getMetricData().get(2).getMetricId().getName(), "count");
		assertEquals(pathMetrics.get(0).getMetricData().get(2).getValue(), countValue);
	}

	/**
	 * Test check identifier
	 *
	 * @throws InvalidNameException
	 */
	@Test
	public void testCheckIdentifier() throws InvalidNameException {
		camino.checkIdentifier("a");
		camino.checkIdentifier("a_b");
		camino.checkIdentifier("aBcDe");
		camino.checkIdentifier("x1c");
		camino.checkIdentifier("_");
		camino.checkIdentifier("$");
		camino.checkIdentifier("a$b");
	}

	/**
	 * Test containsWildcard
	 */
	@Test
	public void testContainsWildcard() {
		assertFalse(camino.containsWildcard(""));
		assertFalse(camino.containsWildcard("abc/123"));
		assertTrue(camino.containsWildcard("abc/*/def"));
		assertTrue(camino.containsWildcard("x/3?4"));
		assertFalse(camino.containsWildcard("pq\\*rs"));
		assertTrue(camino.containsWildcard("/foo/bar[abc].dat"));
		assertTrue(camino.containsWildcard("/n/t[0-9]gh"));
		assertFalse(camino.containsWildcard("vv\\[3\\]ss"));
		assertTrue(camino.containsWildcard("/foo/bar{ee,ff,gg}.dat"));
		assertFalse(camino.containsWildcard("ijk\\{oo\\}lmn"));
	}

	/**
	 * Test check identifier with invalid character
	 *
	 * @throws InvalidNameException
	 */
	@Test(expectedExceptions = InvalidNameException.class)
	public void testCheckIdentifierNonAlphaNumeric() throws InvalidNameException {
		camino.checkIdentifier("a*b");
	}

	/**
	 * Test check identifier with zero length
	 *
	 * @throws InvalidNameException
	 */
	@Test(expectedExceptions = InvalidNameException.class)
	public void testCheckIdentifierZeroLength() throws InvalidNameException {
		camino.checkIdentifier("");
	}

	/**
	 * Test check identifier starting with digit
	 *
	 * @throws InvalidNameException
	 */
	@Test(expectedExceptions = InvalidNameException.class)
	public void testCheckIdentifierInitialDigit() throws InvalidNameException {
		camino.checkIdentifier("0a");
	}

	/**
	 * Creates new repeat
	 *
	 * @param var variable to hold element of list
	 * @param list list to iterate over
	 * @param paths paths under repeat
	 * @return new repeat
	 */
	protected static Repeat newRepeat(String var, String list, Path...paths) {
		return new Repeat(var, list, copyOf(paths), null);
	}

	/**
	 * Mocks a file system in environment
	 *
	 * @param env environment to put file system into
	 * @return mocked file system
	 * @throws IOException
	 */
	protected static FileSystem mockFileSystem(Env env) throws IOException {
		FileSystem fileSystem = mock(FileSystem.class);
		when(env.getFileSystem()).thenReturn(fileSystem);
		return fileSystem;
	}

	/**
	 * Mocks a global context
	 *
	 * @param env environment to put global context into
	 * @return mocked global context
	 */
	protected static Context mockGlobalContext(Env env) {
		Context context = mock(Context.class);
		when(context.getEnv()).thenReturn(env);
		when(context.getGlobal()).thenReturn(context);
		when(env.newContext()).thenReturn(context);
		return context;
	}

	/**
	 * Mock set property of a context
	 *
	 * @param context context
	 * @param name name of property
	 * @param type type of value
	 * @param value value to set
	 * @throws WrongTypeException
	 */
	protected static <T> void mockSetProperty(Context context, String name, Class<T> type,
			T value) throws WrongTypeException {
		when(context.getProperty(name)).thenReturn(value);
		when(context.getProperty(name, type)).thenReturn(value);
	}

	/**
	 * Mocks a child context
	 *
	 * @param parent parent context
	 * @param global global context
	 * @param env environment to put child context into
	 * @return child context
	 */
	protected static Context mockChildContext(Context parent, Context global, Env env) {
		return mockChildContexts(1, parent, global, env).get(0);
	}

	/**
	 * Mocks one or more child contexts
	 *
	 * @param count number of child contexts to mock
	 * @param parent parent context
	 * @param global global context
	 * @param env environment to put child contexts into
	 * @return list of child contexts
	 */
	protected static List<Context> mockChildContexts(int count, Context parent, Context global,
			Env env) {
		List<Context> childContexts = Lists.newArrayListWithExpectedSize(count);
		for (int i = 0; i < count; i++) {
			Context childContext = mock(Context.class);
			when(childContext.getEnv()).thenReturn(env);
			when(childContext.getGlobal()).thenReturn(global);
			childContexts.add(childContext);
		}
		if (count > 1) {
			when(parent.createChild()).thenReturn(childContexts.get(0),
					childContexts.subList(1, count).toArray(new Context[count]));
		} else if (count == 1) {
			when(parent.createChild()).thenReturn(childContexts.get(0));
		}
		return childContexts;
	}

	/**
	 * Mock a metric function
	 *
	 * @param context context
	 * @param name name of function
	 * @param metricValue static value returned by function
	 */
	protected static Function mockMetricFunction(Context context, String name, double metricValue)
			throws FunctionCallException, WrongTypeException {
		Function aggFunction = mock(Function.class);
		when(aggFunction.invoke(any(List.class), any(Context.class))).thenReturn(metricValue);
		mockSetProperty(context, name, Function.class, aggFunction);
		return aggFunction;
	}

	interface PathMetricsFuture extends Future<PathMetrics> {}

	interface PathMetricsFutureList extends List<Future<PathMetrics>> {}

}
