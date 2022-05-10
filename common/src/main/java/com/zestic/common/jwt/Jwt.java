/*
 * Version:  1.0.0
 *
 * Authors:  Kumar <Deebendu Kumar>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zestic.common.jwt;

import java.util.Optional;

public class Jwt {

    public static JwtEncodeResult encode(String secret, String payload) {
        return encode(secret, payload, Optional.empty(), Optional.empty());
    }

    public static JwtEncodeResult encode(String secret, String payload, String header) {
        return encode(secret, payload, Optional.of(header), Optional.empty());
    }

    public static JwtEncodeResult encode(String secret, String payload, Algorithm algorithm) {
        return encode(secret, payload, Optional.empty(), Optional.of(algorithm));
    }

    public static JwtEncodeResult encode(String secret, String payload, Optional<String> header, Optional<Algorithm> algorithm) {
        final var alg = algorithm.orElse(Algorithm.HS256);

        return JwtEncoder.encodeHeader(alg, header)
                .map(encodedHeader -> JwtEncoder.signToken(encodedHeader, payload, secret, alg))
                .orElse(new JwtEncodeResult(new JwtException(JwtExceptionType.FAILED_TO_ENCODE_HEADER)));
    }

    public static JwtDecodeResult decode(String token, String secret) {
        if (token.isBlank()) return new JwtDecodeResult(new JwtException(JwtExceptionType.NOT_ENOUGH_SEGMENTS));
        return JwtDecoder.decodeToken(token, secret);
    }

}
