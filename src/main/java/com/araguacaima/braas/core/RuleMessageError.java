package com.araguacaima.braas.core;

import java.util.Map;

/**
 * Created by Alejandro on 21/11/2014.
 */
@SuppressWarnings("WeakerAccess")
public class RuleMessageError extends RuleMessage {
    public MessageType type = MessageType.ERROR;

    public RuleMessageError(String language, String ruleName, String comment, String expectedValue, String parent, String fieldName, Object value) {
        this(language, ruleName, comment, expectedValue, parent, fieldName, value, null);
    }

    public RuleMessageError(String language, String ruleName, String comment, String expectedValue, String parent, String fieldName, Object value, Map<String, Object> context) {
        super(language, ruleName, comment, expectedValue, parent, fieldName, value, context);
    }

    @Override
    public MessageType getType() {
        return type;
    }
}
