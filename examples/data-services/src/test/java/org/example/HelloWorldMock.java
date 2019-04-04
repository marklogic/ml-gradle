package org.example;

public class HelloWorldMock implements HelloWorld {

	@Override
	public String whatsUp(String greeting, Long frequency) {
		return "This is a mock response";
	}

}
