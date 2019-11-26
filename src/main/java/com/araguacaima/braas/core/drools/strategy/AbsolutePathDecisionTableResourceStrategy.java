package com.araguacaima.braas.core.drools.strategy;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * Created by Alejandro on 12/01/2015.
 */
public class AbsolutePathDecisionTableResourceStrategy implements ResourceStrategy {

    String spreadsheetFileName;

    public AbsolutePathDecisionTableResourceStrategy(File spreadsheetFile)
            throws IOException {
        this.spreadsheetFileName = spreadsheetFile.getCanonicalPath();
    }

    @Override
    public String buildUrl() {
        return spreadsheetFileName;
    }

    @Override
    public ByteArrayOutputStream getStream() {
        return null;
    }

    @Override
    public String getContent() {
        return null;
    }
}
