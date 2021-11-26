package com.zestic.core.exceptions;

public class ExceptionHandler implements Runnable {

    private ExceptionInterface handler;

    public ExceptionHandler(ExceptionInterface handler) {
        this.handler = handler;
    }

    public void run() {
        handler.handle();
    }
}
