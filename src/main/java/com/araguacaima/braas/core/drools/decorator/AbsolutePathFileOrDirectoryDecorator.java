package com.araguacaima.braas.core.drools.decorator;

/**
 * Created by Alejandro on 28/11/2014.
 */
public class AbsolutePathFileOrDirectoryDecorator {

    public static String decorateAbsolutePath(String rulesPath, String artifactName) {
        final String s = rulesPath.replaceAll("\\\\", "/");
        return "file://" + (s.startsWith("/") ? "" : "/") + s + "/" + artifactName;
    }

}
