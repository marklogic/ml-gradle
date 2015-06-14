package com.marklogic.clientutil.modulesloader.xcc;

import java.util.ArrayList;
import java.util.List;

import com.marklogic.xcc.ContentCapability;
import com.marklogic.xcc.ContentPermission;

/**
 * Simple implementation that expects permissions to be comma-delimited in the format
 * role1,capability1,role2,capability2,etc - just like when using mlcp.
 *
 */
public class CommaDelimitedPermissionsParser implements PermissionsParser {

    @Override
    public ContentPermission[] parsePermissions(String text) {
        List<ContentPermission> list = new ArrayList<ContentPermission>();
        if (text != null && text.trim().length() > 0) {
            String[] tokens = text.split(",");
            for (int i = 0; i < tokens.length; i += 2) {
                String role = tokens[i];
                list.add(new ContentPermission(parseCapability(tokens[i + 1]), role));
            }
        }
        return list.toArray(new ContentPermission[] {});
    }

    protected ContentCapability parseCapability(String capability) {
        if (capability.equals("execute")) {
            return ContentCapability.EXECUTE;
        } else if (capability.equals("insert")) {
            return ContentCapability.INSERT;
        } else if (capability.equals("update")) {
            return ContentCapability.UPDATE;
        } else if (capability.equals("read")) {
            return ContentCapability.READ;
        }
        throw new IllegalArgumentException("Unrecognized content capability: " + capability);
    }
}
