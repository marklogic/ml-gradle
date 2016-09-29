package com.marklogic.client.qconsole.impl;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;

import java.io.IOException;

/**
 * Debug-style program for manually verifying how DefaultWorkspaceManager works.
 * <p>
 * If the content source ID (an appserver ID) is not valid, that's fine - the content source just defaults
 * to the first app server.
 * <p>
 * If the user doesn't exist, that's fine - it just won't be accessible.
 */
public class DefaultWorkspaceManagerDebug {

    public static void main(String[] args) throws IOException {
        DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8000, "App-Services", "admin", "admin", DatabaseClientFactory.Authentication.DIGEST);
        DefaultWorkspaceManager dwm = new DefaultWorkspaceManager(client);
        String user = "admin";
        try {
            System.out.println(dwm.exportWorkspaces(user, "Workspace", "Workspace 1"));
            //System.out.println(dwm.importWorkspaces(user));
        } finally {
            client.release();
        }
    }

}
