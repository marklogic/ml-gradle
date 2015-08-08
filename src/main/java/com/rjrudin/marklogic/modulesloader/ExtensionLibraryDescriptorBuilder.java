package com.rjrudin.marklogic.modulesloader;

import com.marklogic.client.admin.ExtensionLibraryDescriptor;

/**
 * Main purpose of this interface is to provide an extension point for setting document permissions
 * for a particular asset.
 */
public interface ExtensionLibraryDescriptorBuilder {

    ExtensionLibraryDescriptor buildDescriptor(Asset asset);
}
