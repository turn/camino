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

/**
 * Error handler interface
 *
 * @author llo
 */
public interface ErrorHandler {

	/**
	 * Called on an error while waiting for path metrics to finish
	 *
	 * @param error
	 */
	void onWaitError(Throwable error);

	/**
	 * Called on an error while computing a repeat
	 *
	 * @param repeat repeat config
	 * @param error error
	 */
	void onRepeatError(Repeat repeat, Throwable error);

	/**
	 * Called on error while computing a path
	 *
	 * @param path path config
	 * @param error error
	 */
	void onPathError(Path path, Throwable error);

}
