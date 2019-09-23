package com.araguacaima.braas.core;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

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
    private Map<String, Object> context;

    private static final String DEFAULT_ENCODING = StandardCharsets.UTF_8.toString();

    public RuleMessage() {

    }


    public RuleMessage(String language, String ruleName, String comment, String expectedValue, String parent, String fieldName, Object object) {
        this(language, ruleName, comment, expectedValue, parent, fieldName, object, null);
    }

    public RuleMessage(String language, String ruleName, String comment, String expectedValue, String parent, String fieldName, Object object, Map<String, Object> context) {
        this.language = language;
        try {
            this.ruleName = URLDecoder.decode(ruleName, DEFAULT_ENCODING);
        } catch (UnsupportedEncodingException ignored) {
            this.ruleName = ruleName;
        }
        try {
            this.comment = URLDecoder.decode(comment, DEFAULT_ENCODING);
        } catch (UnsupportedEncodingException ignored) {
            this.comment = comment;
        }
        try {
            this.expectedValue = URLDecoder.decode(expectedValue, DEFAULT_ENCODING);
        } catch (UnsupportedEncodingException ignored) {
            this.expectedValue = expectedValue;
        }
        this.parent = parent;
        this.fieldName = fieldName;
        this.object = object;
        this.context = context;
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
    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
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
    public String getExpectedValue() {
        return expectedValue;
    }

    @Override
    public void setExpectedValue(String expectedValue) {
        this.expectedValue = expectedValue;
    }

    public Map<String, Object> getContext() {
        return context;
    }

    public void setContext(Map<String, Object> context) {
        this.context = context;
    }
}
