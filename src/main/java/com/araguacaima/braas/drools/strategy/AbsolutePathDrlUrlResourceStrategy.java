package com.araguacaima.braas.drools.strategy;

import com.araguacaima.braas.drools.decorator.MavenArtifactDecorator;

/**
 * Created by Alejandro on 12/01/2015.
 */
public class AbsolutePathDrlUrlResourceStrategy implements UrlResourceStrategy {

    String absoluteLocalPath;
    String artifactName;

    public AbsolutePathDrlUrlResourceStrategy(String absoluteLocalPath, String artifactName) {

        this.absoluteLocalPath = absoluteLocalPath;
        this.artifactName = artifactName;
    }

    @Override
    public String buildUrl() {
        return MavenArtifactDecorator.decorateAbsolutePath(absoluteLocalPath, artifactName);
    }
}
