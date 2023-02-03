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
