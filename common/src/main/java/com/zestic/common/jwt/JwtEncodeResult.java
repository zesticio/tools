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
