package com.marklogic.client.modulesloader.xcc;

import com.marklogic.client.io.Format;
import com.marklogic.client.modulesloader.impl.FormatGetter;
import com.marklogic.xcc.DocumentFormat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Default impl. Feel free to enhance this, subclass it, or roll your own.
 */
public class DefaultDocumentFormatGetter implements DocumentFormatGetter, FormatGetter {

	public final static String[] DEFAULT_BINARY_EXTENSIONS = new String[]{".swf", ".jpeg", ".jpg", ".png", ".gif",
		".svg", ".ttf", ".eot", ".woff", ".woff2", ".otf", ".cur", ".ico"};

	public final static String[] DEFAULT_XML_EXTENSIONS = new String[]{"xml", "xsl", "xslt", "xsd", "tdex"};
	public final static String[] DEFAULT_JSON_EXTENSIONS = new String[]{"json", "tdej"};

	private List<String> binaryExtensions = new ArrayList<String>();
	private List<String> xmlExtensions = new ArrayList<>();
	private List<String> jsonExtensions = new ArrayList<>();

	public DefaultDocumentFormatGetter() {
		for (String ext : DEFAULT_BINARY_EXTENSIONS) {
			binaryExtensions.add(ext);
		}
		for (String ext : DEFAULT_XML_EXTENSIONS) {
			xmlExtensions.add(ext);
		}
		for (String ext : DEFAULT_JSON_EXTENSIONS) {
			jsonExtensions.add(ext);
		}
	}

	@Override
	public Format getFormat(File file) {
		String name = file.getName();

		for (String ext : xmlExtensions) {
			if (name.endsWith(ext)) {
				return Format.XML;
			}
		}

		for (String ext : jsonExtensions) {
			if (name.endsWith(ext)) {
				return Format.JSON;
			}
		}

		boolean isBinary = false;
		for (String ext : binaryExtensions) {
			if (name.endsWith(ext)) {
				isBinary = true;
				break;
			}
		}
		return isBinary ? Format.BINARY : Format.TEXT;
	}

	@Override
	public DocumentFormat getDocumentFormat(File file) {
		String name = file.getName();

		for (String ext : xmlExtensions) {
			if (name.endsWith(ext)) {
				return DocumentFormat.XML;
			}
		}

		for (String ext : jsonExtensions) {
			if (name.endsWith(ext)) {
				return DocumentFormat.JSON;
			}
		}

		boolean isBinary = false;
		for (String ext : binaryExtensions) {
			if (name.endsWith(ext)) {
				isBinary = true;
				break;
			}
		}
		return isBinary ? DocumentFormat.BINARY : DocumentFormat.TEXT;
	}

	public List<String> getBinaryExtensions() {
		return binaryExtensions;
	}

	public void setBinaryExtensions(List<String> binaryExtensions) {
		this.binaryExtensions = binaryExtensions;
	}

	public List<String> getXmlExtensions() {
		return xmlExtensions;
	}

	public void setXmlExtensions(List<String> xmlExtensions) {
		this.xmlExtensions = xmlExtensions;
	}

	public List<String> getJsonExtensions() {
		return jsonExtensions;
	}

	public void setJsonExtensions(List<String> jsonExtensions) {
		this.jsonExtensions = jsonExtensions;
	}
}
