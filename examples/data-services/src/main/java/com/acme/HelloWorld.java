package com.acme;

// IMPORTANT: Do not edit. This file is generated.



import com.marklogic.client.DatabaseClient;

import com.marklogic.client.impl.BaseProxy;

/**
 * Provides a set of operations on the database server
 */
public interface HelloWorld {
    /**
     * Creates a HelloWorld object for executing operations on the database server.
     *
     * The DatabaseClientFactory class can create the DatabaseClient parameter. A single
     * client object can be used for any number of requests and in multiple threads.
     *
     * @param db	provides a client for communicating with the database server
     * @return	an object for session state
     */
    static HelloWorld on(DatabaseClient db) {
        final class HelloWorldImpl implements HelloWorld {
            private BaseProxy baseProxy;

            private HelloWorldImpl(DatabaseClient dbClient) {
                baseProxy = new BaseProxy(dbClient, "/helloWorld/");
            }

            @Override
            public String whatsUp(String greeting, Long frequency) {
              return BaseProxy.StringType.toString(
                baseProxy
                .request("helloWorld.sjs", BaseProxy.ParameterValuesKind.MULTIPLE_ATOMICS)
                .withSession()
                .withParams(
                    BaseProxy.atomicParam("greeting", false, BaseProxy.StringType.fromString(greeting)),
                    BaseProxy.atomicParam("frequency", false, BaseProxy.UnsignedLongType.fromLong(frequency)))
                .withMethod("POST")
                .responseSingle(false, null)
                );
            }

        }

        return new HelloWorldImpl(db);
    }

  /**
   * Invokes the whatsUp operation on the database server
   *
   * @param greeting	provides input
   * @param frequency	provides input
   * @return	as output
   */
    String whatsUp(String greeting, Long frequency);

}
