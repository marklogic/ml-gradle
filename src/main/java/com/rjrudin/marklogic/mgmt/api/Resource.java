package com.rjrudin.marklogic.mgmt.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rjrudin.marklogic.mgmt.ManageClient;
import com.rjrudin.marklogic.mgmt.ResourceManager;

/**
 * Base class for any class that we both want to read/write from/to JSON and make calls to the Management REST API.
 */
public abstract class Resource extends ApiObject {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private API api;
    private ObjectMapper objectMapper;

    protected String format(String format, Object... args) {
        return String.format(format, args);
    }

    protected Resource() {
    }

    protected Resource(API api) {
        this.api = api;
        setObjectMapper(api.getObjectMapper());
    }

    /**
     * A subclass should return a value that is a useful label for the resource, which can be used for log messages.
     * 
     * @return
     */
    protected abstract String getResourceLabel();

    protected abstract ResourceManager getResourceManager();

    protected abstract String getResourceId();

    protected String getResourceName() {
        return getClass().getSimpleName().toLowerCase();
    }

    @JsonIgnore
    public String getJson() {
        ObjectMapper mapper = getObjectMapper();
        if (mapper != null) {
            try {
                return mapper.writeValueAsString(this);
            } catch (JsonProcessingException ex) {
                throw new RuntimeException("Unable to write object as JSON, cause: " + ex.getMessage(), ex);
            }
        }
        return null;
    }

    /**
     * Protected for subclass usage and so that Jackson doesn't try to serialize it.
     * 
     * @return
     */
    protected API getApi() {
        return api;
    }

    /**
     * Protected for subclass usage and so that Jackson doesn't try to serialize it.
     * 
     * @return
     */
    protected ManageClient getClient() {
        return api.getManageClient();
    }

    public void save() {
        String name = getResourceName();
        String label = getResourceLabel();
        if (logger.isInfoEnabled()) {
            logger.info(format("Saving %s %s", name, label));
        }
        getResourceManager().save(getJson());
        if (logger.isInfoEnabled()) {
            logger.info(format("Saved %s %s", name, label));
        }
    }

    public void delete() {
        String name = getResourceName();
        String label = getResourceLabel();
        if (logger.isInfoEnabled()) {
            logger.info(format("Deleting %s %s", name, label));
        }
        getResourceManager().deleteByIdField(getResourceId());
        if (logger.isInfoEnabled()) {
            logger.info(format("Deleted %s %s", name, label));
        }
    }

    public boolean exists() {
        return getResourceManager().exists(getResourceId());
    }

    public void setApi(API api) {
        this.api = api;
    }

    protected ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

}
