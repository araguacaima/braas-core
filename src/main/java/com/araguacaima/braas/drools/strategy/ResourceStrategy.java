package com.araguacaima.braas.drools.strategy;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

/**
 * Created by Alejandro on 12/01/2015.
 */
public interface ResourceStrategy {

    String buildUrl();

    ByteArrayOutputStream getStream();
}
