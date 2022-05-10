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

public abstract class ProcessingThread implements Runnable {

    private static final String PROCESSING_THREAD_NAME = "PROCESSING_THREAD";

    private static int threadIndex = 0;

    private boolean keepProcessing = true;

    private final byte PROC_INITIALISING = 0;

    private final byte PROC_RECEIVING = 1;

    private final byte PROC_FINISHED = 2;

    private byte processingStatus = PROC_INITIALISING;

    private Object processingStatusLock = new Object();

    private Exception termException = null;

    private Thread processingThread = null;

    public abstract void process();

    public void start() throws InterruptedException {
        if (!isProcessing()) { // i.e. is initialising or finished
            setProcessingStatus(PROC_INITIALISING);
            termException = null;
            keepProcessing = true;
            processingThread = new Thread(this);
            processingThread.setName(generateIndexedThreadName());
            processingThread.start();
            while (isInitialising()) {
                Thread.yield(); // we're waiting for the proc thread to start
            }
        }
    }

    public void stop() {
        if (isProcessing()) {
            stopProcessing(null);
            while (!isFinished()) {
                processingStatus = PROC_FINISHED;
                Thread.yield(); // we're waiting for the proc thread to stop
            }
        }
        Runtime.getRuntime().gc();
    }

    protected void stopProcessing(Exception e) {
        setTermException(e);
        keepProcessing = false;
    }

    @Override
    public void run() {
        try {
            System.err.println("inside run");
            setProcessingStatus(PROC_RECEIVING);
            while (keepProcessing) {
                process();
                Thread.yield();
            }
        } catch (Exception e) {
            setTermException(e);
            e.printStackTrace();
        } finally {
            setProcessingStatus(PROC_FINISHED);
        }
    }

    public String getThreadName() {
        return PROCESSING_THREAD_NAME;
    }

    public int getThreadIndex() {
        return ++threadIndex;
    }

    public String generateIndexedThreadName() {
        return getThreadName() + "-" + getThreadIndex();
    }

    protected void setTermException(Exception e) {
        termException = e;
    }

    public Exception getTermException() {
        return termException;
    }

    private void setProcessingStatus(byte value) {
        synchronized (processingStatusLock) {
            processingStatus = value;
        }
    }

    private boolean isInitialising() {
        synchronized (processingStatusLock) {
            return processingStatus == PROC_INITIALISING;
        }
    }

    private boolean isProcessing() {
        synchronized (processingStatusLock) {
            return processingStatus == PROC_RECEIVING;
        }
    }

    private boolean isFinished() {
        synchronized (processingStatusLock) {
            return processingStatus == PROC_FINISHED;
        }
    }
}
