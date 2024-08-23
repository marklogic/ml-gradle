/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.appdeployer.util;

import com.marklogic.client.DatabaseClientFactory;

public interface JavaClientUtil {

	static DatabaseClientFactory.SSLHostnameVerifier toSSLHostnameVerifier(String type) {
		if ("any".equalsIgnoreCase(type)) {
			return DatabaseClientFactory.SSLHostnameVerifier.ANY;
		}
		if ("common".equalsIgnoreCase(type)) {
			return DatabaseClientFactory.SSLHostnameVerifier.COMMON;
		}
		if ("strict".equalsIgnoreCase(type)) {
			return DatabaseClientFactory.SSLHostnameVerifier.STRICT;
		}
		throw new IllegalArgumentException(String.format("Unrecognized SSLHostnameVerifier type: " + type));
	}
}
