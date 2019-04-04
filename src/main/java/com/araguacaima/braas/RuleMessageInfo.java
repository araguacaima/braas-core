package com.araguacaima.braas;

/**
 * Created by Alejandro on 08/12/2014.
 */
public class RuleMessageInfo extends RuleMessage {

    public static MessageType type = MessageType.INFO;

    @Override
    public MessageType getType() {
        return type;
    }
}
