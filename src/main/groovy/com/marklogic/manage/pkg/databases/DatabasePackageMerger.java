package com.marklogic.manage.pkg.databases;

import java.util.List;

import javax.xml.transform.stream.StreamSource;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import com.marklogic.manage.pkg.AbstractPackageMerger;

public class DatabasePackageMerger extends AbstractPackageMerger {

    public String mergeDatabasePackages(List<String> mergePackageFilePaths) {
        try {
            String xml = new String(FileCopyUtils.copyToByteArray(new ClassPathResource("ml-gradle/default-content-database.xml")
                    .getInputStream()));
            StreamSource stylesheetSource = new StreamSource(
                    new ClassPathResource("ml-gradle/content-database-transform.xsl").getInputStream());
            return mergePackages(xml, stylesheetSource, mergePackageFilePaths);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
