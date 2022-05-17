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

package com.zestic.common.ratelimit;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

@Getter
@Setter
public class RateLimiterRule implements Comparable<RateLimiterRule> {

    private String id = "id";
    private String application = "Application";
    private String name;
    //number of token stored per unit time
    private long limit;
    //unit time
    private long period = 1;
    //for now we are fixing it to seconds
    private TimeUnit unit = TimeUnit.SECONDS;
    private long version;

    private LimiterModel limiterModel;

    public RateLimiterRule() {
    }

    public long getMonitor() {
        return unit.toMillis(period);
    }

    @Override
    public int compareTo(RateLimiterRule o) {
        if (this.version < o.getVersion()) {
            return -1;
        } else if (this.version == o.getVersion()) {
            return 0;
        }
        return 1;
    }
}
