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
package com.marklogic.client.ext.modulesloader;

import java.io.File;
import java.util.Date;

/**
 * Defines operations for managing whether a module needs to be installed or not.
 */
public interface ModulesManager {

    /**
     * Give the implementor a chance to initialize itself - e.g. loading data from a properties file or other resource.
     */
    void initialize();

    boolean hasFileBeenModifiedSinceLastLoaded(File file);

    void saveLastLoadedTimestamp(File file, Date date);
}
