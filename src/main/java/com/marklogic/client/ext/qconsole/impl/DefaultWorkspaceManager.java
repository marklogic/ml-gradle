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

		for (String workspace : workspaceNames) {
			File f = new File(userDir, workspace + ".xml");
			if (f.isFile() && f.exists()) {
				client.newServerEval()
					.addVariable("user", user)
					.addVariable("exported-workspace", new FileHandle(f).withFormat(Format.XML))
					.xquery(QconsoleScripts.IMPORT).eval();

				if (logger.isInfoEnabled()) {
					logger.info(format("Imported workspace from %s", f.getAbsolutePath()));
				}

				files.add(f);
			}
		}

		return files;
	}

	/**
	 * Defaults to ~/.qconsole/workspaces.
	 *
	 * @return
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
