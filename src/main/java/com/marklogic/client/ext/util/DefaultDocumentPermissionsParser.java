package com.marklogic.client.ext.util;

import com.marklogic.client.io.DocumentMetadataHandle.Capability;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentPermissions;

public class DefaultDocumentPermissionsParser implements DocumentPermissionsParser {

	@Override
	public void parsePermissions(String str, DocumentPermissions permissions) {
		if (str != null && str.trim().length() > 0) {
			String[] tokens = str.split(",");
			for (int i = 0; i < tokens.length; i += 2) {
				String role = tokens[i];
				if (i + 1 >= tokens.length) {
					throw new IllegalArgumentException("Unable to parse permissions string, which must be a comma-separated " +
						"list of role names and capabilities - i.e. role1,read,role2,update,role3,execute; string: " + str);
				}
				Capability c;
				try {
					c = Capability.getValueOf(tokens[i + 1]);
				} catch (Exception e) {
					throw new IllegalArgumentException("Unable to parse permissions string: " + str + "; cause: " + e.getMessage());
				}
				if (permissions.containsKey(role)) {
					permissions.get(role).add(c);
				} else {
					permissions.add(role, c);
				}
			}
		}
	}

}
