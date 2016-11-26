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

import com.google.common.collect.Maps;
import com.turn.camino.util.Message;
import com.turn.camino.util.MessageExceptionFactory;
import com.turn.camino.util.Validation;

import java.util.Map;

/**
 * Context implementation
 *
 * @author llo
 */
class ContextImpl implements Context {

	private Env env;
	private Context parent;
	private Context global;
	private Map<String, Object> properties = Maps.newHashMap();
	private long instanceTime;

	private Validation<WrongTypeException> validation = new Validation<>(
			new MessageExceptionFactory<WrongTypeException>() {
				@Override
				public WrongTypeException newException(String message) {
					return new WrongTypeException(message);
				}
			});

	/**
	 * Constructor
	 *
	 * @param env environment
	 * @param parent parent context
	 */
	ContextImpl(Env env, Context parent) {
		this.env = env;
		this.parent = parent;
		if (parent != null) {
			this.instanceTime = parent.getGlobalInstanceTime();
			this.global = this.parent.getGlobal();
		} else {
			this.instanceTime = env.getCurrentTime();
			this.global = this;
		}
	}

	ContextImpl(Env env, Context parent, Map<String, Object> properties) {
		this(env, parent);
		this.properties.putAll(properties);
	}

	@Override
	public Env getEnv() {
		return env;
	}

	@Override
	public Context createChild() {
		return new ContextImpl(env, this);
	}

	@Override
	public Context getParent() {
		return parent;
	}

	@Override
	public Context getGlobal() {
		return global;
	}

	@Override
	public void setProperty(String name, Object value) {
		properties.put(name, value);
	}

	@Override
	public Object getProperty(String name) {
		Object value = properties.get(name);
		if (value == null && parent != null) {
			return parent.getProperty(name);
		} else {
			return value;
		}
	}

	@Override
	public <T> T getProperty(String name, Class<T> type) throws WrongTypeException {
		Object value = getProperty(name);
		return value == null ? null : validation.requireType(getProperty(name), type,
				Message.prefix(name));
	}

	@Override
	public long getGlobalInstanceTime() {
		return instanceTime;
	}

}
