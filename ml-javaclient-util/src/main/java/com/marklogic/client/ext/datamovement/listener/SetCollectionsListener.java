/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.ext.datamovement.listener;

public class SetCollectionsListener extends AbstractCollectionsListener {

	public SetCollectionsListener(String... collections) {
		super(collections);
	}

	@Override
	protected String getXqueryFunction() {
		return "xdmp:document-set-collections";
	}
}
