package com.marklogic.mgmt.template;

import com.marklogic.mgmt.api.Resource;

import java.util.Map;

public interface TemplateBuilder {

	Resource buildTemplate(Map<String, Object> propertyMap);

}
