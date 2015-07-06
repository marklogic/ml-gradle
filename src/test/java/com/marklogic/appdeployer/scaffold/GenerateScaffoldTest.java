package com.marklogic.appdeployer.scaffold;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import com.marklogic.rest.mgmt.AbstractMgmtTest;

public class GenerateScaffoldTest extends AbstractMgmtTest {

    @Test
    public void test() throws IOException {
        // Assume this is run out of the main directory, so default to "." and build out src/main etc.
        String path = "src/test/resources/scaffold-test";
        File dir = new File(path);
        dir.delete();
        dir.mkdirs();

        ScaffoldGenerator sg = new ScaffoldGenerator();
        sg.generateScaffold(path);
    }
}
