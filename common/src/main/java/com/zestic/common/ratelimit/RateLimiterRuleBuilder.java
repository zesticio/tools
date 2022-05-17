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

import java.util.concurrent.TimeUnit;

public class RateLimiterRuleBuilder {

    private RateLimiterRule rateLimiterRule;

    public RateLimiterRuleBuilder() {
        this.rateLimiterRule = new RateLimiterRule();
    }

    public RateLimiterRuleBuilder setId(String id) {
        this.rateLimiterRule.setId(id);
        return this;
    }

    public RateLimiterRuleBuilder setApplication(String application) {
        this.rateLimiterRule.setApplication(application);
        return this;
    }

    public RateLimiterRuleBuilder setLimit(long limit) {
        this.rateLimiterRule.setLimit(limit);
        return this;
    }

    public RateLimiterRuleBuilder setPeriod(long period) {
        this.rateLimiterRule.setPeriod(period);
        return this;
    }

    public RateLimiterRuleBuilder setUnit(TimeUnit unit) {
        this.rateLimiterRule.setUnit(unit);
        return this;
    }

    public RateLimiterRuleBuilder setLimiterModel(LimiterModel model) {
        this.rateLimiterRule.setLimiterModel(model);
        return this;
    }

    public RateLimiterRule build() {
        RateLimiterRuleBuilder.check(this.rateLimiterRule);
        return this.rateLimiterRule;
    }

    public static void check(RateLimiterRule rateLimiterRule) {
        assert rateLimiterRule.getPeriod() >= 0;
        assert rateLimiterRule.getPeriod() >= 1;
    }
}
