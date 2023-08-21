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
package com.marklogic.mgmt.admin;

import com.marklogic.mgmt.AbstractManager;
import com.marklogic.rest.util.Fragment;
import com.marklogic.rest.util.RestConfig;
import com.marklogic.rest.util.RestTemplateUtil;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

public class AdminManager extends AbstractManager {

    private int waitForRestartCheckInterval = 1000;
    private int waitForRestartLimit = 30;
    private RestTemplate restTemplate;
    private AdminConfig adminConfig;

	/**
	 * Creates an uninitialized instance that requires a {@code AdminConfig} to be provided in order to be operable.
	 *
	 * @deprecated since 4.5.0; will be removed in 5.0.0
	 */
    public AdminManager() {
    }

    public AdminManager(AdminConfig adminConfig) {
    	setAdminConfig(adminConfig);
    }

	/**
	 * Uses the given AdminConfig instance to construct a Spring RestTemplate for communicating with Manage API
	 * endpoints on port 8001.
	 *
	 * @param adminConfig
	 */
	public void setAdminConfig(AdminConfig adminConfig) {
	    this.adminConfig = adminConfig;
    }

    public void init() {
        init(null, null);
    }

    public void init(String licenseKey, String licensee) {
        final URI uri = adminConfig.buildUri("/admin/v1/init");

        String json = null;
        if (licenseKey != null && licensee != null) {
            json = format("{\"license-key\":\"%s\", \"licensee\":\"%s\"}", licenseKey, licensee);
        } else {
            json = "{}";
        }
        final String payload = json;

        logger.info("Initializing MarkLogic at: " + uri);
        invokeActionRequiringRestart(new ActionRequiringRestart() {
            @Override
            public boolean execute() {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<String> entity = new HttpEntity<>(payload, headers);
                try {
                    ResponseEntity<String> response = getRestTemplate().exchange(uri, HttpMethod.POST, entity, String.class);
                    logger.info("Initialization response: " + response);
                    // According to http://docs.marklogic.com/REST/POST/admin/v1/init, a 202 is sent back in the event a
                    // restart is needed. A 400 or 401 will be thrown as an error by RestTemplate.
                    return HttpStatus.ACCEPTED.equals(response.getStatusCode());
                } catch (HttpClientErrorException hcee) {
                    String body = hcee.getResponseBodyAsString();
                    if (logger.isTraceEnabled()) {
                        logger.trace("Response body: " + body);
                    }
                    if (body != null && body.contains("MANAGE-ALREADYINIT")) {
                        logger.info("MarkLogic has already been initialized");
                        return false;
                    } else {
                        logger.error("Caught error, response body: " + body);
                        throw hcee;
                    }
                }
            }
        });
    }

    public void installAdmin() {
        installAdmin(null, null);
    }

	public void installAdmin(String username, String password) {
		installAdmin(username, password, "public");
	}

    public void installAdmin(String username, String password, String realm) {
        final URI uri = adminConfig.buildUri("/admin/v1/instance-admin");

        String json = null;
        if (username != null && password != null) {
            json = format("{\"admin-username\":\"%s\", \"admin-password\":\"%s\", \"realm\":\"%s\"}",
	            username, password, realm);
        } else {
            json = "{}";
        }
        final String payload = json;

        logger.info("Installing admin user at: " + uri);
        invokeActionRequiringRestart(new ActionRequiringRestart() {
            @Override
            public boolean execute() {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<String> entity = new HttpEntity<>(payload, headers);
                try {
                    ResponseEntity<String> response = getRestTemplate().exchange(uri, HttpMethod.POST, entity, String.class);
                    logger.info("Admin installation response: " + response);
                    // According to http://docs.marklogic.com/REST/POST/admin/v1/init, a 202 is sent back in the event a
                    // restart is needed. A 400 or 401 will be thrown as an error by RestTemplate.
                    return HttpStatus.ACCEPTED.equals(response.getStatusCode());
                } catch (HttpClientErrorException hcee) {
                    if (HttpStatus.BAD_REQUEST.equals(hcee.getStatusCode())) {
                        logger.warn("Caught 400 error, assuming admin user already installed; response body: "
                                + hcee.getResponseBodyAsString());
                        return false;
                    }
                    throw hcee;
                }
            }
        });
    }

    /**
     * This used to be much more complex - the code first got the latest restart timestamp and then waited for a new
     * value. But based on the "delay()" method implementation in marklogic-samplestack, we can just keep catching
     * exceptions until the call to get the restart timestamp works.
     *
     * @param action
     */
    public void invokeActionRequiringRestart(ActionRequiringRestart action) {
        logger.info("Executing action that may require restarting MarkLogic");
        boolean requiresRestart = action.execute();
        if (requiresRestart) {
            logger.info("Waiting for MarkLogic to restart...");
            waitForRestart();
        }
    }

    public String getLastRestartTimestamp() {
        return getRestTemplate().getForEntity(adminConfig.buildUri("/admin/v1/timestamp"), String.class).getBody();
    }

    public void waitForRestart() {
        waitForRestartInternal(1);
    }

