/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
