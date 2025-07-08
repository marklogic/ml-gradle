/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
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
