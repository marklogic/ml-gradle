/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package org.example;

public class HelloWorldMock implements HelloWorld {

	@Override
	public String whatsUp(String greeting, Long frequency) {
		return "This is a mock response";
	}

}
