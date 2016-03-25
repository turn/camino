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

import com.google.common.base.Joiner;
import com.turn.camino.config.Config;
import com.turn.camino.config.ConfigBuilder;
import com.turn.camino.config.Tag;
import com.turn.camino.render.RenderException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.common.collect.Lists;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;

/**
 * Camino App
 *
 * Provides main program to run Camino
 *
 * @author llo
 */
public class CaminoApp {

	private List<File> caminoConfigPaths = Lists.newLinkedList();
	private File outputPath = null;
	private String fsUri = "file:///";

	/**
	 * Constructor
	 *
	 * @param args
	 */
	public CaminoApp(String[] args) {
		for (int i = 0; i < args.length; i++) {
			if ("-f".equals(args[i])) {
				this.fsUri = args[++i];
			} else if ("-o".equals(args[i])) {
				this.outputPath = new File(args[++i]);
			} else {
				this.caminoConfigPaths.add(new File(args[i]));
			}
		}
	}

	/**
	 * Runs Camino
	 *
	 * @throws IOException
	 */
	public void run() throws IOException, InvalidNameException, WrongTypeException,
			RenderException {

		FileOutputStream fos = null;
		ExecutorService executorService = null;
		try {
			// open output writer
			OutputStream os;
			if (outputPath != null) {
				os = fos = new FileOutputStream(outputPath);
			} else {
				os = System.out;
			}
			PrintWriter output = new PrintWriter(new OutputStreamWriter(os, "UTF-8"));

			// create environment
			Config config = readConfig();
			FileSystem fileSystem = getFileSystem();
			executorService = Executors.newSingleThreadExecutor();
			Env env = new EnvBuilder().withFileSystem(fileSystem)
					.withExecutorService(executorService).build();

			// get metrics
			Camino camino = new Camino(env, config);
			List<PathMetrics> pathMetricsList = new ArrayList<>(camino.getPathMetrics());
			for (PathMetrics pathMetrics : pathMetricsList) {
				if (pathMetrics.getError() != null) {
					output.println(String.format("Path %s has error %s",
							pathMetrics.getPath().getName(), pathMetrics.getError().getMessage()));
					continue;
				}
				output.println(String.format("%s (%s)", pathMetrics.getPathStatus().getName(),
						pathMetrics.getPathStatus().getValue()));
				for (MetricDatum metricDatum : pathMetrics.getMetricData()) {
					List<String> tags = Lists.newArrayList();
					for (Tag tag : metricDatum.getMetricId().getTags()) {
						tags.add(String.format("%s=%s", tag.getKey(), tag.getValue()));
					}
					output.println(String.format("\t%s (%s): %.0f",
							metricDatum.getMetricId().getName(),
							Joiner.on(' ').join(tags), metricDatum.getValue()));
				}
			}

			// flush output
			output.flush();
		} finally {
			if (executorService != null) {
				executorService.shutdown();
			}
			if (fos != null) {
				fos.close();
			}
		}
	}

	/**
	 * Reads config from file
	 *
	 * @return config
	 * @throws IOException
	 */
	protected Config readConfig() throws IOException {
		ConfigBuilder configBuilder = new ConfigBuilder();
		for (File caminoConfigPath : caminoConfigPaths) {
			Reader reader = null;
			try {
				reader = new InputStreamReader(new FileInputStream(caminoConfigPath), "UTF-8");
				configBuilder.from(reader);
			} finally {
				if (reader != null) {
					reader.close();
				}
			}
		}
		return configBuilder.build();
	}

	/**
	 * Gets file system from Hadoop core-site.xml config
	 *
	 * @return file system
	 * @throws IOException
	 */
	protected FileSystem getFileSystem() throws IOException {
		Configuration configuration = new Configuration();
		if (fsUri != null) {
			configuration.set("fs.default.name", fsUri);
		}
		return FileSystem.get(configuration);
	}

	/**
	 * Main
	 *
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		try {
			CaminoApp caminoApp = new CaminoApp(args);
			caminoApp.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
