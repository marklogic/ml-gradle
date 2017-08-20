package com.marklogic.mgmt.mapper;

import com.marklogic.mgmt.api.API;
import com.marklogic.mgmt.api.Resource;
import com.marklogic.mgmt.api.security.Role;

import javax.xml.bind.JAXBContext;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

/**
 * This implementation assumes that the API object that it's given has a Jackson ObjectMapper that's configured to use
 * a lower-case hyphenated naming strategy.
 */
public class DefaultResourceMapper implements ResourceMapper {

	private API api;
	private Map<Class<?>, JAXBContext> jaxbContextMap;

	public DefaultResourceMapper() {
		this.jaxbContextMap = new HashMap<>();
	}

	public DefaultResourceMapper(API api) {
		this();
		this.api = api;
	}

	@Override
	public <T extends Resource> T readResource(String payload, Class<T> resourceType) {
		try {
			T resource = null;
			if (isJsonPayload(payload)) {
				resource = api.getObjectMapper().readerFor(resourceType).readValue(payload);
			}
			else {
				JAXBContext context = jaxbContextMap.get(resourceType);
				if (context == null) {
					context = JAXBContext.newInstance(resourceType);
					jaxbContextMap.put(resourceType, context);
				}
				resource = (T)context.createUnmarshaller().unmarshal(new StringReader(payload));
			}
			if (api != null) {
				resource.setApi(api);
				resource.setObjectMapper(api.getObjectMapper());
			}
			return resource;
		} catch (Exception ex) {
			throw new RuntimeException("Unable to read resource payload: " + ex.getMessage(), ex);
		}
	}

	/**
	 * TODO Move this to util class?
	 *
	 * @param payload
	 * @return
	 */
	protected boolean isJsonPayload(String payload) {
		String s = payload.trim();
		return s.startsWith("{") || s.startsWith("[");
	}
}
