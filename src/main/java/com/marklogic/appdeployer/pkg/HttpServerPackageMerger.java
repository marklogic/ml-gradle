package com.marklogic.appdeployer.pkg;

import java.util.List;

import javax.xml.transform.stream.StreamSource;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

public class HttpServerPackageMerger extends AbstractPackageMerger {

    public String mergeHttpServerPackages(List<String> mergePackageFilePaths) {
        try {
            String xml = new String(FileCopyUtils.copyToByteArray(new ClassPathResource(
                    "ml-app-deployer/default-http-server.xml").getInputStream()));
            StreamSource stylesheetSource = new StreamSource(new ClassPathResource(
                    "ml-app-deployer/http-server-transform.xsl").getInputStream());
            return mergePackages(xml, stylesheetSource, mergePackageFilePaths);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
