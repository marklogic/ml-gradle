package com.marklogic.client.schemasloader.impl;

import java.io.File;
import java.util.Set;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.schemasloader.SchemasLoader;

/**
 * Program for manual testing.
 */
public class LoadSchemasDebug {

    public static void main(String[] args) {
        SchemasLoader l = new DefaultSchemasLoader();

        DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8000, "admin", "admin",
                Authentication.DIGEST);

        try {
            Set<File> files = l.loadSchemas(new File("src/test/resources/sample-base-dir/schemas"), new DefaultSchemasFinder(),
                    client);
            System.out.println(files);
        } finally {
            client.release();
        }
    }
}
