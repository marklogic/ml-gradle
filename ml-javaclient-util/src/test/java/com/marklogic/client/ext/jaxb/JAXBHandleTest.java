/*
 * Copyright (c) 2015-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.ext.jaxb;

import com.marklogic.client.io.JAXBHandle;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JAXBHandleTest {

	/**
	 * Included solely to verify that when the test is run using Java 17 or higher, it will succeed given that the
	 * testImplementation configuration in Gradle imports the necessary "old" JAXB libraries.
	 *
	 * @throws JAXBException
	 */
	@Test
	void simpleTest() throws JAXBException {
		JAXBHandle<Product> handle = new JAXBHandle<>(JAXBContext.newInstance(Product.class));
		Product p = new Product();
		p.setName("My name");
		p.setIndustry("My industry");
		handle.set(p);
		String xml = handle.toString();

		Product p2 = handle.bytesToContent(xml.getBytes());
		assertEquals("My name", p2.getName());
		assertEquals("My industry", p2.getIndustry());
	}

	@XmlRootElement
	static public class Product {
		private String name;
		private String industry;

		public Product() {
			super();
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getIndustry() {
			return industry;
		}

		public void setIndustry(String industry) {
			this.industry = industry;
		}
	}

}
