package com.zectic.retrofit.error;

public class RetrofitException extends RuntimeException {
    public RetrofitException() {
        super();
    }

    public RetrofitException(String message) {
        super(message);
    }

    public RetrofitException(String message, Throwable cause) {
        super(message, cause);
    }

    public RetrofitException(Throwable cause) {
        super(cause);
    }

    protected RetrofitException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
