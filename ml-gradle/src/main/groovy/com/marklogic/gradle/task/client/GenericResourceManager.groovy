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
package com.marklogic.gradle.task.client

import com.marklogic.client.extensions.ResourceManager
import com.marklogic.client.io.StringHandle
import com.marklogic.client.util.RequestParameters

class GenericResourceManager extends ResourceManager {

	def get(Map<String, ?> params) {
		return services.get(buildRequestParameter(params), new StringHandle()).get();
	}

	def post(params, body, mimeType) {
		return services.post(buildRequestParameter(params), new StringHandle(body).withMimetype(mimeType), new StringHandle()).get();
	}

	def put(params, body, mimeType) {
		return services.put(buildRequestParameter(params), new StringHandle(body).withMimetype(mimeType), new StringHandle()).get();
	}

	def delete(params) {
		return services.delete(buildRequestParameter(params), new StringHandle()).get();
	}

	def buildRequestParameter(Map<String, ?> params) {
		def requestParams = new RequestParameters()
		for (String key : params.keySet()) {
			requestParams.put(key, params.get(key))
		}
		return requestParams
	}
}
