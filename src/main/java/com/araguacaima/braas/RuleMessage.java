package com.araguacaima.braas;

import java.util.*;

/**
 * Created by Alejandro on 08/12/2014.
 */
public abstract class RuleMessage implements IMessage {

    private String language;
    private String ruleName;
    private String comment;
    private String expectedValue;
    private String parent;
    private String fieldName;
    private Object object;

    public RuleMessage() {

    }

    public RuleMessage(String language, String ruleName, String comment, String expectedValue, String parent, String fieldName, Object object) {
        this.language = language;
        this.ruleName = ruleName;
        this.comment = comment;
        this.expectedValue = expectedValue;
        this.parent = parent;
        this.fieldName = fieldName;
        this.object = object;
    }

    @Override
    public String getFieldName() {
        return fieldName;
    }

    @Override
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public Object getObject() {
        return object;
    }

    @Override
    public void setObject(Object object) {
        this.object = object;
    }

    @Override
    public String getParent() {
        return parent;
    }

    @Override
    public void setParent(String parent) {
        this.parent = parent;
    }

    @Override
    public String getLanguage() {
        return language;
    }

    @Override
    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public String getRuleName() {
        return ruleName;
    }

    @Override
    public String getComment() {
        return comment;
    }

    @Override
    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    @Override
    public String getExpectedValue() {
        return expectedValue;
    }

    @Override
    public void setExpectedValue(String expectedValue) {
        this.expectedValue = expectedValue;
    }
}
