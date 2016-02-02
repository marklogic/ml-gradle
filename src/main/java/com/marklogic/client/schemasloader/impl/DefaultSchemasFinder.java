package com.marklogic.client.schemasloader.impl;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import com.marklogic.client.schemasloader.SchemasFinder;

/**
 * Finds all files in a directory.
 * 
 */
public class DefaultSchemasFinder implements SchemasFinder {

	private FileFilter schemasFileFilter = new FileFilter() {
		public boolean accept(File f) { 
			 return f != null && !f.getName().matches(".(xsd|tde|rules)$");
		}
	};
	
	public FileFilter getSchemasFileFilter() {
		return schemasFileFilter;
	}

	public void setSchemasFileFilter(FileFilter schemasFileFilter) {
		this.schemasFileFilter = schemasFileFilter;
	}

	@Override
	public List<File> findSchemas(File schemasDir) {
		List<File> schemasDataFiles = new ArrayList<>();
		if (schemasDir.exists()) {
			for (File f : schemasDir.listFiles(schemasFileFilter)) {
				schemasDataFiles.add(f);
			}
		}
		return schemasDataFiles;
	}
}
