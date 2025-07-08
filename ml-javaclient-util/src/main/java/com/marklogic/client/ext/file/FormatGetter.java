/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.ext.file;

import com.marklogic.client.io.Format;
import org.springframework.core.io.Resource;

/**
 * Strategy interface for the Format that should be used when writing the given File as a document into MarkLogic.
 */
public interface FormatGetter {

    Format getFormat(Resource resource);
}
