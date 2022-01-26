package com.zestic.common.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Error code for USER module.
 */
public enum Error {

    SUCCESS(200, "Ok"),
    CREATED(201, "Successfully created"),
    ACCEPTED(202, "Accepted"),
    NO_CONTENT(202, "No Content"),
    CONFLICT_EXCEPTION(299, "Conflict Exception"),
    FOUND(302, "Found"),
    BAD_REQUEST(400, "Request was invalid"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not found, requested data not found"),
    METHOD_NOT_ALLOWED(405, "Method not allowed."),
    DUPLICATE(409, "Record exist."),
    PRECONDITION_FAILED(412, "Precondition failed"),
    UNSUPPORTED_MEDIA_TYPE(412, "Unsupported Media Type"),
    MESSAGE_RATE_LIMITED(420, "Message is rate limited"),
    TOO_MANY_REQUESTS(429, "Too many requests"),
    INTERNAL_SERVER_ERROR(500, "Internal server error"),
    NOT_IMPLEMENTED(501, "Not Implemented"),
    TIMEOUT(504, "Timeout");

    private static final Map<Integer, Error> LOOKUP = new HashMap<Integer, Error>();

    static {
        for (final Error enumeration : Error.values()) {
            LOOKUP.put(enumeration.getCode(), enumeration);
        }
    }

    private final Integer code;

    private final String message;

    private Error(final Integer code, final String message) {
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
