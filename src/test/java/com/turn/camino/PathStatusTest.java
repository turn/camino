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

import com.turn.camino.config.Path;

import java.util.List;

import com.google.common.collect.Lists;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 * Unit test for PathStatus
 *
 * @author llo
 */
@Test
public class PathStatusTest {

	@Test
	public void testConstructor() {
		Path path = mock(Path.class);
		PathDetail pathDetail = new PathDetail("/a/b", false, 63000, System.currentTimeMillis());
		List<PathDetail> pathDetails = Lists.newArrayList(pathDetail);
		PathStatus pathStatus = new PathStatus("path", "/a/b", path, pathDetails, null);
		assertEquals(pathStatus.getName(), "path");
		assertEquals(pathStatus.getValue(), "/a/b");
		assertEquals(pathStatus.getPath(), path);
		assertEquals(pathStatus.getPathDetails().size(), 1);
	}

	@Test(expectedExceptions = UnsupportedOperationException.class)
	public void testImmutablePathDetails() {
		PathStatus pathStatus = new PathStatus("path", "/a/b", mock(Path.class),
				Lists.<PathDetail>newArrayList(), null);
		pathStatus.getPathDetails().add(new PathDetail("/a/b", false, 63000,
				System.currentTimeMillis()));
	}

}
