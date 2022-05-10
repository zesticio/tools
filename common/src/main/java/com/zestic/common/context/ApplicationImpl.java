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

package com.zestic.common.context;

import org.springframework.context.ApplicationContext;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author deebendukumar
 */
public class ApplicationImpl extends ConcurrentHashMap<String, Object> implements Application {
    private static final long serialVersionUID = -5865286831705661141L;

    private static ApplicationImpl _instance;
    private ApplicationContext context;


    private ApplicationImpl() {
    }

    public static ApplicationImpl getInstance() {
        if (_instance == null) {
            _instance = new ApplicationImpl();
        }
        return _instance;
    }

    @Override
    public Object register(String identity, Object interceptor) {
        return put(identity, interceptor);
    }

    @Override
    public Boolean unregister(String identity) {
        return remove(identity) != null;
    }

    @Override
    public Optional<Object> getObject(String identity) {
        return Optional.ofNullable(getObject(identity));
    }

    @Override
    public Boolean hasKey(String identity) {
        return containsKey(identity);
    }

    @Override
    public Boolean empty() {
        clear();
        return true;
    }

    public void setApplicationContext(ApplicationContext context) {
        this.context = context;
    }

    public ApplicationContext getApplicationContext() {
        return context;
    }
}
