/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
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
