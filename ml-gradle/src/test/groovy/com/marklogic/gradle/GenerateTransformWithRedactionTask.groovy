/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.gradle

class GenerateTransformWithRedactionTask extends BaseTest {

	final static String EXPECTED_XQY_REDACTION_IMPORT = "import module namespace rdt = \"http://marklogic.com/xdmp/redaction\" at \"/MarkLogic/redaction.xqy\";";
	final static String EXPECTED_XQY_REDACTION_CODE = "  rdt:redact(\$content, ('email-rules','otherRules'))"

	def "generate an XQuery transform that includes a redaction rule"() {
		when:
		print(runTask('mlCreateTransform',  '-PtransformName=xqyRedactEmails', '-PtransformType=xqy', '-Prulesets=email-rules,otherRules').output)

		then:
		def transformPath = new File(testProjectDir.getRoot(), "src/main/ml-modules/transforms/xqyRedactEmails.xqy")
		transformPath.exists()
		def transformLines = transformPath.readLines()
		transformLines.each(line -> println(line))
		transformLines.contains(EXPECTED_XQY_REDACTION_IMPORT)
		transformLines.contains(EXPECTED_XQY_REDACTION_CODE)

		when:
		print(runTask('mlCreateTransform',  '-PtransformName=xqyRedactEmails', '-PtransformType=xqy').output)
		transformPath = new File(testProjectDir.getRoot(), "src/main/ml-modules/transforms/xqyRedactEmails.xqy")
		transformLines = transformPath.readLines()

		then:
		!transformLines.contains(EXPECTED_XQY_REDACTION_IMPORT)
		!transformLines.contains(EXPECTED_XQY_REDACTION_CODE)
	}

	final static String EXPECTED_SJS_REDACTION_IMPORT = "const rdt = require('/MarkLogic/redaction');";
	final static String EXPECTED_SJS_REDACTION_CODE = "  return rdt.redact(content, ['email-rules', 'otherRules']);"

	def "generate an SJS transform that includes a redaction rule"() {
		when:
		print(runTask('mlCreateTransform',  '-PtransformName=sjsRedactEmails', '-PtransformType=sjs', '-Prulesets=email-rules,otherRules').output)

		then:
		def transformPath = new File(testProjectDir.getRoot(), "src/main/ml-modules/transforms/sjsRedactEmails.sjs")
		transformPath.exists()
		def transformLines = transformPath.readLines()
		transformLines.each(line -> println(line))
		transformLines.contains(EXPECTED_SJS_REDACTION_IMPORT)
		transformLines.contains(EXPECTED_SJS_REDACTION_CODE)

		when:
		print(runTask('mlCreateTransform',  '-PtransformName=sjsRedactEmails', '-PtransformType=sjs').output)
		transformPath = new File(testProjectDir.getRoot(), "src/main/ml-modules/transforms/sjsRedactEmails.sjs")
		transformLines = transformPath.readLines()

		then:
		!transformLines.contains(EXPECTED_SJS_REDACTION_IMPORT)
		!transformLines.contains(EXPECTED_SJS_REDACTION_CODE)
	}

	final static String EXPECTED_XSL_REDACTION_NAMESPACE = "        xmlns:rdt=\"http://marklogic.com/xdmp/redaction\""
	final static String EXPECTED_XSL_REDACTION_IMPORT = "  <xdmp:import-module namespace=\"http://marklogic.com/xdmp/redaction\" href=\"/MarkLogic/redaction.xqy\"/>";
	final static String EXPECTED_XSL_REDACTION_CODE = "    <xsl:copy-of select=\"rdt:redact(., ('email-rules','otherRules'))\"/>"

	def "generate an XSL transform that includes a redaction rule"() {
		when:
		print(runTask('mlCreateTransform',  '-PtransformName=xslRedactEmails', '-PtransformType=xsl', '-Prulesets=email-rules,otherRules').output)

		then:
		def transformPath = new File(testProjectDir.getRoot(), "src/main/ml-modules/transforms/xslRedactEmails.xsl")
		transformPath.exists()
		def transformLines = transformPath.readLines()
		transformLines.each(line -> println(line))
		transformLines.contains(EXPECTED_XSL_REDACTION_NAMESPACE)
		transformLines.contains(EXPECTED_XSL_REDACTION_IMPORT)
		transformLines.contains(EXPECTED_XSL_REDACTION_CODE)

		when:
		print(runTask('mlCreateTransform',  '-PtransformName=xslRedactEmails', '-PtransformType=xsl').output)
		transformPath = new File(testProjectDir.getRoot(), "src/main/ml-modules/transforms/xslRedactEmails.xsl")
		transformLines = transformPath.readLines()

		then:
		!transformLines.contains(EXPECTED_XSL_REDACTION_NAMESPACE)
		!transformLines.contains(EXPECTED_XSL_REDACTION_IMPORT)
		!transformLines.contains(EXPECTED_XSL_REDACTION_CODE)
	}

}
