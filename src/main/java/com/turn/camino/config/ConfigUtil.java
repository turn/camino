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
package com.turn.camino.config;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;

/**
 * Utility methods for reading config
 */
public class ConfigUtil {

	/**
	 * Interface for transforming a key/value pair into an object
	 */
	interface Transformer<K,V,T> {
		T transform(K key, V value);
	}

	/**
	 * Transforms map of key/values to list of objects
	 *
	 * @param map map of key/value pairs
	 * @param transformer transformation logic
	 * @return list of objects
	 */
	public static <K,V,T> List<T> mapToList(Map<K,V> map, Transformer<K,V,T> transformer) {
		List<T> list = Lists.newArrayListWithCapacity(map.size());
		for (Map.Entry<K,V> entry : map.entrySet()) {
			list.add(transformer.transform(entry.getKey(), entry.getValue()));
		}
		return list;
	}

}
