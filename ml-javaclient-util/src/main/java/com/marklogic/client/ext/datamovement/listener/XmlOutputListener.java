/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.ext.datamovement.listener;

import com.marklogic.client.datamovement.ExportToWriterListener;
import com.marklogic.client.document.DocumentRecord;
import com.marklogic.client.impl.XmlFactories;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

/**
 * By default, when an XML document is written to a File using ExportToWriterListener, it will include the XML
 * declaration (this is not because of ExportToWriterListener, it's just how the document is returned by MarkLogic).
 * If you're writing multiple XML documents to a single Writer, you most likely do not want the XML declaration
 * included. If so, pass an instance of this class to ExportToWriterListener.onGenerateOutput, as it defaults to
 * removing the XML declaration.
 */
public class XmlOutputListener implements ExportToWriterListener.OutputListener {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	private boolean omitXmlDeclaration = true;
	private final TransformerFactory transformerFactory;

	public XmlOutputListener() {
		transformerFactory = XmlFactories.makeNewTransformerFactory();
	}

	@Override
	public String generateOutput(DocumentRecord documentRecord) {
		if (Format.XML.equals(documentRecord.getFormat())) {
			return convertDocumentToString(documentRecord.getContent(new DOMHandle()).get());
		} else if (logger.isDebugEnabled()) {
			logger.debug(String.format("Document '%s' has a format of '%s', so will not attempt to remove the XML declaration from it",
				documentRecord.getUri(), documentRecord.getFormat().name()));
		}

		return documentRecord.getContent(new StringHandle()).get();
	}

	protected String convertDocumentToString(Document document) {
		try {
			Transformer transformer = transformerFactory.newTransformer();
			if (omitXmlDeclaration) {
				transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			}
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(document), new StreamResult(writer));
			return writer.toString();
		} catch (Exception e) {
			throw new RuntimeException("Unable to serialize XML document to string: " + e.getMessage(), e);
		}
	}

	public void setOmitXmlDeclaration(boolean omitXmlDeclaration) {
		this.omitXmlDeclaration = omitXmlDeclaration;
	}

}
