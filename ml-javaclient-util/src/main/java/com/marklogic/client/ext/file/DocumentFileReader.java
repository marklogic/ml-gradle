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
package com.marklogic.client.ext.file;

import java.util.List;

/**
 * Strategy interface for determining which files to load into MarkLogic, with those files being captured as a List of
 * DocumentFile objects.
 *
 * Note that with the new DataMovementManager in ML9, it should soon make sense to load each file after it's read,
 * with DMM batching up and flushing writes to MarkLogic. An implementation of this can then use DMM to both read and
 * write all files as documents into MarkLogic, and then the returned List of a list of all the files that were
 * processed.
 */
public interface DocumentFileReader {

	List<DocumentFile> readDocumentFiles(String... paths);
}
