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
package com.marklogic.rest.util;

import org.jdom2.Element;
import org.jdom2.Namespace;

import java.util.List;

/**
 * Provides some convenience methods for the XML response from resources endpoints
 */
public class ResourcesFragment extends Fragment {

    public ResourcesFragment(Fragment f) {
        super(f);
    }

    public ResourcesFragment(String xml, Namespace[] namespaces) {
        super(xml, namespaces);
    }

    public int getResourceCount() {
        return Integer.parseInt(getElementValues(
                "/node()/*[local-name(.) = 'list-items']/*[local-name(.) = 'list-count']").get(0));
    }

    public boolean resourceExists(String resourceIdOrName) {
        String xpath = "/node()/*[local-name(.) = 'list-items']/node()"
                + "[*[local-name(.) = 'nameref'] = '%s' or *[local-name(.) = 'idref'] = '%s']";
        xpath = String.format(xpath, resourceIdOrName, resourceIdOrName);
        return elementExists(xpath);
    }

    public String getIdForNameOrId(String resourceIdOrName) {
        return getListItemValue(resourceIdOrName, "idref");
    }

    public String getUriRefForNameRef(String nameref) {
    	return getListItemValue(nameref, "uriref");
    }

    public String getNameRefForUriRef(String uriRef) {
	    String xpath = "/node()/*[local-name(.) = 'list-items']/node()"
		    + "[*[local-name(.) = 'uriref'] = '%s']/*[local-name(.) = 'nameref']";
	    xpath = String.format(xpath, uriRef);
	    return getElementValue(xpath);
    }

    public String getListItemValue(String resourceIdOrName, String elementLocalName) {
        String xpath = "/node()/*[local-name(.) = 'list-items']/node()"
                + "[*[local-name(.) = 'nameref'] = '%s' or *[local-name(.) = 'idref'] = '%s']/*[local-name(.) = '%s']";
        xpath = String.format(xpath, resourceIdOrName, resourceIdOrName, elementLocalName);
        return getElementValue(xpath);
    }

    public List<String> getListItemIdRefs() {
        return getListItemValues("idref");
    }

    public List<String> getListItemNameRefs() {
        return getListItemValues("nameref");
    }

    public List<String> getListItemValues(String elementName) {
        String xpath = "/node()/*[local-name(.) = 'list-items']/node()/*[local-name(.) = '%s']";
        return getElementValues(String.format(xpath, elementName));
    }

    public List<Element> getListItems() {
    	return evaluateForElements("/node()/*[local-name(.) = 'list-items']/node()[local-name(.) = 'list-item']");
    }

}
