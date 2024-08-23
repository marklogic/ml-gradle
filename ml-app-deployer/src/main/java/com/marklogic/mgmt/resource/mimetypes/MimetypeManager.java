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
package com.marklogic.mgmt.resource.mimetypes;

import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.SaveReceipt;
import com.marklogic.mgmt.api.API;
import com.marklogic.mgmt.api.mimetypes.Mimetype;
import com.marklogic.mgmt.mapper.DefaultResourceMapper;
import com.marklogic.mgmt.mapper.ResourceMapper;
import com.marklogic.mgmt.resource.AbstractResourceManager;

public class MimetypeManager extends AbstractResourceManager {

	private boolean updateWhenPropertiesAreEqual = false;
	private ResourceMapper resourceMapper;

	public MimetypeManager(ManageClient client) {
		super(client);
	}

	@Override
	protected String getIdFieldName() {
		return "name";
	}

	/**
	 * To avoid ML restarting when a mimetype exists but its properties aren't being changed by the incoming payload,
	 * this method is overridden so a check can be made to see if the properties are different from what's already in
	 * MarkLogic.
	 * <p>
	 * This behavior can be disabled by setting updateWhenPropertiesAreEqual to true.
	 *
	 * @param payload
	 * @param resourceId
	 * @return
	 */
	@Override
	public SaveReceipt updateResource(String payload, String resourceId) {
		if (updateWhenPropertiesAreEqual) {
			if (logger.isDebugEnabled()) {
				logger.debug(format("updateWhenPropertiesAreEqual is set to true, so mimetype %s will be updated based on the " +
					"incoming payload regardless of whether its properties differ from what's already set in MarkLogic or not", resourceId));
			}
			return super.updateResource(payload, resourceId);
		}

		if (resourceMapper == null) {
			resourceMapper = new DefaultResourceMapper(new API(getManageClient()));
		}

		Mimetype incomingMimetype = resourceMapper.readResource(payload, Mimetype.class);
		final String name = incomingMimetype.getName();

		String existingJson = super.getPropertiesAsJson(name);
		Mimetype existingMimetype = resourceMapper.readResource(existingJson, Mimetype.class);

		if (incomingMimetype.equals(existingMimetype)) {
			logger.info(format("The properties in the payload for mimetype %s are the same as what's already set in " +
				"MarkLogic, so the mimetype will not be updated", name));
			return new SaveReceipt(name, payload, null, null);
		}
		else {
			logger.info(format("The properties in the payload for mimetype %s differ from what's already set in MarkLogic, " +
				"so the mimetype will be updated", name));
			return super.updateResource(payload, resourceId);
		}
	}

	public void setUpdateWhenPropertiesAreEqual(boolean updateWhenPropertiesAreEqual) {
		this.updateWhenPropertiesAreEqual = updateWhenPropertiesAreEqual;
	}

	public void setResourceMapper(ResourceMapper resourceMapper) {
		this.resourceMapper = resourceMapper;
	}
}
