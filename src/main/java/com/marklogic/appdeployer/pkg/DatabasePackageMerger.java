package com.marklogic.appdeployer.pkg;

import java.util.List;

import javax.xml.transform.stream.StreamSource;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

public class DatabasePackageMerger extends AbstractPackageMerger {

    public String mergeDatabasePackages(List<String> mergePackageFilePaths) {
        try {
            String xml = new String(FileCopyUtils.copyToByteArray(new ClassPathResource(
                    "ml-app-deployer/default-content-database.xml").getInputStream()));
            StreamSource stylesheetSource = new StreamSource(new ClassPathResource(
                    "ml-app-deployer/content-database-transform.xsl").getInputStream());
            return mergePackages(xml, stylesheetSource, mergePackageFilePaths);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
