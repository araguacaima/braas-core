package com.araguacaima.braas.drools.strategy;

import com.araguacaima.braas.drools.decorator.MavenArtifactDecorator;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

/**
 * Created by Alejandro on 12/01/2015.
 */
public class AbsolutePathDrlResourceStrategy implements ResourceStrategy {

    String absoluteLocalPath;
    String artifactName;

    public AbsolutePathDrlResourceStrategy(String absoluteLocalPath, String artifactName) {

        this.absoluteLocalPath = absoluteLocalPath;
        this.artifactName = artifactName;
    }

    @Override
    public String buildUrl() {
        return MavenArtifactDecorator.decorateAbsolutePath(absoluteLocalPath, artifactName);
    }

    @Override
    public ByteArrayOutputStream getStream() {
        return null;
    }
}
