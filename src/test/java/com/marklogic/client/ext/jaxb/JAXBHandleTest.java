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
package com.marklogic.client.ext.jaxb;

import com.marklogic.client.io.JAXBHandle;
import org.junit.jupiter.api.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlRootElement;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JAXBHandleTest {

	/**
	 * Included solely to verify that when the test is run using Java 11 or Java 17, it will succeed given that the
	 * testImplementation configuration in Gradle imports the necessary "old" JAXB libraries.
	 *
	 * @throws JAXBException
	 */
	@Test
	void simpleTest() throws JAXBException {
		JAXBHandle handle = new JAXBHandle(JAXBContext.newInstance(Product.class));
		Product p = new Product();
		p.setName("My name");
		p.setIndustry("My industry");
		handle.set(p);
		String xml = handle.toString();

		Product p2 = (Product) handle.bytesToContent(xml.getBytes());
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
