package com.marklogic.gradle.task.client;

import org.junit.Assert;
import org.junit.Test;

public class LoadAssetsViaMlcpTaskTest extends Assert {

    @Test
    public void generateOutputUriReplace() {
        String assetsPath = "build\\ml-gradle\\consolidatedAssets";

        String output = LoadAssetsViaMlcpTask.generateOutputUriReplace(assetsPath);
        // System.out.println(output);
        assertTrue("The replace string must start and end with double quotes",
                output.startsWith("\"") && output.endsWith("\""));
        assertTrue(
                "The replace string must have Windows-style back slashes replaced with forward slashes, and the full replace string should be replaced with empty single quotes",
                output.endsWith("/build/ml-gradle/consolidatedAssets, ''\""));
    }
}