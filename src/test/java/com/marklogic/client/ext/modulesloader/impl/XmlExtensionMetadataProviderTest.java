package com.marklogic.client.ext.modulesloader.impl;

import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.marklogic.client.admin.MethodType;
import com.marklogic.client.admin.ResourceExtensionsManager.MethodParameters;
import com.marklogic.client.ext.modulesloader.ExtensionMetadataAndParams;

public class XmlExtensionMetadataProviderTest extends Assert {

    @Test
    public void test() throws IOException {
        DefaultExtensionMetadataProvider p = new DefaultExtensionMetadataProvider();
        Resource resource = new ClassPathResource("sample-base-dir/services/sample.xqy");
        ExtensionMetadataAndParams emap = p.provideExtensionMetadataAndParams(resource);

        assertEquals("Sample Service", emap.metadata.getTitle());
        assertEquals("<p>You can use <b>HTML</b> in this or any other XML that you like.</p>", emap.metadata.getDescription());
        List<MethodParameters> methods = emap.methods;
        assertEquals(3, methods.size());

        MethodParameters method = methods.get(1);
        assertEquals(MethodType.POST, method.getMethod());
        assertEquals(2, method.size());
        assertEquals("xs:string is the default when no type param is provided", "xs:string", method.get("sourceId")
                .get(0));
        assertEquals("xs:boolean", method.get("edit").get(0));
    }

    @Test
    public void missingMetadata() throws Exception {
        DefaultExtensionMetadataProvider p = new DefaultExtensionMetadataProvider();
        Resource resource = new ClassPathResource("sample-base-dir/services/another-sample.xq");
        ExtensionMetadataAndParams emap = p.provideExtensionMetadataAndParams(resource);
        assertEquals("Title should default to the filename minus the extension", "another-sample", emap.metadata.getTitle());
    }
}
