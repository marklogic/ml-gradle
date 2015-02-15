package com.marklogic.manage.pkg.servers;

import java.util.List;

import javax.xml.transform.stream.StreamSource;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import com.marklogic.manage.pkg.AbstractPackageMerger;

public class HttpServerPackageMerger extends AbstractPackageMerger {

    public String mergeHttpServerPackages(List<String> mergePackageFilePaths) {
        try {
            String xml = new String(FileCopyUtils.copyToByteArray(new ClassPathResource("ml-gradle/default-http-server.xml")
                    .getInputStream()));
            StreamSource stylesheetSource = new StreamSource(
                    new ClassPathResource("ml-gradle/http-server-transform.xsl").getInputStream());
            return mergePackages(xml, stylesheetSource, mergePackageFilePaths);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
