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
