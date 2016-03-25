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

/**
 * Path detail
 *
 * Attributes associated with a file system path
 *
 * @author llo
 */
public class PathDetail {

	private final String pathValue;
	private final boolean directory;
	private final long length;
	private final long lastModifiedTime;

	/**
	 * Constructor
	 *
	 * @param pathValue string value of path
	 * @param directory whether path is a directory
	 * @param length length of path
	 * @param lastModifiedTime last modified time of path
	 */
	public PathDetail(String pathValue, boolean directory, long length,
			long lastModifiedTime) {
		this.pathValue = pathValue;
		this.directory = directory;
		this.length = length;
		this.lastModifiedTime = lastModifiedTime;
	}

	/**
	 * Gets last modified time of path detail
	 *
	 * @return last modified time in epoch milliseconds
	 */
	public long getLastModifiedTime() {
		return lastModifiedTime;
	}

	/**
	 * Gets path value
	 *
	 * @return path value
	 */
	public String getPathValue() {
		return pathValue;
	}

	/**
	 * Determines if path is a directory
	 *
	 * @return true if path is a directory, false otherwise
	 */
	public boolean isDirectory() {
		return directory;
	}

	/**
	 * Gets length of path
	 *
	 * @return length of path
	 */
	public long getLength() {
		return length;
	}

}
