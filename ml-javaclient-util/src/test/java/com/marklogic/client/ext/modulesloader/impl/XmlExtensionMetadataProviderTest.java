/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.client.ext.modulesloader.impl;

import com.marklogic.client.admin.MethodType;
import com.marklogic.client.admin.ResourceExtensionsManager.MethodParameters;
import com.marklogic.client.ext.modulesloader.ExtensionMetadataAndParams;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class XmlExtensionMetadataProviderTest {

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
        assertEquals("xs:string", method.get("sourceId").get(0), "xs:string is the default when no type param is provided");
        assertEquals("xs:boolean", method.get("edit").get(0));
    }

    @Test
    public void missingMetadata() throws Exception {
        DefaultExtensionMetadataProvider p = new DefaultExtensionMetadataProvider();
        Resource resource = new ClassPathResource("sample-base-dir/services/another-sample.xq");
        ExtensionMetadataAndParams emap = p.provideExtensionMetadataAndParams(resource);
        assertEquals("another-sample", emap.metadata.getTitle(), "Title should default to the filename minus the extension");
    }
}
