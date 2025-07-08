/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.gradle.task.client.generator;

import java.io.IOException;
import java.util.Arrays;

class SjsTransformGenerator extends TransformGenerator {

	static final String SJS_TEMPLATE =
			"%%REDACT_NAMESPACE%%function transform(context, params, content)\n" +
            "{\n" +
            "  // Must return the result of the transform%%REDACT_COMMAND%%\n" +
            "};\n" +
            "exports.transform = transform;";

	SjsTransformGenerator(String transformsPath, String[] rulesetNames) {
		super(transformsPath, rulesetNames, TransformGeneratorFactory.TransformType.SJS.toString().toLowerCase());
	}

	public void generate(String name) throws IOException {
		String transform;
		if (rulesetNames.length > 0) {
			transform = SJS_TEMPLATE.replace("%%REDACT_COMMAND%%", "\n  return rdt.redact(content, " + Arrays.toString(rulesetNames) + ");");
			transform = transform.replace("%%REDACT_NAMESPACE%%", "const rdt = require('/MarkLogic/redaction');\n");
		} else {
			transform = SJS_TEMPLATE.replace("%%REDACT_COMMAND%%", "\n  return content");
			transform = transform.replace("%%REDACT_NAMESPACE%%", "");
		}
		generateTransformFile(name, transform);
		generateMetadataFile(name);
	}
}
