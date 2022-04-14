package com.zestic.common.ds;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

public class SynchronizedQueue<T> implements Iterable<T>, Queue<T> {

    private BlockingQueue<T> blockingQueue = new SynchronousQueue<>();

    public SynchronizedQueue() {
    }

    // Return the size of the queue
    public Integer size() {
        return blockingQueue.size();
    }

    // Returns whether or not the queue is empty
    public Boolean isEmpty() {
        return blockingQueue.isEmpty();
    }

    // Peek the element at the front of the queue
    // The method throws an error is the queue is empty
    public T dequeue() {
        if (isEmpty()) throw new RuntimeException("Queue Empty");
        return blockingQueue.poll();
    }

    // Add an element to the back of the queue
    public void enqueue(T element) throws InterruptedException {
        blockingQueue.put(element);
    }

    // Return an iterator to alow the user to traverse
    // through the elements found inside the queue
    @Override
    public java.util.Iterator<T> iterator() {
        return blockingQueue.iterator();
    }
}
