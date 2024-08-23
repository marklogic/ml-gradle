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
package com.marklogic.appdeployer.command.cpf;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.mgmt.resource.cpf.AbstractCpfResourceManager;

import java.io.File;

/**
 * Base class for commands that deploy CPF resources. By default, this will use getCpfDatabaseName on AppConfig to
 * determine what database to deploy CPF resources to. That can be overridden via the setDatabaseIdOrName method
 * on this class.
 */
public abstract class AbstractCpfResourceCommand extends AbstractCommand {

	private String databaseIdOrName;

	protected abstract File getCpfResourceDir(ConfigDir configDir);

	protected abstract AbstractCpfResourceManager getResourceManager(CommandContext context, String databaseIdOrName);

	@Override
	public void execute(CommandContext context) {
		AppConfig config = context.getAppConfig();
		for (ConfigDir configDir : config.getConfigDirs()) {
			File dir = getCpfResourceDir(configDir);
			if (dir.exists()) {
				final String db = databaseIdOrName != null ? databaseIdOrName : config.getCpfDatabaseName();
				AbstractCpfResourceManager mgr = getResourceManager(context, db);
				for (File f : listFilesInDirectory(dir)) {
					String payload = copyFileToString(f, context);
					mgr.save(payload);
				}
			} else {
				logResourceDirectoryNotFound(dir);
			}
		}
	}

	public void setDatabaseIdOrName(String databaseIdOrName) {
		this.databaseIdOrName = databaseIdOrName;
	}
}
