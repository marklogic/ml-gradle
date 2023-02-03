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
package com.marklogic.appdeployer.export;

import java.io.File;

/**
 * Interface for exporting one or more MarkLogic resources via the Management API to disk.
 *
 * This is located in the appdeployer package because of an assumed dependency on the ConfigDir class, which defines
 * where resources should be exported to.
 */
public interface ResourceExporter {

	String FORMAT_XML = "xml";
	String FORMAT_JSON = "json";

	/**
	 * @param baseDir
	 * @return
	 */
	ExportedResources exportResources(File baseDir);
}
