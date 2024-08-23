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
package com.marklogic.appdeployer.command;


/**
 * A command is used to provide behavior when creating an application; it keeps the AppDeployer instance from being
 * tightly coupled to all the things a developer may want to happen.
 */
public interface Command {

    /**
     * Configure the application in some way.
     * 
     * @param context
     */
    public void execute(CommandContext context);

    /**
     * Return a number corresponding to the order in which this command should execute when an application is being
     * deployed, where the lower the number, the earlier the command is invoked.
     */
    public Integer getExecuteSortOrder();

}
