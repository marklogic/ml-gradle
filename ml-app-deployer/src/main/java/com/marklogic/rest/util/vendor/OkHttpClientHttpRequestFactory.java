/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.rest.util.vendor;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.util.Assert;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Fork of the deprecated and soon-to-be-removed
 * {@code org.springframework.http.client.OkHttp3ClientHttpRequestFactory} class. That class will be removed in
 * Spring 7, but we aren't shifting to the JDK HttpClient until marklogic-client-api is first able to.
 * <p>
 * Note that this is identical to the Spring code from Spring 6.2.11 except that "3" is no longer in the class name,
 * as it will work with at least OkHttp 4 and possibly OkHttp 5.
 */
public class OkHttpClientHttpRequestFactory implements ClientHttpRequestFactory, DisposableBean {

	private OkHttpClient client;

	private final boolean defaultClient;

	/**
	 * Create a factory with the given {@link OkHttpClient} instance.
	 *
	 * @param client the client to use
	 */
	public OkHttpClientHttpRequestFactory(OkHttpClient client) {
		Assert.notNull(client, "OkHttpClient must not be null");
		this.client = client;
		this.defaultClient = false;
	}


	/**
	 * Set the underlying read timeout in milliseconds.
	 * A value of 0 specifies an infinite timeout.
	 */
	public void setReadTimeout(int readTimeout) {
		this.client = this.client.newBuilder()
			.readTimeout(readTimeout, TimeUnit.MILLISECONDS)
			.build();
	}

	/**
	 * Set the underlying read timeout in milliseconds.
	 * A value of 0 specifies an infinite timeout.
	 *
	 * @since 6.1
	 */
	public void setReadTimeout(Duration readTimeout) {
		this.client = this.client.newBuilder()
			.readTimeout(readTimeout)
			.build();
	}

	/**
	 * Set the underlying write timeout in milliseconds.
	 * A value of 0 specifies an infinite timeout.
	 */
	public void setWriteTimeout(int writeTimeout) {
		this.client = this.client.newBuilder()
			.writeTimeout(writeTimeout, TimeUnit.MILLISECONDS)
			.build();
	}

	/**
	 * Set the underlying write timeout in milliseconds.
	 * A value of 0 specifies an infinite timeout.
	 *
	 * @since 6.1
	 */
	public void setWriteTimeout(Duration writeTimeout) {
		this.client = this.client.newBuilder()
			.writeTimeout(writeTimeout)
			.build();
	}

	/**
	 * Set the underlying connect timeout in milliseconds.
	 * A value of 0 specifies an infinite timeout.
	 */
	public void setConnectTimeout(int connectTimeout) {
		this.client = this.client.newBuilder()
			.connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
			.build();
	}

	/**
	 * Set the underlying connect timeout in milliseconds.
	 * A value of 0 specifies an infinite timeout.
	 *
	 * @since 6.1
	 */
	public void setConnectTimeout(Duration connectTimeout) {
		this.client = this.client.newBuilder()
			.connectTimeout(connectTimeout)
			.build();
	}


	@Override
	@SuppressWarnings("removal")
	public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) {
		return new OkHttpClientHttpRequest(this.client, uri, httpMethod);
	}


	@Override
	public void destroy() throws IOException {
		if (this.defaultClient) {
			// Clean up the client if we created it in the constructor
			Cache cache = this.client.cache();
			if (cache != null) {
				cache.close();
			}
			this.client.dispatcher().executorService().shutdown();
			this.client.connectionPool().evictAll();
		}
	}

}
