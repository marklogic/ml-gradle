package com.marklogic.appdeployer;

import com.marklogic.clientutil.LoggingObject;

public class AbstractManager extends LoggingObject {

    protected String format(String s, Object... args) {
        return String.format(s, args);
    }
}
