package com.marklogic.appdeployer.spring;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public abstract class RestApiUtil {

    public static String buildDefaultRestApiJson() {
        ObjectMapper m = new ObjectMapper();
        ObjectNode node = m.createObjectNode();
        ObjectNode n = node.putObject("rest-api");
        n.put("name", "%%NAME%%");
        n.put("group", "%%GROUP%%");
        n.put("database", "%%DATABASE%%");
        n.put("modules-database", "%%MODULES_DATABASE%%");
        n.put("port", "%%PORT%%");
        n.put("xdbc-enabled", true);
        n.put("forests-per-host", 3);
        n.put("error-format", "json");

        try {
            String json = m.writer(new DefaultPrettyPrinter()).writeValueAsString(node);
            json = json.replace("\"%%PORT%%\"", "%%PORT%%");
            return json;
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
    }
}
