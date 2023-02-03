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

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.eval.EvalResultIterator;
import com.marklogic.client.ext.helper.LoggingObject;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.ext.qconsole.WorkspaceManager;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation that uses the ML Java Client API to manage workspaces. By default, workspaces
 * will be exported and imported to/from ~/.qconsole/workspaces/(ML username). This can be overridden by
 * calling setBaseDir.
 */
public class DefaultWorkspaceManager extends LoggingObject implements WorkspaceManager {

	private DatabaseClient client;

	private File baseDir;

	public DefaultWorkspaceManager(DatabaseClient client) {
		this.client = client;
	}

	@Override
	public List<File> exportWorkspaces(String user, String... workspaceNames) {
		if (baseDir == null) {
			baseDir = getDefaultWorkspacesDir();
		}

		File userDir = new File(baseDir, user);
		userDir.mkdirs();

		List<File> files = new ArrayList<>();

		for (String workspaceName : workspaceNames) {
			EvalResultIterator result = client.newServerEval()
				.addVariable("user", user)
				.addVariable("workspace", workspaceName)
				.xquery(QconsoleScripts.EXPORT).eval();

			while (result.hasNext()) {
				DOMHandle dom = result.next().get(new DOMHandle());
				File f = new File(userDir, workspaceName + ".xml");
				try {
					FileCopyUtils.copy(dom.toBuffer(), f);
					if (logger.isInfoEnabled()) {
						logger.info(format("Exported workspace %s to %s", workspaceName, f.getAbsolutePath()));
					}
					files.add(f);
				} catch (IOException ie) {
					throw new RuntimeException("Unable to write workspace XML to file, workspace: " + workspaceName + "; cause: " + ie.getMessage());
				}
			}
		}

		return files;
	}

	@Override
	public List<File> importWorkspaces(String user, String... workspaceNames) {
		if (baseDir == null) {
			baseDir = getDefaultWorkspacesDir();
		}

		List<File> files = new ArrayList<>();

		File userDir = new File(baseDir, user);
		if (!userDir.exists()) {
			return files;
		}

		final String importQuery = determineImportScriptToUse();

		for (String workspace : workspaceNames) {
			File f = new File(userDir, workspace + ".xml");
			if (f.isFile() && f.exists()) {
				client.newServerEval()
					.addVariable("user", user)
					.addVariable("exported-workspace", new FileHandle(f).withFormat(Format.XML))
					.xquery(importQuery).eval();

				if (logger.isInfoEnabled()) {
					logger.info(format("Imported workspace from %s", f.getAbsolutePath()));
				}

				files.add(f);
			}
		}

		return files;
	}

	protected String determineImportScriptToUse() {
		String version = client.newServerEval().xquery("xdmp:version()").evalAs(String.class);
		String xquery = QconsoleScripts.IMPORT;
		if (version != null && version.startsWith("8")) {
			return xquery.replace("qconsole-model:default-database()", "qconsole-model:default-content-source()");
		}
		return xquery;
	}

	/**
	 * @return defaults to ~/.qconsole/workspaces
	 */
	protected File getDefaultWorkspacesDir() {
		File homeDir = new File(System.getProperty("user.home"));
		File qcDir = new File(homeDir, ".qconsole");
		File dir = new File(qcDir, "workspaces");
		dir.mkdirs();
		return dir;
	}


	public void setBaseDir(File baseDir) {
		this.baseDir = baseDir;
	}
}
