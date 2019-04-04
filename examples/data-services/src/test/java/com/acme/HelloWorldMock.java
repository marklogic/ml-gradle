package com.acme;

import com.marklogic.client.DatabaseClient;

public class HelloWorldMock implements HelloWorld {

  static HelloWorld on(final DatabaseClient client) {
    return new HelloWorldMock();
  }

  @Override
  public String whatsUp(String greeting, Long frequency) {
    return "Hey! Hey!";
  }

}
