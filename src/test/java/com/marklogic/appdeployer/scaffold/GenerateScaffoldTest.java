package com.marklogic.appdeployer.scaffold;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import com.marklogic.appdeployer.AbstractAppDeployerTest;

public class GenerateScaffoldTest extends AbstractAppDeployerTest {

    @Test
    public void test() throws IOException {
        // Assume this is run out of the main directory, so default to "." and build out src/main etc.
        String path = "src/test/resources/scaffold-test";
        File dir = new File(path);
        dir.delete();
        dir.mkdirs();

        ScaffoldGenerator sg = new ScaffoldGenerator();
        sg.generateScaffold(path, appConfig);

        File configDir = new File(dir, "src/main/ml-config");
        assertTrue(configDir.exists());
        assertTrue(new File(configDir, "rest-api.json").exists());
        assertTrue(new File(configDir, "databases/content-database.json").exists());
        assertTrue(new File(configDir, "security/roles/sample-app-role.json").exists());
        assertTrue(new File(configDir, "security/users/sample-app-user.json").exists());

        File modulesDir = new File(dir, "src/main/ml-modules");
        assertTrue(modulesDir.exists());
        assertTrue(new File(modulesDir, "rest-properties.json").exists());
        assertTrue(new File(modulesDir, "options/sample-app-options.xml").exists());
    }
}
