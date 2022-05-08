package com.zestic.common.jwt;

import lombok.AllArgsConstructor;

@AllArgsConstructor
enum Algorithm {
    HS256("HmacSHA256"),
    HS384("HmacSHA384"),
    HS512("HmacSHA512");
    private final String name;
    String getName() {
        return this.name;
    }
}
