package com.araguacaima.braas.drools.strategy;

import com.araguacaima.braas.drools.decorator.DroolsWorkbenchDecorator;

/**
 * Created by Alejandro on 12/01/2015.
 */
public class WorkbenchRepositoryUrlResourceStrategy implements UrlResourceStrategy {
    private String protocol;
    private String server;
    private String port;
    private String appName;
    private String groupid;
    private String artifactid;
    private String version;

    public WorkbenchRepositoryUrlResourceStrategy(String protocol,
                                                  String server,
                                                  String port,
                                                  String appName,
                                                  String groupid,
                                                  String artifactid,
                                                  String version) {
        this.protocol = protocol;
        this.server = server;
        this.port = port;
        this.appName = appName;
        this.groupid = groupid;
        this.artifactid = artifactid;
        this.version = version;
    }

    @Override
    public String buildUrl() {
        return DroolsWorkbenchDecorator.decorate(protocol, server, port, appName, groupid, artifactid, version);
    }
}
