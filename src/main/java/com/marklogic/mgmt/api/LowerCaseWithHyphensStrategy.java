package com.marklogic.mgmt.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy.PropertyNamingStrategyBase;

/**
 * Based on Jackson's LowerCaseWithUnderscoresStrategy class.
 */
public class LowerCaseWithHyphensStrategy extends PropertyNamingStrategyBase {

    private static final long serialVersionUID = -9011452152476418552L;

    @Override
    public String translate(String input) {
        if (input == null) {
            return input;
        }
        int length = input.length();
        StringBuilder result = new StringBuilder(length * 2);
        int resultLength = 0;
        boolean wasPrevTranslated = false;
        for (int i = 0; i < length; i++) {
            char c = input.charAt(i);
            if (i > 0 || c != '-') // skip first starting hyphen
            {
                if (Character.isUpperCase(c)) {
                    if (!wasPrevTranslated && resultLength > 0 && result.charAt(resultLength - 1) != '-') {
                        result.append('-');
                        resultLength++;
                    }
                    c = Character.toLowerCase(c);
                    wasPrevTranslated = true;
                } else {
                    wasPrevTranslated = false;
                }
                result.append(c);
                resultLength++;
            }
        }
        return resultLength > 0 ? result.toString() : input;
    }

}
