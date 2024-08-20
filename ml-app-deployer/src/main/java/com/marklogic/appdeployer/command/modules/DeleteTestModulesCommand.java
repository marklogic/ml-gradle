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
package com.marklogic.appdeployer.command.modules;

import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;

/**
 * Subclass that is primarily intended for current Roxy users that are accustomed to having test-only modules deployed
 * under the "/test" path.
 */
public class DeleteTestModulesCommand extends DeleteModulesCommand {

	public final static String DEFAULT_TEST_MODULES_PATTERN = "/test/**";

	public DeleteTestModulesCommand() {
		this(DEFAULT_TEST_MODULES_PATTERN);
	}

	public DeleteTestModulesCommand(String pattern) {
		super(pattern);
		setExecuteSortOrder(SortOrderConstants.DELETE_TEST_MODULES);
	}

	@Override
	public void execute(CommandContext context) {
		if (context.getAppConfig().isDeleteTestModules()) {
			String pattern = context.getAppConfig().getDeleteTestModulesPattern();
			if (pattern != null) {
				setPattern(pattern);
			}
			super.execute(context);
		}
	}
}
