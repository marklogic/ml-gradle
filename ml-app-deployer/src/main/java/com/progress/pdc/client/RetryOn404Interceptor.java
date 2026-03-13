/*
 * Copyright (c) 2015-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.progress.pdc.client;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * Addresses at least one issue where after a MarkLogic endpoint is created in PDC, subsequent calls within the
 * next second or two will receive a 404, possibly due to a load balancer restart.
 */
public record RetryOn404Interceptor(int maxRetries, long retryDelayMs) implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        final Request request = chain.request();
        Response response = chain.proceed(request);
        int retryCount = 0;

        while (response.code() == 404 && retryCount < maxRetries) {
            Util.LOGGER.debug("Received 404 for request to {}, retrying {}/{} after {} ms",
                request.url(), retryCount + 1, maxRetries, retryDelayMs);
            response.close();
            retryCount++;

            try {
                Thread.sleep(retryDelayMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException("Retry interrupted", e);
            }

            response = chain.proceed(request);
        }

        return response;
    }
}
