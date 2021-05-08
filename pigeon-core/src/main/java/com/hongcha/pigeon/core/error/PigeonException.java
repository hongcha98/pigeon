package com.hongcha.pigeon.core.error;

public class PigeonException extends RuntimeException {
    public PigeonException(String msg) {
        super(msg);
    }

    public PigeonException(String message, Throwable cause) {
        super(message, cause);
    }

    public PigeonException(Throwable cause) {
        super(cause);
    }
}
