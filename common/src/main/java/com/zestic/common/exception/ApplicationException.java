package com.zestic.common.exception;

public class ApplicationException extends Exception {

    private static final long serialVersionUID = -1771088537167544111L;

    private static final org.apache.log4j.Logger logger = org.apache.log4j.LogManager.getLogger(ApplicationRuntimeException.class);

    private Integer code;

    public ApplicationException(Integer code, String message) {
        super(message);
        this.code = code;
        logger.error(message);
    }
}
