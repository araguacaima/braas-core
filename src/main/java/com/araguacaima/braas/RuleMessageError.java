package com.araguacaima.braas;

/**
 * Created by Alejandro on 21/11/2014.
 */
public class RuleMessageError extends RuleMessage {
    public static MessageType type = MessageType.ERROR;

    @Override
    public MessageType getType() {
        return type;
    }
}
