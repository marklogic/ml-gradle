package com.marklogic.appdeployer.command;

import com.marklogic.client.ext.helper.LoggingObject;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.SaveReceipt;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Abstract base class that provides some convenience methods for implementing a command. Subclasses will typically
 * override the default sort order within the subclass constructor.
 */
public abstract class AbstractCommand extends LoggingObject implements Command {

    private int executeSortOrder = Integer.MAX_VALUE;
    private boolean storeResourceIdsAsCustomTokens = false;

    protected PayloadTokenReplacer payloadTokenReplacer = new DefaultPayloadTokenReplacer();
    private FilenameFilter resourceFilenameFilter = new ResourceFilenameFilter();

    /**
     * A subclass can set the executeSortOrder attribute to whatever value it needs.
     */
    @Override
    public Integer getExecuteSortOrder() {
        return this.executeSortOrder;
    }

    /**
     * Convenience method for setting the names of files to ignore when reading resources from a directory. Will
     * preserve any filenames already being ignored on the underlying FilenameFilter.
     *
     * @param filenames
     */
    public void setFilenamesToIgnore(String... filenames) {
        if (filenames == null || filenames.length == 0) {
            return;
        }
        if (resourceFilenameFilter != null) {
        	if (resourceFilenameFilter instanceof ResourceFilenameFilter) {
		        ResourceFilenameFilter rff = (ResourceFilenameFilter) resourceFilenameFilter;
		        Set<String> set = null;
		        if (rff.getFilenamesToIgnore() != null) {
			        set = rff.getFilenamesToIgnore();
		        } else {
			        set = new HashSet<>();
		        }
		        for (String f : filenames) {
			        set.add(f);
		        }
		        rff.setFilenamesToIgnore(set);
	        } else {
		        logger.warn("resourceFilenameFilter is not an instanceof ResourceFilenameFilter, so unable to set resource filenames to ignore");
	        }
        } else {
            this.resourceFilenameFilter = new ResourceFilenameFilter(filenames);
        }
    }

    public void setResourceFilenamesExcludePattern(Pattern pattern) {
    	if (resourceFilenameFilter != null) {
    		if (resourceFilenameFilter instanceof ResourceFilenameFilter) {
			    ((ResourceFilenameFilter)resourceFilenameFilter).setExcludePattern(pattern);
		    } else {
    			logger.warn("resourceFilenameFilter is not an instanceof ResourceFilenameFilter, so unable to set exclude pattern");
		    }
	    } else {
    		ResourceFilenameFilter rff = new ResourceFilenameFilter();
    		rff.setExcludePattern(pattern);
    		this.resourceFilenameFilter = rff;
	    }
    }

	public void setResourceFilenamesIncludePattern(Pattern pattern) {
		if (resourceFilenameFilter != null) {
			if (resourceFilenameFilter instanceof ResourceFilenameFilter) {
				((ResourceFilenameFilter)resourceFilenameFilter).setIncludePattern(pattern);
			} else {
				logger.warn("resourceFilenameFilter is not an instanceof ResourceFilenameFilter, so unable to set include pattern");
			}
		} else {
			ResourceFilenameFilter rff = new ResourceFilenameFilter();
			rff.setIncludePattern(pattern);
			this.resourceFilenameFilter = rff;
		}
	}

    /**
     * Simplifies reading the contents of a File into a String.
     *
     * @param f
     * @return
     */
    protected String copyFileToString(File f) {
        try {
            return new String(FileCopyUtils.copyToByteArray(f));
        } catch (IOException ie) {
            throw new RuntimeException(
                    "Unable to copy file to string from path: " + f.getAbsolutePath() + "; cause: " + ie.getMessage(),
                    ie);
        }
    }

	/**
	 * Convenience function for reading the file into a string and replace tokens as well. Assumes this is not
	 * for a test-only resource.
	 *
	 * @param f
	 * @param context
	 * @return
	 */
	protected String copyFileToString(File f, CommandContext context) {
		String str = copyFileToString(f);
		return str != null ? payloadTokenReplacer.replaceTokens(str, context.getAppConfig(), false) : str;
	}

    /**
     * Provides a basic implementation for saving a resource defined in a File, including replacing tokens.
     *
     * @param mgr
     * @param context
     * @param f
     * @return
     */
    protected SaveReceipt saveResource(ResourceManager mgr, CommandContext context, File f) {
		String payload = copyFileToString(f, context);
        SaveReceipt receipt = mgr.save(payload);
        if (storeResourceIdsAsCustomTokens) {
            storeTokenForResourceId(receipt, context);
        }
        return receipt;
    }

    /**
     * Any resource that may be referenced by its ID by another resource will most likely need its ID stored as a custom
     * token so that it can be referenced by the other resource. To enable this, the subclass should set
     * storeResourceIdAsCustomToken to true.
     *
     * @param receipt
     * @param context
     */
    protected void storeTokenForResourceId(SaveReceipt receipt, CommandContext context) {
        URI location = receipt.getResponse() != null ? receipt.getResponse().getHeaders().getLocation() : null;

        String idValue = null;
        String resourceName = null;

        if (location != null) {
            String[] tokens = location.getPath().split("/");
            idValue = tokens[tokens.length - 1];
            resourceName = tokens[tokens.length - 2];
        } else {
            String[] tokens = receipt.getPath().split("/");
            // Path is expected to end in /(resources-name)/(id)/properties
            idValue = tokens[tokens.length - 2];
            resourceName = tokens[tokens.length - 3];
        }

        String key = "%%" + resourceName + "-id-" + receipt.getResourceId() + "%%";
        if (logger.isInfoEnabled()) {
            logger.info(format("Storing token with key '%s' and value '%s'", key, idValue));
        }

        context.getAppConfig().getCustomTokens().put(key, idValue);
    }

    protected File[] listFilesInDirectory(File dir) {
        File[] files = dir.listFiles(resourceFilenameFilter);
        if (files != null && files.length > 1) {
	        Arrays.sort(files);
        }
        return files;
    }

    public void setPayloadTokenReplacer(PayloadTokenReplacer payloadTokenReplacer) {
        this.payloadTokenReplacer = payloadTokenReplacer;
    }

    public void setExecuteSortOrder(int executeSortOrder) {
        this.executeSortOrder = executeSortOrder;
    }

    public void setStoreResourceIdsAsCustomTokens(boolean storeResourceIdsAsCustomTokens) {
        this.storeResourceIdsAsCustomTokens = storeResourceIdsAsCustomTokens;
    }

    public void setResourceFilenameFilter(FilenameFilter resourceFilenameFilter) {
        this.resourceFilenameFilter = resourceFilenameFilter;
    }
}
