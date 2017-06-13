package com.marklogic.client.ext.tokenreplacer;

/**
 * Interface for objects that replace implementation-defined tokens within a String of text, which typically will be
 * the content of a document before it's written to MarkLogic.
 */
public interface TokenReplacer {

    String replaceTokens(String text);
}
