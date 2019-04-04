package com.araguacaima.braas.drools.strategy;

import com.araguacaima.braas.drools.decorator.MavenArtifactDecorator;

/**
 * Created by Alejandro on 12/01/2015.
 */
public class MavenRepositoryDrlUrlResourceStrategy implements UrlResourceStrategy {
    private String mavenLocalRepositoryPath;
    private String groupid;
    private String artifactid;
    private String version;

    public MavenRepositoryDrlUrlResourceStrategy(String mavenLocalRepositoryPath,
                                                 String groupid,
                                                 String artifactid,
                                                 String version) {

        this.mavenLocalRepositoryPath = mavenLocalRepositoryPath;
        this.groupid = groupid;
        this.artifactid = artifactid;
        this.version = version;
    }

    @Override
    public String buildUrl() {
        return MavenArtifactDecorator.decorateLocalRepositoryFullPath(mavenLocalRepositoryPath,
                groupid,
                artifactid,
                version);
    }
}
