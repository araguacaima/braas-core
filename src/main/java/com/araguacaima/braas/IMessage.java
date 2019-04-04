package com.araguacaima.braas;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.beans.Transient;
import java.util.Map;
import java.util.Set;

public interface IMessage {

    MessageType getType();

    String getFieldName();

    void setFieldName(String fieldName);

    Object getObject();

    void setObject(Object object);

    String getParent();

    void setParent(String parent);

    String getLanguage();

    void setLanguage(String language);

    String getRuleName();

    String getComment();

    void setComment(String comment);

    void setRuleName(String ruleName);

    String getExpectedValue();

    void setExpectedValue(String expectedValue);
}
