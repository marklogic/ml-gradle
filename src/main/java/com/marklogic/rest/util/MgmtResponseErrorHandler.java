package com.marklogic.rest.util;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

public class MgmtResponseErrorHandler extends DefaultResponseErrorHandler {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        try {
            super.handleError(response);
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Logging HTTP response body to assist with debugging: " + ex.getResponseBodyAsString());
            }
            throw ex;
        }
    }

}
