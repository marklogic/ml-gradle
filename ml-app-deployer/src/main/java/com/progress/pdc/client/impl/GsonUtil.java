/*
 * Copyright (c) 2015-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.progress.pdc.client.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.progress.pdc.client.generated.JSON;

public interface GsonUtil {

    static Gson createGson() {
        GsonBuilder gsonBuilder = JSON.createGson();

        /**
         * Copilot's explanation for registering these:
         *
         * The type adapters in GsonUtil are needed because Gson doesn't natively support Java 8+ date/time types and
         * byte arrays. Without these adapters, Gson would fail to serialize/deserialize:
         *
         * OffsetDateTime - For dates with timezone offsets (e.g., "2025-12-02T18:48:19Z")
         * LocalDate - For dates without time (e.g., "2025-12-02")
         * Date - For java.util.Date objects
         * SqlDate - For java.sql.Date objects
         * ByteArray - For byte[] data, typically encoded as Base64 in JSON
         *
         * These are all common types used in the generated OpenAPI client models (like ApiKeyModel with its
         * expiryDate field). The adapters handle converting between the JSON string representation and the Java
         * objects, using proper formatting like ISO-8601 for dates.
         */
        gsonBuilder.registerTypeAdapter(java.time.OffsetDateTime.class, new JSON.OffsetDateTimeTypeAdapter());
        gsonBuilder.registerTypeAdapter(java.time.LocalDate.class, new JSON.LocalDateTypeAdapter());
        gsonBuilder.registerTypeAdapter(java.util.Date.class, new JSON.DateTypeAdapter());
        gsonBuilder.registerTypeAdapter(java.sql.Date.class, new JSON.SqlDateTypeAdapter());
        gsonBuilder.registerTypeAdapter(byte[].class, new JSON.ByteArrayAdapter());

        // For now, default to pretty-printing for ease of use.
        gsonBuilder.setPrettyPrinting();

        // Accounts for case-sensitive naming bugs in the OpenAPI spec.
        gsonBuilder.registerTypeAdapterFactory(new CaseInsensitiveTypeAdapterFactory());

        return gsonBuilder.create();
    }
}
