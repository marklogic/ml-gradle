package com.marklogic.client.ext.datamovement.listener;

import com.marklogic.client.datamovement.ExportToWriterListener;
import com.marklogic.client.document.DocumentRecord;
import com.marklogic.client.ext.helper.LoggingObject;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import org.w3c.dom.Document;

import java.io.IOException;
import java.io.StringWriter;

/**
 * By default, when an XML document is written to a File using ExportToWriterListener, it will include the XML
 * declaration (this is not because of ExportToWriterListener, it's just how the document is returned by MarkLogic).
 * If you're writing multiple XML documents to a single Writer, you most likely do not want the XML declaration
 * included. If so, pass an instance of this class to ExportToWriterListener.onGenerateOutput, as it defaults to
 * removing the XML declaration.
 */
public class XmlOutputListener extends LoggingObject implements ExportToWriterListener.OutputListener {

	private boolean omitXmlDeclaration = true;

	@Override
	public String generateOutput(DocumentRecord documentRecord) {
		if (Format.XML.equals(documentRecord.getFormat())) {
			DOMHandle handle = documentRecord.getContent(new DOMHandle());
			Document document = handle.get();

			OutputFormat format = new OutputFormat(handle.get());
			format.setOmitXMLDeclaration(omitXmlDeclaration);

			StringWriter writer = new StringWriter();
			try {
				new XMLSerializer(writer, format).serialize(document);
				return writer.toString();
			} catch (IOException e) {
				throw new RuntimeException("Unable to serialize XML document to string: " + e.getMessage(), e);
			}
		} else if (logger.isDebugEnabled()) {
			logger.debug(format("Document '%s' has a format of '%s', so will not attempt to remove the XML declaration from it",
				documentRecord.getUri(), documentRecord.getFormat().name()));
		}

		return documentRecord.getContent(new StringHandle()).get();
	}

	public void setOmitXmlDeclaration(boolean omitXmlDeclaration) {
		this.omitXmlDeclaration = omitXmlDeclaration;
	}

}
