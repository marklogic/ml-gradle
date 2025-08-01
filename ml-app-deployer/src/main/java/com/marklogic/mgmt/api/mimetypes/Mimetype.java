/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt.api.mimetypes;

import com.marklogic.mgmt.api.Resource;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.mimetypes.MimetypeManager;
import org.apache.commons.lang3.builder.EqualsBuilder;

import jakarta.xml.bind.annotation.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@XmlRootElement(name = "mimetype-properties")
@XmlAccessorType(XmlAccessType.FIELD)
public class Mimetype extends Resource {

	private String name;

	@XmlElementWrapper(name = "extensions")
	@XmlElement(name = "extension")
	private Set<String> extension;

	private String format;

	public Mimetype() {
		super();
	}

	public Mimetype(String name, String format, String... extensions) {
		this();
		this.name = name;
		this.format = format;
		this.extension = new HashSet<>();
		this.extension.addAll(Arrays.asList(extensions));
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Mimetype)) {
			return false;
		}

		Mimetype other = (Mimetype) obj;
		return new EqualsBuilder()
			.append(this.name, other.name)
			.append(this.extension, other.extension)
			.append(this.format, other.format)
			.isEquals();
	}

	@Override
	protected ResourceManager getResourceManager() {
		return new MimetypeManager(getClient());
	}

	@Override
	protected String getResourceId() {
		return name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<String> getExtension() {
		return extension;
	}

	public void setExtension(Set<String> extension) {
		this.extension = extension;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}
}
