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
package com.marklogic.mgmt.mapper;

import com.marklogic.mgmt.api.Resource;

/**
 * The eventual plan is for this to have both read and write methods. Immediate need is only for a read method.
 */
public interface ResourceMapper {

	/**
	 * The payload is defined as a string so that the implementation can easily determine whether the payload is
	 * JSON or XML.
	 *
	 * @param payload
	 * @param type
	 * @param <T>
	 * @return
	 */
	<T extends Resource> T readResource(String payload, Class<T> type);

}
