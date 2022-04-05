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
