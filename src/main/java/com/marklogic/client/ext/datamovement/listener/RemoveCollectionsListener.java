package com.marklogic.client.ext.datamovement.listener;

public class RemoveCollectionsListener extends AbstractCollectionsListener {

	public RemoveCollectionsListener(String... collections) {
		super(collections);
	}

	@Override
	protected String getXqueryFunction() {
		return "xdmp:document-remove-collections";
	}
}
