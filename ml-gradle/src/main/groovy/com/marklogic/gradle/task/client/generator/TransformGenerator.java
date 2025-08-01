/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.gradle.task.client.generator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public abstract class TransformGenerator {

	static final String METADATA_TEMPLATE =
			"<metadata>\n" +
            "  <title>%%TRANSFORM_NAME%%</title>\n" +
            "  <description>\n" +
            "    <div>\n" +
            "      Use HTML content to provide a description of this template.\n" +
            "    </div>\n" +
            "  </description>\n" +
            "</metadata>";

	private final String extension;
	protected final String transformsPath;
	protected final String[] rulesetNames;

	TransformGenerator(String transformsPath, String[] rulesetNames, String extension) {
		this.transformsPath = transformsPath;
		this.rulesetNames = rulesetNames;
		this.extension = extension;
	}

	public abstract void generate(String name) throws IOException;

	protected void generateTransformFile(String name, String transform) throws IOException {
		new File(transformsPath).mkdirs();
		File transformFile = new File(transformsPath, name + "." + extension);
		System.out.println("Creating new transform at " + transformFile);
		if (rulesetNames.length > 0) {
			System.out.println("Using rulesets: " + Arrays.toString(rulesetNames));
		}
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(transformFile))) {
			writer.write(transform);
		}
	}

	protected void generateMetadataFile(String name) throws IOException {
		File metadataDir = new File(transformsPath, "metadata");
		metadataDir.mkdirs();
		String metadata = METADATA_TEMPLATE.replace("%%TRANSFORM_NAME%%", name);
		File metadataFile = new File(metadataDir, name + ".xml");
		System.out.println("Creating new transform metadata file at " + metadataFile.getAbsolutePath());
		try (BufferedWriter metadataWriter = new BufferedWriter(new FileWriter(metadataFile))) {
			metadataWriter.write(metadata);
		}
	}

}
