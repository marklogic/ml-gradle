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
