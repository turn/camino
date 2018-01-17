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

import com.turn.camino.config.Path;
import com.turn.camino.config.Repeat;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Error handler that writes to Java logging framework
 *
 * @author llo
 */
public class LoggingErrorHandler implements ErrorHandler {

	private final Logger logger;

	public LoggingErrorHandler(Logger logger) {
		this.logger = logger;
	}

	@Override
	public void onWaitError(Throwable error) {
		logger.log(Level.WARNING, "Error while waiting", error);
	}

	@Override
	public void onRepeatError(Repeat repeat, Throwable error) {
		logger.log(Level.WARNING, String.format("Error computing repeat %s in %s",
				repeat.getVar(), repeat.getList()), error);
	}

	@Override
	public void onPathError(Path path, Throwable error) {
		logger.log(Level.WARNING, String.format("Error computing path %s",
				path.getName()), error);
	}
}
