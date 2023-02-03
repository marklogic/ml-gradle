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
