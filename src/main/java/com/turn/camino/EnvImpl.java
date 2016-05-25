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

import com.turn.camino.render.Renderer;
import com.turn.camino.render.RendererImpl;
import org.apache.hadoop.fs.FileSystem;

import java.util.TimeZone;
import java.util.concurrent.ExecutorService;

/**
 * Concrete implementation of Env
 *
 * @author llo
 */
class EnvImpl implements Env {

	private TimeZone timeZone;
	private FileSystem fileSystem;
	private ExecutorService executorService;
	private ErrorHandler errorHandler;

	/**
	 * Constructor
	 *
	 * @param timeZone time zone
	 * @param fileSystem file system
	 */
	EnvImpl(TimeZone timeZone, FileSystem fileSystem, ExecutorService executorService,
			ErrorHandler errorHandler) {
		this.timeZone = timeZone;
		this.fileSystem = fileSystem;
		this.executorService = executorService;
		this.errorHandler = errorHandler;
	}

	@Override
	public long getCurrentTime() {
		return System.currentTimeMillis();
	}

	@Override
	public TimeZone getTimeZone() {
		return timeZone;
	}

	@Override
	public FileSystem getFileSystem() {
		return fileSystem;
	}

	@Override
	public Context newContext() {
		return new ContextImpl(this, null);
	}

	@Override
	public Renderer getRenderer() {
		return new RendererImpl();
	}

	@Override
	public ExecutorService getExecutorService() {
		return executorService;
	}

	@Override
	public ErrorHandler getErrorHandler() {
		return errorHandler;
	}

}
