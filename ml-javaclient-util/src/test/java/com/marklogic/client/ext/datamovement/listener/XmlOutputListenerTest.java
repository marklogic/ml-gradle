/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
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
