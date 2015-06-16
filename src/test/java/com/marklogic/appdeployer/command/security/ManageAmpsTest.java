package com.marklogic.appdeployer.command.security;

import org.junit.Test;

import com.marklogic.appdeployer.AbstractAppDeployerTest;

public class ManageAmpsTest extends AbstractAppDeployerTest {

    /**
     * Pattern for an abstract class - create two instances of a resource (which means it needs to know what directory
     * to look for JSON files in); then assert that both existed (so we'll need to know what the name of each is); then
     * create them again to verify that no error occurs; then update one of them (so we'll need to know what data to use
     * for an update - possibly just modify the description?); then assert it was updated; then undeploy the app and
     * assert both resources were deleted.
     */
    @Test
    public void test() {

    }
}
