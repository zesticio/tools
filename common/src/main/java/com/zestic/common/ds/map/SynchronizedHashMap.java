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
