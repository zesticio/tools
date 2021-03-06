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

package com.zestic.common.utils;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

public class ConcurrentSynchronizeQueue {

    private ConcurrentLinkedQueue queue = new ConcurrentLinkedQueue();


    private static ConcurrentSynchronizeQueue instance = null;

    private ConcurrentSynchronizeQueue() {
    }

    public static ConcurrentSynchronizeQueue getInstance() {
        if (instance == null) {
            instance = new ConcurrentSynchronizeQueue();
        }
        return instance;
    }

    public synchronized int size() throws InterruptedException {
        try {
            return queue.size();
        } catch (Exception ex) {
            throw new InterruptedException("unable to lock for read");
        } finally {
            Thread.yield();
        }
    }

    public boolean isEmpty() throws InterruptedException {
        try {
            return queue.isEmpty();
        } catch (Exception ex) {
            throw new InterruptedException("unable to lock for read");
        } finally {
            Thread.yield();
        }
    }

    public Object dequeue() throws InterruptedException {
        try {
            Object first = null;
            if (size() > 0) {
                first = queue.poll();
            }
            return first;
        } catch (InterruptedException ex) {
            throw new InterruptedException("unable to lock for read");
        } finally {
            Thread.yield();
        }
    }

    public void enqueue(Object obj) throws IndexOutOfBoundsException, InterruptedException {
        try {
            queue.add(obj);
        } catch (Exception ex) {
            throw new InterruptedException("unable to lock for write");
        } finally {
            Thread.yield();
        }
    }
   
  

    public void remove(Object obj) throws InterruptedException {
        try {
            queue.remove(obj);
        } catch (Exception ex) {
            throw new InterruptedException("unable to lock for write");
        } finally {
            Thread.yield();
        }
    }

    class Synchronizer {

        public Synchronizer() {
        }

        private Semaphore write = new Semaphore(16);
        private Semaphore read = new Semaphore(16);

        public void getWriteLock() throws InterruptedException {
            write.acquire();
        }

        public void releaseWriteLock() {
            write.release();
        }

        public void getReadLock() throws InterruptedException {
            read.acquire();
        }

        public void releaseReadLock() {
            read.release();
        }
    }
}
