package com.marklogic.client.ext.datamovement.listener;

public class AddPermissionsListener extends AbstractPermissionsListener {

	public AddPermissionsListener(String... rolesAndCapabilities) {
		super(rolesAndCapabilities);
	}

	@Override
	protected String getXqueryFunction() {
		return "xdmp:document-add-permissions";
	}
}
