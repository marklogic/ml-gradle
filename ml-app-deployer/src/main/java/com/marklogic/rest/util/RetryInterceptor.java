package com.marklogic.rest.util;

import com.marklogic.client.ext.helper.LoggingObject;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * OkHttp interceptor that retries requests on certain connection failures,
 * which can be helpful when MarkLogic is temporarily unavailable during restarts.
 */
class RetryInterceptor extends LoggingObject implements Interceptor {

	private final int maxRetries;
	private final long initialDelayMs;
	private final double backoffMultiplier;
	private final long maxDelayMs;

	RetryInterceptor(int maxRetries, long initialDelayMs, double backoffMultiplier, long maxDelayMs) {
		this.maxRetries = maxRetries;
		this.initialDelayMs = initialDelayMs;
		this.backoffMultiplier = backoffMultiplier;
		this.maxDelayMs = maxDelayMs;
	}

	@Override
	public Response intercept(Chain chain) throws IOException {
		Request request = chain.request();
		IOException lastException = null;

		for (int attempt = 0; attempt <= maxRetries; attempt++) {
			try {
				return chain.proceed(request);
			} catch (IOException e) {
				lastException = e;

				if (attempt == maxRetries || !isRetryableException(e)) {
					logger.warn("Not retryable: {}; {}", e.getClass(), e.getMessage());
					throw e;
				}

				long delay = calculateDelay(attempt);
				logger.warn("Request to {} failed (attempt {}/{}): {}. Retrying in {}ms",
					request.url(), attempt + 1, maxRetries, e.getMessage(), delay);

				sleep(delay);
			}
		}

		throw lastException;
	}

	private boolean isRetryableException(IOException e) {
		return e instanceof ConnectException ||
			e instanceof SocketTimeoutException ||
			e instanceof UnknownHostException ||
			(e.getMessage() != null && (
				e.getMessage().contains("Failed to connect") ||
					e.getMessage().contains("unexpected end of stream") ||
					e.getMessage().contains("Connection reset") ||
					e.getMessage().contains("Read timed out")
			));
	}

	private long calculateDelay(int attempt) {
		long delay = (long) (initialDelayMs * Math.pow(backoffMultiplier, attempt));
		return Math.min(delay, maxDelayMs);
	}

	private void sleep(long delay) {
		try {
			Thread.sleep(delay);
		} catch (InterruptedException ie) {
			logger.warn("Ignoring InterruptedException while sleeping for retry delay: {}", ie.getMessage());
		}
	}
}
