package com.marklogic.mgmt.template.database;

import com.marklogic.mgmt.api.database.Database;
import com.marklogic.mgmt.api.database.ElementIndex;
import com.marklogic.mgmt.template.GenericTemplateBuilder;

import java.util.ArrayList;
import java.util.List;

public class DatabaseTemplateBuilder extends GenericTemplateBuilder {

	public DatabaseTemplateBuilder() {
		super(Database.class);
		addDefaultPropertyValue("database-name", "CHANGEME-name-of-database");
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
