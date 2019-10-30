package com.araguacaima.braas.core.drools.model.forms;

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