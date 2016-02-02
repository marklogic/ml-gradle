package com.marklogic.client.schemasloader.impl;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.TextDocumentManager;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.helper.LoggingObject;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.schemasloader.SchemasFinder;
import com.marklogic.client.schemasloader.SchemasLoader;

public class DefaultSchemasLoader extends LoggingObject implements SchemasLoader {
	
	@Override
	public Set<File> loadSchemas(File baseDir, SchemasFinder schemasDataFinder, DatabaseClient client) {
		XMLDocumentManager xmlDocMgr = client.newXMLDocumentManager();
		TextDocumentManager textDocMgr = client.newTextDocumentManager();
		
		List<File> schemasData = schemasDataFinder.findSchemas(baseDir);

        Set<File> loadedSchemas = new HashSet<>();

        for (File f : schemasData) {
        	String extension = getExtensionNameFromFile(f);
        	FileHandle handle = new FileHandle(f);
        	if (extension.equals("tde") || extension.equals("xsd")) {
        		xmlDocMgr.write(f.getName(), handle.withFormat(Format.XML));
        	} else {
        		textDocMgr.write(f.getName(), handle.withFormat(Format.TEXT));
        	}
        	loadedSchemas.add(f);
        }

        return loadedSchemas;
	}
	 protected String getExtensionNameFromFile(File file) {
	        String name = file.getName();
	        int pos = name.lastIndexOf('.');
	        if (pos < 0)
	            return name;
	        return name.substring(0, pos);
	    }
}
