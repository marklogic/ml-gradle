package com.marklogic.appdeployer.scaffold;

import java.io.File;
import java.io.IOException;

import org.springframework.util.FileCopyUtils;

import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.appdeployer.spring.RestApiUtil;
import com.marklogic.clientutil.LoggingObject;

public class ScaffoldGenerator extends LoggingObject {

    private ObjectMapper objectMapper = new ObjectMapper();
    private PrettyPrinter prettyPrinter = new DefaultPrettyPrinter();

    public void generateScaffold(String path) {
        File rootDir = new File(path);

        File configDir = getConfigDir(rootDir);
        configDir.mkdirs();

        File modulesDir = getModulesDir(rootDir);
        modulesDir.mkdirs();

        generateRestApiFile(configDir);
        generateContentDatabaseFile(configDir);
    }

    protected File getConfigDir(File rootDir) {
        return new File(rootDir, "src/main/ml-config");
    }

    protected File getModulesDir(File rootDir) {
        return new File(rootDir, "src/main/ml-modules");
    }

    protected void generateRestApiFile(File configDir) {
        ObjectNode node = RestApiUtil.buildDefaultRestApiJson();
        File f = new File(configDir, "rest-api.json");
        logger.info("Generatign REST API file at: " + f.getAbsolutePath());
        writeFile(node, f);
    }

    protected void generateContentDatabaseFile(File configDir) {
        File databasesDir = new File(configDir, "databases");
        databasesDir.mkdirs();

        ObjectNode node = objectMapper.createObjectNode();
        node.put("database-name", "%%DATABASE%%");
        ArrayNode array = node.putArray("range-element-index");
        ObjectNode index = array.addObject();
        index.put("scalar-type", "string");
        index.put("namespace-uri", "CHANGEME");
        index.put("localname", "CHANGEME");
        index.put("collation", "http://marklogic.com/collation");
        index.put("range-value-positions", false);
        index.put("invalid-values", "reject");

        File f = new File(databasesDir, "content-database.json");
        logger.info("Generating content database file at: " + f.getAbsolutePath());
        writeFile(node, f);
    }

    protected void writeFile(ObjectNode node, File f) {
        try {
            byte[] bytes = objectMapper.writer(prettyPrinter).writeValueAsBytes(node);
            FileCopyUtils.copy(bytes, f);
        } catch (IOException ie) {
            throw new RuntimeException("Unable to write JSON to file at: " + f.getAbsolutePath() + "; cause: "
                    + ie.getMessage(), ie);
        }
    }
}
