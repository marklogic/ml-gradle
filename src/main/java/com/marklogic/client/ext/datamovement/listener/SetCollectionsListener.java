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
