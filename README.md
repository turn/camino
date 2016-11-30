<!--
Copyright (C) 2014-2016, Turn Inc. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
-->

Camino - Hadoop File System Path Watcher
========================================

Built by [Turn](http://turn.com)

Camino is a library and a tool to compute metrics on Hadoop file system
paths. What paths and metrics to compute are specified programmatically
or in a configuration file.

Configuration Overview
----------------------

External configuration is specified in JSON format. The following JSON
document is the template of Camino configuration.

	{
	    "includes": [
	        "uri" //, ...
	    ],
		"properties": {
			/** Property */
			"property1 name": "property1 value",
			"property2 name": "property2 value" //, ...
		},
		"paths": [
			{
				/** Path */
				"name": "path name",
				"tags": {
					/** Tag */
					"tag1 key": "tag1 value",
					"tag2 key": "tag2 value"    //, ...
				},
				"value": "path value",
				"expectedCreationTime": "time value",   // optional
				"metrics": [    // optional, very rarely used
					{
						/** Metric */
						"name": "metric name",
						"function": "metric function",       // optional
						"aggregate": "metric aggregate",     // optional
						"aggFunction": "aggregate function"  // optional
					}
				]
			} //, ...
		],
		"repeats": [
			{
				/** Repeat */
				"var": "variable name",
				"list": "list expression",
				"paths": [
					/** array of Path */
				]
			} //, ...
		]
	}

Configuration is primarily specified as JSON strings, but Camino provides
a simple expression language with which to generate dynamic configuration.

Configuration Elements
----------------------

### Include

Camino 1.2 lets you include other configurations in your configuration. This
can reduce duplication of configuration as well as make a cleaner file.

Include array elements are either relative file paths or fully qualified URI's.
In the case of relative file paths, the location of the configuration files.
For example, if the configuration is located at */app/conf/camino.json* and it
has an include of *lib/util.json*, then Camino will include the configuration
located at */app/conf/lib/util.json*.

### Property

A property is a key and value pair that can be used by other values defined
later in the configuration via Camino's embedded expression language. Value
of the property can also contain embedded expressions in Camino's expression
language to provide dynamically rendered value.

By default, the value of a property is a string. To create a non-string value,
use a single expression in the value.

	/** Property */
	"ageLimit": "<%=99%>"

### Path

A path represents a location in the file system. Its value can contain
wildcards so that it can represent zero, one, or multiple physical paths.
Path value can also embed expression language as well.

The property *expectedCreationTime* specifies the expected time the path
should exist. It is optional but if it is present, it must resolve to a time
value. Typically this value is specified as an expression:

    "expectedCreationTime": "<%=timeAdd(today('GMT'),3,'h')%>"

The previous example specifies an expected creation time to be 3 hours after
the current day's midnight in the GMT time zone.

If *expectedCreationTime* is specified, the path will automatically include
the *creation delay* metric (see below).

### Tag

Path can optionally contain tags which can be used to represent arbitrary
attributes of a path. This is primarily geared toward publishing metrics
to monitoring systems like OpenTSDB.

Not that the key and the value of a tag are directly specified as an entry
in the "tag" JSON object.

	/** Tag */
	"myTagKey": "nameOfJob"

### Metric

A metric measures a numeric attribute of a path. As of Cmaino 1.2, metrics
are automatically included and computed for all paths, making the *metrics*
array under *path* to be unnecessary in most cases.

There are four metrics in Camino:

- *Count*: Counts number of physical paths resolved by the path value.
- *Size*: Computes size of physical paths resolved by path value.
- *Age*: Computes the difference in time between the current time and the
	last modified date of a path.
- *Creation Delay*: Computes difference in time between the current time
	and the expected creation time of a path. If path exists or current time
	has not expected creation time then this metric returns zero.

The first three metrics, *count*, *size* and *age* are automatically included
for all paths. The *creation delay* metric is included if the path property
*expectedCreationTime* is present.

Two of the metrics, *size* and *age*, are aggregates. A path whose value
contains wild cards can possibly matches multiple physical paths, so in such
case Camino will add more metrics with different aggregate functions such as
*sum*, *min*, *max*, and *avg*.

The summary of when metrics are included:

- For each path:
  - Add *count*
  - Add *age* (with aggregate *max*)
  - Add *size* (with aggregate *sum*)
  - If path has *expectedCreationTime*:
    - Add *creationDelay*
  - If path contains wildcard:
    - Add *minAge*
    - Add *maxAge*
    - Add *avgAge*
    - Add *minSize*
    - Add *maxSize*
    - Add *avgSize*
    - Add *sumSize*

#### Custom Metrics

Paths can have one or more custom metrics, which must be user-defined. If a
Camino path resolves to at least one file system path, then the custom metric
will apply.

Conceptually, a Camino path can resolve to multiple file system paths due to
the use of wild cards. This is why a metric must have a *function* to compute
the value of a file system path and also an *aggregate* to combine individual
file system path metric into one single metric. The function should be defined
as a lambda expression with two arguments (the first being a Metric instance
and the second a PathDetail object) in the properties section of the
configuration. See below on how to define a function.

The aggregate can be *sum*, *avg*, *max*, and *min*. These affect the behavior
of the default metric aggregation function.

Additionally, if the aggregation is not feasible with the built-in four
aggregate types, you can provide custom aggregation via an *aggFunction*, which
is also defined in the properties section.

### Repeat

A repeat is a way to templatize definition of paths by iterating over a list
and creating paths (and dependent metrics) for each element in the list.

The property name specified by _var_ will contain an element from the list.
Paths and metrics inside the iterator can use the property.

	/** Repeat */
	{
		"var": "username"
		"list": "<%=list('adam','beth','charley','david','edward')%>",
		"paths": [
			{
				"name": "userStorage_<%=username%>",
				"value": "/user/<%=username%>/*"
			}
		]
	}

Expression Language
-------------------

Configuration values can contain embedded expressions in Camino expression
language to add dynamically rendered content. The language is a simple
functional language without inlined operators. To switch from JSON string
mode into expression language mode and back to JSON, the operators <%= and
%> are used. For example:

	{
		"name": "myPath",
		"value": "/path/to/<%=timeFormat(date,'yyyy_MM_dd')%>/data.txt"
	}

In the previous example, the value is a string, and the expression
_<%=timeFormat(date,'yyyy\_MM\_dd')%>_ will be substituted with the outcome
of executing the _timeFormat_ function on the property _date_.

### Native Types

Camino has the following data types: long, double, string, timeValue, list,
dictionary, and function.

There are two primitive numeric types in Camino, namely Java long (64-bit
signed integer), and Java double (64-bit floating point number). The parser
will treat numbers with no decimals as longs, and everything else as doubles.
Scientific notation is also accepted.

Strings in Camino expression language is singly-quoted to avoid conflict with
JSON strings thus making it easy to embed inside JSON values.

Time is another native type due to desired use of Camino to compute metrics
based on time. Time contains both a UTC timestamp and a time zone. There are
three constructors to instantiate a time value.

    "properties": {
        "t1": "<%=now('US/Pacific')%>",
        "t2": "<%=today('GMT')%>",
        "t3": "<%=yesterday('Asia/Tokyo')%>"
    }

Collection types are limited to list and dictionary (Java map), and can be
initialized using the [] operator for list and {} operator for dictionary.

    "properties": {
        "names": "<%=['Amy','Bob','Chuck','Dave']%>",
        "idmap": "<%={'6DKA431':'Amy','3KER481':'Dave','2QJZ387':'Chuck'}%>"
    }

Member access to list and dictionary is thru' the [] operator, like so:

    <%=names[3]%>
    <%=idmap['2QJZ387']%>

Functions are first-class objects in Camino. They are defined as properties.
The keyword *fn* signify an expression as a lambda expression, followed by
parameters and the body of the function after the arrow token *->*.

    "properties": {
        "myFunc": "<%=fn(a) -> add(a,1)%>"
    }

### POJO access

Camino defines a small set of Java classes that Camino expressions can access and
operate on. Some fields of these Java classes can be accessed via the dot operator.

For example, the *now()* function returns an instance of the TimeValue class
which contains the time and the time zone. To retrieve the UTC milliseconds,
you can use this expression:

    "<%=now().timeMillis%>"

Currently there are a number Camino classes that have member access:

- *Path*:
  - *name*: name of path
  - *value*: value of path
  - *metrics*: list of custom metrics
  - *tags*: list of tags
  - *expectedCreationTime*: expression of expected creation time of path
- *Tag*:
  - *key*: returns key of tag
  - *value*: returns value of tag
- *Metric*:
  - *name*: name of metric
  - *function*: function to compute metrics on file system path
  - *aggregate*: aggregate type
  - *aggFunction*: aggregation function
  - *defaultValue*: default value of metric
- *TimeValue*:
  - *timeZone*: time zone ID
  - *timeMillis*: UTC time in milliseconds
- *PathStatus*:
  - *name*: name of path (resolved from Path in configuration)
  - *value*: value of path (resolved from Path in configuration)
  - *path*: resolved file system path pattern
  - *pathDetails*: list of PathDetail objects
  - *expectedCreationTime*: resolved expected creation time of path
- *PathDetail*:
  - *pathValue*: path value, actual file system path
  - *lastModifiedTime*: last modified time of this path
  - *directory*: whether the file system path is a directory or not
  - *length*: length of file system path

### Built-inFunctions

Camino EL provides a number of built-in functions.

#### Math functions

- *add(lhs, rhs)*: Adds two numbers and return result.
- *subtract(lhs, rhs)*: Subtracts two numbers and return result.
- *multiply(lhs, rhs)*: Multiplies two numbers and return result.
- *divide(lhs, rhs)*: Divides two numbers and return result.

#### Logic functions

- *eq(lhs, rhs)*: Tests if _lhs_ is equal to _rhs_.
- *ne(lhs, rhs)*: Tests if _lhs_ is not equal to _rhs_.
- *lt(lhs, rhs)*: Tests if _lhs_ is less than _rhs_.
- *gt(lhs, rhs)*: Tests if _lhs_ is greater than _rhs_.
- *ltEq(lhs, rhs)*: Tests if _lhs_ is less than or equal to _rhs_.
- *gtEq(lhs, rhs)*: Tests if _lhs_ is greater than or equal to _rhs_.
- *not(boolVal)*: Inverts value of boolVal.

#### Time functions

- *now([timeZone])*: Creates time value with current time. _timeZone_ is
	optional. If _timeZone_ is not specified, then default time zone is used.
- *today([timeZone])*: Creates time value for midnight of today, where today
	is defined by _timeZone_ (or default time zone if _timeZone_ is absent).
- *yesterday([timeZone])*: Creates time value for midnight of yesterday,
	where yesteday is defined by _timeZone_ (or default time zone if
	_timeZone_ is absent).
- *timeFormat(timeValue, formatString)*: Formats a time value into using
	format string in the time zone specified by time value.
- *timeParse(timeString, formatString, timeZone)*: Creates time value by
	parsing time string using format string and set to time zone.
- *timeAdd(timeValue, amount, unit)*: Performs time addition. Adds amount of
	time unit to _timeValue_.
- *timeToUnixDay(timeValue)*: Converts time value to number of days since
    Jan 1st, 1970 in the time zone of the time value.
- *unixDayToTime(unixDay, [timeZone])*: Converts number of days since Jan 1st,
    1970 to time value of either the specified time zone or the system time
    zone.

#### Collection functions

- *list(...)*: Creates a list. Accepts any number of arguments.
- *listGet(list, index)*: Gets an element from a list at index.
- *listFirst(list, defaultValue)*: Gets first element of a list, or default
    value if list is empty.
- *listLast(list, defaultValue)*: Gets last element of list, or default value
    if list is empty.
- *dict(...)*: Creates a dictionary. Accepts even number of arguments, where
	argument _2*i_ is the key and _2*i+1_ is the value.
- *dictGet(dict, key)*: Gets a value from dictionary given a key.

Code Example
------------

Camino can easily be instantiated. External dependencies can be injected
using EnvBuilder to build the Env object. Configuration can be loaded using
ConfigBuilder into a Config object.

	Env env = new EnvBuilder().withFileSystem(fileSystem)
		.withTimeZone(timeZone).withExecutorService(executorService)
		.build();
	Config config = new ConfigBuilder().from(uri).build();
	Camino camino = new Camino(config, env);
	List<PathMetrics> pathMetricsList = camino.getPathMetrics();

Note that file system is required in order to build an Env object. The
builder will throw an exception if it is not supplied. By default time zone
is the local time zone. If no executor service is supplied, then Camino
creates a temporary one each time getPathMetrics() is called.

Standard caveats of external services apply, such as for example if you
create and supply the executor service you have to shut it down.

Also, ConfigBuilder provides programmatic ways to add properties, paths,
metrics, and iterators.

	List<Path> paths = new ArrayList<Path>();
	paths.add(new Path(...));
	ConfigBuilder configBuilder = new ConfigBuilder().addPaths(paths);
	...
	Config config = configBuilder.build();

Example Configuration
---------------------

	{
	    "includes": [
	        "base-config.json"
	    ],
		"properties": [
			"appHome": "/myApp",
			"logDir": "<%=appHome%>/log",
			"userDir": "<%=appHome%>/users",
			"amy": "<%={'name':'Amy','dropHour':4)%>",
			"bob": "<%={'name':'Bob','dropHour':6)%>",
			"chuck": "<%={'name':'Chuck','dropHour':7)%>",
			"users": "<%=[amy,bob,chuck]%>",
			"pathDate": "<%=fn(m,p)->timeParse(listLast(split(p.pathValue),'/'),'yyyyMMdd','US/Pacific').timeMillis%>"
		],
		"paths": [
			{
				"name": "appLog",
				"tags": {
				    "pathName": "appLog"
				 },
				"value": "<%=logDir%>/app.log"
			}
		],
		"iterators": [
			{
				"var": "user",
				"list": "<%=users%>",
				"paths": [
					{
						"name": "dailyUserData_<%=user['name']%>",
						"tags": {
						    "pathName": "dailyUserData",
							"user": "<%=user['name']'%>"
						},
						"value": "<%=userDir%>/<%=user['name']%>/<%=timeFormat(today('US/Pacific'),'yyyyMMdd')%>/part-*",
						"expectedCreationTime": "<%=timeAdd(today('US/Pacific'),user['dropHour'],'h')%>"
					},
					{
					    "name": "lastDate_<%=user['name']%>",
					    "tags": {
						    "pathName": "lastDate",
							"user": "<%=user['name']'%>"
					    },
						"value": "<%=userDir%>/<%=user['name']%>/[0-9]*",
						"metrics": [
						    {
						        "name": "maxDate",
						        "function": "pathDate",
						        "aggregate": "max"
						    }
						]
					}
				]
			}
		]
	}
