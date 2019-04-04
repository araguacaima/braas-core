package com.araguacaima.braas;

/**
 * Created by Alejandro on 08/12/2014.
 */
public class RuleMessageWarning extends RuleMessage {

    public static MessageType type = MessageType.WARNING;

    @Override
    public MessageType getType() {
        return type;
    }
}
