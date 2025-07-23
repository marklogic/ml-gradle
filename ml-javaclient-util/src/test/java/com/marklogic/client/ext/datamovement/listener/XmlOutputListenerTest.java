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
package com.marklogic.client.ext.datamovement.listener;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class XmlOutputListenerTest {

    private XmlOutputListener sut = new XmlOutputListener();
    private Document document;

    @BeforeEach
    public void setup() throws Exception {
        String xml = "<test>test</test>";
        document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes()));
    }

    @Test
    public void noXmlDeclaration() {
        sut.setOmitXmlDeclaration(true);
        String xml = sut.convertDocumentToString(document);
        assertEquals("<test>test</test>", xml);
    }

    @Test
    public void withXmlDeclaration() {
        sut.setOmitXmlDeclaration(false);
        String xml = sut.convertDocumentToString(document);
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><test>test</test>", xml);
    }
}
