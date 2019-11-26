package com.araguacaima.braas.core.drools.strategy;

import java.io.ByteArrayOutputStream;

/**
 * Created by Alejandro on 12/01/2015.
 */
public class CsvDecisionTableResourceStrategy implements ResourceStrategy {

    private String csv;

    public CsvDecisionTableResourceStrategy(String csv) {
        this.csv = csv;
    }

    @Override
    public String buildUrl() {
        return null;
    }

    @Override
    public ByteArrayOutputStream getStream() {
        throw new UnsupportedOperationException(CsvDecisionTableResourceStrategy.class.getName() + " strategy operates only with a csv String. Use getContent() method instead");
    }

    @Override
    public String getContent() {
        return this.csv;
    }
}
