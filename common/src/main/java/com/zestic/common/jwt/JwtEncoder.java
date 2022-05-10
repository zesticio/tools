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

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

class JwtEncoder {

    static String signHmac(String msg, String key, Algorithm algorithm) throws JwtException {
        try {
            Mac mac = Mac.getInstance(algorithm.getName());
            mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), algorithm.getName()));
            return encodeBase64(new String(mac.doFinal(msg.getBytes(StandardCharsets.UTF_8))));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new JwtException(JwtExceptionType.INVALID_ALGORITHM);
        }
    }

    static String encodeBase64(String s) {
        return Base64.encodeBase64URLSafeString(s.getBytes(StandardCharsets.UTF_8));
    }

    static JwtEncodeResult signToken(String encodedHeader, String payload, String secret, Algorithm algorithm) {
        var encodedPayload = JwtEncoder.encodeBase64(payload);
        try {
            var signature = JwtEncoder.signHmac(encodedHeader + encodedPayload, secret, algorithm);
            return new JwtEncodeResult(String.format("%s.%s.%s", encodedHeader, encodedPayload, signature));
        } catch (JwtException e) {
            return new JwtEncodeResult(e);
        }
    }

    static Optional<String> encodeHeader(Algorithm algorithm, Optional<String> extraHeader) {
        return getHeaderJson(algorithm, extraHeader)
                .stream()
                .map(JwtEncoder::encodeBase64)
                .findFirst();
    }

    static Optional<String> getHeaderJson(Algorithm algorithm, Optional<String> extraHeader) {
        return extraHeader
                .map(header -> new JwtHeader(algorithm, header).toJson())
                .orElse(new JwtHeader(algorithm).toJson());
    }

}
