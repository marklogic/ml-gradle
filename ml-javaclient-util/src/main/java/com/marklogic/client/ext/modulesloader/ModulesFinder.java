/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.ext.modulesloader;

/**
 * Given a directory, return a Modules instance capturing all of the REST API modules to load.
 */
public interface ModulesFinder {

    Modules findModules(String path);
}
