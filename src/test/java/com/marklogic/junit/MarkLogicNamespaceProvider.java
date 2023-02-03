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
package com.marklogic.junit;

import java.util.ArrayList;
import java.util.List;

import org.jdom2.Namespace;

/**
 * Implementation of NamespaceProvider that registers a handful of commonly-used MarkLogic namespaces and prefixes.
 */
public class MarkLogicNamespaceProvider implements NamespaceProvider {

	@Override
	public Namespace[] getNamespaces() {
		return buildListOfNamespaces().toArray(new Namespace[] {});
	}

	protected List<Namespace> buildListOfNamespaces() {
		List<Namespace> list = new ArrayList<>();
		add(list, "admin", "http://marklogic.com/xdmp/admin");
		add(list, "cts", "http://marklogic.com/cts");
		add(list, "prop", "http://marklogic.com/xdmp/property");
		add(list, "search", "http://marklogic.com/appservices/search");
		add(list, "sec", "http://marklogic.com/xdmp/security");
		add(list, "sem", "http://marklogic.com/semantics");
		return list;
	}

	private void add(List<Namespace> list, String prefix, String uri) {
		list.add(Namespace.getNamespace(prefix, uri));
	}
}
