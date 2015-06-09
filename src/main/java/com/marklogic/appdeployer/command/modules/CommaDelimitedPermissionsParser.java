package com.marklogic.appdeployer.command.modules;

import java.util.ArrayList;
import java.util.List;

import com.marklogic.xcc.ContentCapability;
import com.marklogic.xcc.ContentPermission;

public class CommaDelimitedPermissionsParser implements PermissionsParser {

    @Override
    public ContentPermission[] parsePermissions(String text) {
        List<ContentPermission> list = new ArrayList<ContentPermission>();
        if (text != null && text.trim().length() > 0) {
            String[] tokens = text.split(",");
            for (int i = 0; i < tokens.length; i += 2) {
                String role = tokens[i];
                String capability = tokens[i + 1];

                ContentCapability cc = ContentCapability.READ;
                if (capability.equals("execute")) {
                    cc = ContentCapability.EXECUTE;
                } else if (capability.equals("insert")) {
                    cc = ContentCapability.INSERT;
                } else if (capability.equals("update")) {
                    cc = ContentCapability.UPDATE;
                }

                list.add(new ContentPermission(cc, role));
            }
        }
        return list.toArray(new ContentPermission[] {});
    }

}
