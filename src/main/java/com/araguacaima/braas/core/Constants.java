package com.araguacaima.braas.core;

public class Constants {

    public enum URL_RESOURCE_STRATEGIES {
        WORKBENCH,
        MAVEN,
        ABSOLUTE_DECISION_TABLE_PATH,
        GOOGLE_DRIVE_DECISION_TABLE_PATH,
        ABSOLUTE_DRL_JAR_PATH,
        ABSOLUTE_DRL_FILE_OR_DIRECTORY,
        STREAM_DECISION_TABLE
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
