package com.zestic.common.jwt;

import lombok.Getter;

import java.util.Optional;
import java.util.function.Consumer;

@Getter
public class JwtEncodeResult {

    private final Optional<String> token;
    private final Optional<JwtException> exception;

    JwtEncodeResult(String token) {
        this.token = Optional.of(token);
        this.exception = Optional.empty();
    }

    JwtEncodeResult(JwtException exception) {
        this.token = Optional.empty();
        this.exception = Optional.of(exception);
    }

    void foreach(Consumer<String> f) {
        token.ifPresent(f);
    }
}
