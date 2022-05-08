package com.zestic.common.jwt;

import lombok.AllArgsConstructor;

@AllArgsConstructor
enum JwtExceptionType {

    TOO_MANY_SEGMENTS("Too many segments"),
    NOT_ENOUGH_SEGMENTS("Not enough segments"),
    INVALID_ALGORITHM("Invalid algorithm specified"),
    INVALID_SIGNATURE("Invalid token signature"),
    FAILED_TO_ENCODE_HEADER("Failed to encode header part"),
    FAILED_TO_DECODE_HEADER("Failed to decode header part"),
    FAILED_TO_PARSE_HEADER("Failed to parse header part as json");

    private final String message;

    String getMessage() {
        return this.message;
    }
}
