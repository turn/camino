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

import com.turn.camino.Context;
import com.turn.camino.render.Function;
import com.turn.camino.render.FunctionCallException;
import com.turn.camino.render.FunctionCallExceptionFactory;
import com.turn.camino.util.Message;
import com.turn.camino.util.Validation;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.apache.hadoop.fs.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.turn.camino.util.Message.prefix;

/**
 * File system functions
 *
 * @author llo
 */
public class FileSystemFunctions {

	private final static Validation<FunctionCallException> VALIDATION =
			new Validation<>(new FunctionCallExceptionFactory());

	/**
	 * Abstract directory listing function
	 */
	protected static abstract class AbstractDirList implements Function {

		protected org.apache.hadoop.fs.Path[] dirList(FileSystem fileSystem,
				org.apache.hadoop.fs.Path dirPath, Optional<PathFilter> pathFilter)
				throws FunctionCallException, IOException {

			// check that path exists
			if (!fileSystem.exists(dirPath)) {
				throw new FunctionCallException(String.format("Path %s does not exist",
						dirPath));
			}

			// check that path is directory
			if (!fileSystem.isDirectory(dirPath)) {
				throw new FunctionCallException(String.format("Path %s is not directory",
						dirPath));
			}

			// perform directory listing
			FileStatus fss[];
			if (pathFilter.isPresent()) {
				fss = fileSystem.listStatus(dirPath, pathFilter.get());
			} else {
				fss = fileSystem.listStatus(dirPath);
			}
			return FileUtil.stat2Paths(fss);
		}
	}

	/**
	 * Directory listing function
	 */
	public static class DirList extends AbstractDirList {
		@Override
		public Object invoke(List<?> params, Context context) throws FunctionCallException {
			VALIDATION.requireListSize(params, 1, 2, prefix("parameters"));
			String dir = VALIDATION.requireType(params.get(0), String.class, prefix("arg0"));
			Optional<PathFilter> pathFilter = Optional.empty();
			if (params.size() > 1) {
				Function predicate = VALIDATION.requireType(params.get(1), Function.class,
						prefix("filter"));
				pathFilter = Optional.of(path -> {
					try {
						Object result = predicate.invoke(ImmutableList.of(path.toUri().getPath()),
								context);
						return VALIDATION.requireType(result, Boolean.class,
								Message.full("File filter predicate must return boolean value"));
					} catch (FunctionCallException e) {
						throw new RuntimeException(e);
					}
				});
			}
			org.apache.hadoop.fs.Path dirPath = new org.apache.hadoop.fs.Path(dir);
			FileSystem dfs = context.getEnv().getFileSystem();
			try {
				org.apache.hadoop.fs.Path[] paths = dirList(dfs, dirPath, pathFilter);
				ArrayList<String> list = Lists.newArrayListWithExpectedSize(paths.length);
				for (org.apache.hadoop.fs.Path path : paths) {
					list.add(path.toUri().getPath());
				}
				return list;
			} catch (IOException e) {
				throw new FunctionCallException("Cannot make directory listing for " + dir);
			}
		}
	}

	/**
	 * Directory listing function only returning name of files
	 */
	public static class DirListName extends AbstractDirList {
		@Override
		@SuppressWarnings("unchecked")
		public Object invoke(List<?> params, Context context) throws FunctionCallException {
			VALIDATION.requireListSize(params, 1, 2, prefix("parameters"));
			String dir = VALIDATION.requireType(params.get(0), String.class, prefix("arg0"));
			Optional<PathFilter> pathFilter = Optional.empty();
			if (params.size() > 1) {
				Function predicate = VALIDATION.requireType(params.get(1), Function.class,
						prefix("filter"));
				pathFilter = Optional.of(path -> {
					try {
						Object result = predicate.invoke(ImmutableList.of(path.getName()),
								context);
						return VALIDATION.requireType(result, Boolean.class,
								Message.full("File filter predicate must return boolean value"));
					} catch (FunctionCallException e) {
						throw new RuntimeException(e);
					}
				});
			}
			org.apache.hadoop.fs.Path dirPath = new org.apache.hadoop.fs.Path(dir);
			FileSystem dfs = context.getEnv().getFileSystem();
			try {
				org.apache.hadoop.fs.Path[] paths = dirList(dfs, dirPath, pathFilter);
				ArrayList<String> list = Lists.newArrayListWithExpectedSize(paths.length);
				for (org.apache.hadoop.fs.Path path : paths) {
					list.add(path.getName());
				}
				return list;
			} catch (IOException e) {
				throw new FunctionCallException("Cannot make directory listing for " + dir);
			}
		}
	}

	/**
	 * Function to test if path exists
	 */
	public static class Exists implements Function {

		@Override
		public Object invoke(List<?> params, Context context) throws FunctionCallException {
			VALIDATION.requireListSize(params, 1, 1, prefix("parameters"));
			String path = VALIDATION.requireType(params.get(0), String.class, prefix("path"));
			org.apache.hadoop.fs.Path fsPath = new org.apache.hadoop.fs.Path(path);
			FileSystem fs = context.getEnv().getFileSystem();
			try {
				return fs.exists(fsPath);
			} catch (IOException e) {
				throw new FunctionCallException("Unexpected exception testing if path exists");
			}
		}
	}

	/**
	 * Function to test if path is directory
	 */
	public static class IsDir implements Function {

		@Override
		public Object invoke(List<?> params, Context context) throws FunctionCallException {
			VALIDATION.requireListSize(params, 1, 1, prefix("parameters"));
			String path = VALIDATION.requireType(params.get(0), String.class, prefix("path"));
			org.apache.hadoop.fs.Path fsPath = new org.apache.hadoop.fs.Path(path);
			FileSystem fs = context.getEnv().getFileSystem();
			try {
				return fs.exists(fsPath) && fs.isDirectory(fsPath);
			} catch (IOException e) {
				throw new FunctionCallException("Unexpected exception testing if path is directory");
			}
		}
	}

}
