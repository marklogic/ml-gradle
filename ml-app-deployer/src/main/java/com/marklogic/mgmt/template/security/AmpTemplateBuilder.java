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
package com.marklogic.mgmt.template.security;

import com.marklogic.mgmt.api.security.Amp;
import com.marklogic.mgmt.template.GenericTemplateBuilder;

import java.util.Arrays;

public class AmpTemplateBuilder extends GenericTemplateBuilder {

	public AmpTemplateBuilder() {
		super(Amp.class);
		addDefaultPropertyValue("local-name", "CHANGEME-name-of-the-function-to-amp");
		addDefaultPropertyValue("namespace", "CHANGEME-namespace-of-the-module");
		addDefaultPropertyValue("document-uri", "CHANGEME-module-path");
		addDefaultPropertyValue("modules-database", "Modules");
		addDefaultPropertyValue("role", Arrays.asList("rest-reader", "rest-writer"));
	}
}
