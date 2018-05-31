package com.marklogic.client.ext.modulesloader.impl;

/**
 * Ideally this would just have been a Consumer that receives an instance of Throwable. And also, it's only for
 * loading REST modules, which DefaultModulesLoader loads by default in parallel.
 */
public interface LoadModulesFailureListener {

	void processFailure(Throwable throwable);

}
