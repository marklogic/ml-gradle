package com.marklogic.rest.util;

import com.marklogic.client.ext.helper.LoggingObject;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.net.ConnectException;

/**
 * OkHttp interceptor that retries requests on certain connection failures,
 * which can be helpful when MarkLogic is temporarily unavailable during restarts.
 *
 * @since 6.0.0
 */
class RetryInterceptor extends LoggingObject implements Interceptor {

	private final int maxAttempts;
	private final long initialDelayMs;
	private final double delayMultiplier;
	private final long maxDelayMs;

	RetryInterceptor(int maxAttempts, long initialDelayMs, double delayMultiplier, long maxDelayMs) {
		this.maxAttempts = maxAttempts;
		this.initialDelayMs = initialDelayMs;
		this.delayMultiplier = delayMultiplier;
		this.maxDelayMs = maxDelayMs;
	}

	@Override
	public Response intercept(Chain chain) throws IOException {
		Request request = chain.request();
		IOException lastException = null;

		for (int attempt = 0; attempt <= maxAttempts; attempt++) {
			try {
				return chain.proceed(request);
			} catch (IOException e) {
				lastException = e;

				if (attempt == maxAttempts || !isConnectionFailure(e)) {
					logger.warn("Not retryable: {}; {}", e.getClass(), e.getMessage());
					throw e;
				}

				long delay = calculateDelay(attempt);
				logger.warn("Request to {} failed (attempt {}/{}): {}. Retrying in {}ms",
					request.url(), attempt + 1, maxAttempts, e.getMessage(), delay);

				sleep(delay);
			}
		}

		throw lastException;
	}

	private boolean isConnectionFailure(IOException e) {
		return e instanceof ConnectException ||
			(e.getMessage() != null && (
				e.getMessage().contains("Failed to connect") ||
					e.getMessage().contains("unexpected end of stream") ||
					e.getMessage().contains("Socket reset")
			));
	}

	private long calculateDelay(int attempt) {
		long delay = (long) (initialDelayMs * Math.pow(delayMultiplier, attempt));
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
