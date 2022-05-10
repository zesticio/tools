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

/**
 * Hashmap store the data in the form of key-value pairs. These values of HashMap can be accessed by using their respective keys.
 * The key value can also be assessed using their indexes
 * HashMap can have one null key and any number of null values
 * @param <K>
 * @param <V>
 */
public interface HashMap<K, V> {

    public void put(K key, V value) throws InterruptedException;

    public V get(K key);

    public V get();

    public Integer size();

    public Boolean isEmpty();
}
