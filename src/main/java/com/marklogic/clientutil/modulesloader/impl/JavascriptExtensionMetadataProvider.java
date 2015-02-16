package com.marklogic.clientutil.modulesloader.impl;

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
import com.marklogic.clientutil.LoggingObject;
import com.marklogic.clientutil.modulesloader.ExtensionMetadataAndParams;
import com.marklogic.clientutil.modulesloader.ExtensionMetadataProvider;

public class JavascriptExtensionMetadataProvider extends LoggingObject implements ExtensionMetadataProvider {

    @Override
    public ExtensionMetadataAndParams provideExtensionMetadataAndParams(File resourceFile) {
        File metadataDir = new File(resourceFile.getParent(), "metadata");
        File metadataFile = new File(metadataDir, getFilenameMinusExtension(resourceFile) + ".sjs");

        ExtensionMetadata m = new ExtensionMetadata();
        List<MethodParameters> paramList = new ArrayList<>();

        m.setScriptLanguage(ScriptLanguage.JAVASCRIPT);
        m.setVersion("1.0");

        setDefaults(m, resourceFile);

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
