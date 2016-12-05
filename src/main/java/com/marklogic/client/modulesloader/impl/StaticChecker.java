package com.marklogic.client.modulesloader.impl;

import java.util.List;

public interface StaticChecker {

	public void checkLoadedAssets(List<LoadedAsset> assets);
}
