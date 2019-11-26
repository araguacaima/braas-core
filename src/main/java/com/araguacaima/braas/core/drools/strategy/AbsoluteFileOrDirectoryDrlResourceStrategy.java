package com.araguacaima.braas.core.drools.strategy;

import com.araguacaima.braas.core.drools.decorator.AbsolutePathFileOrDirectoryDecorator;

import java.io.ByteArrayOutputStream;

/**
 * Created by Alejandro on 12/01/2015.
 */
public class AbsoluteFileOrDirectoryDrlResourceStrategy implements ResourceStrategy {

    String rulesPath;
    String ruleName;

    public AbsoluteFileOrDirectoryDrlResourceStrategy(String rulesPath, String ruleName) {
        this.rulesPath = rulesPath;
        this.ruleName = ruleName;
    }

    @Override
    public String buildUrl() {
        return AbsolutePathFileOrDirectoryDecorator.decorateAbsolutePath(rulesPath, ruleName);
    }

    @Override
    public ByteArrayOutputStream getStream() {
        return null;
    }

    @Override
    public String getContent() {
        return null;
    }
}
