package com.zestic.common.ds;

import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinQueue<T> implements Queue<T> {

    private java.util.LinkedList<T> list = new java.util.LinkedList<T>();
    private AtomicInteger index = new AtomicInteger();

    public RoundRobinQueue() {
        index.set(0);
    }

    // Return the size of the queue
    public Integer size() {
        return list.size();
    }

    // Returns whether or not the queue is empty
    public Boolean isEmpty() {
        return list.isEmpty();
    }

    // Peek the element at the front of the queue
    // The method throws an error is the queue is empty
    public T dequeue() {
        if (isEmpty()) throw new RuntimeException("Queue Empty");
        if (index.get() >= list.size()) {
            index.set(0);
        }
        return list.get(index.getAndIncrement());
    }

    // Add an element to the back of the queue
    public void enqueue(T element) throws InterruptedException {
        list.add(element);
    }

    public Boolean remove(T element) {
        Boolean status = false;
        if (!list.isEmpty()) {
            status = list.remove(element);
        }
        return status;
    }
}
