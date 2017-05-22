package com.marklogic.client.ext.util;

import com.marklogic.client.io.DocumentMetadataHandle.DocumentPermissions;

public interface DocumentPermissionsParser {

    /**
     * Parse the string and add role/capability sets to the given DocumentPermissions object.
     *
     * @param str
     * @param permissions
     */
    void parsePermissions(String str, DocumentPermissions permissions);
}
