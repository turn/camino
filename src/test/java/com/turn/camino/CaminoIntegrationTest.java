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
import com.google.common.io.Files;
import com.turn.camino.config.*;
import com.turn.camino.render.RenderException;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.common.collect.ImmutableList;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Integration test for Camino
 *
 * Uses its own source code for test
 *
 * @author llo
 */
@Test
public class CaminoIntegrationTest {

	private final static double EPSILON = 1e-6;

	private final static String CAMINO_CONFIG_TEMPLATE = "{\n" +
			"  \"properties\": {" +
			"    \"pkv\": \"readme.1\"\n" +
			"  },\n" +
			"  \"paths\": [\n" +
			"    {\n" +
			"      \"name\": \"readme\",\n" +
			"      \"tags\": {\"pk\": \"<%%=pkv%%>\"},\n" +
			"      \"value\": \"%s\"\n" +
			"    }\n" +
			"  ]\n" +
			"}";

	private File root;
	private File readme;

	@BeforeClass
	public void setUp() {
		URL url = CaminoIntegrationTest.class.getProtectionDomain().getCodeSource().getLocation();
		this.root = new File(url.getPath()).getParentFile().getParentFile();
		this.readme = new File(root, "README.md");
	}

	/**
	 * Test camino
	 *
	 * @throws IOException
	 * @throws WrongTypeException
	 * @throws RenderException
	 */
	@Test
	public void testCamino() throws IOException, WrongTypeException, RenderException,
			InvalidNameException {

		// build environment
		ExecutorService executorService = Executors.newSingleThreadExecutor();
		TimeZone timeZone = TimeZone.getTimeZone("US/Eastern");
		Configuration conf = new Configuration(false);
		conf.set("fs.default.name", "file:///");
		conf.set("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getCanonicalName());
		FileSystem fileSystem = FileSystem.get(conf);
		Env env = new EnvBuilder().withTimeZone(timeZone).withFileSystem(fileSystem)
				.withExecutorService(executorService).build();

		// build configuration
		Config config = new ConfigBuilder().addProperties(ImmutableList.of(
				new Property("pkv", "readme.1")))
				.addPaths(ImmutableList.of(
						new Path("readme", readme.getAbsolutePath(), ImmutableList.<Metric>of(),
								ImmutableMap.of("pk", "<%=pkv%>"), null))).build();

		// create camino
		long t0 = System.currentTimeMillis();
		Camino camino = new Camino(env, config);
		List<PathMetrics> pathMetricsList = camino.getPathMetrics();
		assertEquals(pathMetricsList.size(), 1);
		assertEquals(pathMetricsList.get(0).getPathStatus().getName(), "readme");
		long t1 = System.currentTimeMillis();

		// check
		for (MetricDatum metricDatum : pathMetricsList.get(0).getMetricData()) {
			if (metricDatum.getMetricId().getFullName().equals("readme.size")) {
				assertEquals(metricDatum.getValue(), readme.length(), EPSILON);
			} else if (metricDatum.getMetricId().getFullName().equals("readme.count")) {
				assertEquals(metricDatum.getValue(), 1, EPSILON);
			} else if (metricDatum.getMetricId().getFullName().equals("readme.age")) {
				assertTrue(metricDatum.getValue() >= t0 - readme.lastModified());
				assertTrue(metricDatum.getValue() <= t1 - readme.lastModified());
			} else {
				assertTrue(false, String.format("Unknown metric %s", metricDatum.getMetricId().getFullName()));
			}
			assertEquals(metricDatum.getMetricId().getTags().size(), 1);
			assertEquals(metricDatum.getMetricId().getTags().get(0).getKey(), "pk");
			assertEquals(metricDatum.getMetricId().getTags().get(0).getValue(), "readme.1");
		}

		// shut down executor service
		executorService.shutdown();
	}

	/**
	 * Test camino app
	 *
	 * @throws IOException
	 */
	@Test
	public void testCaminoApp() throws IOException {

		// create a temporary directory that we can use for integration test
		File tempRoot = Files.createTempDir();

		// create camino config path
		File caminoConfigPath = new File(tempRoot, "camino-test.json");
		PrintWriter caminoConfig = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(caminoConfigPath), "UTF-8"));
		caminoConfig.println(String.format(CAMINO_CONFIG_TEMPLATE, readme.getAbsolutePath()));
		caminoConfig.close();

		// output path
		File outputPath = new File(tempRoot, "camino-test.out");

		// run camino app
		long t0 = System.currentTimeMillis();
		CaminoApp.main(new String[] { "-f", "file:///", "-o", outputPath.getAbsolutePath(),
				caminoConfigPath.getAbsolutePath() });
		long t1 = System.currentTimeMillis();

		// verify output
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(outputPath), "UTF-8"));
		String line;
		while ((line = reader.readLine()) != null) {
			if (line.startsWith("\t")) {
				String[] parts = line.substring(1).split(": ", 2);
				String name = parts[0];
				Double value = Double.parseDouble(parts[1]);
				if (name.equals("readme.existence")) {
					assertEquals(value, 1, EPSILON);
				} else if (name.equals("readme.count")) {
					assertEquals(value, 1, EPSILON);
				} else if (name.equals("readme.size")) {
					assertEquals(value, readme.length(), EPSILON);
				} else if (parts[0].equals("readme.age")) {
					assertTrue(value >= t0 - readme.lastModified());
					assertTrue(value <= t1 - readme.lastModified());
				}
			} else {
				assertEquals(line, String.format("%s (%s)", "readme", readme.getAbsolutePath()));
			}
		}
		reader.close();

		// delete temporary directory
		tempRoot.deleteOnExit();
	}
}
