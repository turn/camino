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
package com.turn.camino.lang.ast;

/**
 * Represents location of token
 */
public class Location {

	private final int line;
	private final int column;

	/**
	 * Constructor
	 *
	 * @param line line of token
	 * @param column column of token
	 */
	public Location(int line, int column) {
		this.line = line;
		this.column = column;
	}

	/**
	 * Gets line of location
	 *
	 * @return line
	 */
	public int getLine() {
		return line;
	}

	/**
	 * Gets column of location
	 *
	 * @return column
	 */
	public int getColumn() {
		return column;
	}

	/**
	 * Gets string representation of the location
	 *
	 * @return string of the format <i>LINE</i>:<i>COLUMN</i>
	 */
	@Override
	public String toString() {
		return String.format("%d:%s", line, column);
	}

	/**
	 * Test for equality with another object
	 *
	 * @param object another object
	 * @return true if this and other object are same, false otherwise
	 */
	@Override
	public boolean equals(Object object) {
		if (object instanceof Location) {
			return column == ((Location) object).getColumn() &&
					line == ((Location) object).getLine();
		}
		return false;
	}

}
