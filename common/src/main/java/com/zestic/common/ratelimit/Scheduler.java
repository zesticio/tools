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

import com.zestic.common.utils.DateUtil;

import java.util.Timer;
import java.util.TimerTask;

public class Scheduler {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(Scheduler.class);

    protected void start(long delay, long period, long limit, RateLimiterListener listener) {
        logger.debug("delay " + delay + " period " + period + " limit " + limit);
        new Timer(true).schedule(new TimerTask() {

            @Override
            public void run() {
                logger.debug("Going to refill the bucket " + DateUtil.dateStringFromLocalDate() + " : with " + " " + limit + " tokens");
                listener.refill();
            }
        }, delay, period);
    }
}
