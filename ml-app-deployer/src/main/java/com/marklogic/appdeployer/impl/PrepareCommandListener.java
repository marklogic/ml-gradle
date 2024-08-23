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
package com.marklogic.appdeployer.impl;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.Command;

import java.util.List;
import java.util.regex.Pattern;

public class PrepareCommandListener extends DeployerListenerSupport {

	@Override
	public void beforeCommandExecuted(Command command, DeploymentContext context, List<Command> remainingCommands) {
		if (command instanceof AbstractCommand) {
			AppConfig appConfig = context.getAppConfig();
			String[] filenamesToIgnore = appConfig.getResourceFilenamesToIgnore();
			Pattern excludePattern = appConfig.getResourceFilenamesExcludePattern();
			Pattern includePattern = appConfig.getResourceFilenamesIncludePattern();

			AbstractCommand abstractCommand = (AbstractCommand) command;
			if (filenamesToIgnore != null) {
				abstractCommand.setFilenamesToIgnore(filenamesToIgnore);
			}
			if (excludePattern != null) {
				abstractCommand.setResourceFilenamesExcludePattern(excludePattern);
			}
			if (includePattern != null) {
				abstractCommand.setResourceFilenamesIncludePattern(includePattern);
			}
		}
	}
}
