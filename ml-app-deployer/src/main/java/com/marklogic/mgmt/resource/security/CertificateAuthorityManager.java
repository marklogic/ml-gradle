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
package com.marklogic.mgmt.resource.security;

import com.marklogic.mgmt.AbstractManager;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.rest.util.ResourcesFragment;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

public class CertificateAuthorityManager extends AbstractManager {

    private ManageClient manageClient;

    public CertificateAuthorityManager(ManageClient client) {
        this.manageClient = client;
    }

	@Override
	protected boolean useSecurityUser() {
		return true;
	}

    public ResponseEntity<String> create(String payload) {
        RestTemplate t = manageClient.getRestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);

        HttpEntity<String> entity = new HttpEntity<>(payload, headers);
        ResponseEntity<String> response = t.exchange(manageClient.buildUri("/manage/v2/certificate-authorities"),
                HttpMethod.POST, entity, String.class);
        return response;
    }

    public ResourcesFragment getAsXml() {
        return new ResourcesFragment(manageClient.getXml("/manage/v2/certificate-authorities"));
    }

    public void delete(String resourceIdOrName) {
        manageClient.delete("/manage/v2/certificate-authorities/" + resourceIdOrName);
    }
}
