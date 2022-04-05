package com.marklogic.client.ext.modulesloader.impl;

import org.junit.Test;

import java.io.File;
import java.nio.file.Path;

import static org.junit.Assert.*;
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

		assertEquals("Drive letter is lower-cased", "c:\\temp", moduleManager.normalizeDriveLetter(mockFile));
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

		assertEquals("Since getRoot() was null, original path is returned", moduleManager.normalizeDriveLetter(mockFile));
	}
}
