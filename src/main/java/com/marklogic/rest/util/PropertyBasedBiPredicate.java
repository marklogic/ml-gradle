package com.marklogic.rest.util;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.appdeployer.command.ResourceReference;

import java.util.function.BiPredicate;

public class PropertyBasedBiPredicate implements BiPredicate<ResourceReference, ResourceReference> {

	private String propertyName;

	public PropertyBasedBiPredicate(String propertyName) {
		this.propertyName = propertyName;
	}

	@Override
	public boolean test(ResourceReference reference1, ResourceReference reference2) {
		final ObjectNode node1 = reference1.getObjectNode();
		final ObjectNode node2 = reference2.getObjectNode();
		final String value1 = node1.has(propertyName) ? node1.get(propertyName).asText() : null;
		if (value1 == null) return false;
		final String value2 = node2.has(propertyName) ? node2.get(propertyName).asText() : null;
		return value1.equals(value2);
	}
}
