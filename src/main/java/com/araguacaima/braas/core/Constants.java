package com.araguacaima.braas.core;

import java.util.HashMap;
import java.util.Map;

public class Constants {

    public static Map<String, String> environment;
    private static ProcessBuilder processBuilder = new ProcessBuilder();

    static {
        environment = new HashMap<>(processBuilder.environment());
    }

    public enum URL_RESOURCE_STRATEGIES {
        WORKBENCH,
        MAVEN,
        ABSOLUTE_DECISION_TABLE_PATH,
        GOOGLE_DRIVE_DECISION_TABLE_PATH,
        ABSOLUTE_DRL_JAR_PATH,
        ABSOLUTE_DRL_FILE_OR_DIRECTORY,
        STREAM_DECISION_TABLE,
        BINARY_BASE_64_DECISION_TABLE,
        CSV_DECISION_TABLE
    }

    public enum RULES_SESSION_TYPE {
        STATEFUL,
        STATELESS
    }

    public enum RULES_REPOSITORY_STRATEGIES {
        DRL,
        DECISION_TABLE
    }

    public enum CREDENTIALS_ORIGIN_STRATEGIES {
        SERVER,
        LOCAL,
        DB
    }
}
