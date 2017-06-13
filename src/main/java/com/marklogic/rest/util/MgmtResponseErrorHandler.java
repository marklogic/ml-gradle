package com.marklogic.rest.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.io.IOException;

public class MgmtResponseErrorHandler extends DefaultResponseErrorHandler {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        try {
            super.handleError(response);
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
			String message = "Logging HTTP response body to assist with debugging: " + ex.getResponseBodyAsString();
			if (HttpStatus.SERVICE_UNAVAILABLE.equals(ex.getStatusCode())) {
				if (logger.isDebugEnabled()) {
					logger.debug(message);
				}
			}
            else if (logger.isErrorEnabled()) {
                logger.error(message);
            }
            throw ex;
        }
    }

}
