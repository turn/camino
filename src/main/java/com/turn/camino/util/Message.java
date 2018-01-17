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
package com.turn.camino.util;

/**
 * Message for exception
 *
 * @author llo
 */
public abstract class Message {

	/**
	 * Reformats message to specific exception
	 *
	 * @param formatString format string
	 * @param arguments arguments
	 * @return new message
	 */
	public abstract Message reformat(String formatString, Object...arguments);

	/**
	 * Creates message with specific prefix
	 *
	 * On reformat, the message contain prefix in the start of text
	 *
	 * @param prefix prefix of message
	 * @return message instance
	 */
	public static Message prefix(final String prefix) {
		return new Message() {
			private String text = prefix;
			@Override
			public Message reformat(String formatString, Object...arguments) {
				return Message.full(String.format("%s: %s", prefix,
						String.format(formatString, arguments)));
			}
			@Override
			public String toString() {
				return text;
			}
		};
	}

	/**
	 * Creates fully-formed message
	 *
	 * The message is immutable. Reformat does not affect the message.
	 *
	 * @param text text of message
	 * @return message instance
	 */
	public static Message full(final String text) {
		return new Message() {
			@Override
			public Message reformat(String formatString, Object...arguments) {
				return this;
			}
			@Override
			public String toString() {
				return text;
			}
		};
	}

}
