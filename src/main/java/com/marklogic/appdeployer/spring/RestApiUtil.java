package com.marklogic.appdeployer.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public abstract class RestApiUtil {

    public static ObjectNode buildDefaultRestApiJson() {
        ObjectNode node = new ObjectMapper().createObjectNode();
        ObjectNode n = node.putObject("rest-api");
        n.put("name", "%%NAME%%");
        n.put("group", "%%GROUP%%");
        n.put("database", "%%DATABASE%%");
        n.put("modules-database", "%%MODULES_DATABASE%%");
        n.put("port", "%%PORT%%");
        n.put("xdbc-enabled", true);
        n.put("forests-per-host", 3);
        n.put("error-format", "json");
        return n;
    }
}
