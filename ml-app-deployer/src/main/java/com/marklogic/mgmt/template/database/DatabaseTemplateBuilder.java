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
package com.marklogic.mgmt.template.database;

import com.marklogic.mgmt.api.database.Database;
import com.marklogic.mgmt.api.database.ElementIndex;
import com.marklogic.mgmt.template.GenericTemplateBuilder;

import java.util.ArrayList;
import java.util.List;

public class DatabaseTemplateBuilder extends GenericTemplateBuilder {

	public DatabaseTemplateBuilder() {
		this("CHANGEME-name-of-database");
	}

	public DatabaseTemplateBuilder(String databaseName) {
		super(Database.class);
		addDefaultPropertyValue("database-name", databaseName);
		addDefaultPropertyValue("enabled", "true");
		addDefaultPropertyValue("wordSearches", "true");

		ElementIndex index = new ElementIndex();
		index.setLocalname("CHANGEME-name-of-element");
		index.setNamespaceUri("CHANGEME-namespace-of-element");
		index.setScalarType("string");
		index.setCollation("http://marklogic.com/collation/");
		index.setRangeValuePositions(false);
		List<ElementIndex> list = new ArrayList<>();
		list.add(index);
		addDefaultPropertyValue("range-element-index", list);
	}
}
