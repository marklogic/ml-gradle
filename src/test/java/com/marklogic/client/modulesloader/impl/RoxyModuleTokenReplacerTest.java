package com.marklogic.client.modulesloader.impl;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.FileCopyUtils;

import com.marklogic.client.modulesloader.tokenreplacer.RoxyModuleTokenReplacer;

public class RoxyModuleTokenReplacerTest extends Assert {

    @Test
    public void test() throws Exception {
        RoxyModuleTokenReplacer r = new RoxyModuleTokenReplacer();
        String original = new String(
                FileCopyUtils.copyToByteArray(new File("src/test/resources/token-replace/ext/test.xqy")));

        String modified = r.replaceTokensInModule(original);
        assertTrue(modified.contains("<color>red</color>"));
        assertTrue(modified.contains("<number>20</number>"));
        assertTrue(modified.contains("<vehicle>red wagon</vehicle>"));
        assertTrue(modified.contains("<outfit>red dress</outfit>"));
    }

}
