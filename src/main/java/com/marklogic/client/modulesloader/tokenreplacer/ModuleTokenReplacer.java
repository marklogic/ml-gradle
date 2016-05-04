package com.marklogic.client.modulesloader.tokenreplacer;

public interface ModuleTokenReplacer {

    public String replaceTokensInModule(String moduleText);
}
