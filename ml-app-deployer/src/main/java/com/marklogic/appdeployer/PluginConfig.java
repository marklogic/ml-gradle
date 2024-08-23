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
package com.marklogic.appdeployer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * This is for installing MarkLogic plugins, not Data Hub Framework plugins.
 */
public class PluginConfig {

	public final static String DEFAULT_PLUGIN_PATH = "src/main/ml-plugins";
	public final static String DEFAULT_PLUGIN_URI_PREFIX = "/com.marklogic/plugins/";

	public final static String DEFAULT_INSTALL_SCRIPT = "import module namespace plugin = 'http://marklogic.com/extension/plugin' at 'MarkLogic/plugin/plugin.xqy'; " +
		"declare variable $uri external; " +
		"declare variable $scope external; " +
		"plugin:install-from-zip($scope, fn:doc($uri)/node())";

	public final static String DEFAULT_UNINSTALL_SCRIPT = "import module namespace plugin = 'http://marklogic.com/extension/plugin' at 'MarkLogic/plugin/plugin.xqy'; " +
		"declare variable $scope external; " +
		"plugin:uninstall($scope)";

	private List<String> pluginPaths;
	private boolean enabled = true;
	private String databaseName;
	private String uriPrefix = DEFAULT_PLUGIN_URI_PREFIX;
	private String installScript = DEFAULT_INSTALL_SCRIPT;
	private String uninstallScript = DEFAULT_UNINSTALL_SCRIPT;
	private String makeCommand = "make";
	private String scope = "native";

	private File projectDir;

	public PluginConfig(File projectDir) {
		this.projectDir = projectDir;

		pluginPaths = new ArrayList<>();
		String path = projectDir != null ? new File(projectDir, DEFAULT_PLUGIN_PATH).getAbsolutePath() : DEFAULT_PLUGIN_PATH;
		pluginPaths.add(path);
	}

	public List<String> getPluginPaths() {
		return pluginPaths;
	}

	public void setPluginPaths(List<String> pluginPaths) {
		this.pluginPaths = pluginPaths;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public String getUriPrefix() {
		return uriPrefix;
	}

	public void setUriPrefix(String uriPrefix) {
		this.uriPrefix = uriPrefix;
	}

	public String getInstallScript() {
		return installScript;
	}

	public void setInstallScript(String installScript) {
		this.installScript = installScript;
	}

	public String getMakeCommand() {
		return makeCommand;
	}

	public void setMakeCommand(String makeCommand) {
		this.makeCommand = makeCommand;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getUninstallScript() {
		return uninstallScript;
	}

	public void setUninstallScript(String uninstallScript) {
		this.uninstallScript = uninstallScript;
	}

	public File getProjectDir() {
		return projectDir;
	}
}
