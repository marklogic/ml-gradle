package com.rjrudin.marklogic.modulesloader.xcc;

import com.marklogic.xcc.ContentPermission;

/**
 * Strategy interface for how XCC permissions are parsed from text.
 */
public interface PermissionsParser {

    public ContentPermission[] parsePermissions(String text);
}
