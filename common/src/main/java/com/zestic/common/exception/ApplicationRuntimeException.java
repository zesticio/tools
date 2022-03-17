package com.zestic.common.exception;

public class ApplicationRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private static final org.apache.log4j.Logger logger = org.apache.log4j.LogManager.getLogger(ApplicationRuntimeException.class);

    public ApplicationRuntimeException(Exception ex) {
        super(ex);
        logger.error(ex.toString());
    }
}
