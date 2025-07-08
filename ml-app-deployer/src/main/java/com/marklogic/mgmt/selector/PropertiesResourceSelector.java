/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt.selector;

import com.marklogic.mgmt.ManageClient;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class PropertiesResourceSelector implements ResourceSelector {

	private Properties props;

	public PropertiesResourceSelector(File file) {
		props = new Properties();
		try {
			FileReader reader = new FileReader(file);
			props.load(reader);
			reader.close();
		} catch (IOException ie) {
			throw new RuntimeException(ie);
		}
	}

	public PropertiesResourceSelector(Properties props) {
		this.props = props;
	}

	@Override
	public ResourceSelection selectResources(ManageClient manageClient) {
		MapResourceSelection selection = new MapResourceSelection();

		for (String prop : props.stringPropertyNames()) {
			String[] values = props.getProperty(prop).split(",");
			for (String value : values) {
				selection.select(prop, value);
			}
		}

		return selection;
	}
}
