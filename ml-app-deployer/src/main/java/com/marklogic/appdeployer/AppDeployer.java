/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer;

/**
 * Abstraction for deploying and undeploying an application, with an {@code AppConfig} instance providing a variety of
 * configuration information for the application.
 * 
 * Implementors can do whatever they want, but it is expected that an implementor utilize a sequence of {@code Command}
 * objects to define what steps are taken as part of a deploy or undeploy operation. The {@code SimpleAppDeployer}
 * implementation in the "impl" subpackage is the common implementation to use which does depend on command instances.
 */
public interface AppDeployer {

    /**
     * Deploy an application based on the configuration found in the given {@code AppConfig} instance.
     * 
     * @param appConfig
     */
    public void deploy(AppConfig appConfig);

    /**
     * Undeploy an application - which normally means removing all traces of it from MarkLogic - based on the
     * configuration found in the given {@code AppConfig} instance.
     * 
     * @param appConfig
     */
    public void undeploy(AppConfig appConfig);
}
