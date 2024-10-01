package com.marklogic.gradle.task.client.generator;

import java.io.IOException;

class XslTransformGenerator extends TransformGenerator {

	static final String XSL_TEMPLATE =
		"<xsl:stylesheet version=\"2.0\"\n" +
		"        xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"\n" +
		"        xmlns:map=\"http://marklogic.com/xdmp/map\"%%REDACT_NAMESPACE%%\n" +
		"    >\n" +
  		"  <xsl:param name=\"context\" as=\"map:map\"/>\n" +
        "  <xsl:param name=\"params\"  as=\"map:map\"/>%%REDACT_IMPORT%%\n" +
  	    "  <xsl:template match=\"/\">" +
        "  %%REDACT_COMMAND%%\n" +
        "  </xsl:template>\n" +
        "</xsl:stylesheet>";

	static final String XSL_DECLARATION =
		"\n" +
		"        xmlns:xdmp=\"http://marklogic.com/xdmp\"\n" +
	    "        xmlns:rdt=\"http://marklogic.com/xdmp/redaction\"\n" +
		"        extension-element-prefixes=\"xdmp\"";

	XslTransformGenerator(String transformsPath, String[] rulesetNames) {
		super(transformsPath, rulesetNames, TransformGeneratorFactory.TransformType.XSL.toString().toLowerCase());
	}

	public void generate(String name) throws IOException {
		String transform;
		if (rulesetNames.length > 0) {
			transform = XSL_TEMPLATE.replace("%%REDACT_NAMESPACE%%", XSL_DECLARATION);
			transform = transform.replace("%%REDACT_IMPORT%%", "\n  <xdmp:import-module namespace=\"http://marklogic.com/xdmp/redaction\" href=\"/MarkLogic/redaction.xqy\"/>");
			String rulesetNameList = "(" + String.join(",", rulesetNames) + ")";
			transform = transform.replace("%%REDACT_COMMAND%%", "\n    <xsl:copy-of select=\"rdt:redact(., " + rulesetNameList + ")\"/>");
		} else {
			transform = XSL_TEMPLATE.replace("%%REDACT_NAMESPACE%%", "");
			transform = transform.replace("%%REDACT_IMPORT%%", "");
			transform = transform.replace("%%REDACT_COMMAND%%", "\n    <xsl:copy-of select=\".\"/>");
		}
		generateTransformFile(name, transform);
		generateMetadataFile(name);
	}
}
