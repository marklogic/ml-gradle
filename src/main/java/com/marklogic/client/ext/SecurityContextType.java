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
package com.marklogic.client.ext;

/**
 * The Authentication enum in marklogic-client-api was deprecated in 4.0.1, but this enum is useful in
 * DatabaseClientConfig as a way of referencing a particular SecurityContext implementation to use.
 */
public enum SecurityContextType {

	BASIC,
	CERTIFICATE,
	/**
	 * @since 4.5.0
	 */
	CLOUD,
	DIGEST,
	KERBEROS,
	/**
	 * @since 4.5.0
	 */
	SAML,
	@Deprecated // Deprecated in 4.5.0; the Java Client requires a SecurityContext
	NONE
}
