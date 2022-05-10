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
