package com.rjrudin.marklogic.mgmt.security;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.rjrudin.marklogic.mgmt.AbstractManager;
import com.rjrudin.marklogic.mgmt.ManageClient;
import com.rjrudin.marklogic.rest.util.ResourcesFragment;

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
        ResponseEntity<String> response = t.exchange(manageClient.getBaseUrl() + "/manage/v2/certificate-authorities",
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
