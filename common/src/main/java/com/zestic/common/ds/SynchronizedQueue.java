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
