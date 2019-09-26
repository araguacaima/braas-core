package com.araguacaima.braas.core;

import java.util.Map;

public interface IMessage {

    MessageType getType();

    String getFieldName();

    void setFieldName(String fieldName);

    Object getValue();

    void setValue(Object value);

    String getParent();

    void setParent(String parent);

    String getLanguage();

    void setLanguage(String language);

    String getRuleName();

    void setRuleName(String ruleName);

    String getComment();

    void setComment(String comment);

    String getExpectedValue();

    void setExpectedValue(String expectedValue);

    Map<String, Object> getContext();

    void setContext(Map<String, Object> context);
}
