/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.client.ext.modulesloader.impl;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PropertiesModuleManagerTest {

	@Test
	public void normalizeDriveLetter() {
		PropertiesModuleManager moduleManager = new PropertiesModuleManager();
		File mockFile = mock(File.class);
		Path mockPath = mock(Path.class);
		when(mockPath.toAbsolutePath()).thenReturn(mockPath);
		when(mockPath.toString()).thenReturn("C:\\temp");
		when(mockPath.getRoot()).thenReturn(mockPath);
		when(mockFile.toPath()).thenReturn(mockPath);

		assertEquals("c:\\temp", moduleManager.normalizeDriveLetter(mockFile), "Drive letter is lower-cased");
	}

	@Test
	public void normalizeDriveLetterNullRoot() {
		PropertiesModuleManager moduleManager = new PropertiesModuleManager();
		File mockFile = mock(File.class);
		Path mockPath = mock(Path.class);
		when(mockPath.toAbsolutePath()).thenReturn(mockPath);
		when(mockPath.toString()).thenReturn("Temp");
		when(mockPath.getRoot()).thenReturn(null);
		when(mockFile.toPath()).thenReturn(mockPath);

		assertEquals("Temp", moduleManager.normalizeDriveLetter(mockFile),"Since getRoot() was null, original path is returned");
	}
}
