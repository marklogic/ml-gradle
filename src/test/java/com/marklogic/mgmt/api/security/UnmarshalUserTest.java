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
package com.marklogic.mgmt.api.security;

import com.marklogic.mgmt.mapper.DefaultResourceMapper;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class UnmarshalUserTest  {

	@Test
	public void xmlSmokeTest() {
		String xml = "<user-properties xmlns=\"http://marklogic.com/manage\">\n" +
			"  <user-name>infostudio-admin</user-name>\n" +
			"  <description>Information Studio CPF pipeline and task runner</description>\n" +
			"  <roles>\n" +
			"    <role>cpf-restart</role>\n" +
			"    <role>infostudio-user</role>\n" +
			"  </roles>\n" +
			"</user-properties>\n";

		User user = new DefaultResourceMapper().readResource(xml, User.class);
		assertEquals("infostudio-admin", user.getUserName());
		assertEquals(2, user.getRole().size());
	}
}
