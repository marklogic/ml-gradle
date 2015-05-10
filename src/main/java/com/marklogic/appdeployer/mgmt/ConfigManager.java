package com.marklogic.appdeployer.mgmt;

import java.io.File;
import java.io.IOException;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.ClassUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.util.RestTemplateUtil;
import com.marklogic.clientutil.LoggingObject;

public class ConfigManager extends LoggingObject {

    private ConfigDir configDir;
    private ManageClient client;

    public ConfigManager(ConfigDir configDir, ManageClient client) {
        this.configDir = configDir;
        this.client = client;
    }

    /**
     * Expect a single JSON/XML file in /rest-apis?
     */
    public void createRestApi(AppConfig config) {
        File f = configDir.getRestApiFile();
        String json = null;
        try {
            json = new String(FileCopyUtils.copyToByteArray(f));
        } catch (IOException ie) {
            throw new RuntimeException(ie);
        }
        new ServiceManager(client).createRestApi(config.getName(), json);
    }

    /**
     * TODO Need to run this against 8000/v1/eval, so we can't really assume that the manage username/password will
     * work, but we'll use that for now.
     * 
     * @param config
     */
    public void uninstallApp(AppConfig config) {
        String xquery = loadStringFromClassPath("uninstall-app.xqy");
        xquery = xquery.replace("%%APP_NAME%%", config.getName());
        logger.info("Uninstalling app with name: " + config.getName());
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
            map.add("xquery", xquery);

            RestTemplate rt = RestTemplateUtil.newRestTemplate("localhost", 8000, "admin", "admin");
            rt.exchange("http://localhost:8000/v1/eval", HttpMethod.POST,
                    new HttpEntity<MultiValueMap<String, String>>(map, headers), String.class);
        } catch (Exception e) {
            logger.warn("Could not uninstall app; it may not be installed yet? Cause: " + e.getMessage());
        }
    }

    protected String loadStringFromClassPath(String path) {
        path = ClassUtils.addResourcePathToPackagePath(getClass(), path);
        try {
            return new String(FileCopyUtils.copyToByteArray(new ClassPathResource(path).getInputStream()));
        } catch (IOException ie) {
            throw new RuntimeException("Unable to load string from classpath resource at: " + path + "; cause: "
                    + ie.getMessage(), ie);
        }
    }
}
