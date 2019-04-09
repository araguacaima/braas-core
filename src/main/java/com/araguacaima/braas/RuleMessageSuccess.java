package com.araguacaima.braas;

/**
 * Created by Alejandro on 08/12/2014.
 */
public class RuleMessageSuccess extends RuleMessage {

    public MessageType type = MessageType.SUCCESS;

    public RuleMessageSuccess(String language, String ruleName, String comment, String expectedValue, String parent, String fieldName, Object object) {
        super(language, ruleName, comment, expectedValue, parent, fieldName, object);
    }

    @Override
    public MessageType getType() {
        return type;
    }
}
