package com.marklogic.appdeployer.cli;

public class DeployerDebug {

	public static void main(String[] args) throws Exception {
		args = new String[]{
			"-l", "info",
			"-PmlAppName=cli",
			"-PmlContentForestsPerHost=1",
			"-PmlConfigPath=src/test/resources/sample-app/db-only-config",
			"mlDeployContentDatabases"
		};

//		args = new String[]{
//			"-f", "build/deployer.properties",
//			"-u",
//			"-l", "INFO",
//			"mlDeployContentDatabases"
//		};
		//args = new String[]{};
		Main.main(args);
	}
}
