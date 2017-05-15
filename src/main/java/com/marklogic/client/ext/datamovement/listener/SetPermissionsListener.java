package com.marklogic.client.ext.datamovement.listener;

public class SetPermissionsListener extends AbstractPermissionsListener {

	public SetPermissionsListener(String... rolesAndCapabilities) {
		super(rolesAndCapabilities);
	}

	@Override
	protected String getXqueryFunction() {
		return "xdmp:document-set-permissions";
	}
}
