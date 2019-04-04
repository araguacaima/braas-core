package com.araguacaima.braas;

/**
 * Created by Alejandro on 08/12/2014.
 */
public class RuleMessageSuccess extends RuleMessage {

    public static MessageType type = MessageType.SUCCESS;

    @Override
    public MessageType getType() {
        return type;
    }
}
