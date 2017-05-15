package com.marklogic.client.ext.datamovement.listener;

public class RemovePermissionsListener extends AbstractPermissionsListener {

	public RemovePermissionsListener(String... rolesAndCapabilities) {
		super(rolesAndCapabilities);
	}

	@Override
	protected String getXqueryFunction() {
		return "xdmp:document-remove-permissions";
	}
}
