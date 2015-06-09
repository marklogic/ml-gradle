package com.marklogic.appdeployer.command.modules;

import com.marklogic.xcc.ContentPermission;

public interface PermissionsParser {

    public ContentPermission[] parsePermissions(String text);
}
