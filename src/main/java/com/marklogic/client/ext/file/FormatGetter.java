package com.marklogic.client.ext.file;

import java.io.File;

import com.marklogic.client.io.Format;

public interface FormatGetter {

    Format getFormat(File file);
}
