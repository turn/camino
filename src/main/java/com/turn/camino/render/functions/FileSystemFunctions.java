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
package com.turn.camino.render.functions;

import com.turn.camino.Context;
import com.turn.camino.render.Function;
import com.turn.camino.render.FunctionCallException;
import com.turn.camino.render.FunctionCallExceptionFactory;
import com.turn.camino.util.Validation;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.turn.camino.util.Message.prefix;

/**
 * File system functions
 *
 * @author llo
 */
public class FileSystemFunctions implements FunctionFamily {

	private final static Validation<FunctionCallException> VALIDATION =
			new Validation<FunctionCallException>(new FunctionCallExceptionFactory());

	@Override
	public Map<String, Function> getFunctions() {
		return ImmutableMap.<String, Function>builder()
				.put("dirList", new DirList())
				.put("dirListName", new DirListName())
				.build();
	}

	protected static abstract class AbstractDirList implements Function {

		protected org.apache.hadoop.fs.Path[] dirList(FileSystem fileSystem,
				org.apache.hadoop.fs.Path dirPath) throws FunctionCallException, IOException {

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
			FileStatus fss[] = fileSystem.listStatus(dirPath);
			return FileUtil.stat2Paths(fss);
		}
	}


	public static class DirList extends AbstractDirList {
		@Override
		public Object invoke(List<?> params, Context context) throws FunctionCallException {
			VALIDATION.requireListSize(params, 1, 1, prefix("parameters"));
			String dir = VALIDATION.requireType(params.get(0), String.class, prefix("arg0"));
			org.apache.hadoop.fs.Path dirPath = new org.apache.hadoop.fs.Path(dir);
			FileSystem dfs = context.getEnv().getFileSystem();
			try {
				org.apache.hadoop.fs.Path[] paths = dirList(dfs, dirPath);
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

	public static class DirListName extends AbstractDirList {
		@Override
		@SuppressWarnings("unchecked")
		public Object invoke(List<?> params, Context context) throws FunctionCallException {
			VALIDATION.requireListSize(params, 1, 1, prefix("parameters"));
			String dir = VALIDATION.requireType(params.get(0), String.class, prefix("arg0"));
			org.apache.hadoop.fs.Path dirPath = new org.apache.hadoop.fs.Path(dir);
			FileSystem dfs = context.getEnv().getFileSystem();
			try {
				org.apache.hadoop.fs.Path[] paths = dirList(dfs, dirPath);
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

}
