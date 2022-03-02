package com.zestic.retrofit.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author deebendukumar
 */
public enum DateFormat {

    ISO_8601("yyyy-MM-dd'T'HH:mm:ssX", "yyyy-MM-dd'T'HH:mm:ssX"),
    DATE_TIME("yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss"),
    DATE("yyyy-MM-dd", "yyyy-MM-dd");

    private static final Map<String, DateFormat> LOOKUP = new HashMap<>();

    static {
        for (final DateFormat enumeration : DateFormat.values()) {
            LOOKUP.put(enumeration.getCode(), enumeration);
        }
    }

    private final String code;
    private final String message;

    private DateFormat(final String code, final String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
