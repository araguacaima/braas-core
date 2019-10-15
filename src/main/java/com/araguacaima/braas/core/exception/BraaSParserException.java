package com.araguacaima.braas.core.exception;

public class BraaSParserException extends InternalBraaSException {
    public BraaSParserException(Throwable e) {
        super(e);
    }

    public BraaSParserException(String message) {
        super(message);
    }
}
