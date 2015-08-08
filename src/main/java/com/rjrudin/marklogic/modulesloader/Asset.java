package com.rjrudin.marklogic.modulesloader;

import java.io.File;

public class Asset {

    private File file;
    private String path;

    public Asset(File file, String path) {
        super();
        this.file = file;
        this.path = path;
    }

    public File getFile() {
        return file;
    }

    public String getPath() {
        return path;
    }

}
