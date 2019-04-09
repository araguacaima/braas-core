package com.araguacaima.braas.drools.factory;

import com.araguacaima.braas.Constants;
import com.araguacaima.braas.drools.DroolsConfig;
import com.araguacaima.braas.drools.strategy.*;
import com.araguacaima.braas.google.GoogleDriveUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.net.URL;

/**
 * Created by Alejandro on 12/01/2015.
 */
public class UrlResourceStrategyFactory {

    public static ResourceStrategy getUrlResourceStrategy(DroolsConfig droolsConfig) {
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
                return new WorkbenchRepositoryResourceStrategy(protocol,
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
                return new MavenRepositoryDrlResourceStrategy(mavenLocalRepositoryPath,
                        groupid,
                        artifactid,
                        version);
            case ABSOLUTE_DRL_PATH:
                absoluteLocalPath = droolsConfig.getAbsoluteLocalPath();
                artifactName = droolsConfig.getArtifactName();
                return new AbsolutePathDrlResourceStrategy(absoluteLocalPath, artifactName);
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
                    return new AbsolutePathDecisionTableResourceStrategy(new File(file1));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            case GOOGLE_DRIVE_DECISION_TABLE_PATH:
                absoluteLocalPath = droolsConfig.getAbsoluteLocalPath();
                try {
                    ByteArrayOutputStream excelStream = GoogleDriveUtils.getSpreadsheet(absoluteLocalPath);
                    return new StreamDecisionTableResourceStrategy(excelStream);
                } catch (Throwable t) {
                    t.printStackTrace();
                    return null;
                }
            default:
                throw new IllegalArgumentException();
        }
    }
}