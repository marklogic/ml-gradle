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
package com.marklogic.mgmt.api.mimetypes;

import com.marklogic.mgmt.api.Resource;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.mimetypes.MimetypeManager;
import org.apache.commons.lang3.builder.EqualsBuilder;

import javax.xml.bind.annotation.*;
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
