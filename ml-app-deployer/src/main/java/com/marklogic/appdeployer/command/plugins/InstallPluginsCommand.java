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
package com.marklogic.appdeployer.command.plugins;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.PluginConfig;
import com.marklogic.appdeployer.command.AbstractUndoableCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.BinaryDocumentManager;
import com.marklogic.client.eval.ServerEvaluationCall;
import com.marklogic.client.io.FileHandle;
import com.marklogic.rest.util.Fragment;
import org.jdom2.Namespace;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * This is for installing MarkLogic plugins, not Data Hub Framework plugins.
 */
public class InstallPluginsCommand extends AbstractUndoableCommand {

	public InstallPluginsCommand() {
		setExecuteSortOrder(SortOrderConstants.INSTALL_PLUGINS);
		setUndoSortOrder(SortOrderConstants.UNINSTALL_PLUGINS);
	}

	@Override
	public void execute(CommandContext context) {
		List<String> paths = getPluginPaths(context);
		if (paths == null || paths.isEmpty()) {
			return;
		}

		DatabaseClient client = determineDatabaseClient(context.getAppConfig());
		for (String path : paths) {
			installPluginsInPath(path, context.getAppConfig(), client);
		}
	}

	@Override
	public void undo(CommandContext context) {
		List<String> paths = getPluginPaths(context);
		if (paths == null || paths.isEmpty()) {
			return;
		}

		DatabaseClient client = determineDatabaseClient(context.getAppConfig());
		for (String path : paths) {
			uninstallPluginsInPath(path, context.getAppConfig(), client);
		}
	}

	protected List<String> getPluginPaths(CommandContext context) {
		PluginConfig config = context.getAppConfig().getPluginConfig();
		if (config == null) {
			return null;
		}

		if (!config.isEnabled()) {
			logger.info("Installing/uninstalling plugins is disabled");
			return null;
		}

		return config.getPluginPaths();
	}

	protected void installPluginsInPath(String path, AppConfig appConfig, DatabaseClient client) {
		File pluginsDir = new File(path);
		if (pluginsDir == null || !pluginsDir.exists()) {
			return;
		}

		for (File dir : new File(path).listFiles()) {
			if (!dir.isDirectory()) {
				continue;
			}

			makePlugin(dir, appConfig);
			final String binaryUri = insertPluginZip(dir, appConfig, client);
			if (binaryUri != null) {
				installPlugin(binaryUri, appConfig, client);
			}
		}
	}

	protected void makePlugin(File dir, AppConfig appConfig) {
		final String command = appConfig.getPluginConfig().getMakeCommand();

		logger.info(format("Invoking command '%s' in directory: %s", command, dir.getAbsolutePath()));
		try {
			Process process = new ProcessBuilder(command).directory(dir).start();
			byte[] output = FileCopyUtils.copyToByteArray(process.getInputStream());
			process.waitFor();
			logger.info(format("Output from executing command '%s': %s", command, new String(output)));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String insertPluginZip(File dir, AppConfig appConfig, DatabaseClient client) {
		BinaryDocumentManager binaryDocumentManager = client.newBinaryDocumentManager();

		final File file = findPluginZipInDirectory(dir);
		if (file == null) {
			return null;
		}

		final String uri = appConfig.getPluginConfig().getUriPrefix() + file.getName();
		logger.info("Writing plugin zip file to URI: " + uri);
		binaryDocumentManager.write(uri, new FileHandle(file));
		return uri;
	}

	protected File findPluginZipInDirectory(File dir) {
		File[] files = dir.listFiles((dir1, name) -> name.endsWith(".zip"));
		if (files.length == 0) {
			logger.info("No files ending in .zip found in directory: " + dir.getAbsolutePath());
			return null;
		}
		if (files.length > 1) {
			logger.info("Multiple files ending in .zip found in directory: " + dir.getAbsolutePath() +
				"; please ensure only a single .zip file is in the directory.");
			return null;
		}
		return files[0];
	}

	protected void installPlugin(String uri, AppConfig appConfig, DatabaseClient client) {
		final String scope = appConfig.getPluginConfig().getScope();
		final String script = appConfig.getPluginConfig().getInstallScript();
		ServerEvaluationCall eval = client
			.newServerEval()
			.xquery(script)
			.addVariable("uri", uri)
			.addVariable("scope", scope);
		logger.info(format("Installing plugin with scope '%s' from URI '%s' via script: %s", scope, uri, script));
		String result = eval.evalAs(String.class);
		logger.info(format("Installed plugin with scope '%s', result: %s", scope, result));
	}

	protected void uninstallPluginsInPath(String path, AppConfig appConfig, DatabaseClient client) {
		File pluginsDir = new File(path);
		if (pluginsDir == null || !pluginsDir.exists()) {
			return;
		}

		for (File dir : new File(path).listFiles()) {
			if (!dir.isDirectory()) {
				continue;
			}

			final String pluginName = getPluginName(dir, appConfig);
			if (pluginName != null) {
				uninstallPlugin(pluginName, appConfig, client);
			}
		}
	}

	protected String getPluginName(File dir, AppConfig appConfig) {
		File manifestFile = new File(dir, "manifest.xml");
		if (manifestFile == null || !manifestFile.exists()) {
			// Need to make the plugin so the metadata file is guaranteed to exist
			makePlugin(dir, appConfig);
			manifestFile = new File(dir, "manifest.xml");
		}

		try {
			String xml = new String(FileCopyUtils.copyToByteArray(manifestFile));
			Fragment manifest = new Fragment(xml, Namespace.getNamespace("p", "http://marklogic.com/extension/plugin"));
			return manifest.getElementValue("/p:plugin/p:name");
		} catch (IOException e) {
			logger.warn(format("Unable to get plugin name from dir: %s; cause: %s", dir.getAbsolutePath(), e.getMessage()), e);
			return null;
		}
	}

	protected void uninstallPlugin(String pluginName, AppConfig appConfig, DatabaseClient client) {
		final String scope = appConfig.getPluginConfig().getScope() + "/" + pluginName;
		final String script = appConfig.getPluginConfig().getUninstallScript();
		ServerEvaluationCall eval = client
			.newServerEval()
			.xquery(script)
			.addVariable("scope", scope);
		logger.info(format("Uninstalling plugin with scope '%s' via script: %s", scope, script));
		String result = eval.evalAs(String.class);
		if (result != null && result.trim().length() > 0) {
			logger.info(format("Uninstalled plugin with scope '%s', result: %s", scope, result));
		} else {
			logger.info(format("Uninstalled plugin with scope '%s'", scope));
		}
	}

	/**
	 * The assumption is that newDatabaseClient on the given AppConfig object specifies the connection to use for
	 * loading data. If databaseName is set on the PluginConfig object belonging to the AppConfig object, then
	 * a connection is made to that database via the App-Services port configured on the AppConfig object.
	 *
	 * @param appConfig
	 * @return
	 */
	protected DatabaseClient determineDatabaseClient(AppConfig appConfig) {
		PluginConfig config = appConfig.getPluginConfig();
		final String databaseName = config.getDatabaseName();
		if (StringUtils.hasText(databaseName)) {
			logger.info("Will install plugins via App-Services port and database: " + databaseName);
			return appConfig.newAppServicesDatabaseClient(databaseName);
		}
		return appConfig.newDatabaseClient();
	}

}
