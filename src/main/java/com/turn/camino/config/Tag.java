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
package com.turn.camino.config;

import com.google.common.base.Preconditions;
import com.turn.camino.annotation.Member;

/**
 * Tag element in Camino configuration
 *
 * @author llo
 */
public class Tag {

	private final String key;
	private final String value;

    /**
     * Constructor
     *
     * @param key key of tag
     * @param value value of tag
     */
	public Tag(String key, String value) {
		Preconditions.checkNotNull(key, "Tag key cannot be null");
		Preconditions.checkNotNull(value, "Tag value cannot be null");
		this.key = key;
		this.value = value;
	}

	/**
	 * Gets key of tag
	 *
	 * @return key of tag
	 */
	@Member("key")
	public String getKey() {
		return key;
	}

	/**
	 * Gets value of tag
	 *
	 * @return value of tag
	 */
	@Member("value")
	public String getValue() {
		return value;
	}

}
