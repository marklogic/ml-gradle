package com.marklogic.appdeployer.command;

import java.io.File;
import java.io.IOException;

import org.springframework.util.FileCopyUtils;

import com.marklogic.clientutil.LoggingObject;

/**
 * Abstract base class that provides some convenience methods for implementing a command. Subclasses will typically override
 * the default sort order within the subclass constructor.
 */
public abstract class AbstractCommand extends LoggingObject implements Command {

    protected TokenReplacer tokenReplacer = new DefaultTokenReplacer();

    protected int executeSortOrder = Integer.MAX_VALUE;

    protected String copyFileToString(File f) {
        try {
            return new String(FileCopyUtils.copyToByteArray(f));
        } catch (IOException ie) {
            throw new RuntimeException("Unable to copy file to string from path: " + f.getAbsolutePath() + "; cause: "
                    + ie.getMessage(), ie);
        }
    }

    protected boolean isResourceFile(File f) {
        return f.getName().endsWith(".json") || f.getName().endsWith(".xml");
    }

    public void setTokenReplacer(TokenReplacer tokenReplacer) {
        this.tokenReplacer = tokenReplacer;
    }

    public TokenReplacer getTokenReplacer() {
        return tokenReplacer;
    }

    @Override
    public Integer getExecuteSortOrder() {
        return this.executeSortOrder;
    }

    public void setExecuteSortOrder(int executeSortOrder) {
        this.executeSortOrder = executeSortOrder;
    }

}
