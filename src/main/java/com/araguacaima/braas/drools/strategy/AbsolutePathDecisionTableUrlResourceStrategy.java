package com.araguacaima.braas.drools.strategy;

import java.io.File;
import java.io.IOException;

/**
 * Created by Alejandro on 12/01/2015.
 */
public class AbsolutePathDecisionTableUrlResourceStrategy implements UrlResourceStrategy {

    String excelFileName;

    public AbsolutePathDecisionTableUrlResourceStrategy(File excelFile)
            throws IOException {
        this.excelFileName = excelFile.getCanonicalPath();
    }

    @Override
    public String buildUrl() {
        return excelFileName;
    }
}
