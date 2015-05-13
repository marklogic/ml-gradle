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

/**
 * Sequence - add security stuff, which is global to ML. Create a REST API. Update the content databases. Update the
 * module databases (low priority). Update the REST servers. Then load the modules, or start doing things like CPF and
 * scheduled tasks?
 */
public class ConfigManager extends LoggingObject {

    private ManageClient client;
    private AppServicesConfig appServicesConfig;

    public ConfigManager(ManageClient client) {
        this.client = client;
    }

    public void createRestApi(ConfigDir configDir, AppConfig config) {
        File f = configDir.getRestApiFile();
        String input = copyFileToString(f);

        ServiceManager mgr = new ServiceManager(client);

        String body = replaceRestApiTokens(input, config);
        mgr.createRestApi(config.getRestServerName(), body);

        if (config.isTestPortSet()) {
            body = replaceRestApiTokens(input, config);
            mgr.createRestApi(config.getTestRestServerName(), body);
        }
    }

    public void updateDatabases(ConfigDir configDir, AppConfig config) {
        File f = configDir.getContentDatabaseFile();
        if (f.exists()) {

        } else {
            logger.info("Not updating content databases, no database file found at: " + f.getAbsolutePath());
        }
    }

    protected String replaceRestApiTokens(String input, AppConfig config) {
        input = input.replace("%%NAME%%", config.getRestServerName());
        input = input.replace("%%GROUP%%", config.getGroupName());
        input = input.replace("%%DATABASE%%", config.getContentDatabaseName());
        input = input.replace("%%MODULES-DATABASE%%", config.getModulesDatabaseName());
        input = input.replace("%%PORT%%", config.getRestPort() + "");
        return input;
    }

    protected String replaceTestRestApiTokens(String input, AppConfig config) {
        input = input.replace("%%NAME%%", config.getTestRestServerName());
        input = input.replace("%%GROUP%%", config.getGroupName());
        input = input.replace("%%DATABASE%%", config.getTestContentDatabaseName());
        input = input.replace("%%MODULES-DATABASE%%", config.getModulesDatabaseName());
        input = input.replace("%%PORT%%", config.getTestRestPort() + "");
        return input;
    }

    public void uninstallApp(AppConfig config) {
        if (appServicesConfig == null) {
            throw new IllegalStateException("Cannot uninstall an app without an instance of AppServicesConfig set");
        }
        String xquery = loadStringFromClassPath("uninstall-app.xqy");
        xquery = xquery.replace("%%APP_NAME%%", config.getName());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("xquery", xquery);

        logger.info("Uninstalling app with name: " + config.getName());
        try {
            RestTemplate rt = RestTemplateUtil.newRestTemplate(appServicesConfig);
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

    protected String copyFileToString(File f) {
        try {
            return new String(FileCopyUtils.copyToByteArray(f));
        } catch (IOException ie) {
            throw new RuntimeException("Unable to copy file to string from path: " + f.getAbsolutePath() + "; cause: "
                    + ie.getMessage(), ie);
        }
    }

    public void setAppServicesConfig(AppServicesConfig appServicesConfig) {
        this.appServicesConfig = appServicesConfig;
    }
}
