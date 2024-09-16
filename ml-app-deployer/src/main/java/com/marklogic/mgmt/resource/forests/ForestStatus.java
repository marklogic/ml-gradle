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
package com.marklogic.mgmt.resource.forests;

import com.marklogic.rest.util.Fragment;
import org.jdom2.Namespace;

public class ForestStatus extends Fragment {

    public ForestStatus(Fragment other) {
        super(other);
    }

    public ForestStatus(String xml, Namespace... namespaces) {
        super(xml, namespaces);
    }

    public boolean isPrimary() {
        String id = getElementValue("/f:forest-status/f:id");
        String masterForestId = getElementValue("/f:forest-status/f:status-properties/f:master-forest");
        return id != null && masterForestId != null && id.equals(masterForestId);
    }

    public boolean hasReplicas() {
        return !getElementValues("/f:forest-status/f:status-properties/f:replica-forests/f:replica-forest").isEmpty();
    }
}
