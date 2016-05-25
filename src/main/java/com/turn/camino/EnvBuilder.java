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

import com.turn.camino.util.Message;
import com.turn.camino.util.MessageExceptionFactory;
import com.turn.camino.util.Validation;
import org.apache.hadoop.fs.FileSystem;

import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

/**
 * Builder for Env
 *
 * @author llo
 */
public class EnvBuilder {

	private FileSystem fileSystem;
	private TimeZone timeZone = TimeZone.getDefault();
	private ExecutorService executorService;
	private ErrorHandler errorHandler = new LoggingErrorHandler(Logger.getLogger(
			Camino.class.getCanonicalName()));
	private Validation<NullPointerException> npeValidation =
			new Validation<>(
					new MessageExceptionFactory<NullPointerException>() {
							@Override
							public NullPointerException newException(String message) {
								return new NullPointerException(message);
							}
						});

	/**
	 * Set time zone
	 *
	 * @param timeZone time zone
	 * @return this
	 */
	public EnvBuilder withTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
		return this;
	}

	/**
	 * Set file system
	 *
	 * @param fileSystem file system
	 * @return this
	 */
	public EnvBuilder withFileSystem(FileSystem fileSystem) {
		this.fileSystem = fileSystem;
		return this;
	}

	/**
	 * Sets executor service
	 *
	 * @param executorService executor service
	 * @return this
	 */
	public EnvBuilder withExecutorService(ExecutorService executorService) {
		this.executorService = executorService;
		return this;
	}

	/**
	 * Sets error handler
	 *
	 * @param errorHandler error handler
	 * @return this
	 */
	public EnvBuilder withErrorHandler(ErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
		return this;
	}

	/**
	 * Builds environment
	 *
	 * @return Env object
	 * @throws RuntimeException
	 */
	public Env build() {
		npeValidation.requireNotNull(timeZone, Message.prefix("Time zone"));
		npeValidation.requireNotNull(fileSystem, Message.prefix("File system"));
		return new EnvImpl(timeZone, fileSystem, executorService, errorHandler);
	}

}
