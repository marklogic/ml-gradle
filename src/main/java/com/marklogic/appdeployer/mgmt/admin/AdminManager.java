package com.marklogic.appdeployer.mgmt.admin;

import org.springframework.web.client.RestTemplate;

import com.marklogic.appdeployer.AbstractManager;
import com.marklogic.appdeployer.util.RestTemplateUtil;

public class AdminManager extends AbstractManager {

    private int waitForRestartCheckInterval = 500;
    private RestTemplate restTemplate;
    private AdminConfig adminConfig;

    public AdminManager(AdminConfig adminConfig) {
        this.adminConfig = adminConfig;
        this.restTemplate = RestTemplateUtil.newRestTemplate(adminConfig);
    }

    public void invokeActionRequiringRestart(ActionRequiringRestart action) {
        String lastRestartTimestamp = getLastRestartTimestamp();
        boolean requiresRestart = action.execute();
        if (requiresRestart) {
            waitForRestart(lastRestartTimestamp);
        }
    }
    
    public String getLastRestartTimestamp() {
        return restTemplate.getForEntity(adminConfig.getBaseUrl() + "/admin/v1/timestamp", String.class).getBody();
    }

    public void waitForRestart(String lastRestartTimestamp) {
        logger.info("Waiting for MarkLogic to restart, last restart timestamp: " + lastRestartTimestamp);
        logger.info("Ignore any HTTP client logging about socket exceptions and retries, those are expected while waiting for MarkLogic to restart");
        try {
            while (true) {
                sleepUntilNextRestartCheck();
                String restart = getLastRestartTimestamp();
                if (restart != null && !restart.equals(lastRestartTimestamp)) {
                    logger.info(String
                            .format("MarkLogic has successfully restarted; new restart timestamp [%s] is greater than last restart timestamp [%s]",
                                    restart, lastRestartTimestamp));
                    break;
                }
            }
        } catch (Exception e) {
            String message = "Caught exception while waiting for MarkLogic to restart: " + e.getMessage();
            if (logger.isDebugEnabled()) {
                logger.warn(message, e);
            } else {
                logger.warn(message);
            }
        }
    }

    protected void sleepUntilNextRestartCheck() {
        try {
            Thread.sleep(waitForRestartCheckInterval);
        } catch (Exception e) {
            // ignore
        }
    }

    public void setWaitForRestartCheckInterval(int waitForRestartCheckInterval) {
        this.waitForRestartCheckInterval = waitForRestartCheckInterval;
    }

}
