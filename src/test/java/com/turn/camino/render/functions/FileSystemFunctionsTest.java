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

import com.turn.camino.Context;
import com.turn.camino.Env;
import com.turn.camino.render.Function;
import com.turn.camino.render.FunctionCallException;

import com.google.common.collect.ImmutableList;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.PathFilter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;
import java.util.TimeZone;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Tests file system functions
 *
 * @author llo
 */
@Test
public class FileSystemFunctionsTest {

	private Context context;
	private FileSystemFunctions.DirList dirList = new FileSystemFunctions.DirList();
	private FileSystemFunctions.DirListName dirListName = new FileSystemFunctions.DirListName();
	private FileSystemFunctions.Exists exists = new FileSystemFunctions.Exists();
	private FileSystemFunctions.IsDir isDir = new FileSystemFunctions.IsDir();

	/**
	 * Set up environment
	 */
	@BeforeClass
	public void setUp() throws IOException {
		// mock environment
		FileSystem fileSystem = mock(FileSystem.class);
		FileStatus[] fss = new FileStatus[] {
				new FileStatus(1200000L, false, 3, 1000L, 1409302856296L,
						new org.apache.hadoop.fs.Path("/a/b/1.dat")),
				new FileStatus(1400000L, false, 3, 1000L, 1409302867303L,
						new org.apache.hadoop.fs.Path("/a/b/2.dat")),
				new FileStatus(1060000L, false, 3, 1000L, 1409302844187L,
						new org.apache.hadoop.fs.Path("/a/b/3.dat"))
		};
		org.apache.hadoop.fs.Path dir = new org.apache.hadoop.fs.Path("/a/b");
		when(fileSystem.exists(dir)).thenReturn(true);
		when(fileSystem.isDirectory(dir)).thenReturn(true);
		when(fileSystem.getFileStatus(dir)).thenReturn(new FileStatus(0L, true, 3,
				100L, 1409302844187L, dir));
		when(fileSystem.listStatus(dir)).thenReturn(fss);
		doCallRealMethod().when(fileSystem).listStatus(any(org.apache.hadoop.fs.Path.class),
				any(PathFilter.class));

		dir = new org.apache.hadoop.fs.Path("/x/y");
		when(fileSystem.exists(new org.apache.hadoop.fs.Path("/x/y"))).thenReturn(false);
		when(fileSystem.getFileStatus(dir)).thenReturn(null);

		dir = new org.apache.hadoop.fs.Path("/u/v");
		when(fileSystem.exists(dir)).thenReturn(true);
		when(fileSystem.isDirectory(dir)).thenReturn(false);
		when(fileSystem.getFileStatus(dir)).thenReturn(new FileStatus(0L, true, 3,
				100L, 1409302844187L, dir));

		doThrow(new IOException()).when(fileSystem).listStatus(new org.apache.hadoop.fs.Path("/foo"));

		context = mock(Context.class);
		Env env = mock(Env.class);
		when(context.getEnv()).thenReturn(env);
		when(env.getCurrentTime()).thenReturn(1409389256296L);
		when(env.getTimeZone()).thenReturn(TimeZone.getTimeZone("GMT"));
		when(env.getFileSystem()).thenReturn(fileSystem);
	}

	/**
	 * Test directory listing
	 */
	@Test
	public void testDirList() throws FunctionCallException {
		Object result = dirList.invoke(ImmutableList.of("/a/b"), context);
		assertTrue(result instanceof List);
		List<?> list = (List<?>) result;
		assertEquals(list.size(), 3);
		assertEquals(list.get(0), "/a/b/1.dat");
		assertEquals(list.get(1), "/a/b/2.dat");
		assertEquals(list.get(2), "/a/b/3.dat");
	}

	/**
	 * Test directory listing with filter
	 */
	@Test
	public void testDirListWithFilter() throws FunctionCallException {
		Function predicate = (params, context) -> params.get(0).toString().endsWith("1.dat");
		Object result = dirList.invoke(ImmutableList.of("/a/b", predicate), context);
		assertTrue(result instanceof List);
		List<?> list = (List<?>) result;
		assertEquals(list.size(), 1);
		assertEquals(list.get(0), "/a/b/1.dat");
	}

	/**
	 * Test when path doesn't exist
	 *
	 * @throws FunctionCallException
	 */
	@Test(expectedExceptions = FunctionCallException.class)
	public void testDirListNotExists() throws FunctionCallException {
		dirList.invoke(ImmutableList.of("/x/y"), context);
	}

	/**
	 * Test when path is not directory
	 *
	 * @throws FunctionCallException
	 */
	@Test(expectedExceptions = FunctionCallException.class)
	public void testDirListIsNotDirectory() throws FunctionCallException {
		dirList.invoke(ImmutableList.of("/u/v"), context);
	}

	/**
	 * Test when file system throws IOException
	 *
	 * @throws FunctionCallException
	 */
	@Test(expectedExceptions = FunctionCallException.class)
	public void testDirListIOException() throws FunctionCallException {
		dirList.invoke(ImmutableList.of("/foo"), context);
	}

	/**
	 * Test directory listing
	 */
	@Test
	public void testDirListName() throws FunctionCallException {
		Object result = dirListName.invoke(ImmutableList.of("/a/b"), context);
		assertTrue(result instanceof List);
		List<?> list = (List<?>) result;
		assertEquals(list.size(), 3);
		assertEquals(list.get(0), "1.dat");
		assertEquals(list.get(1), "2.dat");
		assertEquals(list.get(2), "3.dat");
	}

	/**
	 * Test directory listing with filter
	 */
	@Test
	public void testDirListNameWithFilter() throws FunctionCallException {
		Function predicate = (params, context) -> params.get(0).toString().matches("[12].dat");
		Object result = dirListName.invoke(ImmutableList.of("/a/b", predicate), context);
		assertTrue(result instanceof List);
		List<?> list = (List<?>) result;
		assertEquals(list.size(), 2);
		assertEquals(list.get(0), "1.dat");
		assertEquals(list.get(1), "2.dat");
	}

	/**
	 * Test when file system throws IOException
	 *
	 * @throws FunctionCallException
	 */
	@Test(expectedExceptions = FunctionCallException.class)
	public void testDirListNameIOException() throws FunctionCallException {
		dirListName.invoke(ImmutableList.of("/foo"), context);
	}

	/**
	 * Test exists function
	 *
	 * @throws FunctionCallException
	 */
	@Test
	public void testExists() throws FunctionCallException {
		assertTrue((Boolean) exists.invoke(ImmutableList.of("/a/b"), context));
		assertFalse((Boolean) exists.invoke(ImmutableList.of("/a/e"), context));
	}

	/**
	 * Tet isDir function
	 *
	 * @throws FunctionCallException
	 */
	@Test
	public void testIsDir() throws FunctionCallException {
		assertTrue((Boolean) isDir.invoke(ImmutableList.of("/a/b"), context));
		assertFalse((Boolean) isDir.invoke(ImmutableList.of("/a/b/1.dat"), context));
	}
}
