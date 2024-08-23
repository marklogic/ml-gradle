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
 * Interface for an object that can load files as documents from many paths. These files could be modules, or schemas, or
 * content documents - it doesn't matter. The expected implementation of this is GenericFileLoader or a subclass of it,
 * where the subclass may have knowledge of a specific type of document to load.
 */
public interface FileLoader {

	List<DocumentFile> loadFiles(String... paths);
}
