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
package com.marklogic.mgmt;

import org.springframework.http.ResponseEntity;

public class SaveReceipt {

    private String resourceId;
    private String payload;
    private ResponseEntity<String> response;
    private String path;

    public SaveReceipt(String resourceId, String payload, String path, ResponseEntity<String> response) {
        super();
        this.resourceId = resourceId;
        this.payload = payload;
        this.path = path;
        this.response = response;
    }

    public boolean hasLocationHeader() {
        return response != null && response.getHeaders().getLocation() != null;
    }

    public String getResourceId() {
        return resourceId;
    }

    public String getPath() {
        return path;
    }

    public ResponseEntity<String> getResponse() {
        return response;
    }

    public String getPayload() {
        return payload;
    }
}
