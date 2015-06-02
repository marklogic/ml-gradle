package com.marklogic.appdeployer.plugin;

import java.io.File;
import java.io.IOException;

import org.springframework.util.FileCopyUtils;

import com.marklogic.appdeployer.AppPlugin;
import com.marklogic.appdeployer.DefaultTokenReplacer;
import com.marklogic.appdeployer.TokenReplacer;
import com.marklogic.clientutil.LoggingObject;

/**
 * Abstract base class that provides some convenience methods for implementing a plugin. Requires that the Spring
 * Ordered interface be implemented so that the implementor takes into account when this particular plugin should be
 * executed relative to other plugins.
 */
public abstract class AbstractPlugin extends LoggingObject implements AppPlugin {

    protected TokenReplacer tokenReplacer = new DefaultTokenReplacer();

    /**
     * By default, assumes that the sort order on undeploy should be the same as on deploy. Subclasses can override this
     * to provide an alternate approach.
     */
    @Override
    public Integer getSortOrderOnUndeploy() {
        return getSortOrderOnDeploy();
    }

    protected String format(String s, Object... args) {
        return String.format(s, args);
    }

    protected String copyFileToString(File f) {
        try {
            return new String(FileCopyUtils.copyToByteArray(f));
        } catch (IOException ie) {
            throw new RuntimeException("Unable to copy file to string from path: " + f.getAbsolutePath() + "; cause: "
                    + ie.getMessage(), ie);
        }
    }

    public void setTokenReplacer(TokenReplacer tokenReplacer) {
        this.tokenReplacer = tokenReplacer;
    }

    public TokenReplacer getTokenReplacer() {
        return tokenReplacer;
    }

}
