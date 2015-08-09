package com.rjrudin.marklogic.rest.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonNodeUtil {

    public static JsonNode mergeJsonFiles(List<File> files) {
        ObjectMapper m = new ObjectMapper();
        List<JsonNode> nodes = new ArrayList<>();
        Set<String> fieldNames = new HashSet<>();
        for (File f : files) {
            if (f.exists()) {
                JsonNode node = null;
                try {
                    node = m.readTree(f);
                } catch (Exception e) {
                    throw new RuntimeException("Unable to read JSON from file: " + f.getAbsolutePath(), e);
                }
                nodes.add(node);
                Iterator<String> names = node.fieldNames();
                while (names.hasNext()) {
                    fieldNames.add(names.next());
                }
            }
        }

        if (nodes.isEmpty()) {
            return null;
        }

        // Merge each node back into the previous one
        for (int i = 1; i < nodes.size(); i++) {
            ObjectNode target = (ObjectNode) nodes.get(i);
            ObjectNode source = (ObjectNode) nodes.get(i - 1);
            for (String name : fieldNames) {
                JsonNode targetField = target.get(name);
                JsonNode sourceField = source.get(name);
                if (sourceField == null) {
                    continue;
                } else if (targetField == null) {
                    target.set(name, sourceField);
                } else if (sourceField.isArray()) {
                    ArrayNode sourceArray = (ArrayNode) sourceField;
                    ArrayNode targetArray = (ArrayNode) targetField;
                    targetArray.addAll(sourceArray);
                }
            }
        }

        return nodes.get(nodes.size() - 1);
    }

}
