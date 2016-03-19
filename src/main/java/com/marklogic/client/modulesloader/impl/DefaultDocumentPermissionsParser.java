package com.marklogic.client.modulesloader.impl;

import com.marklogic.client.io.DocumentMetadataHandle.Capability;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentPermissions;

public class DefaultDocumentPermissionsParser implements DocumentPermissionsParser {

    @Override
    public void parsePermissions(String str, DocumentPermissions permissions) {
        if (str != null && str.trim().length() > 0) {
            String[] tokens = str.split(",");
            for (int i = 0; i < tokens.length; i += 2) {
                String role = tokens[i];
                String capability = tokens[i + 1];
                Capability c = null;
                if (capability.equals("execute")) {
                    c = Capability.EXECUTE;
                } else if (capability.equals("insert")) {
                    c = Capability.INSERT;
                } else if (capability.equals("update")) {
                    c = Capability.UPDATE;
                } else if (capability.equals("read")) {
                    c = Capability.READ;
                }
                permissions.add(role, c);
            }
        }
    }

}
