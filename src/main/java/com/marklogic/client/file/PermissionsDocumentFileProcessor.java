package com.marklogic.client.file;

import com.marklogic.client.modulesloader.impl.DefaultDocumentPermissionsParser;
import com.marklogic.client.modulesloader.impl.DocumentPermissionsParser;

import java.util.Properties;

/**
 * Looks for a special file in each directory - defaults to permissions.properties - that contains properties where the
 * key is the name of a file in the directory, and the value is a comma-delimited list of role,capability,role,capability,etc.
 */
public class PermissionsDocumentFileProcessor extends PropertiesDrivenDocumentFileProcessor {

	private DocumentPermissionsParser documentPermissionsParser;

	public PermissionsDocumentFileProcessor() {
		this("permissions.properties");
	}

	public PermissionsDocumentFileProcessor(String propertiesFilename) {
		this(propertiesFilename, new DefaultDocumentPermissionsParser());
	}

	public PermissionsDocumentFileProcessor(String propertiesFilename, DocumentPermissionsParser documentPermissionsParser) {
		super(propertiesFilename);
		this.documentPermissionsParser = documentPermissionsParser;
	}

	@Override
	protected void processProperties(DocumentFile documentFile, Properties properties) {
		String name = documentFile.getFile().getName();
		if (properties.containsKey(name)) {
			String value = properties.getProperty(name);
			documentPermissionsParser.parsePermissions(value, documentFile.getDocumentMetadata().getPermissions());
		}
	}

	public void setDocumentPermissionsParser(DocumentPermissionsParser documentPermissionsParser) {
		this.documentPermissionsParser = documentPermissionsParser;
	}
}