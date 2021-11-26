/*
 * Copyright (C) 2010, FuseSource Corp.  All rights reserved.
 */
package com.zestic.dispatch;

/*
 * <p>
 *  We prefer the use of Task over Runnable since the
 *  JVM can more efficiently invoke methods of
 *  an abstract class than a interface.
 * </p>
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public abstract class Task implements Runnable {
    abstract public void run();
}
