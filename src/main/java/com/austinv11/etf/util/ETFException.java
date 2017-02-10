package com.austinv11.etf.util;

/**
 * This represents a generic exception thrown when parsing/writing etf.
 */
public class ETFException extends RuntimeException {

    public ETFException() {
    }

    public ETFException(String message) {
        super(message);
    }

    public ETFException(String message, Throwable cause) {
        super(message, cause);
    }

    public ETFException(Throwable cause) {
        super(cause);
    }

    public ETFException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
