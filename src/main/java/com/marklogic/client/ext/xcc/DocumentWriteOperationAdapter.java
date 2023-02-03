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
package com.marklogic.client.ext.xcc;

import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.xcc.Content;

/**
 * Interface for adapting a REST DocumentWriteOperation instance into an XCC Content instance. Intended to support
 * objects that prefer using the REST API to insert a document, where the document to insert is defined via a
 * DocumentWriteOperation instance, but can then shift to XCC if needed.
 */
public interface DocumentWriteOperationAdapter {

	Content adapt(DocumentWriteOperation operation);

}
