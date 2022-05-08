package com.zestic.common.jwt;

import java.util.Optional;

public class Jwt {

    /**
     * Path of least resistance to create a token.
     * Only requires a secret and payload.
     * Default header will be generated.
     * Algorithm will default to HmacSHA256.
     *
     * @param secret  key to encrypt signature with
     * @param payload String data to encode into JWT token
     * @return {@link JwtEncodeResult}
     */

    public static JwtEncodeResult encode(String secret, String payload) {
        return encode(secret, payload, Optional.empty(), Optional.empty());
    }

    /**
     * Create JWT token with custom header field.
     * Algorithm will default to HmacSHA256.
     *
     * @param secret  key to encrypt signature with
     * @param payload String data to encode into JWT token
     * @param header  Add custom header field
     * @return {@link JwtEncodeResult}
     */

    public static JwtEncodeResult encode(String secret, String payload, String header) {
        return encode(secret, payload, Optional.of(header), Optional.empty());
    }

    /**
     * Create JWT token by specifying algorithm to use.
     * Default header will be generated.
     *
     * @param secret    key to encrypt signature with
     * @param payload   String data to encode into JWT token
     * @param algorithm Algorithm to sign token with {@link Algorithm}
     * @return {@link JwtEncodeResult}
     */

    public static JwtEncodeResult encode(String secret, String payload, Algorithm algorithm) {
        return encode(secret, payload, Optional.empty(), Optional.of(algorithm));
    }

    /**
     * Create JWT token by specifying all the needed parameters.
     * No defaults will be used.
     *
     * @param secret    key to encrypt signature with
     * @param payload   String data to encode into JWT token
     * @param header    Add custom header field
     * @param algorithm Algorithm to sign token with {@link Algorithm}
     * @return {@link JwtEncodeResult}
     */

    public static JwtEncodeResult encode(String secret, String payload, Optional<String> header, Optional<Algorithm> algorithm) {
        final var alg = algorithm.orElse(Algorithm.HS256);

        return JwtEncoder.encodeHeader(alg, header)
                .map(encodedHeader -> JwtEncoder.signToken(encodedHeader, payload, secret, alg))
                .orElse(new JwtEncodeResult(new JwtException(JwtExceptionType.FAILED_TO_ENCODE_HEADER)));
    }

    /**
     * Decode a valid JWT token. Returns the decoded payload.
     * Use the same token as used to encode the token.
     * Returns JWTException {@link JwtException} if any error occurs.
     *
     * @param token  Valid JWT token
     * @param secret key to decrypt token with (Needs to be the same as the key used to encrypt the token)
     * @return {@link JwtDecodeResult}
     */

    public static JwtDecodeResult decode(String token, String secret) {
        if (token.isBlank()) return new JwtDecodeResult(new JwtException(JwtExceptionType.NOT_ENOUGH_SEGMENTS));
        return JwtDecoder.decodeToken(token, secret);
    }

}
