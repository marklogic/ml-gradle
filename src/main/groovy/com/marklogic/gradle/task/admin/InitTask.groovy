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
package com.marklogic.gradle.task.admin

import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction;

import com.marklogic.gradle.task.MarkLogicTask;

class InitTask extends MarkLogicTask {

	@Input
	@Optional
    String licenseKey

	@Input
	@Optional
    String licensee

    @TaskAction
    void initializeMarkLogic() {
		if (project.hasProperty("mlLicenseKey")) {
			licenseKey = project.property("mlLicenseKey")
		}
		if (project.hasProperty("mlLicensee")) {
			licensee = project.property("mlLicensee")
		}

		// Permit this task to run without having mlUsername and mlPassword defined
		if (!project.hasProperty("mlUsername")) {
			getAdminManager().getAdminConfig().setUsername("")
		}
		if (!project.hasProperty("mlPassword")) {
			getAdminManager().getAdminConfig().setPassword("")
		}

        getAdminManager().init(licenseKey, licensee)
    }
}
