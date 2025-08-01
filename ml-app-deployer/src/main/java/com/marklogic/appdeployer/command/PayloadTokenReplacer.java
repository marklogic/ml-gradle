/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.command;

import com.marklogic.appdeployer.AppConfig;

/**
 * Used by commands to replace tokens in configuration files that are dependent on names of resources in the application.
 * Typically, the tokens are replaced by values in the AppConfig instance. This allows for configuration files to be
 * reused across applications with different names.
 */
public interface PayloadTokenReplacer {

    String replaceTokens(String payload, AppConfig appConfig, boolean isTestResource);
}
