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

package com.zestic.common.ds.map;

import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.util.concurrent.ConcurrentMap;

public class SynchronizedHashMap<K, V> implements HashMap<K, V> {

    private ConcurrentMap map;

    public SynchronizedHashMap(String name) {
        DB db = DBMaker.memoryDB().make();
        map = db.hashMap(name).make();
    }

    @Override
    public void put(K key, V value) throws InterruptedException {
        map.put(key, value);
    }

    @Override
    public V get(K key) {
        return (V) map.get(key);
    }

    @Override
    public V get() {
        return null;
    }

    @Override
    public Integer size() {
        return map.size();
    }

    @Override
    public Boolean isEmpty() {
        return map.isEmpty();
    }
}
