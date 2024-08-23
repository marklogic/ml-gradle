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

import com.marklogic.client.admin.ExtensionMetadata;
import com.marklogic.client.admin.ExtensionMetadata.ScriptLanguage;
import com.marklogic.client.admin.MethodType;
import com.marklogic.client.admin.ResourceExtensionsManager.MethodParameters;
import com.marklogic.client.ext.helper.FilenameUtil;
import com.marklogic.client.ext.helper.LoggingObject;
import com.marklogic.client.ext.modulesloader.ExtensionMetadataAndParams;
import com.marklogic.client.ext.modulesloader.ExtensionMetadataProvider;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DefaultExtensionMetadataProvider extends LoggingObject implements ExtensionMetadataProvider {

    private ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

    public ExtensionMetadataAndParams provideExtensionMetadataAndParams(Resource r) {
        String filename = getFilenameMinusExtension(r);
        URL url = null;
        try {
            url = r.getURL();
        } catch (IOException ie) {
            throw new RuntimeException(ie);
        }
        String metadataPath = url.toString().replace(r.getFilename(), "");
        String metadataFile = metadataPath + "metadata/" + filename + ".xml";

        ExtensionMetadata m = new ExtensionMetadata();
        List<MethodParameters> paramList = new ArrayList<>();

        if (FilenameUtil.isJavascriptFile(r.getFilename())) {
            m.setScriptLanguage(ScriptLanguage.JAVASCRIPT);
            m.setVersion("1.0");
        }

        Resource metadataResource = resolver.getResource(metadataFile);
        if (metadataResource != null) {
            try {
                Element root = new SAXBuilder().build(metadataResource.getInputStream()).getRootElement();
                m.setTitle(root.getChildText("title"));
                Element desc = root.getChild("description");
                if (desc.getChildren() != null && desc.getChildren().size() == 1) {
                    m.setDescription(new XMLOutputter().outputString(desc.getChildren().get(0)));
                } else {
                    m.setDescription(desc.getText());
                }
                for (Element method : root.getChildren("method")) {
                    MethodParameters mp = new MethodParameters(MethodType.valueOf(method.getAttributeValue("name")));
                    paramList.add(mp);
                    for (Element param : method.getChildren("param")) {
                        String name = param.getAttributeValue("name");
                        String type = "xs:string";
                        if (param.getAttribute("type") != null) {
                            type = param.getAttributeValue("type");
                        }
                        mp.add(name, type);
                    }
                }
            } catch (IOException ie) {
                // Log at debug level, this may just be due to the file missing
                logger.debug("Unable to build metadata from resource file: " + url.toString() + "; cause: "
                        + ie.getMessage());
                setDefaults(m, r);
            } catch (Exception e) {
                logger.warn("Unable to build metadata from resource file: " + url.toString() + "; cause: "
                        + e.getMessage());
                setDefaults(m, r);
            }
        } else {
            setDefaults(m, r);
        }

        return new ExtensionMetadataAndParams(m, paramList);
    }

    protected String getFilenameMinusExtension(Resource file) {
        // Would think there's an easier way to do this in Java...
        String[] tokens = file.getFilename().split("\\.");
        tokens = Arrays.copyOfRange(tokens, 0, tokens.length - 1);
        String filename = tokens[0];
        for (int i = 1; i < tokens.length; i++) {
            filename += "." + tokens[i];
        }
        return filename;
    }

    private void setDefaults(ExtensionMetadata metadata, Resource resourceFile) {
        metadata.setTitle(getFilenameMinusExtension(resourceFile));
    }
}
