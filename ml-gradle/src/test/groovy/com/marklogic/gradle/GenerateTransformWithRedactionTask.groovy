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

import groovy.json.JsonSlurper
import org.springframework.http.HttpMethod

class GenerateTransformWithRedactionTask extends BaseTest {

	final static JsonSlurper parser = new JsonSlurper()
	final static String BASE_DOCUMENTS_URL = "http://localhost:8028/v1/documents?uri=/jane.json&transform="

	def reloadTransforms() {
		print(runTask('mlReloadModules', '-PmlAppName=ml-javaclient-util-test', '-PmlUsername=admin', '-PmlPassword=admin', '-PmlRestPort=8028').output)
	}

	def retrieveDocumentWithTransform(String transformName) {
		def url = BASE_DOCUMENTS_URL + transformName
		def response = manageClient.getRestTemplate().exchange(url, HttpMethod.GET, null, String.class, [:])
		printf response.getBody()
		return parser.parseText(response.getBody())
	}

	def verifyEmailRedacted(String transformName) {
		def docContents = retrieveDocumentWithTransform(transformName)
		"NAME@DOMAIN".equals(docContents['email'])
	}

	def verifyEmailNotRedacted(String transformName) {
		def docContents = retrieveDocumentWithTransform(transformName)
		"jane@example.org".equals(docContents['email'])
	}

	final static String EXPECTED_XQY_REDACTION_IMPORT = "import module namespace rdt = \"http://marklogic.com/xdmp/redaction\" at \"/MarkLogic/redaction.xqy\";"
	final static String EXPECTED_XQY_REDACTION_CODE = "  rdt:redact(\$content, ('email-rules'))"

	def "generate an XQuery transform that includes a redaction rule"() {
		when:
		print(runTask('mlCreateTransform', '-PtransformName=xqyRedactEmails', '-PtransformType=xqy', '-Prulesets=email-rules').output)
		def transformPath = new File(testProjectDir.getRoot(), "src/main/ml-modules/transforms/xqyRedactEmails.xqy")
		def transformLines = transformPath.readLines()

		then:
		transformLines.contains(EXPECTED_XQY_REDACTION_IMPORT)
		transformLines.contains(EXPECTED_XQY_REDACTION_CODE)

		when:
		reloadTransforms()

		then:
		verifyEmailRedacted("xqyRedactEmails")
	}

	def "generate an XQuery transform that does not include a redaction rule"() {
		when:
		print(runTask('mlCreateTransform',  '-PtransformName=xqyRedactEmails', '-PtransformType=xqy').output)
		def transformPath = new File(testProjectDir.getRoot(), "src/main/ml-modules/transforms/xqyRedactEmails.xqy")
		def transformLines = transformPath.readLines()

		then:
		!transformLines.contains(EXPECTED_XQY_REDACTION_IMPORT)
		!transformLines.contains(EXPECTED_XQY_REDACTION_CODE)

		when:
		reloadTransforms()

		then:
		verifyEmailNotRedacted("xqyRedactEmails")
	}

	final static String EXPECTED_SJS_REDACTION_IMPORT = "const rdt = require('/MarkLogic/redaction');"
	final static String EXPECTED_SJS_REDACTION_CODE = "  return rdt.redact(content, ['email-rules']);"

	def "generate an SJS transform that includes a redaction rule"() {
		when:
		print(runTask('mlCreateTransform',  '-PtransformName=sjsRedactEmails', '-PtransformType=sjs', '-Prulesets=email-rules').output)
		def transformPath = new File(testProjectDir.getRoot(), "src/main/ml-modules/transforms/sjsRedactEmails.sjs")
		def transformLines = transformPath.readLines()

		then:
		transformLines.contains(EXPECTED_SJS_REDACTION_IMPORT)
		transformLines.contains(EXPECTED_SJS_REDACTION_CODE)

		when:
		reloadTransforms()

		then:
		verifyEmailRedacted("sjsRedactEmails")
	}

	def "generate an SJS transform that does not include a redaction rule"() {
		when:
		print(runTask('mlCreateTransform',  '-PtransformName=sjsRedactEmails', '-PtransformType=sjs').output)
		def transformPath = new File(testProjectDir.getRoot(), "src/main/ml-modules/transforms/sjsRedactEmails.sjs")
		def transformLines = transformPath.readLines()

		then:
		!transformLines.contains(EXPECTED_SJS_REDACTION_IMPORT)
		!transformLines.contains(EXPECTED_SJS_REDACTION_CODE)

		when:
		reloadTransforms()

		then:
		verifyEmailNotRedacted("sjsRedactEmails")
	}

	final static String EXPECTED_XSL_REDACTION_NAMESPACE = "        xmlns:rdt=\"http://marklogic.com/xdmp/redaction\""
	final static String EXPECTED_XSL_REDACTION_IMPORT = "  <xdmp:import-module namespace=\"http://marklogic.com/xdmp/redaction\" href=\"/MarkLogic/redaction.xqy\"/>"
	final static String EXPECTED_XSL_REDACTION_CODE = "    <xsl:copy-of select=\"rdt:redact(., ('email-rules'))\"/>"

	def "generate an XSL transform that includes a redaction rule"() {
		when:
		print(runTask('mlCreateTransform',  '-PtransformName=xslRedactEmails', '-PtransformType=xsl', '-Prulesets=email-rules').output)
		def transformPath = new File(testProjectDir.getRoot(), "src/main/ml-modules/transforms/xslRedactEmails.xsl")
		def transformLines = transformPath.readLines()

		then:
		transformLines.contains(EXPECTED_XSL_REDACTION_NAMESPACE)
		transformLines.contains(EXPECTED_XSL_REDACTION_IMPORT)
		transformLines.contains(EXPECTED_XSL_REDACTION_CODE)

		when:
		reloadTransforms()

		then:
		verifyEmailRedacted("xslRedactEmails")
	}

	def "generate an XSL transform that does not include a redaction rule"() {
		when:
		print(runTask('mlCreateTransform',  '-PtransformName=xslRedactEmails', '-PtransformType=xsl').output)
		def transformPath = new File(testProjectDir.getRoot(), "src/main/ml-modules/transforms/xslRedactEmails.xsl")
		def transformLines = transformPath.readLines()

		then:
		!transformLines.contains(EXPECTED_XSL_REDACTION_NAMESPACE)
		!transformLines.contains(EXPECTED_XSL_REDACTION_IMPORT)
		!transformLines.contains(EXPECTED_XSL_REDACTION_CODE)

		when:
		reloadTransforms()

		then:
		verifyEmailNotRedacted("xslRedactEmails")
	}

}
