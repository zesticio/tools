package com.zestic.common.entity;

import com.zestic.common.utils.Error;

import java.util.HashMap;
import java.util.Map;

/**
 * Error code for USER module.
 */
public enum MessageType {

    TEXT_MESSAGE(0x100000, "Test Message"),
    OBJECT_MESSAGE(0x100001, "Object Message");

    private static final Map<Integer, Error> LOOKUP = new HashMap<Integer, Error>();

    static {
        for (final Error enumeration : Error.values()) {
            LOOKUP.put(enumeration.getCode(), enumeration);
        }
    }

    private final Integer code;

    private final String message;

    private MessageType(final Integer code, final String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
