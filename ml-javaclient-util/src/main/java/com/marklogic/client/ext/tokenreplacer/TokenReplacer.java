/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.ext.tokenreplacer;

/**
 * Interface for objects that replace implementation-defined tokens within a String of text, which typically will be
 * the content of a document before it's written to MarkLogic.
 */
public interface TokenReplacer {

    String replaceTokens(String text);
}
