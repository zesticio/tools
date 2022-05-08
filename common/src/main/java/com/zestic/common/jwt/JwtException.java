package com.zestic.common.jwt;

public class JwtException extends Exception {

    private JwtExceptionType type;

    JwtException(JwtExceptionType type) {
        super(type.getMessage());
    }
}
