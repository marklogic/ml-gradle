package com.marklogic.client.ext.datamovement.listener;

public class AddCollectionsListener extends AbstractCollectionsListener {

	public AddCollectionsListener(String... collections) {
		super(collections);
	}

	@Override
	protected String getXqueryFunction() {
		return "xdmp:document-add-collections";
	}
}
