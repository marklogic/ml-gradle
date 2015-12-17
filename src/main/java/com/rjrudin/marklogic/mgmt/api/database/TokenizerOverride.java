package com.rjrudin.marklogic.mgmt.api.database;

public class TokenizerOverride {

    private String character;
    private String tokenizerClass;

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public String getTokenizerClass() {
        return tokenizerClass;
    }

    public void setTokenizerClass(String tokenizerClass) {
        this.tokenizerClass = tokenizerClass;
    }

}