    private void waitForRestartInternal(int attempt) {
        if (attempt > this.waitForRestartLimit) {
            logger.error("Reached limit of " + waitForRestartLimit
                    + ", and MarkLogic has not restarted yet; check MarkLogic status");
            return;
        }
        try {
            Thread.sleep(waitForRestartCheckInterval);
            getLastRestartTimestamp();
            if (logger.isInfoEnabled()) {
                logger.info("Finished waiting for MarkLogic to restart");
            }
        } catch (Exception ex) {
            attempt++;
            logger.info("Waiting for MarkLogic to restart, attempt: " + attempt);
            if (logger.isTraceEnabled()) {
                logger.trace("Caught exception while waiting for MarkLogic to restart: " + ex.getMessage(), ex);
            }
            waitForRestartInternal(attempt);
        }
    }

    /**
     * Set whether SSL FIPS is enabled on the cluster or not by running against /v1/eval on the given appServicesPort.
     */
    public void setSslFipsEnabled(final boolean enabled, final int appServicesPort) {
        final String xquery = "import module namespace admin = 'http://marklogic.com/xdmp/admin' at '/MarkLogic/admin.xqy'; "
                + "admin:save-configuration(admin:cluster-set-ssl-fips-enabled(admin:get-configuration(), " + enabled
                + "()))";

        invokeActionRequiringRestart(new ActionRequiringRestart() {
            @Override
            public boolean execute() {
				RestConfig evalConfig = new RestConfig(adminConfig);
				evalConfig.setPort(appServicesPort);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
                map.add("xquery", xquery);
                HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map,
					headers);

                if (logger.isInfoEnabled()) {
                    logger.info("Setting SSL FIPS enabled: " + enabled);
                }
				URI url = evalConfig.buildUri("/v1/eval");
				RestTemplateUtil.newRestTemplate(evalConfig).exchange(url, HttpMethod.POST, entity, String.class);
                if (logger.isInfoEnabled()) {
                    logger.info("Finished setting SSL FIPS enabled: " + enabled);
                }
                return true;
            }
        });
    }

    public Fragment getServerConfig() {
        return new Fragment(getRestTemplate().getForObject(adminConfig.buildUri("/admin/v1/server-config"), String.class));
    }

    public String getServerVersion() {
        return getServerConfig().getElementValue("/m:host/m:version");
    }

	/**
	 *
	 * @return
	 * @since 4.6.0
	 */
	public String getServerTimestamp() {
		return getServerConfig().getElementValue("/m:host/m:timestamp");
	}

    public void setWaitForRestartCheckInterval(int waitForRestartCheckInterval) {
        this.waitForRestartCheckInterval = waitForRestartCheckInterval;
    }

    public void setWaitForRestartLimit(int waitForRestartLimit) {
        this.waitForRestartLimit = waitForRestartLimit;
    }


	/**
	 * Part of the steps required to join a cluster. This posts the host config of the host that wants to
	 * join the cluster, to one of the cluster hosts. The cluster host returns a zip of the cluster config
	 * for the joining host to use
	 * @param joiningHostConfig - Output of getServerConfig of the joining host
	 * @param group - The group in the cluster to join
	 * @param zone - String that will get stored as the zone (optional)
	 * @return An array of bytes that represent a zip file of the cluster config
	 * @throws Exception
	 */
	public byte[] postJoiningHostConfig(Fragment joiningHostConfig, String group, String zone) throws Exception {

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("group", group);
		if(zone != null && !zone.isEmpty()){
			map.add("zone", zone);
		}
		map.add("server-config", joiningHostConfig.getPrettyXml());

		HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

		URI url = adminConfig.buildUri("/admin/v1/cluster-config");
		ResponseEntity<byte[]> bytes = getRestTemplate().exchange(url, HttpMethod.POST, entity, byte[].class);
		return bytes.getBody();
	}

	/**
	 * Final step of adding a host to a cluster
	 * Takes the zip file created from calling postJoiningHostConfig, which is the cluster config,
	 * and posts it to the joining host
	 * @param clusterConfigZipBytes Array of bytes that represent a zip file of the cluster config
	 */
	public void postClustConfigToJoiningHost(byte[] clusterConfigZipBytes) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-type", "application/zip");

		URI clusterConfigUri = adminConfig.buildUri("/admin/v1/cluster-config");

		HttpEntity<Resource> resourceEntity = new HttpEntity<>(new ByteArrayResource(clusterConfigZipBytes), headers);
		ResponseEntity<String> response = getRestTemplate().exchange(clusterConfigUri, HttpMethod.POST, resourceEntity, String.class);
		if(response.getStatusCode().value() == 202){
			waitForRestart();
		}
	}

	/**
	 * Instructs the server referred to by this AdminManager to leave the cluster it belongs to.
	 * Note that once it does so, the server will need to be initialized again
	 */
	public void leaveCluster() {
		ResponseEntity<String> response = getRestTemplate().exchange(adminConfig.buildUri("/admin/v1/host-config"), HttpMethod.DELETE, null, String.class);
		if (response.getStatusCode().value() == 202) {
			waitForRestart();
		}
	}

	public AdminConfig getAdminConfig() {
		return adminConfig;
	}

	public RestTemplate getRestTemplate() {
		if (this.restTemplate == null) {
			this.restTemplate = RestTemplateUtil.newRestTemplate(adminConfig);
		}
		return this.restTemplate;
	}
}
