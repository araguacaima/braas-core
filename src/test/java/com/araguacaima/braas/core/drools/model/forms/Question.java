package com.araguacaima.braas.core.drools.model.forms;

import java.util.LinkedHashSet;
import java.util.Set;

public class Question {
    private String title;
    private String description;
    private QuestionType type;
    private int maxScore;
    private double calculatedScore;
    private Set<QuestionOption> options = new LinkedHashSet<>();

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

    public QuestionType getType() {
        return type;
    }

    public void setType(QuestionType type) {
        this.type = type;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(int maxScore) {
        this.maxScore = maxScore;
    }

    public double getCalculatedScore() {
        return calculatedScore;
    }

    public void setCalculatedScore(double calculatedScore) {
        this.calculatedScore = calculatedScore;
    }

    public Set<QuestionOption> getOptions() {
        return options;
    }

    public void setOptions(Set<QuestionOption> options) {
        this.options = options;
    }
}
