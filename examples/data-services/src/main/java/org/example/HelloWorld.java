package org.example;

// IMPORTANT: Do not edit. This file is generated.



import com.marklogic.client.DatabaseClient;
import com.marklogic.client.io.marker.JSONWriteHandle;

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
     * @return	an object for executing database operations
     */
    static HelloWorld on(DatabaseClient db) {
      return on(db, null);
    }
    /**
     * Creates a HelloWorld object for executing operations on the database server.
     *
     * The DatabaseClientFactory class can create the DatabaseClient parameter. A single
     * client object can be used for any number of requests and in multiple threads.
     *
     * The service declaration uses a custom implementation of the same service instead
     * of the default implementation of the service by specifying an endpoint directory
     * in the modules database with the implementation. A service.json file with the
     * declaration can be read with FileHandle or a string serialization of the JSON
     * declaration with StringHandle.
     *
     * @param db	provides a client for communicating with the database server
     * @param serviceDeclaration	substitutes a custom implementation of the service
     * @return	an object for executing database operations
     */
    static HelloWorld on(DatabaseClient db, JSONWriteHandle serviceDeclaration) {
        final class HelloWorldImpl implements HelloWorld {
            private DatabaseClient dbClient;
            private BaseProxy baseProxy;

            private BaseProxy.DBFunctionRequest req_whatsUp;

            private HelloWorldImpl(DatabaseClient dbClient, JSONWriteHandle servDecl) {
                this.dbClient  = dbClient;
                this.baseProxy = new BaseProxy("/helloWorld/", servDecl);

                this.req_whatsUp = this.baseProxy.request(
                    "whatsUp.sjs", BaseProxy.ParameterValuesKind.MULTIPLE_ATOMICS);
            }

            @Override
            public String whatsUp(String greeting, Long frequency) {
                return whatsUp(
                    this.req_whatsUp.on(this.dbClient), greeting, frequency
                    );
            }
            private String whatsUp(BaseProxy.DBFunctionRequest request, String greeting, Long frequency) {
              return BaseProxy.StringType.toString(
                request
                      .withParams(
                          BaseProxy.atomicParam("greeting", false, BaseProxy.StringType.fromString(greeting)),
                          BaseProxy.atomicParam("frequency", false, BaseProxy.UnsignedLongType.fromLong(frequency))
                          ).responseSingle(false, null)
                );
            }
        }

        return new HelloWorldImpl(db, serviceDeclaration);
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
