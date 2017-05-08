package com.marklogic.mgmt.selector;

import com.marklogic.mgmt.ManageClient;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class PropertiesFileResourceSelector implements ResourceSelector {

	private File file;

	public PropertiesFileResourceSelector(File file) {
		this.file = file;
	}

	@Override
	public ResourceSelection selectResources(ManageClient manageClient) {
		Properties props = new Properties();
		try {
			FileReader reader = new FileReader(file);
			props.load(reader);
			reader.close();
		} catch (IOException ie) {
			throw new RuntimeException(ie);
		}

		MapResourceSelection selection = new MapResourceSelection();

		for (String prop : props.stringPropertyNames()) {
			String[] names = props.getProperty(prop).split(",");
			for (String name : names) {
				selection.select(prop, name);
			}
		}

		return selection;
	}
}
