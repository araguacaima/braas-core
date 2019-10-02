package com.araguacaima.braas.core.drools.model.forms;

public class QuestionOption {
    private String title;
    private String description;
    private double weighing;
    private boolean isText;
    private boolean selected = false;

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

    public double getWeighing() {
        return weighing;
    }

    public void setWeighing(double weighing) {
        this.weighing = weighing;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isText() {
        return isText;
    }

    public void setText(boolean text) {
        isText = text;
    }
}
