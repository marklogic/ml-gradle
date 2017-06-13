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
