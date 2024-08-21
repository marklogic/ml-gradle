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
package com.marklogic.mgmt.selector;

import com.marklogic.mgmt.ManageClient;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class PropertiesResourceSelector implements ResourceSelector {

	private Properties props;

	public PropertiesResourceSelector(File file) {
		props = new Properties();
		try {
			FileReader reader = new FileReader(file);
			props.load(reader);
			reader.close();
		} catch (IOException ie) {
			throw new RuntimeException(ie);
		}
	}

	public PropertiesResourceSelector(Properties props) {
		this.props = props;
	}

	@Override
	public ResourceSelection selectResources(ManageClient manageClient) {
		MapResourceSelection selection = new MapResourceSelection();

		for (String prop : props.stringPropertyNames()) {
			String[] values = props.getProperty(prop).split(",");
			for (String value : values) {
				selection.select(prop, value);
			}
		}

		return selection;
	}
}
