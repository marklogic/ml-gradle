package com.marklogic.appdeployer.pkg;

import java.util.List;

import javax.xml.transform.stream.StreamSource;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ClassUtils;

public class HttpServerPackageMerger extends AbstractPackageMerger {

    public String mergeHttpServerPackages(List<String> mergePackageFilePaths) {
        String transformPath = ClassUtils.addResourcePathToPackagePath(getClass(), "http-server-transform.xsl");
        try {
            String xml = loadStringFromClasspath("default-http-server.xml");
            StreamSource stylesheetSource = new StreamSource(new ClassPathResource(transformPath).getInputStream());
            return mergePackages(xml, stylesheetSource, mergePackageFilePaths);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
