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

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Getter
@JsonPropertyOrder({"alg", "extraHeader"})
@NoArgsConstructor
public class JwtHeader {

    private Algorithm alg;
    @JsonRawValue
    private String extraHeader;
    private String type;

    JwtHeader(Algorithm alg) {
        this.alg = alg;
        this.type = "JWT";
    }

    JwtHeader(Algorithm alg, String header) {
        this.alg = alg;
        this.type = "JWT";
        this.extraHeader = header;
    }

    Optional<String> toJson() {
        try {
            return Optional.of(new ObjectMapper().writeValueAsString(this));
        } catch (JsonProcessingException e) {
            return Optional.empty();
        }
    }

    static JwtHeader fromJson(String json) throws JwtException {
        try {
            return new ObjectMapper().readerFor(JwtHeader.class).readValue(json);
        } catch (JsonProcessingException e) {
            throw new JwtException(JwtExceptionType.FAILED_TO_PARSE_HEADER);
        }
    }
}
