package com.araguacaima.braas.core.google.model;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

public class Config implements Serializable {

    private String key;


    @NotNull
    private String value;

    public Config() {

    }

    public Config(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Config config = (Config) o;
        return key.equals(config.key) &&
                value.equals(config.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }
}
