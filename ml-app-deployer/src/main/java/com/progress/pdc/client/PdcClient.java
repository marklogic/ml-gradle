/*
 * Copyright (c) 2015-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.progress.pdc.client;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientBuilder;
import com.progress.pdc.client.generated.ApiClient;
import com.progress.pdc.client.generated.ApiException;
import com.progress.pdc.client.generated.JSON;
import com.progress.pdc.client.generated.api.ServiceGroupApi;
import com.progress.pdc.client.generated.model.ServiceGroupViewModel;
import com.progress.pdc.client.impl.GsonUtil;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

import java.io.Closeable;
import java.util.Objects;
import java.util.UUID;

public class PdcClient implements Closeable {

	public static Builder newBuilder(String host, String apiKey) {
		return new Builder(host, apiKey);
	}

	public static class Builder {
		private final String host;
		private final String apiKey;
		private Interceptor okHttpInterceptor;

		public Builder(String host, String apiKey) {
			this.host = host;
			this.apiKey = apiKey;
		}

		public Builder okHttpInterceptor(Interceptor interceptor) {
			this.okHttpInterceptor = interceptor;
			return this;
		}

		public PdcClient build() {
			return new PdcClient(host, apiKey, okHttpInterceptor);
		}
	}

	private final ApiClient apiClient;
	private final DatabaseClient databaseClient;
	private final Interceptor okHttpInterceptor;

	private PdcClient(String host, String apiKey, Interceptor okHttpInterceptor) {
		DatabaseClient databaseClient = new DatabaseClientBuilder()
			.withHost(host)
			.withCloudAuth(apiKey, null)
			.build();

		OkHttpClient okHttpClient = (OkHttpClient) databaseClient.getClientImplementation();
		Objects.requireNonNull(okHttpClient, "OkHttpClient implementation expected from DatabaseClient");
		if (okHttpInterceptor != null) {
			okHttpClient = okHttpClient.newBuilder()
				.addInterceptor(okHttpInterceptor)
				.build();
		}

		ApiClient apiClient = new ApiClient(okHttpClient);
		apiClient.setBasePath("https://%s".formatted(databaseClient.getHost()));

		this.databaseClient = databaseClient;
		this.apiClient = apiClient;
		this.okHttpInterceptor = okHttpInterceptor;
		JSON.setGson(GsonUtil.createGson());
	}

	public UUID getEnvironmentId() {
		try {
			ServiceGroupViewModel viewModel = new ServiceGroupApi(apiClient).apiServicegroupGet(null).get(0);
			return viewModel.getId();
		} catch (ApiException ex) {
			throw new PdcClientException("Unable to get environment ID, could not get service groups from PDC", ex);
		}
	}

	public String getHost() {
		return this.databaseClient.getHost();
	}

	public ApiClient getApiClient() {
		return apiClient;
	}

	@Override
	public void close() {
		this.databaseClient.release();
	}

}
