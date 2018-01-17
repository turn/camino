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

import com.turn.camino.render.Renderer;
import org.apache.hadoop.fs.FileSystem;

import java.util.TimeZone;
import java.util.concurrent.ExecutorService;

/**
 * System environment
 *
 * @author llo
 */
public interface Env {

	/**
	 * Get current time
	 *
	 * @return current time in epoch milliseconds
	 */
	long getCurrentTime();

	/**
	 * Get default time zone
	 *
	 * @return default time zone
	 */
	TimeZone getTimeZone();

	/**
	 * Get file system
	 *
	 * @return Hadoop file system
	 */
	FileSystem getFileSystem();

	/**
	 * Creates new context
	 *
	 * @return new global context
	 */
	Context newContext();

	/**
	 * Get renderer
	 *
	 * @return new renderer
	 */
	Renderer getRenderer();

	/**
	 * Get executor service
	 *
	 * @return executor service
	 */
	ExecutorService getExecutorService();

	/**
	 * Get error handler
	 *
	 * @return error handler
	 */
	ErrorHandler getErrorHandler();
	
}
