/*
 * Copyright (c) 2015-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.rest.util.vendor;

import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.InputStream;

/**
 * Fork of the deprecated and soon-to-be-removed
 * {@code org.springframework.http.client.OkHttp3ClientHttpResponse} class.
 */
class OkHttpClientHttpResponse implements ClientHttpResponse {

	private final Response response;

	private volatile HttpHeaders headers;

	public OkHttpClientHttpResponse(Response response) {
		Assert.notNull(response, "Response must not be null");
		this.response = response;
	}


	@Override
	public HttpStatusCode getStatusCode() throws IOException {
		return HttpStatusCode.valueOf(this.response.code());
	}

	@Override
	public String getStatusText() {
		return this.response.message();
	}

	@Override
	public InputStream getBody() throws IOException {
		ResponseBody body = this.response.body();
		return (body != null ? body.byteStream() : InputStream.nullInputStream());
	}

	@Override
	public HttpHeaders getHeaders() {
		HttpHeaders headers = this.headers;
		if (headers == null) {
			headers = new HttpHeaders();
			for (String headerName : this.response.headers().names()) {
				for (String headerValue : this.response.headers(headerName)) {
					headers.add(headerName, headerValue);
				}
			}
			this.headers = headers;
		}
		return headers;
	}

	@Override
	public void close() {
		ResponseBody body = this.response.body();
		if (body != null) {
			body.close();
		}
	}

}
