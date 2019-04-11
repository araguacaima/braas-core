package com.araguacaima.braas;

import java.util.Map;

/**
 * Created by Alejandro on 08/12/2014.
 */
@SuppressWarnings("WeakerAccess")
public class RuleMessageSuccess extends RuleMessage {

    public MessageType type = MessageType.SUCCESS;

    public RuleMessageSuccess(String language, String ruleName, String comment, String expectedValue, String parent, String fieldName, Object object) {
        this(language, ruleName, comment, expectedValue, parent, fieldName, object, null);
    }

    public RuleMessageSuccess(String language, String ruleName, String comment, String expectedValue, String parent, String fieldName, Object object, Map<String, Object> context) {
        super(language, ruleName, comment, expectedValue, parent, fieldName, object, context);
    }


    @Override
    public MessageType getType() {
        return type;
    }
}
