package com.marklogic.gradle.task.manage;

import java.util.ArrayList;
import java.util.List;

import com.marklogic.gradle.RestHelper;

public class DatabasePackageManager {

    private String appName;

    public DatabasePackageManager(String appName) {
        this.appName = appName;
    }

    /**
     * @param rh
     * @param contentDatabaseFilePath
     * @param includeTestDatabase
     * @param format
     * @return a list of the names of databases that were added to the package. The package name is based on the appName
     *         argument passed into the constructor.
     */
    public List<String> addContentDatabasesToPackage(RestHelper rh, String contentDatabaseFilePath,
            boolean includeTestDatabase, String format) {
        List<String> databaseNames = new ArrayList<>();

        String packageName = getPackageName();
        rh.addDatabase(packageName, getContentDatabaseName(), contentDatabaseFilePath, format);
        databaseNames.add(getContentDatabaseName());

        if (includeTestDatabase) {
            rh.addDatabase(packageName, getTestContentDatabaseName(), contentDatabaseFilePath, format);
            databaseNames.add(getTestContentDatabaseName());
        }

        return databaseNames;
    }

    public String getPackageName() {
        return appName + "-package";
    }

    public String getContentDatabaseName() {
        return appName + "-content";
    }

    public String getTestContentDatabaseName() {
        return appName + "-test-content";
    }
}
