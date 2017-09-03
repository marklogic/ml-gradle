package com.marklogic.appdeployer.export.forests;

import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.export.impl.AbstractNamedResourceExporter;
import com.marklogic.appdeployer.export.impl.ExportInputs;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.forests.ForestManager;

import java.io.File;

public class ForestExporter extends AbstractNamedResourceExporter {

	private String databaseName;
	private boolean removeRange = true;

	public ForestExporter(String databaseName, ManageClient manageClient, String... forestNames) {
		super(manageClient, forestNames);
		this.databaseName = databaseName;
	}

	@Override
	protected String beforeResourceWrittenToFile(ExportInputs exportInputs, String payload) {
		return isRemoveRange() ? removeJsonKeyFromPayload(payload, "range") : payload;
	}

	@Override
	protected String[] getExportMessages() {
		return new String[]{"The 'range' key was removed from each exported forest, as the forest cannot be deployed when its value is null."};
	}

	@Override
	protected ResourceManager newResourceManager(ManageClient manageClient) {
		return new ForestManager(manageClient);
	}

	@Override
	protected File getResourceDirectory(File baseDir) {
		File forestsDir = new ConfigDir(baseDir).getForestsDir();
		forestsDir.mkdirs();
		File dbForestsDir = new File(forestsDir, databaseName);
		dbForestsDir.mkdirs();
		return dbForestsDir;
	}

	public boolean isRemoveRange() {
		return removeRange;
	}

	public void setRemoveRange(boolean removeRange) {
		this.removeRange = removeRange;
	}
}
