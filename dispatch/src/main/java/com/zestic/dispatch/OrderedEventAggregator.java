/*
 * Copyright (c) 2008-2009 Apple Inc. All rights reserved.
 * Copyright (C) 2012 FuseSource, Inc.
 * http://fusesource.com
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

package com.zestic.dispatch;

/*
 * This class should be implemented by Aggregator classes which
 * depend on FIFO ordering of events from the point of view of
 * the serial queue which merges events into it.
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public interface OrderedEventAggregator<Event, MergedEvent> extends EventAggregator<Event, MergedEvent>{
}
