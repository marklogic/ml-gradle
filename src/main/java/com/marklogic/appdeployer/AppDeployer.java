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
