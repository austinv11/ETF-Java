package com.austinv11.etf.util;

import java.util.Arrays;

/**
 * This represents a generic exception thrown when parsing/writing etf.
 */
public class ETFException extends RuntimeException {
    
    private byte[] data = null;
    private int position = -1;

    public ETFException() {
        super();
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
    
    public ETFException withData(byte[] data) {
        this.data = data;
        return this;
    }
    
    public ETFException withData(byte[] data, int position) {
        this.data = data;
        this.position = position;
        return this;
    }
    
    @Override
    public String getMessage() {
        String message = super.getMessage();
        
        if (data != null) {
            message += System.lineSeparator() + Arrays.toString(data);
        }
        
        if (position != -1) {
            message += "\n ";
            for (int i = 0; i < position; i++) {
                String byteStringVal = String.valueOf(data[i]);
                for (int j = 0; j < byteStringVal.length(); j++)
                    message += " ";
                message += "  ";
            }
            message += "^";
        }
        
        return message;
    }
}
