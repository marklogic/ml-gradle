package com.marklogic.appdeployer.pkg;

import java.util.List;

import javax.xml.transform.stream.StreamSource;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ClassUtils;

public class DatabasePackageMerger extends AbstractPackageMerger {

    public String mergeDatabasePackages(List<String> mergePackageFilePaths) {
        String transformPath = ClassUtils.addResourcePathToPackagePath(getClass(), "content-database-transform.xsl");
        try {
            String xml = loadStringFromClasspath("default-content-database.xml");
            StreamSource stylesheetSource = new StreamSource(new ClassPathResource(transformPath).getInputStream());
            return mergePackages(xml, stylesheetSource, mergePackageFilePaths);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
