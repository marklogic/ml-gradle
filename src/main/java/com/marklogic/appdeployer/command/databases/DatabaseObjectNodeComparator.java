package com.marklogic.appdeployer.command.databases;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.mgmt.api.database.Database;

import java.io.IOException;
import java.util.Comparator;

public class DatabaseObjectNodeComparator implements Comparator<ObjectNode> {

	private ObjectReader objectReader;
	private boolean reverseOrder;

	public DatabaseObjectNodeComparator(ObjectMapper objectMapper) {
		this(objectMapper, false);
	}

	public DatabaseObjectNodeComparator(ObjectMapper objectMapper, boolean reverseOrder) {
		this.objectReader = objectMapper.readerFor(Database.class);
		this.reverseOrder = reverseOrder;
	}

	@Override
	public int compare(ObjectNode o1, ObjectNode o2) {
		Database db1, db2;

		try {
			db1 = objectReader.readValue(o1);
			db2 = objectReader.readValue(o2);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		int result = db1.compareTo(db2);
		if (result == 0) {
			return 0;
		}
		if (reverseOrder) {
			return result == 1 ? -1 : 1;
		}
		return result;
	}
}
