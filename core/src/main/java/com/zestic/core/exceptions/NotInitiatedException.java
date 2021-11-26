package com.zestic.core.exceptions;

import com.zestic.core.util.StrUtil;

public class NotInitiatedException extends RuntimeException {
    private static final long serialVersionUID = 8247610319171014183L;

    public NotInitiatedException(Throwable e) {
        super(e);
    }

    public NotInitiatedException(String message) {
        super(message);
    }

    public NotInitiatedException(String messageTemplate, Object... params) {
        super(StrUtil.format(messageTemplate, params));
    }

    public NotInitiatedException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public NotInitiatedException(String message, Throwable throwable, boolean enableSuppression,
                                 boolean writableStackTrace) {
        super(message, throwable, enableSuppression, writableStackTrace);
    }

    public NotInitiatedException(Throwable throwable, String messageTemplate, Object... params) {
        super(StrUtil.format(messageTemplate, params), throwable);
    }
}
