/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt.template;

import com.marklogic.mgmt.api.Resource;

import java.util.Map;

public interface TemplateBuilder {

	Resource buildTemplate(Map<String, Object> propertyMap);

}
