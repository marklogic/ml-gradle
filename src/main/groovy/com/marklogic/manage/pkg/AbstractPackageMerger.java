package com.marklogic.manage.pkg;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.List;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * Not a great use for an abstract class, just making this to avoid duplication for now.
 */
public abstract class AbstractPackageMerger {

    private TransformerFactory transformerFactory;

    public String mergePackages(String initialPackageXml, Source stylesheetSource,
            List<String> mergePackageFilePaths) {
        try {
            if (transformerFactory == null) {
                transformerFactory = TransformerFactory.newInstance();
            }

            Transformer t = transformerFactory.newTransformer(stylesheetSource);

            String xml = initialPackageXml;
            for (String path : mergePackageFilePaths) {
                t.setParameter("mergePackageFilePath", path);
                StringWriter sw = new StringWriter();
                ByteArrayInputStream bais = new ByteArrayInputStream(xml.getBytes());
                t.transform(new StreamSource(bais), new StreamResult(sw));
                xml = sw.toString();
            }

            return xml;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
