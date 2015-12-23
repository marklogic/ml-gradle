package com.marklogic.client.modulesloader.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

import com.marklogic.client.admin.ExtensionMetadata;
import com.marklogic.client.admin.ExtensionMetadata.ScriptLanguage;
import com.marklogic.client.admin.MethodType;
import com.marklogic.client.admin.ResourceExtensionsManager.MethodParameters;
import com.marklogic.client.helper.FilenameUtil;
import com.marklogic.client.helper.LoggingObject;
import com.marklogic.client.modulesloader.ExtensionMetadataAndParams;
import com.marklogic.client.modulesloader.ExtensionMetadataProvider;


public class DefaultExtensionMetadataProvider extends LoggingObject implements ExtensionMetadataProvider {

    @Override
    public ExtensionMetadataAndParams provideExtensionMetadataAndParams(File resourceFile) {
        File metadataDir = new File(resourceFile.getParent(), "metadata");
        File metadataFile = new File(metadataDir, getFilenameMinusExtension(resourceFile) + ".xml");

        ExtensionMetadata m = new ExtensionMetadata();
        List<MethodParameters> paramList = new ArrayList<>();

        if (FilenameUtil.isJavascriptFile(resourceFile.getName())) {
            m.setScriptLanguage(ScriptLanguage.JAVASCRIPT);
            m.setVersion("1.0");
        }

        if (metadataFile.exists()) {
            try {
                Element root = new SAXBuilder().build(metadataFile).getRootElement();
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
            } catch (Exception e) {
                logger.warn("Unable to build metadata from resource file: " + resourceFile.getAbsolutePath()
                        + "; cause: " + e.getMessage(), e);
                setDefaults(m, resourceFile);
            }
        } else {
            setDefaults(m, resourceFile);
        }

        return new ExtensionMetadataAndParams(m, paramList);
    }

    protected String getFilenameMinusExtension(File file) {
        // Would think there's an easier way to do this in Java...
        String[] tokens = file.getName().split("\\.");
        tokens = Arrays.copyOfRange(tokens, 0, tokens.length - 1);
        String filename = tokens[0];
        for (int i = 1; i < tokens.length; i++) {
            filename += "." + tokens[i];
        }
        return filename;
    }

    private void setDefaults(ExtensionMetadata metadata, File resourceFile) {
        metadata.setTitle(getFilenameMinusExtension(resourceFile));
    }
}
