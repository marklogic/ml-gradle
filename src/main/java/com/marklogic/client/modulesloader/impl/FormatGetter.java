package com.marklogic.client.modulesloader.impl;

import java.io.File;

import com.marklogic.client.io.Format;

public interface FormatGetter {

    public Format getFormat(File file);
}
