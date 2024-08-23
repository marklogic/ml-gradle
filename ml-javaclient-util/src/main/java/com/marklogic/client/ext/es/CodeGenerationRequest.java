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
package com.marklogic.client.ext.es;

public class CodeGenerationRequest {

	private boolean generateDatabaseProperties = true;
	private boolean generateExtractionTemplate = true;
	private boolean generateInstanceConverter = true;
	private boolean generateSchema = true;
	private boolean generateSearchOptions = true;
	private boolean generateVersionTranslator = true;

	public boolean isGenerateDatabaseProperties() {
		return generateDatabaseProperties;
	}

	public void setGenerateDatabaseProperties(boolean generateDatabaseProperties) {
		this.generateDatabaseProperties = generateDatabaseProperties;
	}

	public boolean isGenerateExtractionTemplate() {
		return generateExtractionTemplate;
	}

	public void setGenerateExtractionTemplate(boolean generateExtractionTemplate) {
		this.generateExtractionTemplate = generateExtractionTemplate;
	}

	public boolean isGenerateInstanceConverter() {
		return generateInstanceConverter;
	}

	public void setGenerateInstanceConverter(boolean generateInstanceConverter) {
		this.generateInstanceConverter = generateInstanceConverter;
	}

	public boolean isGenerateSchema() {
		return generateSchema;
	}

	public void setGenerateSchema(boolean generateSchema) {
		this.generateSchema = generateSchema;
	}

	public boolean isGenerateSearchOptions() {
		return generateSearchOptions;
	}

	public void setGenerateSearchOptions(boolean generateSearchOptions) {
		this.generateSearchOptions = generateSearchOptions;
	}

	public boolean isGenerateVersionTranslator() {
		return generateVersionTranslator;
	}

	public void setGenerateVersionTranslator(boolean generateVersionTranslator) {
		this.generateVersionTranslator = generateVersionTranslator;
	}
}
