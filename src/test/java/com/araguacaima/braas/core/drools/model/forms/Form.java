package com.araguacaima.braas.core.drools.model.forms;

import java.util.LinkedHashSet;
import java.util.Set;

public class Form {

    private String id;
    private FormLocale locale;
    private String title;
    private String description;
    private String url;
    private Set<Question> questions = new LinkedHashSet<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public FormLocale getLocale() {
        return locale;
    }

    public void setLocale(FormLocale locale) {
        this.locale = locale;
    }

    public Set<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(Set<Question> questions) {
        this.questions = questions;
    }

    public enum FormLocale {

        ES("es"),
        EN("en");
        private final String value;

        FormLocale(String value) {
            this.value = value;
        }

        public static FormLocale fromValue(String value) {
            for (FormLocale c : FormLocale.values()) {
                if (c.value.equals(value)) {
                    return c;
                }
            }
            throw new IllegalArgumentException(value);
        }

        @Override
        public String toString() {
            return this.value;
        }

    }
}
