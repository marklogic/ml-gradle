package com.marklogic.client.ext.qconsole.impl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class QconsoleScripts {

	private final static String MODULE_PATH = "com/marklogic/client/ext/qconsole/impl/";
	public static final String IMPORT;
	public final static String EXPORT;
	static {
		IMPORT = readFile( MODULE_PATH + "import-workspaces.xqy");
		EXPORT = readFile(MODULE_PATH + "export-workspaces.xqy");
	}

	private static String readFile(String fileName){
		InputStream inputStream = QconsoleScripts.class.getClassLoader().getResourceAsStream(fileName);
		return new BufferedReader(
			new InputStreamReader(inputStream, StandardCharsets.UTF_8))
			.lines()
			.collect(Collectors.joining("\n"));
	}
}
