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
package com.marklogic.junit;

import com.marklogic.xcc.template.XccTemplate;

/**
 * Provides convenience methods for instantiating new TestHelper and ResourceManager implementations. Also extends
 * XmlHelper so that this can be used as a base class for test classes.
 */
public class BaseTestHelper extends XmlHelper {

	private NamespaceProvider namespaceProvider;

	public BaseTestHelper() {
		namespaceProvider = new MarkLogicNamespaceProvider();
	}

	/**
	 * Convenience method for getting the permissions for a document as a fragment.
	 *
	 * @param uri
	 * @param t
	 * @return
	 */
	public PermissionsFragment getDocumentPermissions(String uri, XccTemplate t) {
		String xquery = format("for $perm in xdmp:document-get-permissions('%s') ", uri);
		xquery += "return element {fn:node-name($perm)} {";
		xquery += "  $perm/*,";
		xquery += "  xdmp:eval('import module namespace sec=\"http://marklogic.com/xdmp/security\" at \"/MarkLogic/security.xqy\"; sec:get-role-names(' || $perm/sec:role-id/fn:string() || ')', (), ";
		xquery += "    <options xmlns='xdmp:eval'><database>{xdmp:security-database()}</database></options>) }";
		return new PermissionsFragment(parse("<permissions>" + t.executeAdhocQuery(xquery) + "</permissions>"));
	}

	protected NamespaceProvider getNamespaceProvider() {
		return this.namespaceProvider;
	}
}
