package com.marklogic.client.ext.datamovement.job;

import java.util.function.Consumer;

public interface JobProperty {

	String getPropertyName();

	String getPropertyDescription();

	Consumer<String> getPropertyValueConsumer();

	boolean isRequired();
}
