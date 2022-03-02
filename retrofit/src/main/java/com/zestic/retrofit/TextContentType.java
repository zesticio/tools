package com.zestic.retrofit;

import java.util.HashMap;
import java.util.Map;

/**
 * @author deebendukumar
 */
public enum TextContentType {

    TEXT_CSS("text/css", "text/css"),
    TEXT_CSV("text/csv", "text/csv"),
    TEXT_HTML("text/html", "text/html"),
    TEXT_JAVASCRIPT("text/javascript", "text/javascript"),
    TEXT_PLAIN("text/plain", "text/plain"),
    TEXT_XML("text/xml", "text/xml");

    private static final Map<String, TextContentType> LOOKUP = new HashMap<>();

    static {
        for (final TextContentType enumeration : TextContentType.values()) {
            LOOKUP.put(enumeration.getCode(), enumeration);
        }
    }

    private final String code;
    private final String message;

    private TextContentType(final String code, final String message) {
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
