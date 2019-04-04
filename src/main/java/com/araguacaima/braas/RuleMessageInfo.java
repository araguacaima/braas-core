package com.araguacaima.braas;

/**
 * Created by Alejandro on 08/12/2014.
 */
public class RuleMessageInfo extends RuleMessage {

    public static MessageType type = MessageType.INFO;

    public RuleMessageInfo(String language, String ruleName, String comment, String expectedValue, String parent, String fieldName, Object object) {
        super(language, ruleName, comment, expectedValue, parent, fieldName, object);
    }

    @Override
    public MessageType getType() {
        return type;
    }
}
