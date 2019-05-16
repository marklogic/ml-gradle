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
