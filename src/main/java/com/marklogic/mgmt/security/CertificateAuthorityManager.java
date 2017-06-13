package com.marklogic.mgmt.security;

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

    public ResponseEntity<String> create(String payload) {
        RestTemplate t = manageClient.getRestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);

        HttpEntity<String> entity = new HttpEntity<String>(payload, headers);
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
