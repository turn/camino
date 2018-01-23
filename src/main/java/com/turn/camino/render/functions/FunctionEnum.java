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
package com.turn.camino.render.functions;

import com.google.common.collect.Maps;
import com.turn.camino.render.Function;

import java.util.Map;

/**
 * Built-in functions
 *
 * @author llo
 */
public enum FunctionEnum {

	// common functions
	COMPARE("compare", new CommonFunctions.Compare()),

	// math functions
	ADD("add", new MathFunctions.Add()),
	SUB("sub", new MathFunctions.Subtract()),
	MUL("mul", new MathFunctions.Multiply()),
	DIV("div", new MathFunctions.Divide()),

	// logic functions
	NOT("not", new LogicFunctions.Not()),
	EQ("eq", new LogicFunctions.Eq()),
	NE("ne", new LogicFunctions.Ne()),
	LT("lt", new LogicFunctions.Lt()),
	GT("gt", new LogicFunctions.Gt()),
	LTEQ("ltEq", new LogicFunctions.LtEq()),
	GTEQ("gtEq", new LogicFunctions.GtEq()),

	// string functions
	MATCH("match", new StringFunctions.Match()),
	MATCHER("matcher", new StringFunctions.Matcher()),
	REPLACE("replace", new StringFunctions.Replace()),
	REPLACE_REGEXP("replaceRegex", new StringFunctions.ReplaceRegex()),
	SPLIT("split", new StringFunctions.Split()),
	JOIN("join", new StringFunctions.Join()),
	CONCAT("concat", new StringFunctions.Concat()),

	// time functions
	NOW("now", new TimeFunctions.Now()),
	TODAY("today", new TimeFunctions.Today()),
	YESTERDAY("yesterday", new TimeFunctions.Yesterday()),
	TIME_ADD("timeAdd", new TimeFunctions.TimeAdd()),
	TIME_FORMAT("timeFormat", new TimeFunctions.TimeFormat()),
	TIME_PARSE("timeParse", new TimeFunctions.TimeParse()),
	TIME_TO_UNIX_DAY("timeToUnixDay", new TimeFunctions.TimeToUnixDay()),
	UNIX_DAY_TO_TIME("unixDayToTime", new TimeFunctions.UnixDayToTime()),

	// collection functions
	LIST("list", new CollectionFunctions.ListCreate()),
	LIST_GET("listGet", new CollectionFunctions.ListGet()),
	LIST_FIRST("listFirst", new CollectionFunctions.ListFirst()),
	LIST_LAST("listLast", new CollectionFunctions.ListLast()),
	DICT("dict", new CollectionFunctions.DictCreate()),
	DICT_GET("dictGet", new CollectionFunctions.DictGet()),
	SORT("sort", new CollectionFunctions.Sort()),

	// file system functions
	DIR_LIST("dirList", new FileSystemFunctions.DirList()),
	DIR_LIST_NAME("dirListName", new FileSystemFunctions.DirListName()),

	// metric functions
	METRIC_AGG("metricAgg", new MetricFunctions.MetricAggregateFunction()),
	AGE("age", new MetricFunctions.Age()),
	COUNT("count", new MetricFunctions.Count()),
	SIZE("size", new MetricFunctions.Size()),
	CREATION_DELAY("creationDelay", new MetricFunctions.CreationDelay());

	private final String name;
	private final Function function;

	/**
	 * Constructor
	 *
	 * @param name name of function
	 * @param function function object
	 */
	FunctionEnum(String name, Function function) {
		this.name = name;
		this.function = function;
	}

	public String getName() {
		return name;
	}

	public Function getFunction() {
		return function;
	}

	/**
	 * Returns all functions as a map
	 *
	 * @return functions as a map
	 */
	public static Map<String, Function> toMap() {
		Map<String, Function> map = Maps.newHashMap();
		for (FunctionEnum nativeFunction : values()) {
			map.put(nativeFunction.getName(), nativeFunction.getFunction());
		}
		return map;
	}
}
