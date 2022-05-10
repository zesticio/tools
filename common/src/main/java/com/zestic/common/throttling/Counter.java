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

package com.zestic.common.throttling;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public final class Counter {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(Counter.class);

    private final long RATE_INTERVAL_MILLISECONDS;
    private AtomicInteger index;

    public Counter() {
        index = new AtomicInteger(0);
        RATE_INTERVAL_MILLISECONDS = TimeUnit.SECONDS.toMillis(1);
    }

    public Integer increment() {
        return index.incrementAndGet();
    }

    public Integer get() {
        return index.get();
    }

    public void reset() {
        index = new AtomicInteger(0);
    }
}
