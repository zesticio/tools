/*
 * Copyright (C) 2012 FuseSource, Inc.
 * http://fusesource.com
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

package com.zestic.dispatch.internal;

/*
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
abstract public class WorkerThread extends Thread {

    public abstract ThreadDispatchQueue getDispatchQueue();

    public abstract void unpark();
    public abstract NioManager getNioManager();

    protected WorkerThread() {
    }

    protected WorkerThread(ThreadGroup threadGroup, String s) {
        super(threadGroup, s);
    }

    protected WorkerThread(String s) {
        super(s);
    }

    public static WorkerThread currentWorkerThread() {
        Thread thread = Thread.currentThread();
        if( thread instanceof WorkerThread) {
            return (WorkerThread)thread;
        }
        return null;
    }


    @Override
    public void setUncaughtExceptionHandler(UncaughtExceptionHandler uncaughtExceptionHandler) {
        getDispatchQueue().getDispatcher().setUncaughtExceptionHandler(uncaughtExceptionHandler);
    }
}
