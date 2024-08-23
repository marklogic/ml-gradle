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

public class DeleteReceipt {

    private String resourceId;
    private String path;
    private boolean deleted;

    public DeleteReceipt(String resourceId, String path, boolean deleted) {
        this.resourceId = resourceId;
        this.path = path;
        this.deleted = deleted;
    }

    public String getResourceId() {
        return resourceId;
    }

    public String getPath() {
        return path;
    }

    public boolean isDeleted() {
        return deleted;
    }
}
