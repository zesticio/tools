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

import org.apache.commons.codec.binary.Base64;

public class JwtDecoder {

    static JwtDecodeResult decodeToken(String token, String key) {
        try {
            final var parts = partitionToken(token);
            final var headerJson = decodeBase64(parts[0]);
            final var header = JwtHeader.fromJson(headerJson);
            final var payload = decodeBase64(parts[1]);

            if (verifySignature(header.getAlg(), key, parts[0] + parts[1], parts[2])) {
                return new JwtDecodeResult(payload, header);
            }
            return new JwtDecodeResult(new JwtException(JwtExceptionType.INVALID_SIGNATURE));
        } catch (JwtException e) {
            return new JwtDecodeResult(e);
        }
    }

    protected static String[] partitionToken(String token) throws JwtException {
        final var parts = token.split("\\.");
        if (parts.length != 3) throw new JwtException(JwtExceptionType.NOT_ENOUGH_SEGMENTS);
        return parts;
    }

    protected static boolean verifySignature(Algorithm algorithm, String key, String signingInput, String signature) throws JwtException {
        return JwtEncoder.signHmac(signingInput, key, algorithm).equals(signature);
    }

    protected static String decodeBase64(String s) {
        return new String(Base64.decodeBase64(s));
    }
}
