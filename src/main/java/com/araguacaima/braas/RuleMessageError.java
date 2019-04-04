package com.araguacaima.braas;

/**
 * Created by Alejandro on 21/11/2014.
 */
public class RuleMessageError extends RuleMessage {
    public static MessageType type = MessageType.ERROR;

    public RuleMessageError(String language, String ruleName, String comment, String expectedValue, String parent, String fieldName, Object object) {
        super(language, ruleName, comment, expectedValue, parent, fieldName, object);
    }

    @Override
    public MessageType getType() {
        return type;
    }
}
