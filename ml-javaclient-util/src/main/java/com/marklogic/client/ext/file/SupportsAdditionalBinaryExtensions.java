package com.marklogic.client.ext.file;

/**
 * Intended to allow for {@code DefaultDocumentFileReader} to pass along a user-supplied list of additional binary
 * extensions without being coupled tightly to the {@code DocumentFileProcessor} that uses them.
 */
public interface SupportsAdditionalBinaryExtensions {

	void setAdditionalBinaryExtensions(String[] extensions);

}
