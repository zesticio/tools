/*
 *
 * Version:  1.0.0
 *
 * Authors:  Kumar <kumar@elitasolutions.in>
 *
 *********************
 *
 * Copyright (c) 2009,2010,2011 Elita IT Solutions
 * All Rights Reserved.
 *
 *********************
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Elita IT Solutions and its suppliers, if any.
 * The intellectual and technical concepts contained
 * herein are proprietary to Elita IT Solutions
 * and its suppliers and may be covered by India and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Elita IT Solutions.
 *
 * The above copyright notice and this permission notice must be included
 * in all copies of this file.
 *
 * Description:
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
