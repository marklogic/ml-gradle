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
package com.marklogic.client.ext.qconsole;

import java.io.File;
import java.util.List;

/**
 * Interface for exporting and importing qconsole workspaces to/from disk.
 */
public interface WorkspaceManager {

    /**
     * @param user
     * @param workspaceNames
     * @return a list of files that workspaces for the given user were exported to
     */
    public List<File> exportWorkspaces(String user, String... workspaceNames);

    /**
     * @param user
     * @param workspaceNames
     * @return a list of files that workspaces for the given user were imported from
     */
    public List<File> importWorkspaces(String user, String... workspaceNames);
}
