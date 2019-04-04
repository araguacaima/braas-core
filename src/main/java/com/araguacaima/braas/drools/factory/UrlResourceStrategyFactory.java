package com.araguacaima.braas.drools.factory;

import com.araguacaima.braas.Constants;
import com.araguacaima.braas.drools.DroolsConfig;
import com.araguacaima.braas.drools.strategy.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Created by Alejandro on 12/01/2015.
 */
public class UrlResourceStrategyFactory {

    public static UrlResourceStrategy getUrlResourceStrategy(DroolsConfig droolsConfig) {
        String protocol;
        String server;
        String port;
        String appName;
        String groupid;
        String artifactid;
        String version;
        String mavenLocalRepositoryPath;
        String absoluteLocalPath;
        String decisionTablePath;
        String artifactName;

        Constants.URL_RESOURCE_STRATEGIES urlResourceStrategy = Constants.URL_RESOURCE_STRATEGIES.valueOf
                (droolsConfig.getUrlResourceStrategy());
        switch (urlResourceStrategy) {
            case WORKBENCH:
                protocol = droolsConfig.getProtocol();
                server = droolsConfig.getServer();
                port = droolsConfig.getPort();
                appName = droolsConfig.getAppName();
                groupid = droolsConfig.getGroupid();
                artifactid = droolsConfig.getArtifactid();
                version = droolsConfig.getVersion();
                return new WorkbenchRepositoryUrlResourceStrategy(protocol,
                        server,
                        port,
                        appName,
                        groupid,
                        artifactid,
                        version);
            case MAVEN:
                groupid = droolsConfig.getGroupid();
                artifactid = droolsConfig.getArtifactid();
                version = droolsConfig.getVersion();
                mavenLocalRepositoryPath = droolsConfig.getMavenLocalRepositoryPath();
                return new MavenRepositoryDrlUrlResourceStrategy(mavenLocalRepositoryPath,
                        groupid,
                        artifactid,
                        version);
            case ABSOLUTE_DRL_PATH:
                absoluteLocalPath = droolsConfig.getAbsoluteLocalPath();
                artifactName = droolsConfig.getArtifactName();
                return new AbsolutePathDrlUrlResourceStrategy(absoluteLocalPath, artifactName);
            case ABSOLUTE_DECISION_TABLE_PATH:
                decisionTablePath = droolsConfig.getDecisionTablePath();
                String file1;
                try {
                    if (new File(decisionTablePath).exists()) {
                        file1 = decisionTablePath;
                    } else {
                        URL url = UrlResourceStrategyFactory.class.getResource(decisionTablePath);
                        file1 = url.getFile();
                    }
                    return new AbsolutePathDecisionTableUrlResourceStrategy(new File(file1));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            case GOOGLE_DRIVE_DECISION_TABLE_PATH:
                decisionTablePath = droolsConfig.getDecisionTablePath();
                String file2;
                try {
                    URL url = UrlResourceStrategyFactory.class.getResource(decisionTablePath);
                    file2 = url.getFile();
                    return new AbsolutePathDecisionTableUrlResourceStrategy(new File(file2));
                } catch (Exception ignored) {
                    File file = new File(decisionTablePath);
                    if (file.exists()) {
                        try {
                            return new AbsolutePathDecisionTableUrlResourceStrategy(file);
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
                return null;
            default:
                throw new IllegalArgumentException();
        }
    }
}