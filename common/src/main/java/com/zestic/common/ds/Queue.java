package com.zestic.common.ds;

public interface Queue<T> {

    public void enqueue(T element) throws InterruptedException;

    public T dequeue();

    public Integer size();

    public Boolean isEmpty();
}
