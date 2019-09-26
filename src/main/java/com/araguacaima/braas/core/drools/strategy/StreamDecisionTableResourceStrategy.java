package com.araguacaima.braas.core.drools.strategy;

import java.io.ByteArrayOutputStream;

/**
 * Created by Alejandro on 12/01/2015.
 */
public class StreamDecisionTableResourceStrategy implements ResourceStrategy {

    private ByteArrayOutputStream spreadsheetStream;

    public StreamDecisionTableResourceStrategy(ByteArrayOutputStream spreadsheetStream) {
        this.spreadsheetStream = spreadsheetStream;
    }

    @Override
    public String buildUrl() {
        return null;
    }

    @Override
    public ByteArrayOutputStream getStream() {
        return this.spreadsheetStream;
    }
}
