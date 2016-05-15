package com.marklogic.client.qconsole;

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
