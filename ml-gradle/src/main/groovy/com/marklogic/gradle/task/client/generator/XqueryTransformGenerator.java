package com.marklogic.gradle.task.client.generator;

import java.io.IOException;

class XqueryTransformGenerator extends TransformGenerator {

	static final String XQUERY_TEMPLATE =
			"xquery version \"1.0-ml\";\n\n" +
            "module namespace transform = \"http://marklogic.com/rest-api/transform/%%TRANSFORM_NAME%%\";\n" +
            "%%REDACT_NAMESPACE%%\n" +
            "declare function transform(\n" +
			"  $context as map:map,\n" +
			"  $params as map:map,\n" +
			"  $content as document-node()\n" +
			") as document-node() {\n" +
            "  %%REDACT_COMMAND%%\n" +
            "};";

	XqueryTransformGenerator(String transformsPath, String[] rulesetNames) {
		super(transformsPath, rulesetNames, TransformGeneratorFactory.TransformType.XQY.toString().toLowerCase());
	}

	public void generate(String name) throws IOException {
		String transform = XQUERY_TEMPLATE.replace("%%TRANSFORM_NAME%%", name);
		if (rulesetNames.length > 0) {
			String rulesetNameList = "(" + String.join(",", rulesetNames) + ")";
			transform = transform.replace("%%REDACT_COMMAND%%", "rdt:redact($content, " + rulesetNameList + ")");
			transform = transform.replace("%%REDACT_NAMESPACE%%", "\nimport module namespace rdt = \"http://marklogic.com/xdmp/redaction\" at \"/MarkLogic/redaction.xqy\";\n");
		} else {
			transform = transform.replace("%%REDACT_COMMAND%%", "()");
			transform = transform.replace("%%REDACT_NAMESPACE%%", "");
		}
		generateTransformFile(name, transform);
		generateMetadataFile(name);
	}
}
